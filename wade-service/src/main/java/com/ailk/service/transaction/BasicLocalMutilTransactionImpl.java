package com.ailk.service.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;

/**
 * Copyright: Copyright (c) 2016 Asiainfo
 * 
 * @className: BasicLocalMutilTransactionImpl
 * @description: 本地多事务控制基础实现类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2016-9-23
 */
public class BasicLocalMutilTransactionImpl {

	private static final Logger LOG = LoggerFactory.getLogger(BasicLocalMutilTransactionImpl.class);

	private static IConnectionManager ConnectionManager = ConnectionManagerFactory.getConnectionManager();
	
	/**
	 * 当前事务上下文信息
	 */
	private static ThreadLocal<ThreadInfo> tx = new ThreadLocal<ThreadInfo>();

	public void setCurDataSource(String dataSourceName) {
		ThreadInfo threadinfo = getThreadInfo();
		threadinfo.curDataSource = dataSourceName;
	}
	
	/**
	 * 获取当前数据源名
	 * 
	 * @return
	 */
	public String getCurDataSource() {
		return getThreadInfo().curDataSource;
	}
	
	/**
	 * 根据当前数据源获取数据库连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return getConnection(getCurDataSource());
	}
	
	/**
	 * 获取全新的连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getNewConnection() throws SQLException {
		return getNewConnection(true, getCurDataSource());
	}
	
	/**
	 * 根据传入的数据源名或者数据库连接
	 * 
	 * @param dataSource
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String dataSource) throws SQLException {
		
		Connection conn = null;
		
		ThreadInfo obj = getThreadInfo();
		if (obj.txConnections.containsKey(dataSource)) {
			conn = (Connection) obj.txConnections.get(dataSource);
		} else {
			Connection objConnection = getNewConnection(false, dataSource);
			obj.txConnections.put(dataSource, objConnection);
			conn = objConnection;
		}
		
		return conn;
		
	}

	/**
	 * 获取一个全新的连接
	 * 
	 * @param isRecorded 是否被记录
	 * @param dataSource 数据源名称
	 * @return
	 * @throws SQLException
	 */
	public Connection getNewConnection(boolean isRecorded, String dataSource) throws SQLException {
		
		if (null == dataSource) {
			throw new NullPointerException("数据源名不可为空!");
		}
		
		ThreadInfo obj = getThreadInfo();
		Connection conn = ConnectionManager.getConnection(dataSource);
		
		if (isRecorded) {
			obj.txAsyncConnections.add(conn);
		}
		
		return conn;
		
	}
	
	/**
	 * 事务是否已开启
	 * 
	 * @return
	 */
	public boolean isStartTransaction() {
		
	    ThreadInfo objThreadInfo = getThreadInfo();
	    
	    if (null == objThreadInfo) {
	      return false;
	    }
	    
	    return true;
	    
	}
	
	/**
	 * 开启事务
	 * 
	 * @throws Exception
	 */
	public void startTransaction() throws Exception {
		
		LOG.debug("startTransaction 开启事务!");
		
		if (isStartTransaction()) {
			throw new Exception("不可重复开启事务!");
		}
		
		setThreadInfo(new ThreadInfo());
		
	}
	
	/**
	 * 提交事务
	 * 
	 * @throws Exception
	 */
	public void commitTransaction() throws Exception {
		
		boolean commitSuccess = true;
		ThreadInfo objThreadInfo = getThreadInfo();
		
		try {
		
			for (String dataSource : objThreadInfo.txConnections.keySet()) {
				Connection conn = objThreadInfo.txConnections.get(dataSource);
				try {
					if (commitSuccess) {
						conn.commit();
					} else {
						conn.rollback();
					}
				} catch (Throwable e) {
					commitSuccess = false;
					LOG.error("事务提交出错, dataSource: " + dataSource, e);
				} finally {
					if (!conn.isClosed()) {
						conn.close();
					}
				}
			}
			
		} finally {
			afterCompletion();
		}
		
		setThreadInfo(null);
	}

	/**
	 * 回滚事务
	 * 
	 * @throws Exception
	 */
	public void rollbackTransaction() throws Exception {
		
		ThreadInfo objThreadInfo = getThreadInfo();
		
		try {
			for (String dataSource : objThreadInfo.txConnections.keySet()) {
				Connection conn = objThreadInfo.txConnections.get(dataSource);
				
				try {
					conn.rollback();
				} catch (Throwable e) {
					LOG.error("事务回滚出错, dataSource: " + dataSource, e);
				} finally {
					if (!conn.isClosed()) {
						conn.close();
					}
				}
			}
		} finally {
			afterCompletion();
		}
		
		setThreadInfo(null);
	}
	
	/**
	 * 清理
	 */
	public void afterCompletion() {
		
		// 异步连接框架不负责提交，但要负责回收!
		ThreadInfo objThreadInfo = getThreadInfo();
		for (Connection conn : objThreadInfo.txAsyncConnections) {
			try {
				if (!conn.isClosed()) {
					conn.rollback();
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		setCurDataSource(null);
	}
	
	/**
	 * 设置上下文信息
	 * 
	 * @param objThreadInfo
	 */
	public void setThreadInfo(ThreadInfo objThreadInfo) {
		tx.set(objThreadInfo);
	}
	
	/**
	 * 获取上下文信息
	 * 
	 * @return
	 */
	public ThreadInfo getThreadInfo() {
		return (ThreadInfo) tx.get();
	}
	
	/**
	 * Copyright: Copyright (c) 2016 Asiainfo
	 * 
	 * @className: ThreadInfo
	 * @description: 线程上下文信息
	 * 
	 * @version: v1.0.0
	 * @author: zhoulin2
	 * @date: 2016-9-26
	 */
	private static class ThreadInfo {
		
		/**
		 * 同步连接
		 */
		Map<String, Connection> txConnections = new HashMap<String, Connection>();
		
		/**
		 * 异步连接
		 */
		List<Connection> txAsyncConnections = new ArrayList<Connection>();
		
		/**
		 * 当前数据源名
		 */
		String curDataSource = null;
	}
}
