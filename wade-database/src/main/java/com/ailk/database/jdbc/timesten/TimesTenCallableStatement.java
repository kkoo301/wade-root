/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.timesten;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
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
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.database.jdbc.wrapper.ConnectionWrapper;

/**
 * TimesTenCallableStatement 对象
 * 
 * @className: TimesTenCallableStatement.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class TimesTenCallableStatement implements CallableStatement {
	private static final Logger log = Logger.getLogger(TimesTenCallableStatement.class);
	
	private CallableStatement stmt;
	
	private ConnectionWrapper proxyConnection = null;
	
	public TimesTenCallableStatement(ConnectionWrapper proxyConnection, CallableStatement stmt) throws SQLException {
		if (log.isDebugEnabled()) {
			log.debug("创建TimesTenCallableStatement,queryTimeout=10.");
		}
		
		this.proxyConnection = proxyConnection;
		this.stmt = stmt;
		this.stmt.setQueryTimeout(10);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeQuery()
	 */
	@Override
	public ResultSet executeQuery() throws SQLException {
		ResultSet rs = stmt.executeQuery();
		return new TimesTenResultSet(rs);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#executeUpdate()
	 */
	@Override
	public int executeUpdate() throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		return stmt.executeUpdate();
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
		stmt.setAsciiStream(parameterIndex, x, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.PreparedStatement#setUnicodeStream(int, java.io.InputStream, int)
	 */
	@Override
	@Deprecated
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
		
		return stmt.execute();
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
		stmt.setDate(parameterIndex, x, cal);
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
		stmt.setObject(parameterIndex, x, targetSqlType);
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

	/* (non-Javadoc)
	 * @see java.sql.Statement#executeQuery(java.lang.String)
	 */
	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		ResultSet rs = stmt.executeQuery(sql);
		return new TimesTenResultSet(rs);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String)
	 */
	@Override
	public int executeUpdate(String sql) throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		return stmt.executeUpdate(sql);
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
		
		return stmt.execute(sql);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#getResultSet()
	 */
	@Override
	public ResultSet getResultSet() throws SQLException {
		return stmt.getResultSet();
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
		
		return stmt.executeBatch();
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
		
		return stmt.executeUpdate(sql, autoGeneratedKeys);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
	 */
	@Override
	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		return stmt.executeUpdate(sql, columnIndexes);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
	 */
	@Override
	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		return stmt.executeUpdate(sql, columnNames);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, int)
	 */
	@Override
	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		return stmt.execute(sql, autoGeneratedKeys);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, int[])
	 */
	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		return stmt.execute(sql, columnIndexes);
	}

	/* (non-Javadoc)
	 * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		//启动事务标识
		this.proxyConnection.activeTransaction();
		
		return stmt.execute(sql, columnNames);
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
	 * @see java.sql.CallableStatement#registerOutParameter(int, int)
	 */
	@Override
	public void registerOutParameter(int parameterIndex, int sqlType)
			throws SQLException {
		stmt.registerOutParameter(parameterIndex, sqlType);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, int)
	 */
	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale)
			throws SQLException {
		stmt.registerOutParameter(parameterIndex, sqlType, scale);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#wasNull()
	 */
	@Override
	public boolean wasNull() throws SQLException {
		return stmt.wasNull();
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getString(int)
	 */
	@Override
	public String getString(int parameterIndex) throws SQLException {
		return stmt.getString(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getBoolean(int)
	 */
	@Override
	public boolean getBoolean(int parameterIndex) throws SQLException {
		return stmt.getBoolean(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getByte(int)
	 */
	@Override
	public byte getByte(int parameterIndex) throws SQLException {
		return stmt.getByte(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getShort(int)
	 */
	@Override
	public short getShort(int parameterIndex) throws SQLException {
		return stmt.getShort(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getInt(int)
	 */
	@Override
	public int getInt(int parameterIndex) throws SQLException {
		return stmt.getInt(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getLong(int)
	 */
	@Override
	public long getLong(int parameterIndex) throws SQLException {
		return stmt.getLong(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getFloat(int)
	 */
	@Override
	public float getFloat(int parameterIndex) throws SQLException {
		return stmt.getFloat(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getDouble(int)
	 */
	@Override
	public double getDouble(int parameterIndex) throws SQLException {
		return stmt.getDouble(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(int, int)
	 */
	@Override
	@Deprecated
	public BigDecimal getBigDecimal(int parameterIndex, int scale)
			throws SQLException {
		return stmt.getBigDecimal(parameterIndex, scale);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getBytes(int)
	 */
	@Override
	public byte[] getBytes(int parameterIndex) throws SQLException {
		return stmt.getBytes(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getDate(int)
	 */
	@Override
	public Date getDate(int parameterIndex) throws SQLException {
		return stmt.getDate(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getTime(int)
	 */
	@Override
	public Time getTime(int parameterIndex) throws SQLException {
		return stmt.getTime(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(int)
	 */
	@Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return stmt.getTimestamp(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getObject(int)
	 */
	@Override
	public Object getObject(int parameterIndex) throws SQLException {
		return stmt.getObject(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(int)
	 */
	@Override
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return stmt.getBigDecimal(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getObject(int, java.util.Map)
	 */
	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map)
			throws SQLException {
		return stmt.getObject(parameterIndex, map);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getRef(int)
	 */
	@Override
	public Ref getRef(int parameterIndex) throws SQLException {
		return stmt.getRef(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getBlob(int)
	 */
	@Override
	public Blob getBlob(int parameterIndex) throws SQLException {
		return stmt.getBlob(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getClob(int)
	 */
	@Override
	public Clob getClob(int parameterIndex) throws SQLException {
		return stmt.getClob(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getArray(int)
	 */
	@Override
	public Array getArray(int parameterIndex) throws SQLException {
		return stmt.getArray(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getDate(int, java.util.Calendar)
	 */
	@Override
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return stmt.getDate(parameterIndex, cal);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getTime(int, java.util.Calendar)
	 */
	@Override
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return stmt.getTime(parameterIndex, cal);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(int, java.util.Calendar)
	 */
	@Override
	public Timestamp getTimestamp(int parameterIndex, Calendar cal)
			throws SQLException {
		return stmt.getTimestamp(parameterIndex, cal);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(int, int, java.lang.String)
	 */
	@Override
	public void registerOutParameter(int parameterIndex, int sqlType,
			String typeName) throws SQLException {
		stmt.registerOutParameter(parameterIndex, sqlType, typeName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int)
	 */
	@Override
	public void registerOutParameter(String parameterName, int sqlType)
			throws SQLException {
		stmt.registerOutParameter(parameterName, sqlType);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, int)
	 */
	@Override
	public void registerOutParameter(String parameterName, int sqlType,
			int scale) throws SQLException {
		stmt.registerOutParameter(parameterName, sqlType, scale);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#registerOutParameter(java.lang.String, int, java.lang.String)
	 */
	@Override
	public void registerOutParameter(String parameterName, int sqlType,
			String typeName) throws SQLException {
		stmt.registerOutParameter(parameterName, sqlType, typeName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getURL(int)
	 */
	@Override
	public URL getURL(int parameterIndex) throws SQLException {
		return stmt.getURL(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setURL(java.lang.String, java.net.URL)
	 */
	@Override
	public void setURL(String parameterName, URL val) throws SQLException {
		stmt.setURL(parameterName, val);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int)
	 */
	@Override
	public void setNull(String parameterName, int sqlType) throws SQLException {
		stmt.setNull(parameterName, sqlType);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setBoolean(java.lang.String, boolean)
	 */
	@Override
	public void setBoolean(String parameterName, boolean x) throws SQLException {
		stmt.setBoolean(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setByte(java.lang.String, byte)
	 */
	@Override
	public void setByte(String parameterName, byte x) throws SQLException {
		stmt.setByte(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setShort(java.lang.String, short)
	 */
	@Override
	public void setShort(String parameterName, short x) throws SQLException {
		stmt.setShort(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setInt(java.lang.String, int)
	 */
	@Override
	public void setInt(String parameterName, int x) throws SQLException {
		stmt.setInt(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setLong(java.lang.String, long)
	 */
	@Override
	public void setLong(String parameterName, long x) throws SQLException {
		stmt.setLong(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setFloat(java.lang.String, float)
	 */
	@Override
	public void setFloat(String parameterName, float x) throws SQLException {
		stmt.setFloat(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setDouble(java.lang.String, double)
	 */
	@Override
	public void setDouble(String parameterName, double x) throws SQLException {
		stmt.setDouble(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	@Override
	public void setBigDecimal(String parameterName, BigDecimal x)
			throws SQLException {
		stmt.setBigDecimal(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setString(java.lang.String, java.lang.String)
	 */
	@Override
	public void setString(String parameterName, String x) throws SQLException {
		stmt.setString(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setBytes(java.lang.String, byte[])
	 */
	@Override
	public void setBytes(String parameterName, byte[] x) throws SQLException {
		stmt.setBytes(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date)
	 */
	@Override
	public void setDate(String parameterName, Date x) throws SQLException {
		stmt.setDate(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time)
	 */
	@Override
	public void setTime(String parameterName, Time x) throws SQLException {
		stmt.setTime(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	@Override
	public void setTimestamp(String parameterName, Timestamp x)
			throws SQLException {
		stmt.setTimestamp(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	@Override
	public void setAsciiStream(String parameterName, InputStream x, int length)
			throws SQLException {
		stmt.setAsciiStream(parameterName, x, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	@Override
	public void setBinaryStream(String parameterName, InputStream x, int length)
			throws SQLException {
		stmt.setBinaryStream(parameterName, x, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int, int)
	 */
	@Override
	public void setObject(String parameterName, Object x, int targetSqlType,
			int scale) throws SQLException {
		stmt.setObject(parameterName, x, targetSqlType);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void setObject(String parameterName, Object x, int targetSqlType)
			throws SQLException {
		stmt.setObject(parameterName, x, targetSqlType);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setObject(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setObject(String parameterName, Object x) throws SQLException {
		stmt.setObject(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	@Override
	public void setCharacterStream(String parameterName, Reader reader,
			int length) throws SQLException {
		stmt.setCharacterStream(parameterName, reader, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setDate(java.lang.String, java.sql.Date, java.util.Calendar)
	 */
	@Override
	public void setDate(String parameterName, Date x, Calendar cal)
			throws SQLException {
		stmt.setDate(parameterName, x, cal);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setTime(java.lang.String, java.sql.Time, java.util.Calendar)
	 */
	@Override
	public void setTime(String parameterName, Time x, Calendar cal)
			throws SQLException {
		stmt.setTime(parameterName, x, cal);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setTimestamp(java.lang.String, java.sql.Timestamp, java.util.Calendar)
	 */
	@Override
	public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
			throws SQLException {
		stmt.setTimestamp(parameterName, x, cal);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setNull(java.lang.String, int, java.lang.String)
	 */
	@Override
	public void setNull(String parameterName, int sqlType, String typeName)
			throws SQLException {
		stmt.setNull(parameterName, sqlType, typeName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getString(java.lang.String)
	 */
	@Override
	public String getString(String parameterName) throws SQLException {
		return stmt.getString(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getBoolean(java.lang.String)
	 */
	@Override
	public boolean getBoolean(String parameterName) throws SQLException {
		return stmt.getBoolean(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getByte(java.lang.String)
	 */
	@Override
	public byte getByte(String parameterName) throws SQLException {
		return stmt.getByte(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getShort(java.lang.String)
	 */
	@Override
	public short getShort(String parameterName) throws SQLException {
		return stmt.getShort(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getInt(java.lang.String)
	 */
	@Override
	public int getInt(String parameterName) throws SQLException {
		return stmt.getInt(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getLong(java.lang.String)
	 */
	@Override
	public long getLong(String parameterName) throws SQLException {
		return stmt.getLong(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getFloat(java.lang.String)
	 */
	@Override
	public float getFloat(String parameterName) throws SQLException {
		return stmt.getFloat(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getDouble(java.lang.String)
	 */
	@Override
	public double getDouble(String parameterName) throws SQLException {
		return stmt.getDouble(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getBytes(java.lang.String)
	 */
	@Override
	public byte[] getBytes(String parameterName) throws SQLException {
		return stmt.getBytes(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getDate(java.lang.String)
	 */
	@Override
	public Date getDate(String parameterName) throws SQLException {
		return stmt.getDate(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getTime(java.lang.String)
	 */
	@Override
	public Time getTime(String parameterName) throws SQLException {
		return stmt.getTime(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String)
	 */
	@Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return stmt.getTimestamp(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getObject(java.lang.String)
	 */
	@Override
	public Object getObject(String parameterName) throws SQLException {
		return stmt.getObject(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getBigDecimal(java.lang.String)
	 */
	@Override
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return stmt.getBigDecimal(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getObject(java.lang.String, java.util.Map)
	 */
	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map)
			throws SQLException {
		return stmt.getObject(parameterName, map);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getRef(java.lang.String)
	 */
	@Override
	public Ref getRef(String parameterName) throws SQLException {
		return stmt.getRef(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getBlob(java.lang.String)
	 */
	@Override
	public Blob getBlob(String parameterName) throws SQLException {
		return stmt.getBlob(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getClob(java.lang.String)
	 */
	@Override
	public Clob getClob(String parameterName) throws SQLException {
		return stmt.getClob(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getArray(java.lang.String)
	 */
	@Override
	public Array getArray(String parameterName) throws SQLException {
		return stmt.getArray(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getDate(java.lang.String, java.util.Calendar)
	 */
	@Override
	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return stmt.getDate(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getTime(java.lang.String, java.util.Calendar)
	 */
	@Override
	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return stmt.getTime(parameterName, cal);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	@Override
	public Timestamp getTimestamp(String parameterName, Calendar cal)
			throws SQLException {
		return stmt.getTimestamp(parameterName, cal);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getURL(java.lang.String)
	 */
	@Override
	public URL getURL(String parameterName) throws SQLException {
		return stmt.getURL(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getRowId(int)
	 */
	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {
		return stmt.getRowId(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getRowId(java.lang.String)
	 */
	@Override
	public RowId getRowId(String parameterName) throws SQLException {
		return stmt.getRowId(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setRowId(java.lang.String, java.sql.RowId)
	 */
	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
		stmt.setRowId(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setNString(java.lang.String, java.lang.String)
	 */
	@Override
	public void setNString(String parameterName, String value)
			throws SQLException {
		stmt.setNString(parameterName, value);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setNCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	@Override
	public void setNCharacterStream(String parameterName, Reader value,
			long length) throws SQLException {
		stmt.setNCharacterStream(parameterName, value, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setNClob(java.lang.String, java.sql.NClob)
	 */
	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		stmt.setNClob(parameterName, value);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setClob(java.lang.String, java.io.Reader, long)
	 */
	@Override
	public void setClob(String parameterName, Reader reader, long length)
			throws SQLException {
		stmt.setClob(parameterName, reader, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setBlob(java.lang.String, java.io.InputStream, long)
	 */
	@Override
	public void setBlob(String parameterName, InputStream inputStream,
			long length) throws SQLException {
		stmt.setBlob(parameterName, inputStream, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setNClob(java.lang.String, java.io.Reader, long)
	 */
	@Override
	public void setNClob(String parameterName, Reader reader, long length)
			throws SQLException {
		stmt.setNClob(parameterName, reader, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getNClob(int)
	 */
	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {
		return stmt.getNClob(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getNClob(java.lang.String)
	 */
	@Override
	public NClob getNClob(String parameterName) throws SQLException {
		return stmt.getNClob(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setSQLXML(java.lang.String, java.sql.SQLXML)
	 */
	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject)
			throws SQLException {
		stmt.setSQLXML(parameterName, xmlObject);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getSQLXML(int)
	 */
	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return stmt.getSQLXML(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getSQLXML(java.lang.String)
	 */
	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return stmt.getSQLXML(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getNString(int)
	 */
	@Override
	public String getNString(int parameterIndex) throws SQLException {
		return stmt.getNString(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getNString(java.lang.String)
	 */
	@Override
	public String getNString(String parameterName) throws SQLException {
		return stmt.getNString(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getNCharacterStream(int)
	 */
	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return stmt.getNCharacterStream(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getNCharacterStream(java.lang.String)
	 */
	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {
		return stmt.getNCharacterStream(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getCharacterStream(int)
	 */
	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return stmt.getCharacterStream(parameterIndex);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#getCharacterStream(java.lang.String)
	 */
	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		return stmt.getCharacterStream(parameterName);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setBlob(java.lang.String, java.sql.Blob)
	 */
	@Override
	public void setBlob(String parameterName, Blob x) throws SQLException {
		stmt.setBlob(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setClob(java.lang.String, java.sql.Clob)
	 */
	@Override
	public void setClob(String parameterName, Clob x) throws SQLException {
		stmt.setClob(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream, long)
	 */
	@Override
	public void setAsciiStream(String parameterName, InputStream x, long length)
			throws SQLException {
		stmt.setAsciiStream(parameterName, x, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream, long)
	 */
	@Override
	public void setBinaryStream(String parameterName, InputStream x, long length)
			throws SQLException {
		stmt.setBinaryStream(parameterName, x, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	@Override
	public void setCharacterStream(String parameterName, Reader reader,
			long length) throws SQLException {
		stmt.setCharacterStream(parameterName, reader, length);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setAsciiStream(java.lang.String, java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(String parameterName, InputStream x)
			throws SQLException {
		stmt.setAsciiStream(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setBinaryStream(java.lang.String, java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(String parameterName, InputStream x)
			throws SQLException {
		stmt.setBinaryStream(parameterName, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setCharacterStream(java.lang.String, java.io.Reader)
	 */
	@Override
	public void setCharacterStream(String parameterName, Reader reader)
			throws SQLException {
		stmt.setCharacterStream(parameterName, reader);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setNCharacterStream(java.lang.String, java.io.Reader)
	 */
	@Override
	public void setNCharacterStream(String parameterName, Reader value)
			throws SQLException {
		stmt.setNCharacterStream(parameterName, value);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setClob(java.lang.String, java.io.Reader)
	 */
	@Override
	public void setClob(String parameterName, Reader reader)
			throws SQLException {
		stmt.setClob(parameterName, reader);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setBlob(java.lang.String, java.io.InputStream)
	 */
	@Override
	public void setBlob(String parameterName, InputStream inputStream)
			throws SQLException {
		stmt.setBlob(parameterName, inputStream);
	}

	/* (non-Javadoc)
	 * @see java.sql.CallableStatement#setNClob(java.lang.String, java.io.Reader)
	 */
	@Override
	public void setNClob(String parameterName, Reader reader)
			throws SQLException {
		stmt.setNClob(parameterName, reader);
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		stmt.closeOnCompletion();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return stmt.isCloseOnCompletion();
	}

	@Override
	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return stmt.getObject(parameterIndex, type);
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return stmt.getObject(parameterName, type);
	}

}
