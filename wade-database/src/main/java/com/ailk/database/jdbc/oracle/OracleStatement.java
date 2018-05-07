/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.oracle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.ailk.database.jdbc.wrapper.ConnectionWrapper;

/**
 * Oracle数据库连接封装类
 * 
 * @className: OracleStatement.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class OracleStatement implements Statement {
	private static final Logger log = Logger.getLogger(OracleStatement.class);
	
	private Statement stmt;
	private ConnectionWrapper proxyConnection = null;
	
	public OracleStatement (ConnectionWrapper proxyConnection, Statement stmt) throws SQLException {
		this.proxyConnection = proxyConnection;
		this.stmt = stmt;
		this.stmt.setQueryTimeout(proxyConnection.getStmtTimeout());
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("创建OracleStatement,queryTimeout=%d.", proxyConnection.getStmtTimeout()));
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return stmt.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return stmt.isWrapperFor(iface);
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		
		long start = System.currentTimeMillis();
		try {
			return new OracleResultSet(stmt.executeQuery(sql));
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(sql);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		long start = System.currentTimeMillis();
		try {
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(sql);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	@Override
	public void close() throws SQLException {
		stmt.close();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return stmt.getMaxFieldSize();
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		stmt.setMaxFieldSize(max);
	}

	@Override
	public int getMaxRows() throws SQLException {
		return stmt.getMaxRows();
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		stmt.setMaxRows(max);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		stmt.setEscapeProcessing(enable);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return stmt.getQueryTimeout();
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		stmt.setQueryTimeout(seconds);
	}

	@Override
	public void cancel() throws SQLException {
		stmt.cancel();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return stmt.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		stmt.clearWarnings();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		stmt.setCursorName(name);
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		long start = System.currentTimeMillis();
		
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		try {
			return stmt.execute(sql);
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(sql);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return new OracleResultSet(stmt.getResultSet());
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return stmt.getUpdateCount();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return stmt.getMoreResults();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		stmt.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return stmt.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		stmt.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {
		return stmt.getFetchSize();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return stmt.getResultSetConcurrency();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return stmt.getResultSetType();
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		stmt.addBatch(sql);
	}

	@Override
	public void clearBatch() throws SQLException {
		stmt.clearBatch();
	}

	@Override
	public int[] executeBatch() throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		
		long start = System.currentTimeMillis();
		try {
			return stmt.executeBatch();
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append("");
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return stmt.getConnection();
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return stmt.getMoreResults(current);
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return stmt.getGeneratedKeys();
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		long start = System.currentTimeMillis();
		try {
			return stmt.executeUpdate(sql, autoGeneratedKeys);
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(sql);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		long start = System.currentTimeMillis();
		try {
			return stmt.executeUpdate(sql, columnIndexes);
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(sql);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		long start = System.currentTimeMillis();
		try {
			return stmt.executeUpdate(sql, columnNames);
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(sql);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		long start = System.currentTimeMillis();
		try {
			return stmt.execute(sql, autoGeneratedKeys);
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(sql);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		long start = System.currentTimeMillis();
		try {
			return stmt.execute(sql, columnIndexes);
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(sql);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	@Override
	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		long start = System.currentTimeMillis();
		try {
			return stmt.execute(sql, columnNames);
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(sql);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return stmt.getResultSetHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return stmt.isClosed();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		stmt.setPoolable(poolable);
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return stmt.isPoolable();
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		stmt.closeOnCompletion();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return stmt.isCloseOnCompletion();
	}

}
