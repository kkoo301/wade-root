package com.ailk.database.dbconn.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;
import com.ailk.database.dbconn.IDBPasswordCreator;
import com.ailk.database.jdbc.DataSourceManager;
import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.util.TripleDES;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: RefineConnectionManager
 * @description: 精细化连接配置
 * 
 * @version: v1.0.0
 * @author: zhoulin
 * @date: 2013-7-20
 */
public class RefineConnectionManager implements IConnectionManager {

	private static final Logger log = Logger.getLogger(RefineConnectionManager.class);

	private static DataSourceManager manager = null;
	private static final String baseDataSourceName = "base";
	private static Map<String, Map<String, String>> configs = new HashMap<String, Map<String,String>>(100);
	
	private boolean init = false;
	
	private static String sql = null;
	
	static {
		StringBuilder buf = new StringBuilder(200);
		buf.append("SELECT ACCT.DATA_SOURCE, ACCT.USERNAME, ACCT.PASSWORD, ACCT.DRIVER_CLASS_NAME, ACCT.OWNER_NAME, URL.URL_CODE, URL.URL, URL.SERVICE_NAME,\n");
		buf.append(" CONN.INITIAL_SIZE, CONN.MAX_ACTIVE, CONN.MAX_IDLE, CONN.MAX_WAIT, CONN.STMT_TIMEOUT, CONN.READONLY, CONN.DIALECT, CONN.WRAPPER\n ");
		buf.append("  FROM WD_DATASOURCE_ACCT ACCT, WD_DATASOURCE_URL URL, WD_DATASOURCE_CONNECT CONN\n ");
		buf.append(" WHERE ACCT.DATA_SOURCE = CONN.DATA_SOURCE\n ");
		if (!"wade".equals(DatabaseCfg.getVersion())) {
			buf.append("   AND URL.URL_CODE = ACCT.URL_CODE\n ");
		}
		buf.append("   AND URL.URL_CODE = CONN.URL_CODE\n ");
		buf.append("   AND ACCT.STATE = 'U'\n ");
		buf.append("   AND URL.STATE = 'U'\n ");
		buf.append("   AND CONN.STATE = 'U'\n ");
		buf.append("   AND CONN.SERVER_NAME = ? \n "); // SERVER_NAME
		sql = buf.toString();
	}

	public RefineConnectionManager() {
		
	}
	
