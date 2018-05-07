/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.oracle;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.ailk.database.jdbc.wrapper.ConnectionWrapper;

/**
 * Oracle PreparedStatement对象,getGeneratedKeys()未处理
 * 需验证getResultSet对象是否会存在不释放的情况
 * 需验证getConnection()是否为MySQLConnection实例
 * 
 * @className: OraclePreparedStatement.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class OraclePreparedStatement implements PreparedStatement {

	private static final Logger log = Logger.getLogger(OraclePreparedStatement.class);
	
	private PreparedStatement stmt = null;
	private ConnectionWrapper proxyConnection = null;
	private String prepareSQL = null;
	
	public OraclePreparedStatement(ConnectionWrapper proxyConnection, PreparedStatement stmt) throws SQLException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("创建OraclePreparedStatement,queryTimeout=%d.", proxyConnection.getStmtTimeout()));
		}
		
		this.proxyConnection = proxyConnection;
		this.stmt = stmt;
		this.stmt.setQueryTimeout(proxyConnection.getStmtTimeout());
	}
	
	public OraclePreparedStatement(ConnectionWrapper proxyConnection, PreparedStatement stmt, String prepareSQL) throws SQLException {
		if (log.isDebugEnabled()) {
			log.debug(String.format("创建OraclePreparedStatement,queryTimeout=%d.", proxyConnection.getStmtTimeout()));
		}
		
		this.proxyConnection = proxyConnection;
		this.stmt = stmt;
		this.stmt.setQueryTimeout(proxyConnection.getStmtTimeout());
		this.prepareSQL = prepareSQL;
	}
	
	/* (non-Javadoc)
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		long start = System.currentTimeMillis();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			return new OracleResultSet(rs);
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

	/* (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see java.sql.Statement#close()
	 */
	@Override
	public void close() throws SQLException {
		stmt.close();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getMaxFieldSize()
	 */
	@Override
	public int getMaxFieldSize() throws SQLException {
		return stmt.getMaxFieldSize();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#setMaxFieldSize(int)
	 */
	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		stmt.setMaxFieldSize(max);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getMaxRows()
	 */
	@Override
	public int getMaxRows() throws SQLException {
		return stmt.getMaxRows();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#setMaxRows(int)
	 */
	@Override
	public void setMaxRows(int max) throws SQLException {
		stmt.setMaxRows(max);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#setEscapeProcessing(boolean)
	 */
	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		stmt.setEscapeProcessing(enable);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getQueryTimeout()
	 */
	@Override
	public int getQueryTimeout() throws SQLException {
		return stmt.getQueryTimeout();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#setQueryTimeout(int)
	 */
	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		stmt.setQueryTimeout(seconds);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#cancel()
	 */
	@Override
	public void cancel() throws SQLException {
		stmt.cancel();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getWarnings()
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		return stmt.getWarnings();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#clearWarnings()
	 */
	@Override
	public void clearWarnings() throws SQLException {
		stmt.clearWarnings();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#setCursorName(java.lang.String)
	 */
	@Override
	public void setCursorName(String name) throws SQLException {
		stmt.setCursorName(name);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String)
	 */
	@Override
	public boolean execute(String sql) throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		long start = System.currentTimeMillis();
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

	/* (non-Javadoc)
	 * @see java.sql.Statement#getResultSet()
	 */
	@Override
	public ResultSet getResultSet() throws SQLException {
		ResultSet rs = stmt.getResultSet();
		return new OracleResultSet(rs);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getUpdateCount()
	 */
	@Override
	public int getUpdateCount() throws SQLException {
		return stmt.getUpdateCount();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getMoreResults()
	 */
	@Override
	public boolean getMoreResults() throws SQLException {
		return stmt.getMoreResults();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#setFetchDirection(int)
	 */
	@Override
	public void setFetchDirection(int direction) throws SQLException {
		stmt.setFetchDirection(direction);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getFetchDirection()
	 */
	@Override
	public int getFetchDirection() throws SQLException {
		return stmt.getFetchDirection();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#setFetchSize(int)
	 */
	@Override
	public void setFetchSize(int rows) throws SQLException {
		stmt.setFetchSize(rows);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getFetchSize()
	 */
	@Override
	public int getFetchSize() throws SQLException {
		return stmt.getFetchSize();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getResultSetConcurrency()
	 */
	@Override
	public int getResultSetConcurrency() throws SQLException {
		return stmt.getResultSetConcurrency();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getResultSetType()
	 */
	@Override
	public int getResultSetType() throws SQLException {
		return stmt.getResultSetType();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#addBatch(java.lang.String)
	 */
	@Override
	public void addBatch(String sql) throws SQLException {
		stmt.addBatch(sql);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#clearBatch()
	 */
	@Override
	public void clearBatch() throws SQLException {
		stmt.clearBatch();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#executeBatch()
	 */
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
				sb.append(prepareSQL);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return stmt.getConnection();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getMoreResults(int)
	 */
	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return stmt.getMoreResults(current);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getGeneratedKeys()
	 */
	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return stmt.getGeneratedKeys();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int)
	 */
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

	/* (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
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

	/* (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
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

	/* (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
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

	/* (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
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

	/* (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
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

	/* (non-Javadoc)
	 * @see java.sql.Statement#getResultSetHoldability()
	 */
	@Override
	public int getResultSetHoldability() throws SQLException {
		return stmt.getResultSetHoldability();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#isClosed()
	 */
	@Override
	public boolean isClosed() throws SQLException {
		return stmt.isClosed();
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#setPoolable(boolean)
	 */
	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		stmt.setPoolable(poolable);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#isPoolable()
	 */
	@Override
	public boolean isPoolable() throws SQLException {
		return stmt.isPoolable();
	}

	/* (non-Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return stmt.unwrap(iface);
	}

	/* (non-Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return stmt.isWrapperFor(iface);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	@Override
	public ResultSet executeQuery() throws SQLException {
		long start = System.currentTimeMillis();
		try {
			ResultSet rs = stmt.executeQuery();
			return new OracleResultSet(rs);
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(prepareSQL);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	@Override
	public int executeUpdate() throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		long start = System.currentTimeMillis();
		try {
			return stmt.executeUpdate();
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(prepareSQL);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int)
	 */
	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		stmt.setNull(parameterIndex, sqlType);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBoolean(int, boolean)
	 */
	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		stmt.setBoolean(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setByte(int, byte)
	 */
	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		stmt.setByte(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setShort(int, short)
	 */
	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		stmt.setShort(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setInt(int, int)
	 */
	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		stmt.setInt(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setLong(int, long)
	 */
	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		stmt.setLong(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setFloat(int, float)
	 */
	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		stmt.setFloat(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDouble(int, double)
	 */
	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		stmt.setDouble(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBigDecimal(int, java.math.BigDecimal)
	 */
	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		stmt.setBigDecimal(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setString(int, java.lang.String)
	 */
	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		stmt.setString(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBytes(int, byte[])
	 */
	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		stmt.setBytes(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date)
	 */
	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		stmt.setDate(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time)
	 */
	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		stmt.setTime(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	@Override
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		stmt.setTimestamp(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, int)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		stmt.setAsciiStream(parameterIndex, x);
	}

	/*
	 * (non-Javadoc)
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 */
	@Deprecated
	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		stmt.setUnicodeStream(parameterIndex, x, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, int)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		stmt.setBinaryStream(parameterIndex, x, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#clearParameters()
	 */
	@Override
	public void clearParameters() throws SQLException {
		stmt.clearParameters();
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int)
	 */
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		stmt.setObject(parameterIndex, x, targetSqlType);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object)
	 */
	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		stmt.setObject(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#execute()
	 */
	@Override
	public boolean execute() throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		long start = System.currentTimeMillis();
		try {
			return stmt.execute();
		} catch (SQLException e) {
			String message = e.getMessage();
			//处理超时的SQL异常
			if (null != message && message.indexOf("ORA-01013") > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(start);
				sb.append(",");
				sb.append(proxyConnection.getName());
				sb.append(",");
				sb.append(prepareSQL);
				sb.append(",");
				sb.append((System.currentTimeMillis() - start));
				sb.append(" ms.");
				
				log.error(sb.toString());
			}
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#addBatch()
	 */
	@Override
	public void addBatch() throws SQLException {
		stmt.addBatch();
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, int)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		stmt.setCharacterStream(parameterIndex, reader, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setRef(int, java.sql.Ref)
	 */
	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		stmt.setRef(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.sql.Blob)
	 */
	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		stmt.setBlob(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.sql.Clob)
	 */
	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		stmt.setClob(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setArray(int, java.sql.Array)
	 */
	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		stmt.setArray(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#getMetaData()
	 */
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return stmt.getMetaData();
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setDate(int, java.sql.Date, java.util.Calendar)
	 */
	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		stmt.setDate(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTime(int, java.sql.Time, java.util.Calendar)
	 */
	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		stmt.setTime(parameterIndex, x, cal);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setTimestamp(int, java.sql.Timestamp, java.util.Calendar)
	 */
	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		stmt.setTimestamp(parameterIndex, x, cal);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNull(int, int, java.lang.String)
	 */
	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName)
			throws SQLException {
		stmt.setNull(parameterIndex, sqlType, typeName);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setURL(int, java.net.URL)
	 */
	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		stmt.setURL(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#getParameterMetaData()
	 */
	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return stmt.getParameterMetaData();
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setRowId(int, java.sql.RowId)
	 */
	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		stmt.setRowId(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNString(int, java.lang.String)
	 */
	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		stmt.setNString(parameterIndex, value);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader, long)
	 */
	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		stmt.setNCharacterStream(parameterIndex, value, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNClob(int, java.sql.NClob)
	 */
	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		stmt.setNClob(parameterIndex, value);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.io.Reader, long)
	 */
	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		stmt.setClob(parameterIndex, reader, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream, long)
	 */
	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		stmt.setBlob(parameterIndex, inputStream, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader, long)
	 */
	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		stmt.setNClob(parameterIndex, reader, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setSQLXML(int, java.sql.SQLXML)
	 */
	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		stmt.setSQLXML(parameterIndex, xmlObject);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setObject(int, java.lang.Object, int, int)
	 */
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException {
		stmt.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream, long)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		stmt.setAsciiStream(parameterIndex, x, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream, long)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		stmt.setBinaryStream(parameterIndex, x, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader, long)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		stmt.setCharacterStream(parameterIndex, reader, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setAsciiStream(int, java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		stmt.setAsciiStream(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBinaryStream(int, java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		stmt.setBinaryStream(parameterIndex, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setCharacterStream(int, java.io.Reader)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		stmt.setCharacterStream(parameterIndex, reader);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNCharacterStream(int, java.io.Reader)
	 */
	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		stmt.setNCharacterStream(parameterIndex, value);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setClob(int, java.io.Reader)
	 */
	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		stmt.setClob(parameterIndex, reader);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setBlob(int, java.io.InputStream)
	 */
	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		stmt.setBlob(parameterIndex, inputStream);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setNClob(int, java.io.Reader)
	 */
	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		stmt.setNClob(parameterIndex, reader);
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
