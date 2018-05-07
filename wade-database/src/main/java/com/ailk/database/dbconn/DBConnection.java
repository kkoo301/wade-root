package com.ailk.database.dbconn;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executor;

import javax.naming.NamingException;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: DBConnection
 * @description: 数据库连接
 * 
 * @version: v1.0.0
 * @author: liaos
 * @date: 2013-7-20
 */
public class DBConnection implements Connection {

	protected static IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();

	protected Connection connection;

	protected String dataSourceName;
	protected String userName;
	protected int stmtTimeout = -10000;
	private UUID traceId = null;
	
	private boolean transaction = false;
	private boolean queryOnly = false;
	private long connCostTime = 0;
	

	/**
	 * create database connection
	 * @param dataSourceName
	 * @param transaction
	 * @param autoCommit
	 * @throws NamingException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public DBConnection(String dataSourceName, boolean transaction, boolean autoCommit) throws SQLException {
		this(dataSourceName, manager.getConnection(dataSourceName), false);

		activeTransaction();
		setAutoCommit(autoCommit);
	}
	
	/**
	 * create database connection
	 * @param dataSourceName
	 * @param connection
	 * @throws SQLException
	 */
	public DBConnection(String dataSourceName, Connection connection) throws SQLException {
		this(dataSourceName, connection, false);
	}
	
	
	private DBConnection(String dataSourceName, Connection connection, boolean trace) throws SQLException {
		if (null == dataSourceName || dataSourceName.length() < 0) {
			throw new SQLException("数据源名称不能为空");
		}
		if (null == connection) {
			throw new SQLException("数据源[" + dataSourceName + "]的连接实例为空");
		}
		this.connection = connection;
		this.dataSourceName = dataSourceName;
		
		this.traceId = UUID.randomUUID();
		if (trace) {
			manager.trace(dataSourceName, traceId);
		}
	}
	
	

	public String getName() {
		return this.dataSourceName;
	}

	public Connection getConnection() {
		return this.connection;
	}
	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		if (null == userName) {
			userName = manager.getUserName(dataSourceName);
		}
		return userName;
	}
	
	/**
	 * @return the traceId
	 */
	public UUID getTraceId() {
		return traceId;
	}
	
	/**
	 * @return the stmtTimeout
	 */
	public int getStmtTimeout() {
		return stmtTimeout;
	}
	
	/**
	 * @param stmtTimeout the stmtTimeout to set
	 */
	public void setStmtTimeout(int stmtTimeout) {
		this.stmtTimeout = stmtTimeout;
	}
	
	/**
	 * 执行了executeUpdate语句后该属性为true
	 * @return the transact
	 */
	public boolean isTransaction() {
		return transaction;
	}
	
	/**
	 * @param transaction the transact to set
	 */
	public void activeTransaction() {
		this.transaction = true;
	}
	
	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(boolean transaction) {
		this.transaction = transaction;
	}
	
	/**
	 * @return the queryOnly
	 */
	public boolean isQueryOnly() {
		return queryOnly;
	}
	
	/**
	 * @param queryOnly the queryOnly to set
	 */
	public void setQueryOnly(boolean queryOnly) {
		this.queryOnly = queryOnly;
	}

	public void commit() throws SQLException {
		connection.commit();
	}

	public void rollback() throws SQLException {
		connection.rollback();
	}

	public void close() throws SQLException {
		try {
			connection.close();
		} finally {
			manager.cleanTrace(dataSourceName, getTraceId());
		}
	}
	
	public void destroy() {
		this.connection = null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.connection.isWrapperFor(iface);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.connection.unwrap(iface);
	}

	public void clearWarnings() throws SQLException {
		this.connection.clearWarnings();
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return this.connection.createArrayOf(typeName, elements);
	}

	public Blob createBlob() throws SQLException {
		return this.connection.createBlob();
	}

	public Clob createClob() throws SQLException {
		return this.connection.createClob();
	}

	public NClob createNClob() throws SQLException {
		return this.connection.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		return this.connection.createSQLXML();
	}

	public Statement createStatement() throws SQLException {
		return this.connection.createStatement();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.connection.createStatement(resultSetType, resultSetConcurrency);
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return this.connection.createStruct(typeName, attributes);
	}

	public boolean getAutoCommit() throws SQLException {
		return this.connection.getAutoCommit();
	}

	public String getCatalog() throws SQLException {
		return this.connection.getCatalog();
	}

	public Properties getClientInfo() throws SQLException {
		return this.connection.getClientInfo();
	}

	public String getClientInfo(String name) throws SQLException {
		return this.connection.getClientInfo(name);
	}

	public int getHoldability() throws SQLException {
		return this.connection.getHoldability();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return this.connection.getMetaData();
	}

	public int getTransactionIsolation() throws SQLException {
		return this.connection.getTransactionIsolation();
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return this.connection.getTypeMap();
	}

	public SQLWarning getWarnings() throws SQLException {
		return this.connection.getWarnings();
	}

	public boolean isClosed() throws SQLException {
		return this.connection.isClosed();
	}

	public boolean isReadOnly() throws SQLException {
		return this.connection.isReadOnly();
	}

	public boolean isValid(int timeout) throws SQLException {
		return this.connection.isValid(timeout);
	}

	public String nativeSQL(String sql) throws SQLException {
		return this.connection.nativeSQL(sql);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		return this.connection.prepareCall(sql);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.connection.prepareStatement(sql);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return this.connection.prepareStatement(sql, autoGeneratedKeys);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return this.connection.prepareStatement(sql, columnIndexes);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return this.connection.prepareStatement(sql, columnNames);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.connection.releaseSavepoint(savepoint);
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		this.connection.rollback(savepoint);
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.connection.setAutoCommit(autoCommit);
	}

	public void setCatalog(String catalog) throws SQLException {
		this.connection.setCatalog(catalog);
	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		this.connection.setClientInfo(properties);
	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		this.connection.setClientInfo(name, value);
	}

	public void setHoldability(int holdability) throws SQLException {
		this.connection.setHoldability(holdability);
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		this.connection.setReadOnly(readOnly);
	}

	public Savepoint setSavepoint() throws SQLException {
		return this.connection.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return this.connection.setSavepoint(name);
	}

	public void setTransactionIsolation(int level) throws SQLException {
		this.connection.setTransactionIsolation(level);
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		this.connection.setTypeMap(map);
	}
	
	/**
	 * @return the connCostTime
	 */
	public long getConnCostTime() {
		return connCostTime;
	}
	
	/**
	 * @param connCostTime the connCostTime to set
	 */
	public void setConnCostTime(long connCostTime) {
		this.connCostTime = connCostTime;
	}
	
	
	@Override
	public int getNetworkTimeout() throws SQLException {
		return this.connection.getNetworkTimeout();
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		this.connection.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return this.connection.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		this.connection.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		this.connection.setNetworkTimeout(executor, milliseconds);
	}
}
