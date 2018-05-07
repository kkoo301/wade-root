package com.ailk.database.dbconn;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.jdbc.altibase.AltibaseDataSourceWrapper;
import com.ailk.database.jdbc.mysql.MySQLDataSourceWrapper;
import com.ailk.database.jdbc.oracle.OracleDataSrouceWrapper;
import com.ailk.database.jdbc.sqlite.SQLiteDataSrouceWrapper;
import com.ailk.database.jdbc.timesten.TimesTenDataSrouceWrapper;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: DataSourceFactory
 * @description: 数据源工厂类
 * 
 * @version: v1.0.0
 * @author: $Id: DataSourceFactory.java 14975 2018-05-04 08:15:38Z liaos $
 * @date: 2013-7-20
 */
public final class DataSourceFactory {

	private static final Logger log = Logger.getLogger(DataSourceFactory.class);
	private static final DataSourceFactory instance = new DataSourceFactory();

	public static final int DB_DIALECT_ORACLE = 1;
	public static final int DB_DIALECT_MYSQL = 2;
	public static final int DB_DIALECT_DB2 = 3;
	public static final int DB_DIALECT_ALTIBASE = 4;
	public static final int DB_DIALECT_SQLSERVER = 5;

	private static final String DB2_DIALECT_TEST_SQL = "SELECT 1 FROM SYSIBM.SYSDUMMY";
	private static final String SQLSERVER_DIALECT_TEST_SQL = "SELECT 1";

	private DataSourceFactory() {
		// 工厂类无需初始化
	}

	/**
	 * 获取数据源工厂实例
	 * 
	 * @return
	 */
	public static final DataSourceFactory getInstance() {
		return instance;
	}

	/**
	 * 获取JDBC数据源
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public final DataSource getJDBCDataSource(String user, String passwd,
			String driver, String url) throws Exception {
		log.info("---------- 创建" + user + "数据库连接池成功!---------- ");
		log.info("数据库驱动: " + driver);
		log.info("数据库URL:" + url);

		return new SimpleJDBCDataSource(user, passwd, driver, url);
	}

	/**
	 * 获取JNDI数据源
	 * 
	 * @param jndi
	 * @return
	 * @throws Exception
	 */
	public final DataSource getJNDIDataSource(String jndi) throws Exception {
		log.info("---------- 创建" + jndi + "数据库连接池成功!---------- ");
		log.info("创建JNDI连接池: " + jndi);

		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup(jndi);
		ctx.close();

		return ds;
	}

	/**
	 * 获取DBCP数据源
	 * 
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public final DataSource getDBCPDataSource(Map<String, String> config)
			throws SQLException {
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
		String validationQuery = getDBDialectTestSQL(dialect);

		return getDBCPDataSource(user, passwd, driver, url, initialSize,
				maxActive, maxIdle, maxWait, validationQuery);
	}
	
	/**
	 * 设置数据源参数
	 * @param datasource
	 * @param user
	 * @param passwd
	 * @param driver
	 * @param url
	 * @param initialSize
	 * @param maxActive
	 * @param maxIdle
	 * @param maxWait
	 * @param validationQuery
	 * @throws SQLException
	 */
	public final void setDBCPDataSource(BasicDataSource datasource, String user, String passwd,
			String driver, String url, String initialSize, String maxActive,
			String maxIdle, String maxWait, String validationQuery)
			throws SQLException {
		
		if (null == initialSize || initialSize.length() <= 0) {
			initialSize = "1";
		}

		if (null == maxActive || maxActive.length() <= 0) {
			maxActive = "1";
		}

		if (null == maxIdle || maxIdle.length() <= 0) {
			maxIdle = "5";
		}

		if (null == maxWait || maxWait.length() <= 0) {
			maxWait = "10000";
		}

		datasource.setDriverClassName(driver);
		datasource.setUsername(user);
		datasource.setPassword(passwd);
		datasource.setUrl(url);
		datasource.setInitialSize(Integer.parseInt(initialSize));
		datasource.setMaxActive(Integer.parseInt(maxActive));
		datasource.setMaxIdle(Integer.parseInt(maxIdle));
		datasource.setMaxWait(Integer.parseInt(maxWait));
		datasource.setMinIdle(Integer.parseInt(initialSize));

		/*
		 * GenericObjectPool中针对pool管理，起了一个Evict的TimerTask定时线程进行控制(
		 * 可通过设置参数timeBetweenEvictionRunsMillis
		 * >0),定时对线程池中的链接进行validateObject校验，对无效的链接进行关闭后
		 * ，会调用ensureMinIdle，适当建立链接保证最小的minIdle连接数
		 */
		datasource.setTestWhileIdle(true);
		/*
		 * 设置的Evict线程的时间，单位ms，大于0才会开启evict检查线程
		 */
		datasource.setTimeBetweenEvictionRunsMillis(30000);
		
		/*
		 * 就是在进行borrowObject进行处理时，对拿到的connection进行validateObject校验
		 */
		datasource.setTestOnBorrow(DatabaseCfg.isTestOnBorrow());

		/*
		 * 就是在进行returnObject对返回的connection进行validateObject校验
		 */
		datasource.setTestOnReturn(false);
		
		datasource.setValidationQuery(validationQuery);
		datasource.setValidationQueryTimeout(1000);
		
		/*
		 * 每次检查链接的数量，建议设置和maxIdle一样大，这样每次可以有效检查所有的链接
		 */
		datasource.setNumTestsPerEvictionRun(Integer.parseInt(maxIdle));
		
		/*
		 * 超过removeAbandonedTimeout时间后，是否进行没用连接（废弃）的回收（默认为false，调整为true)
		 */
		datasource.setRemoveAbandoned(true);
		
		/*
		 * 超过时间限制，回收没有用(废弃)的连接（默认为 300秒，调整为10
		 */
		datasource.setRemoveAbandonedTimeout(10);
		
		/*
		 * 连接池中连接，在时间段内一直空闲， 被逐出连接池的时间 （默认为30分钟，可以适当做调整，需要和后
		 * 端服务端的策略配置相关）
		 */
		datasource.setMinEvictableIdleTimeMillis(1800000);
	}

