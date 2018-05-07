/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月22日
 * 
 * Just Do IT.
 */
package com.ailk.database.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;

/**
 * @description
 * 本地事务管理
 */
public class LocalMutilTransaction {
	
	private static final Logger log = LoggerFactory.getLogger(LocalMutilTransaction.class);
	
	/**
	 * 连接管理器
	 */
	private static final IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
	
	/**
	 * 线程级连接管理
	 */
	private static ThreadLocal<Map<String, Connection>> transcation = new ThreadLocal<Map<String,Connection>>() {
		protected Map<String,Connection> initialValue() {
			return new HashMap<String, Connection>(10);
		};
	};
	
	/**
	 * 获取数据库连接
	 * @return
	 */
	public static Connection getConnection(String dataSourceName) throws SQLException {
		Connection conn = transcation.get().get(dataSourceName);
		if (null == conn || conn.isClosed()) {
			conn = manager.getConnection(dataSourceName);
			conn.setAutoCommit(false);
			
			transcation.get().put(dataSourceName, conn);
		}
		return conn;
	}
	
	/**
	 * 全局事务提交
	 */
	public static void commit() {
		boolean commitError = false;
		for (Map.Entry<String, Connection> item : transcation.get().entrySet()) {
			if (commitError) {
				rollback();
				return;
			}
			try {
				Connection conn = item.getValue();
				if (null != conn && !conn.isClosed()) {
					conn.commit();
				}
			} catch (Exception e) {
				commitError = true;
				log.error("全局事务提交异常" + item.getKey(), e);
			}
		}
	}
	
	
	/**
	 * 全局事务回滚
	 */
	public static void rollback() {
		for (Map.Entry<String, Connection> item : transcation.get().entrySet()) {
			try {
				Connection conn = item.getValue();
				if (null != conn && !conn.isClosed()) {
					conn.rollback();
				}
			} catch (Exception e) {
				log.error("全局事务回滚异常" + item.getKey(), e);
			}
		}
	}
	
	/**
	 * 全局事务资源释放
	 */
	public static void close() {
		for (Map.Entry<String, Connection> item : transcation.get().entrySet()) {
			try {
				Connection conn = item.getValue();
				if (null != conn && !conn.isClosed()) {
					conn.close();
				}
			} catch (Exception e) {
				log.error("全局事务资源释放异常" + item.getKey(), e);
			}
		}
	}

}
