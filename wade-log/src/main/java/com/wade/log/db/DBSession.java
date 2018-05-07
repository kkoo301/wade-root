package com.wade.log.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.config.SystemCfg;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;

public class DBSession{
	
	private static final Logger log = Logger.getLogger(DBSession.class);
	
	private static final int CONNECTION_INIT_SIZE = 20;
	private static IConnectionManager manager = null;
	
	/**
	 * 存放当前线程所有的连接
	 */
	Map<String, Connection> connectionMap = new LinkedHashMap<String, Connection>(CONNECTION_INIT_SIZE);
		
	public DBSession(){
		if (null == manager){
			synchronized(DBSession.class){
				if( null == manager ){
					try {
						manager = ConnectionManagerFactory.getConnectionManager();
					} catch (Exception e) {
						if (SystemCfg.isDisabledDataBaseConfig) {
							log.info("数据库配置初始化异常，但不影响系统运行");
						} else {
							throw new IllegalStateException("数据库配置初始化异常", e);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 获取本地事务的连接对象, async=true, 标识为异步连接, 开发得自己Commit, 否则连接最终释放时会Rollback
	 * @param dataSourceName
	 * @param async
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String dataSourceName) throws SQLException {
		Map<String, Connection> conns = connectionMap;
		
		String key = dataSourceName;

		Connection conn = conns.get(key);
		
		try {
			if (null == conn || conn.isClosed()) {
				
				conn = null;
				conn = manager.getConnection(dataSourceName);
				
				conn.setAutoCommit(false);
				conns.put(key, conn);
			}
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new NullPointerException(
					String.format("获取连接 [%s]失败,[%s]", key, e.getMessage()));
		} finally {
			if (log.isDebugEnabled())
				log.debug(String.format("获取连接 [%s],%s", key, null != conn ? "成功" : "失败"));
		}
		
		return conn;
	}

	/**
	 * 全局事务提交,如果其中任一个事务提交异常，后续所有事务全做回滚和关闭操作
	 */
	public void commit() throws SQLException {
		
		StringBuilder sqlerr = null;
		boolean hasCommitedError = false;
		
		//处理本地事务
		Map<String, Connection> conns = connectionMap;
		if(conns.size() <= 0 )
			return;
		
		if (log.isDebugEnabled())
			log.debug(String.format("准备提交事务..."));
		
		Iterator<String> iter = conns.keySet().iterator();
		while (iter.hasNext()) {
			String dataSourceName = iter.next();
			Connection conn = conns.get(dataSourceName);
			if (null != conn) {
				try {
					if (hasCommitedError)
						conn.rollback();
					else
						conn.commit();
					
					if (log.isDebugEnabled()) {
						log.debug(String.format("提交本地事务[%s]成功", dataSourceName));
					}
					
				} catch (SQLException e) {
					hasCommitedError = true;
					if (null == sqlerr) {
						sqlerr = new StringBuilder();
						sqlerr.append("本地事务提交异常.");
					}
					sqlerr.append("数据源").append(dataSourceName).append(":").append(e.getMessage());
					log.error(String.format("提交本地事务[%s]失败", dataSourceName), e);
				}
			}
		}
		
		if (null != sqlerr) {
			SQLException e = new SQLException(sqlerr.toString());
			log.error(sqlerr.toString(), e);
			throw e;
		}
	}
	
	/**
	 * 全局事务回滚
	 */
	public void rollback() throws SQLException {
	
		StringBuilder sqlerr = null;
		
		// 处理本地事务
		Map<String, Connection> conns = connectionMap;
		if(conns.size() <= 0)
			return;
		
		if (log.isDebugEnabled())
			log.debug(String.format("准备回滚事务..."));		
		
		Iterator<String> iter = conns.keySet().iterator();
		while (iter.hasNext()) {
			String dataSourceName = iter.next();
			Connection conn = conns.get(dataSourceName);
			if (null != conn) {
				try {
					conn.rollback();
					
					if (log.isDebugEnabled()) {
						log.debug(String.format("回滚本地事务[%s]成功", dataSourceName));
					}
					
				} catch (SQLException e) {
					if (null == sqlerr) {
						sqlerr = new StringBuilder();
						sqlerr.append("本地事务回滚异常.");
					}
					sqlerr.append("数据源").append(dataSourceName).append(":").append(e.getMessage());
					log.error(String.format("回滚本地事务[%s]失败", dataSourceName), e);
				}
			}
		}
		
		if (null != sqlerr) {
			log.error(sqlerr.toString());
		}
	}
	
	/**
	 * 连接关闭
	 */
	public void close() {

		StringBuilder sqlerr = null;
		
		Map<String, Connection> conns = connectionMap;
		if(conns.size() <= 0)
			return;
		
		if (log.isDebugEnabled())
			log.debug(String.format("准备关闭连接..."));		
		
		Iterator<String> iter = conns.keySet().iterator();
		while (iter.hasNext()) {
			String dataSourceName = iter.next();
			Connection conn = conns.get(dataSourceName);
			if (null != conn) {
				try {
					if (!conn.isClosed()) {
						conn.close();
						
						if (log.isDebugEnabled()) {
							log.debug(String.format("关闭连接[%s]成功", dataSourceName));
						}
					} else {
						if (log.isDebugEnabled()) {
							log.debug(String.format("关闭连接[%s]成功，已关闭", dataSourceName));
						}
					}
				} catch (SQLException e) {
					if (null == sqlerr) {
						sqlerr = new StringBuilder();
						sqlerr.append("连接关闭异常.");
					}
					sqlerr.append("数据源").append(dataSourceName).append(":").append(e.getMessage());
					
					log.error(String.format("关闭连接[%s]失败", dataSourceName), e);
				} finally {
					conn = null;
				}
			}
		}
		
		if (null != sqlerr) {
			log.error(sqlerr.toString());
		}
	}

}