	/**
	 * 获取DBCP数据源
	 * 
	 * @param config
	 * @return
	 * @throws SQLException
	 */
	public final BasicDataSource getDBCPDataSource(String user, String passwd,
			String driver, String url, String initialSize, String maxActive,
			String maxIdle, String maxWait, String validationQuery)
			throws SQLException {
		long start = System.currentTimeMillis();

		BasicDataSource datasource = new BasicDataSource();

		setDBCPDataSource(datasource, user, passwd, driver, url, initialSize, maxActive, maxIdle, maxWait, validationQuery);

		String wadeServerName = System.getProperty("wade.server.name", "wade");
		datasource.setConnectionProperties("v$session.program=" + wadeServerName);
		
		// 初始化
		Connection conn = null;
		try {
			conn = datasource.getConnection();
		} catch (Exception e) {
			throw new SQLException("数据源初始化失败" + user, e);
		} finally {
			if (null != conn) {
				conn.close();
			}
		}

		log.info("");
		log.info("---------- 创建" + user + "连接池成功!---------- ");
		log.info("数据库驱动:" + driver);
		log.info("数据库URL:" + url);
		log.info("探测SQL:" + validationQuery);
		log.info("初始连接数:" + initialSize);
		log.info("最大连接数:" + maxActive);
		log.info("最大闲置数:" + maxIdle);
		log.info("最大等待时间:" + maxWait + "毫秒");
		log.info("创建" + user + "数据库连接耗时" + (System.currentTimeMillis() - start) + "毫秒");

		return datasource;
	}

	/**
	 * 数据库方言的TestSQL
	 * 
	 * @param dialect
	 * @return
	 */
	public final String getDBDialectTestSQL(String dialect) {
		String dburl = dialect.toLowerCase();
		if (dburl.indexOf("oracle") != -1) {
			return OracleDataSrouceWrapper.VALIDSQL;
		}

		if (dburl.indexOf("mysql") != -1) {
			return MySQLDataSourceWrapper.VALIDSQL;
		}

		if (dburl.indexOf("db2") != -1) {
			return DB2_DIALECT_TEST_SQL;
		}

		if (dburl.indexOf("sqlserver") != -1) {
			return SQLSERVER_DIALECT_TEST_SQL;
		}

		if (dburl.indexOf("altibase") != -1) {
			return AltibaseDataSourceWrapper.VALIDSQL;
		}

		if (dburl.indexOf("timesten") != -1) {
			return TimesTenDataSrouceWrapper.VALIDSQL;
		}
		
		if (dburl.indexOf("sqlite") != -1) {
			return SQLiteDataSrouceWrapper.VALIDSQL;
		}

		throw new NullPointerException("不支持的数据库方言，无法获取探测SQL[" + dialect + "]");
	}

	class SimpleJDBCDataSource implements DataSource {

		private String user = null;
		private String passwd = null;
		private String driver = null;
		private String url = null;

		/**
		 * JDBCDataSource
		 * 
		 * @param user
		 * @param passwd
		 * @param driver
		 * @param url
		 */
		public SimpleJDBCDataSource(String user, String passwd, String driver,
				String url) {
			this.user = user;
			this.passwd = passwd;
			this.driver = driver;
			this.url = url;

			try {
				Class.forName(this.driver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		public Connection getConnection() throws SQLException {
			return DriverManager.getConnection(url, user, passwd);
		}

		@Override
		public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return null;
		}
		
		
		public Connection getConnection(String username, String password)
				throws SQLException {
			return DriverManager.getConnection(url, username, password);
		}

		public int getLoginTimeout() throws SQLException {
			return 0;
		}

		public PrintWriter getLogWriter() throws SQLException {
			return null;
		}

		public void setLoginTimeout(int arg0) throws SQLException {
		}

		public boolean isWrapperFor(Class<?> arg0) throws SQLException {
			return false;
		}

		public void setLogWriter(PrintWriter arg0) throws SQLException {

		}

		public <T> T unwrap(Class<T> iface) throws SQLException {
			return null;
		}
	}
}