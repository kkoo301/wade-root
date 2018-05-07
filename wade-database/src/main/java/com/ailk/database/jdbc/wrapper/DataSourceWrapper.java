/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc.wrapper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import com.ailk.database.dbconn.DataSourceFactory;

/**
 * 数据源封装类
 * 
 * @className: DataSourceWrapper.java
 * @author: liaosheng
 * @date: 2014-3-21
 */
public class DataSourceWrapper implements DataSource {
	
	private static final Logger log = Logger.getLogger(DataSourceWrapper.class);
	
	private static final DataSourceFactory factory = DataSourceFactory.getInstance();
	
	private DataSource ds;
	
	/**
	 * 数据源名称
	 */
	private String name;
	
	private String center;
	
	/**
	 * 数据源账户名
	 */
	private String user;
	
	/**
	 * 连接池专用的测试SQL语句
	 */
	private String validSQL;
	
	/**
	 * 语句执行超时时间，依赖不同的驱动对其实现，目前仅Oracle开启该功能
	 */
	private int stmtTimeout;
	
	/**
	 * 行业定制，UCR->UOP，指定通过UCR用户获取表结构以提升获取表结构的性能
	 */
	private String owner;
	
	/**
	 * 数据库方言，默认为Oracle
	 */
	private String dialect = "oracle";
	
	/**
	 * 是否为DBCP连接池
	 */
	private boolean isDBCP = false;
	
	
	private int maxActive = 0;
	private long maxActiveTime = 0;
	/**
	 * 是否只读
	 */
	private boolean readOnly = false;
	
	//数据源,数据中心,类型,用户名,归属名,只读,初始连接,最大活动连接,最大空闲连接,最小空闲连接,当前活动连接,当前空闲连接,最大等待时长,历史最大活动连接,语句执行时长,跟踪状态
	private static final String format = "%s,%s,%s,%s,%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%s";
	
	public DataSourceWrapper(DataSource ds, String user, String owner, String name) {
		this.ds = ds;
		this.user = user;
		this.name = name;
		this.owner = owner;
		if (ds instanceof BasicDataSource) {
			this.isDBCP = true;
			maxActiveTime = System.currentTimeMillis() >> 16;
		}
	}
	
	/**
	 * @return the tableMetaData
	 */
	public TableMetaData getTableMetaData() {
		throw new IllegalAccessError("TableMetaData 必须由子类实现，不能直接使用");
	}
	
	/**
	 * 
	 * @return
	 */
	public DataBaseMetaData getDataBaseMetaData() {
		throw new IllegalAccessError("TableMetaData 必须由子类实现，不能直接使用");
	}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * @param dialect the dialect to set
	 */
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	
	/**
	 * @return the dialect
	 */
	public String getDialect() {
		return dialect;
	}
	
	/**
	 * @param validSQL the validSQL to set
	 */
	public void setValidSQL(String validSQL) {
		this.validSQL = validSQL;
	}
	
	/**
	 * @return the validSQL
	 */
	public String getValidSQL() {
		return validSQL;
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
	 * @return the operUser
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * @param operUser the operUser to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getCenter() {
		return center;
	}
	
	public void setCenter(String center) {
		this.center = center;
	}
	
	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/* (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getLogWriter()
	 */
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return ds.getLogWriter();
	}

	/* (non-Javadoc)
	 * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
	 */
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		ds.setLogWriter(out);
	}

	/* (non-Javadoc)
	 * @see javax.sql.CommonDataSource#setLoginTimeout(int)
	 */
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		ds.setLoginTimeout(seconds);
	}

	/* (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getLoginTimeout()
	 */
	@Override
	public int getLoginTimeout() throws SQLException {
		return ds.getLoginTimeout();
	}

	/* (non-Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return ds.unwrap(iface);
	}

	/* (non-Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return ds.isWrapperFor(iface);
	}

	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		
		try {
			conn = ds.getConnection();
		} catch (Exception e) {
			throw new SQLException(e);
		}
		
		if (isDBCP) {
			long mat = System.currentTimeMillis() >> 16;
			if (mat != maxActiveTime) {
				int numActive = ((BasicDataSource)ds).getNumActive();
				if (numActive > maxActive) {
					maxActive = numActive;
				}
				maxActiveTime = mat;
			}
		}
		if (log.isDebugEnabled())
			log.debug(String.format("创建连接[%s]%s", getName(), null == conn ? "失败" : "成功"));
		
		return conn;
	}

	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		return ds.getConnection(username, password);
	}
	
	
	public void close() {
		try {
			if (ds instanceof BasicDataSource) {
				((BasicDataSource)ds).close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public void reset(Map<String, String> config) throws SQLException {
		if (ds instanceof BasicDataSource) {
			BasicDataSource bds = (BasicDataSource)ds;
			
			String user = config.get("user");
			String passwd = config.get("passwd");
			String driver = config.get("driver");
			String url = config.get("url");
			String initialSize = config.get("initialSize");
			String maxActive = config.get("maxActive");
			String maxIdle = config.get("maxIdle");
			String maxWait = config.get("maxWait");
			String dialect = config.get("dialect");

			if (null == dialect || dialect.length() <= 0)
				dialect = "oracle";
			String validationQuery = factory.getDBDialectTestSQL(dialect);
			
			factory.setDBCPDataSource(bds, user, passwd, driver, url, initialSize, maxActive, maxIdle, maxWait, validationQuery);
		}
	}
	
	/**
	 * 监控日志
	 */
	public String monitor() {
		String traceable = System.getProperty("trace_datasource_" + name, "false");
		if (isDBCP) {
			BasicDataSource bds = (BasicDataSource)ds;
			return String.format(format, 
					name,
					center,
					dialect,
					user,
					owner,
					isReadOnly(),
					bds.getInitialSize(), 
					bds.getMaxActive(), 
					bds.getMaxIdle(), 
					bds.getMinIdle(), 
					bds.getNumActive(), 
					bds.getNumIdle(), 
					bds.getMaxWait(),
					maxActive,
					stmtTimeout,
					traceable);
		}
		
		return String.format(format, 
				name,
				center,
				dialect,
				user,
				owner,
				isReadOnly(),
				0, 
				0, 
				0, 
				0, 
				0, 
				0, 
				0,
				0,
				traceable);
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}
