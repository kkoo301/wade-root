package com.wade.relax.tm.context.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.relax.tm.LocalXSupervise;
import com.wade.relax.tm.context.XContext;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DevelopXContext
 * @description: 开发用事务上下文
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-12-15
 */
public class DevelopXContext extends XContext {

	private static final Logger LOG = LoggerFactory.getLogger(DevelopXContext.class);
	
	/**
	 * 事务ID
	 */
	private String tid;
	
	public DevelopXContext(int timeout) {
		this.tid = createTID(timeout);
	}

	public DevelopXContext(String tid) {
		this.tid = tid;
	}
	
	@Override
	public String getTID() {
		return this.tid;
	}

	@Override
	public Connection getConnection(String connName) throws Exception {
		
		Map<String, Connection> map = LocalXSupervise.LTC.get(this.tid);
		if (null == map) {
			map = new HashMap<String, Connection>();
			LocalXSupervise.LTC.put(this.tid, map);
		}

		Connection conn = (Connection) map.get(connName);
		if (null == conn) {
			conn = CONN_MANAGER.getConnection(connName);
			map.put(connName, conn);
		}

		return conn;
		
	}

	@Override
	public void commit() throws SQLException {
		LOG.debug("提交事务, tid: {}", this.tid);
		LocalXSupervise.commit(this.tid);
	}

	@Override
	public void rollback() throws SQLException {
		LOG.debug("回滚事务, tid: {}", this.tid);
		LocalXSupervise.rollback(this.tid);
	}

	@Override
	public String getActiveInstance(String centerName) {
		return "127.0.0.1:8080";
	}

}
