package com.wade.relax.tm.context.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.cache.redis.RedisFactory;
import com.ailk.cache.redis.client.RedisClient;
import com.wade.relax.tm.LocalXGuarder;
import com.wade.relax.tm.RemoteXSupervise;
import com.wade.relax.tm.LocalXSupervise;
import com.wade.relax.tm.context.XContext;
import com.wade.relax.registry.SystemRuntime;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: ProductXContext
 * @description: 生产用事务上下文
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-12-15
 */
public final class ProductXContext extends XContext {

	private static final Logger LOG = LoggerFactory.getLogger(ProductXContext.class);
	private static final RedisClient redis = RedisFactory.getRedisClient("dtm");
	
	/**
	 * 是否为事务的发起点
	 */
	private XStartPoint xStartPoint = XStartPoint.INIT;
	
	/**
	 * 事务ID
	 */
	private String tid;

	/**
	 * 中心名
	 */
	private String centerName;

	/**
	 * 实例地址
	 */
	private String address;

	public ProductXContext(int timeout) {
		this.tid = createTID(timeout);
		this.centerName = SystemRuntime.getCenterName();
		this.address = SystemRuntime.getListenAddress();
		this.xStartPoint = XStartPoint.TRUE;
		
		// 映射关系上报给DTP
		reportDTP();
		
	}

	public ProductXContext(String tid) {
		this.tid = tid;
		this.centerName = SystemRuntime.getCenterName();
		this.address = SystemRuntime.getListenAddress();
		this.xStartPoint = XStartPoint.FALSE;

		if (LocalXSupervise.LTC.containsKey(tid)) {
			// 本地已经有该事务的情况下，就不上报DTP。
			return;
		} else {
			// 映射关系上报给DTP
			reportDTP();
		}
		
	}

	@Override
	public String getTID() {
		return this.tid;
	}

	@Override
	public String getActiveInstance(String centerName) {
		String address = redis.hget(this.tid, centerName);
		if (null != address) {
			LOG.debug("根据中心名 {} 找到活跃实例地址: {}", centerName, address);
		}
		return address;
	}

	/**
	 * 获取事务关联的实例地址
	 */
	public Set<String> getSockSites() {
		Set<String> addresses = redis.hvals(this.tid);
		
		String localAddress = SystemRuntime.getListenAddress();
		addresses.remove(localAddress);
		
		return addresses;
	}

	@Override
	public Connection getConnection(String connName) throws Exception {

		Map<String, Connection> map = LocalXSupervise.LTC.get(this.tid);
		if (null == map) {
			map = new HashMap<String, Connection>();
			LocalXSupervise.LTC.put(this.tid, map);
		}

		Connection conn = (Connection) map.get(connName);
		if (null == conn || conn.isClosed()) {
			conn = CONN_MANAGER.getConnection(connName);
			conn.setAutoCommit(false);
			
			map.put(connName, conn);
		}

		return conn;

	}

	@Override
	public void commit() throws SQLException {

		if (XStartPoint.INIT == this.xStartPoint) {
			throw new IllegalStateException("事务构建与事务提交不再同一线程上!");
		}
		
		if (XStartPoint.FALSE == this.xStartPoint) {
			LOG.debug("非事务发起点, 不做事务提交动作!");
			return;
		}
		
		boolean timeout = LocalXGuarder.isTimeout(this.tid);

		if (timeout) {
			LOG.debug("事务已超时, 立即进行回滚操作! tid: {}", tid);
			rollback();
			return;
		}

		// 首先提交远端事务
		for (String address : getSockSites()) {
			LOG.debug("下发COMMIT指令, address:{}, tid:{}", address, this.tid);
			RemoteXSupervise.commit(address, this.tid);
		}

		redis.del(this.tid);

		// 最后提交本地事务
		LOG.debug("提交本地事务! tid:{}", this.tid);
		LocalXSupervise.commit(this.tid);
	}

	@Override
	public void rollback() throws SQLException {

		if (XStartPoint.INIT == this.xStartPoint) {
			throw new IllegalStateException("事务构建与事务提交不再同一线程上!");
		}
		
		if (XStartPoint.FALSE == this.xStartPoint) {
			LOG.debug("非事务发起点, 不做事务回滚动作!");
			return;
		}
		
		// 首先回滚远端事务
		for (String address : getSockSites()) {
			LOG.debug("下发ROLLBACK指令, 地址:{}", address);
			RemoteXSupervise.rollback(address, this.tid);
		}

		// 最后回滚本地事务
		LOG.debug("回滚本地事务! tid:{}", this.tid);
		LocalXSupervise.rollback(this.tid);
	}

	/**
	 * 将事务ID,中心名,实例地址关系,上报给DTP
	 */
	private void reportDTP() {
		redis.hset(this.tid, this.centerName, this.address);
	}

	/**
	 * 事务开始点
	 */
	private static enum XStartPoint {
	    INIT, TRUE, FALSE
	}

}
