package com.wade.relax.tm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: LocalXSupervise
 * @description: 事务守护者
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-12-15
 */
public class LocalXSupervise {
	
	private static final Logger LOG = LoggerFactory.getLogger(LocalXSupervise.class);
	
	/**
	 * 记录本地事务与数据库连接的映射关系 < TID -> <connName -> Connection> >
	 */
	public static Map<String, Map<String, Connection>> LTC = new HashMap<String, Map<String, Connection>>();
	
	/**
	 * 根据事务ID，提交对应的本地事务
	 * 
	 * @param tid
	 * @throws SQLException
	 */
	public static void commit(String tid) {
		
		Map<String, Connection> xEntity = LTC.get(tid);
		if (null == xEntity) {
			LOG.error("根据TID:{},未找到对应的事务!", tid);
			return;
		}
		
		boolean success = true;
		Connection conn = null;
		for (String key : xEntity.keySet()) {
			try {
				conn = xEntity.get(key);
				
				if (success) {
					conn.commit();
				} else {
					conn.rollback();
				}
				
			} catch (SQLException e) {
				success = false;
				LOG.error("事务提交异常! tid:" + tid, e);
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					LOG.error("数据库连接关闭异常! tid:" + tid, e);
				}
			}
		}
		
		LTC.remove(tid);
	}
	
	/**
	 * 根据事务ID，回滚对应的本地事务
	 * 
	 * @param tid
	 * @throws SQLException
	 */
	public static void rollback(String tid) throws SQLException {
		
		Map<String, Connection> xEntity = LTC.get(tid);
		if (null == xEntity) {
			return;
		}
		
		for (String key : xEntity.keySet()) {
			Connection conn = null;
			try {
				conn = xEntity.get(key);
				conn.rollback();
			} finally {
				conn.close();
			}
		}
		
		LTC.remove(tid);
	}
	
}