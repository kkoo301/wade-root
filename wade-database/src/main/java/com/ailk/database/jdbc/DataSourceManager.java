/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.jdbc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dbconn.DataSourceFactory;
import com.ailk.database.dbconn.IConnectionManager;
import com.ailk.database.jdbc.altibase.AltibaseDataSourceWrapper;
import com.ailk.database.jdbc.mysql.MySQLDataSourceWrapper;
import com.ailk.database.jdbc.oracle.OracleDataSrouceWrapper;
import com.ailk.database.jdbc.sqlite.SQLiteDataSrouceWrapper;
import com.ailk.database.jdbc.timesten.TimesTenDataSrouceWrapper;
import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.util.TripleDES;

/**
 * 管理database.xml里的所有数据源
 * 
 * @className: DataSourceManager.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class DataSourceManager implements IConnectionManager {
	
	private static final Logger log = Logger.getLogger(DataSourceManager.class);
	
	/**
	 * database.xml里所有的数据源实例
	 */
	private Map<String, DataSourceWrapper> wrappers = new HashMap<String, DataSourceWrapper>(100);
	
	/**
	 * 统一的数据源创建工厂
	 */
	private static DataSourceFactory factory = DataSourceFactory.getInstance();
	
	private Map<String, Map<UUID, TraceInfo>> trace = new ConcurrentHashMap<String, Map<UUID,TraceInfo>>(10000);
	
	public DataSourceManager() {
		loadDataSource();
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#initConnectionManager()
	 */
	@Override
	public void initConnectionManager() {
		
	}
	
	
	/**
	 * 根据database.xml@type配置的类型创建数据源实例，并以结点名为key添加到 wrappers
	 */
	private void loadDataSource() {
		Map<String, Map<String, String>> configs = DatabaseCfg.getAllDBConfig();
		if (null == configs ) {
			throw new NullPointerException("database.xml未初始化或初始化失败");
		}
		
		log.info("开始加载database.xml配置的数据源");
		
		Iterator<String> it = configs.keySet().iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			
			Map<String, String> config = configs.get(name);	
			load(name, config);
		}
	}
	
	public void load(String dataSourceName, Map<String, String> config) {
		String type = config.get("type");
		if (null == type) {
			type = "jdbc";
		}
		
		String passwd = config.get("passwd");

		/**
		 * 判断是否采用了3DES加密
		 */
		if (passwd.startsWith("{3DES}")) {
			passwd = TripleDES.decrypt(passwd.substring(6));
			config.put("passwd", passwd);
		}
		
		try {
			synchronized(wrappers) {
				DataSourceWrapper datasource = null;
				if ("dbcp".equalsIgnoreCase(type)) {
					datasource = createDBCPWrapper(dataSourceName, config);
				} else if ("jndi".equalsIgnoreCase(type)) {
					datasource = createJNDIWrapper(dataSourceName, config);
				} else if ("jdbc".equalsIgnoreCase(type)) {
					datasource = createJDBCWrapper(dataSourceName, config);
				}
				
				String stmtTimeout = config.get("stmtTimeout");
				
				if (null == stmtTimeout || stmtTimeout.length() <= 0)
					stmtTimeout = "0";
				datasource.setStmtTimeout(Integer.parseInt(stmtTimeout));
				
				String readonly = config.get("readonly");
				boolean read = false;
				if (null != readonly && "true".equals(readonly)) {
					read = true;
				}
				datasource.setReadOnly(read);
				
				String center = config.get("center");
				datasource.setCenter(center);
				
				wrappers.put(dataSourceName, datasource);
				
				if (DatabaseCfg.isTrace()) {
					System.setProperty("trace_datasource_" + dataSourceName, "true");
				}
			}
		} catch (Exception e) {
			log.error("加载数据源错误,name=" + dataSourceName + ",type=" + type, e);
			throw new IllegalArgumentException("加载数据源错误,name=" + dataSourceName + ",type=" + type, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#getDataSource(java.lang.String)
	 */
	@Override
	public DataSourceWrapper getDataSource(String dataSourceName) {
		if (null == dataSourceName || dataSourceName.length() <= 0) {
			throw new NullPointerException("数据源名称不能不空");
		}
		
		DataSourceWrapper ds = wrappers.get(dataSourceName);
		if (null == ds) {
			throw new NullPointerException(String.format("未定义的数据源名称%s", dataSourceName));
		}
		return ds;
	}
	
	/**
	 * 根据数据库方言创建对应的数据源实例
	 * @param ds
	 * @param dialect
	 * @return
	 */
	private DataSourceWrapper createDataSourceByDialect(DataSource ds, String dialect, String user, String operUser, String name) {
		
		if ("oracle".equalsIgnoreCase(dialect)) {
			return new OracleDataSrouceWrapper(ds, user, operUser, name);
		} else if ("mysql".equalsIgnoreCase(dialect)) {
			return new MySQLDataSourceWrapper(ds, user, operUser, name);
		} else if ("altibase".equalsIgnoreCase(dialect)) {
			return new AltibaseDataSourceWrapper(ds, user, operUser, name);
		} else if ("db2".equalsIgnoreCase(dialect)) {
			return null;
		} else if ("timesten".equalsIgnoreCase(dialect)) {
			return new TimesTenDataSrouceWrapper(ds, user, operUser, name);
		} else if ("sqlite".equalsIgnoreCase(dialect)) {
			return new SQLiteDataSrouceWrapper(ds, user, operUser, name);
		}
		
		return new OracleDataSrouceWrapper(ds, user, operUser, name);
	}
	
	
	/**
	 * 根据配置的封装类创建数据源
	 * @param ds
	 * @param wrapperClass
	 * @param user
	 * @param operUser
	 * @param name
	 * @return
	 */
	private DataSourceWrapper createDataSourceByWrapperClass(DataSource ds, String wrapperClass, String user, String operUser, String name) {
		DataSourceWrapper wrapper = null;
		try {
			Class<?> clazz = Class.forName(wrapperClass);
			Constructor<?> constructor = clazz.getConstructor(DataSource.class, String.class, String.class, String.class);
			wrapper = (DataSourceWrapper) constructor.newInstance(ds, user, operUser, name);
		} catch (Exception e) {
			log.error("加载自定义数据源失败", e);
		} finally {
			if (log.isInfoEnabled()) {
				log.info("创建自定义数据库方言" + wrapper);
			}
		}
		return wrapper;
	}
	
	
	/**
	 * 创建DBCP数据源
	 * @param config
	 * @return
	 * @throws SQLException
	 */
	private DataSourceWrapper createDBCPWrapper(String name, Map<String, String> config) throws SQLException {
		String dialect = config.get("dialect");
		String user = config.get("user");
		String owner = config.get("owner");
		String wrapper = config.get("wrapper");
		if (null != wrapper) {
			return createDataSourceByWrapperClass(factory.getDBCPDataSource(config), wrapper, user, owner, name);
		} else {
			return createDataSourceByDialect(factory.getDBCPDataSource(config), dialect, user, owner, name);
		}
	}
	
	
	
	
	
	
	
	/**
	 * 创建JNDI数据源
	 * @param config
	 * @return
	 * @throws Exception
	 */
	private DataSourceWrapper createJNDIWrapper(String name, Map<String, String> config) throws Exception {
		String jndi = config.get("jndi");
		if (null == jndi || jndi.length() <= 0) {
			throw new NullPointerException("未配置jndi数据name=" + name + ",jndi=" + jndi);
		}
		
		return createDataSourceByDialect(factory.getJNDIDataSource(jndi), config.get("dialect"), config.get("user"), config.get("owner"), name);
	}
	
	/**
	 * 创建JDBC数据源
	 * @param config
	 * @return
	 * @throws Exception
	 */
	private DataSourceWrapper createJDBCWrapper(String name, Map<String, String> config) throws Exception {
		String user = config.get("user");
		if (null == user || user.length() <= 0) {
			throw new NullPointerException("未配置jndi数据name=" + name + ",user=" + user);
		}
		
		String passwd = config.get("passwd");
		if (null == passwd || passwd.length() <= 0) {
			throw new NullPointerException("未配置jndi数据name=" + name + ",passwd=" + passwd);
		}
		
		String driver = config.get("driver");
		if (null == driver || driver.length() <= 0) {
			throw new NullPointerException("未配置jndi数据name=" + name + ",driver=" + driver);
		}
		
		String url = config.get("url");
		if (null == url || url.length() <= 0) {
			throw new NullPointerException("未配置jndi数据name=" + name + ",url=" + url);
		}
		
		return createDataSourceByDialect(factory.getJDBCDataSource(user, passwd, driver, url), config.get("dialect"), user, config.get("owner"), name);
	}

	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#getConnection(java.lang.String)
	 */
	@Override
	public Connection getConnection(String dataSourceName) throws SQLException {
		if (null == dataSourceName || dataSourceName.length() <= 0) {
			throw new SQLException("数据源名称为空");
		}
		
		DataSourceWrapper ds = wrappers.get(dataSourceName);
		if (null == ds) {
			throw new SQLException(String.format("找不到数据源名称[%s]", dataSourceName));
		}
		
		boolean success = true;
		String serviceName = System.getProperty("wade.server.name");
		long start = System.currentTimeMillis();
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			success = false;
			
			StringBuilder err = new StringBuilder();
			err.append("get connection error ds=");
			err.append(dataSourceName);
			err.append(", app=");
			err.append(serviceName);
			err.append(", costtime=");
			err.append((System.currentTimeMillis() - start));
			err.append(".");
			err.append(traceInfo(dataSourceName));
			
			log.error(err.toString(), e);
			
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("获取连接:" + dataSourceName + ", 耗时:" + (System.currentTimeMillis() - start) + "毫秒, " + (success ? "成功" : "失败"));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#trace(java.lang.String, com.ailk.database.dbconn.DBConnection)
	 */
	@Override
	public void trace(String dataSourceName, UUID traceId) {
		String traceable = System.getProperty("trace_datasource_" + dataSourceName, "false");
		
		if ("true".equals(traceable)) {
			Throwable t = new Throwable();
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			String stack = sw.toString();
			
			Map<UUID, TraceInfo> info = trace.get(dataSourceName);
			if (null == info) {
				info = new HashMap<UUID, TraceInfo>();
			}
			
			TraceInfo ti = new TraceInfo();
			ti.setUuid(traceId);
			ti.setStack(stack);
			ti.setTraceTime(System.currentTimeMillis());
			
			info.put(traceId, ti);
			trace.put(dataSourceName, info);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#cleanTrace(java.lang.String, com.ailk.database.dbconn.DBConnection)
	 */
	@Override
	public void cleanTrace(String dataSourceName, UUID traceId) {
		Map<UUID, TraceInfo> info = trace.get(dataSourceName);
		if (null != info)
			info.remove(traceId);
	}

	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#getUserName(java.lang.String)
	 */
	@Override
	public String getUserName(String dataSourceName) {
		return wrappers.get(dataSourceName).getUser();
	}

	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#destroyConnections()
	 */
	@Override
	public void destroyConnections() throws SQLException {
		
	}

	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#getStatTimeout(java.lang.String)
	 */
	@Override
	public int getStatTimeout(String dataSourceName) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#testConnection(java.lang.String)
	 */
	@Override
	public void testConnection() {
		Iterator<String> iter = wrappers.keySet().iterator();
		while (iter.hasNext()) {
			wrappers.get(iter.next()).monitor();
		}
	}
	
	public void close(String dataSourceName) {
		try {
			getDataSource(dataSourceName).close();
			wrappers.remove(dataSourceName);
		} finally {
			log.info(String.format("关闭连接池%s", dataSourceName));
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#listDataSource()
	 */
	@Override
	public Set<String> listDataSource() {
		return wrappers.keySet();
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#traceInfo(java.lang.String)
	 */
	@Override
	public String traceInfo(String dataSourceName) {
		DataSourceWrapper wrapper = wrappers.get(dataSourceName);
		if (null == wrapper) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(wrapper.monitor());
		sb.append(getTraceInfo(dataSourceName));
		
		return sb.toString();
	}
	
	
	private String getTraceInfo(String dataSourceName) {
		StringBuilder sb = new StringBuilder(",:");
		
		Map<UUID, TraceInfo> infos = trace.get(dataSourceName);
		if (null != infos) {
			Iterator<UUID> iter = infos.keySet().iterator();
			while (iter.hasNext()) {
				UUID traceId = iter.next();
				TraceInfo ti = infos.get(traceId);
				
				sb.append(ti.getTraceTime());
				sb.append("->");
				sb.append(ti.getStack());
				sb.append("~");
			}
		}
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#cleanAllTrace(java.lang.String)
	 */
	@Override
	public void cleanAllTrace() {
		trace = new ConcurrentHashMap<String, Map<UUID, TraceInfo>>(10000);
	}

}
