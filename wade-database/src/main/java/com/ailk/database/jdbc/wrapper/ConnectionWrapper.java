/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.wrapper;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.ailk.database.dbconn.DBConnection;

/**
 * 数据库连接封装类，为精细化控制事务，添加属性transcat，当执行rollback,close,commit时，将该属性置为false
 * 
 * @className: OracleConnection.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class ConnectionWrapper extends DBConnection {
	
	private static final Logger log = Logger.getLogger(ConnectionWrapper.class);
	
	public ConnectionWrapper(String dataSourceName, Connection connection) throws SQLException {
		super(dataSourceName, connection);
	}
	
	/**
	 * 提交事务
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("提交数据库连接 %s:%d->%s", dataSourceName, Thread.currentThread().getId(), isTransaction()));
		}
		
		try {
			if (isTransaction())
				connection.commit();
		} catch (SQLException e) {
			throw e;
		} finally {
			setTransaction(false);
			setQueryOnly(false);
			manager.cleanTrace(dataSourceName, getTraceId());
		}
	}

	/**
	 * 回滚事务
	 * 
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("回滚数据库连接 %s:%d->%s", dataSourceName, Thread.currentThread().getId(), isTransaction()));
		}
		
		try {
			if (isTransaction())
				connection.rollback();
		} catch (SQLException e) {
			throw e;
		} finally {
			setTransaction(false);
			setQueryOnly(false);
			manager.cleanTrace(dataSourceName, getTraceId());
		}
	}

	/**
	 * 如果有事务则暂时不关闭连接
	 */
	public void close() throws SQLException {
		boolean existsTransaction = isTransaction();
		try {
			if (!existsTransaction) {
				setTransaction(false);
				setQueryOnly(false);
				connection.close();
				manager.cleanTrace(dataSourceName, getTraceId());
			}
		} catch (SQLException e) {
			throw e;
		}
	}
}