	public void initConnectionManager() {
		manager = new DataSourceManager();
		
		log.info("开始加载数据库配置的数据源");
		
		String wadeServerName = System.getProperty("wade.server.name");
		
		try {
			if (null == wadeServerName || wadeServerName.length() <= 0) {
				throw new Exception("进程启动参数中未配置wade.server.name参数，初始化数据库连接失败！");
			}

			long start = System.currentTimeMillis();
			
			if (loadDataSourceConfig(wadeServerName)) {
				throw new Exception("数据源初始化失败, 请查看应用启动日志");
			}

			Set<String> ds = new TreeSet<String>();
			ds.addAll(configs.keySet());
			
			log.info("成功初始化列表:" + ds);
			log.info("初始化所有数据源总耗时:" + (System.currentTimeMillis() - start) + "毫秒");
			
			try {
				DataSourceWrapper base = manager.getDataSource(baseDataSourceName);
				if (null != base) {
					base.close();
				}
				
				log.info("释放base数据源：" + baseDataSourceName);
			} catch (Exception e2) {
				log.error("释放base数据源失败：" + baseDataSourceName, e2);
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new IllegalArgumentException("数据源初始化失败", e);
		} finally {
			init = true;
		}
	}

	/**
	 * 根据数据源名称获取数据库用户名
	 */
	@Override
	public String getUserName(String dataSourceName) {
		return manager.getUserName(dataSourceName);
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#getDataSource(java.lang.String)
	 */
	@Override
	public DataSourceWrapper getDataSource(String dataSourceName) {
		return manager.getDataSource(dataSourceName);
	}

	/**
	 * 根据连接池名获取数据库连接
	 */
	@Override
	public Connection getConnection(String dataSourceName) throws SQLException {
		
		if (!init) {
			initConnectionManager();
		}
		
		if (null == dataSourceName || dataSourceName.length() <= 0) {
			throw new IllegalArgumentException("数据库连接名[" + dataSourceName + "]不能为空!");
		}
		
		/**
		 * 判断数据库连接名在当前进程上是否正常，约定如下为异常：
		 * serviceName + "." + dataSourceName + ".disable" = true 并且 serviceName + "." + dataSourceName + ".disable.time" <= 60秒
		 */
		String serviceName = System.getProperty("wade.server.name");
		String disabled = System.getProperty(serviceName + "." + dataSourceName + ".disabled");
		String disabledTime = System.getProperty(serviceName + "." + dataSourceName + ".disabled.time");
		
		if (log.isDebugEnabled()) {
			log.debug("数据库连接状态: disabled=" + disabled + ", time=" + disabledTime);
		}
		
		if ("true".equals(disabled) && disabledTime != null) {
			long time = 0L;
			try {
				time = Long.parseLong(disabledTime);
			} catch (Exception e) {
				time = 0L;
			}
			
			if (System.currentTimeMillis() - time <= 60000) {
				throw new SQLException("数据库连接异常" + serviceName + ":" + disabledTime);
			}
		}
		
		return manager.getConnection(dataSourceName);
	}
	
	
	/**
	 * 探测数据库连接,规则如下：
	 * 1.如果当前进程未配置dataSourceName则返回true
	 * 2.在约定时间内成功获取数据库连接并执行完探测SQL则返回true,否则返回false
	 */
	@Override
	public void testConnection() {
		manager.testConnection();
	}

	/**
	 * 销毁所有链接池
	 * 
	 * @throws SQLException
	 */
	public void destroyConnections() throws SQLException {
		
	}

	/**
	 * 加载数据源配置
	 * 
	 * @param wadeServerName
	 * @return
	 */
	private static boolean loadDataSourceConfig(String wadeServerName) throws Exception {
		log.info("加载数据源配置: wade.server.name=" + wadeServerName);

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		boolean hasError = false;
		
		try {
			conn = manager.getConnection(baseDataSourceName);
			
			if (null == conn) {
				throw new Exception(String.format("获取默认数据库连接[%s]失败,请检查database.xml文件的配置", baseDataSourceName));
			}
			
			log.info("查询数据源配置语句:" + sql);
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, wadeServerName);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				try {
					String dataSource = rs.getString("DATA_SOURCE");
					String username = rs.getString("USERNAME"); 		// 数据库帐号
					String password = rs.getString("PASSWORD"); 		// 数据库密码
					String driver = rs.getString("DRIVER_CLASS_NAME"); 	// 驱动
					String url = rs.getString("URL"); 					// 数据库URL
					String initialSize = rs.getString("INITIAL_SIZE"); 	// 初始化大小
					String maxActive = rs.getString("MAX_ACTIVE"); 		// 最大活跃连接数
					String maxIdle = rs.getString("MAX_IDLE"); 			// 最大闲置连接数
					String maxWait = rs.getString("MAX_WAIT"); 			// 最大等待时间
					String servicename = rs.getString("SERVICE_NAME");	// 统一密码
					String readonly = rs.getString("READONLY");			// 是否只读
					String dialect = rs.getString("DIALECT");			// 数据库方言
					String owner = rs.getString("OWNER_NAME");			// 归属用户名
					String stmtTimeout = rs.getString("STMT_TIMEOUT");	// 语句执行超时阀值
					String urlCode = rs.getString("URL_CODE");			// 中心编码
					String wrapper = rs.getString("WRAPPER");			// 自定义封装类
					
					if (null == stmtTimeout || stmtTimeout.length() <= 0 || "0".equals(stmtTimeout))
						stmtTimeout = "0";

					Map<String, String> config = new HashMap<String, String>(20);
					config.put("type", "dbcp");
					config.put("user", username);
					config.put("passwd", getPassword(username, password, servicename));
					config.put("driver", driver);
					config.put("url", url);
					config.put("initialSize", initialSize);
					config.put("maxActive", maxActive);
					config.put("maxIdle", maxIdle);
					config.put("maxWait", maxWait);
					config.put("readonly", readonly);
					config.put("dialect", dialect);
					config.put("owner", owner);
					config.put("stmtTimeout", stmtTimeout);
					config.put("center", urlCode);
					
					if (null != wrapper && wrapper.length() > 0) {
						config.put("wrapper", wrapper);
					}
					
					configs.put(dataSource, config);
					manager.load(dataSource, config);
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					hasError = true;
				}
			}
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}

				if (null != pstmt) {
					pstmt.close();
				}

				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			} finally {
				//manager.close(baseDataSourceName);
			}
		}
		
		
		return hasError;
	}
	
	/**
	 * 获取真实密码，如果配置了database.xml@pwdcreator，则统一走密码管理，否则使用原始密码
	 * @param userName
	 * @param passwd
	 * @param serviceName
	 * @return
	 */
	private static String getPassword(String userName, String passwd, String serviceName) {
		if (serviceName == null || serviceName.length() <= 0)
			return passwd;
		
		/**
		 * 判断是否采用了3DES加密
		 */
		if (passwd.startsWith("{3DES}")) {
			return TripleDES.decrypt(passwd.substring(6));
		}
		
		IDBPasswordCreator creator = ConnectionManagerFactory.getDBPwdCreator();
		if (null == creator)
			return passwd;
		
		String password = creator.getDBPassword(userName, serviceName, passwd);
		
		if (null == password || password.length() <=0) {
			throw new NullPointerException("创建统一密码[userName=" + userName + ",serviceName=" + serviceName +"]失败，返回值为空");
		}
		
		return password;
	}
	
	/**
	 * 获取连接名dataSource的SQL超时时长
	 */
	@Override
	public int getStatTimeout(String dataSourceName) {
		return Integer.parseInt(configs.get(dataSourceName).get("stmtTimeout"));
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#listDataSource()
	 */
	public Set<String> listDataSource() {
		return manager.listDataSource();
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#traceInfo(java.lang.String)
	 */
	@Override
	public String traceInfo(String dataSourceName) {
		return manager.traceInfo(dataSourceName);
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#cleanTrace(java.lang.String, java.lang.Long)
	 */
	@Override
	public void cleanTrace(String dataSourceName, UUID traceId) {
		manager.cleanTrace(dataSourceName, traceId);
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#trace(java.lang.String, java.lang.Long)
	 */
	@Override
	public void trace(String dataSourceName, UUID traceId) {
		manager.trace(dataSourceName, traceId);
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.database.dbconn.IConnectionManager#cleanAllTrace()
	 */
	@Override
	public void cleanAllTrace() {
		manager.cleanAllTrace();
	}
	
}
