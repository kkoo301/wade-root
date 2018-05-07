/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.service.session.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ailk.common.config.SystemCfg;
import com.ailk.common.data.IVisit;
import com.ailk.common.thread.ThreadSession;
import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.dbconn.IConnectionManager;
import com.wade.relax.tm.context.XContext;

/**
 * 每个线程即一个AppSession对象，并将当前会话的所有用到的连接、事务统一管理起来
 * 
 * @className: AppSession.java
 * @author: liaosheng
 * @date: 2014-3-27
 */
public class AppSession extends Thread implements ThreadSession {
	
	private static final Logger log = Logger.getLogger(AppSession.class);
	
	private static final int CONNECTION_INIT_SIZE = 20;
	
	/**
	 * 存放当前线程所有的连接
	 */
	Map<String, Connection> connectionMap = new LinkedHashMap<String, Connection>(CONNECTION_INIT_SIZE);
	
	AppStatusControl statusControl = new AppStatusControl();
	
	
	/**
	 * 会话ID，在reset时重置
	 */
	private String sessionId = UUID.randomUUID().toString();
	
	private AppSessionLock lock = new AppSessionLock();
	
	private SessionShareObject<ISessionShareObject> cache = new SessionShareObject<ISessionShareObject>();
	
	private static IConnectionManager manager = null;
	
	/**
	 * 会话上下文
	 */
	private IVisit context = null;
	
	/**
	 * 服务对象
	 */
	private Stack<Object> services = new Stack<Object>();
	
	private int connectionStmtTimeout = -1;
	
	static {
		
	}
	
	AppSession (ThreadGroup group, Runnable r, String name) {
		super(group, r, name, 0);
		try {
			if (null == manager)
				manager = ConnectionManagerFactory.getConnectionManager();
		} catch (Exception e) {
			if (SystemCfg.isDisabledDataBaseConfig) {
				log.info("数据库配置初始化异常，但不影响系统运行");
			} else {
				throw new IllegalStateException("数据库配置初始化异常", e);
			}
		}
	}
	
	/**
	 * 根据当前线程
	 * @return
	 */
	public static AppSession getSession() {
		Thread thread = Thread.currentThread();
		if (thread instanceof AppSession) {
			return (AppSession) thread;
		}
		
		return new AppSession(Thread.currentThread().getThreadGroup(), null, "new-app-session");
	}
	
	
	/**
	 * 获取会话ID
	 * @return
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	
	/* (non-Javadoc)
	 * @see com.ailk.common.thread.ThreadSession#getObject()
	 */
	@Override
	public Object getValue(String name) {
		try {
			return getConnection(name);
		} catch (SQLException e) {
			throw new IllegalAccessError(e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ailk.common.thread.ThreadSession#getContext()
	 */
	@Override
	public IVisit getContext() {
		return context;
	}
	
	/**
	 * set context
	 * @param visit
	 */
	public void setContext(IVisit visit) {
		this.context = visit;
	}
	
	public Object getService() {
		return this.services.get(0);
	}
	
	public Object getCurrentService() {
		return this.services.peek();
	}
	
	public Object pushService(Object service) {
		return this.services.push(service);
	}
	
	public Object popService() {
		return this.services.pop();
	}
	
	public boolean lock(Class<?> clazz, Object[] params) throws Exception {
		return lock.lock(clazz, params);
	}
	
	public boolean unlock(Class<?> clazz, Object[] params) throws Exception {
		return lock.unlock(clazz, params);
	}
	
	private void cleanLocks() {
		if (log.isDebugEnabled()) {
			log.debug(String.format("会话 [%s] 释放线程锁", getSessionId()));
		}
		lock.cleanLocks();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getShareObject(Class<?> clazz) throws Exception {
		return (T) cache.get(clazz);
	}
	
	private void cleanCaches() {
		if (log.isDebugEnabled()) {
			log.debug(String.format("会话 [%s] 清空线程缓存", getSessionId()));
		}
		cache.cleanCaches();
	}
	
	
	/**
	 * 获取分布式事务的连接对象
	 * @param dataSoruceName
	 * @return
	 * @throws SQLException
	 */
	private Connection getXConnection(String dataSoruceName) throws SQLException {
		Connection conn = null;
		try {
			conn = XContext.getInstance().getConnection(dataSoruceName);
			if (log.isDebugEnabled()) {
				log.debug(String.format("获取分布式连接: %s->%s", XContext.getInstance().getTID(), dataSoruceName));
			}
			return conn;
		} catch (Exception e) {
			StringBuilder err = new StringBuilder(100);
			err.append("获取分布式连接异常, 数据源名称:");
			err.append(dataSoruceName);
			throw new SQLException(err.toString(), e);
		}
	}
	
	/**
	 * 获取本地事务的连接对象, async=true, 标识为异步连接, 开发得自己Commit, 否则连接最终释放时会Rollback
	 * @param dataSourceName
	 * @param async
	 * @return
	 * @throws SQLException
	 */
	private Connection getConnection(String dataSourceName, boolean async) throws SQLException {
		Map<String, Connection> conns = connectionMap;
		
		String key = "";
		if (async) {
			key = dataSourceName + "-ASYNC";
		} else {
			key = dataSourceName;
		}
		
		Connection conn = conns.get(key);
		
		try {
			if (null == conn || conn.isClosed()) {
				
				if (statusControl.isDestroyed()) {
					String err = String.format("会话[%s]已超时并被回收，无法再获取连接[%s]", getSessionId(), dataSourceName);
					log.error(err);
					throw new SQLException(err);
				}
				
				if (log.isDebugEnabled())
					log.debug(String.format("会话 [%s] 获取新连接[%s]", getSessionId(), key));
				
				conn = null;
				long start = System.currentTimeMillis();
				conn = manager.getConnection(dataSourceName);
				
				conn.setAutoCommit(false);
				
				if (conn instanceof DBConnection && getConnectionStmtTimeout() > 0) {
					((DBConnection) conn).setStmtTimeout(getConnectionStmtTimeout());
					((DBConnection) conn).setConnCostTime((System.currentTimeMillis() - start));
				}
				conns.put(key, conn);
			}
			
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new NullPointerException(
					String.format("会话 [%s] 获取连接 [%s]失败,[%s]", getSessionId(), key, e.getMessage()));
		} finally {
			if (log.isDebugEnabled())
				log.debug(String.format("会话 [%s] 获取连接 [%s],%s", getSessionId(), key, null != conn ? "成功" : "失败"));
		}
		
		return conn;
	}
	
	/**
	 * 从连接管理器获取对应DataSourceName的连接，并存放在connectionMap里
	 * @param dataSourceName
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(String dataSourceName) throws SQLException {
		if (DatabaseCfg.useDTM()) {
			return getXConnection(dataSourceName);
		}
		return getConnection(dataSourceName, false);
	}
	
	public Connection getAsyncConnection(String dataSourceName) throws SQLException {
		return getConnection(dataSourceName, true);
	}
	
	/**
	 * @return the connectionStmtTimeout
	 */
	public int getConnectionStmtTimeout() {
		return connectionStmtTimeout;
	}
	
	/**
	 * @param connectionStmtTimeout the connectionStmtTimeout to set
	 */
	public void setConnectionStmtTimeout(int connectionStmtTimeout) {
		if (connectionStmtTimeout <=0 )
			this.connectionStmtTimeout = -1;
		
		if (connectionStmtTimeout > 600)
			throw new IllegalThreadStateException("不能设置不合理的语句超时阀值:" + connectionStmtTimeout);
		
		this.connectionStmtTimeout = connectionStmtTimeout;
	}
	
	
	/**
	 * 分布式事务提交
	 * @throws SQLException
	 */
	public void commitX() throws SQLException {
		XContext.getInstance().commit();
	}
	
	/**
	 * 分布式事务回滚
	 * @throws SQLException
	 */
	public void rollbackX() throws SQLException {
		XContext.getInstance().rollback();
	}
	
	
	/**
	 * 全局事务提交,如果其中任一个事务提交异常，后续所有事务全做回滚和关闭操作
	 */
	public void commit() throws SQLException {
		if (log.isDebugEnabled())
			log.debug(String.format("会话 [%s] 准备提交事务...", getSessionId()));
		
		StringBuilder sqlerr = null;
		boolean hasCommitedError = false;
		
		//处理本地事务
		Map<String, Connection> conns = connectionMap;
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
						log.debug(String.format("会话 [%s] 提交本地事务[%s]成功", getSessionId(), dataSourceName));
					}
					
				} catch (SQLException e) {
					hasCommitedError = true;
					if (null == sqlerr) {
						sqlerr = new StringBuilder();
						sqlerr.append("会话").append(getSessionId()).append("本地事务提交异常.");
					}
					sqlerr.append("数据源").append(dataSourceName).append(":").append(e.getMessage());
					log.error(String.format("会话 [%s] 提交本地事务[%s]失败", getSessionId(), dataSourceName), e);
				}
			}
		}
		
		// 处理分步式事务
		try {
			if (DatabaseCfg.useDTM()) {
				if (hasCommitedError) {
					rollbackX();
					if (log.isDebugEnabled()) {
						log.debug(String.format("会话 [%s] 回滚分布式事务[%s]成功.", getSessionId(), XContext.getInstance().getTID()));
					}
				} else {
					commitX();
					if (log.isDebugEnabled()) {
						log.debug(String.format("会话 [%s] 提交分布式事务[%s]成功.", getSessionId(), XContext.getInstance().getTID()));
					}
				}
			}
		} catch (SQLException e) {
			if (null == sqlerr) {
				sqlerr = new StringBuilder();
			}
			sqlerr.append("会话").append(getSessionId()).append("分布式事务提交异常.").append(XContext.getInstance().getTID());
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
		if (log.isDebugEnabled())
			log.debug(String.format("会话 [%s] 准备回滚事务...", getSessionId()));
		
		StringBuilder sqlerr = null;
		
		// 处理本地事务
		Map<String, Connection> conns = connectionMap;
		Iterator<String> iter = conns.keySet().iterator();
		
		while (iter.hasNext()) {
			String dataSourceName = iter.next();
			Connection conn = conns.get(dataSourceName);
			if (null != conn) {
				try {
					conn.rollback();
					
					if (log.isDebugEnabled()) {
						log.debug(String.format("会话 [%s] 回滚本地事务[%s]成功", getSessionId(), dataSourceName));
					}
					
				} catch (SQLException e) {
					if (null == sqlerr) {
						sqlerr = new StringBuilder();
						sqlerr.append("会话").append(getSessionId()).append("本地事务回滚异常.");
					}
					sqlerr.append("数据源").append(dataSourceName).append(":").append(e.getMessage());
					log.error(String.format("会话 [%s] 回滚本地事务[%s]失败", getSessionId(), dataSourceName), e);
				}
			}
		}
		
		// 处理分步式事务
		try {
			if (DatabaseCfg.useDTM())
				rollbackX();
			
			if (log.isDebugEnabled()) {
				log.debug(String.format("会话 [%s] 回滚分布式事务成功", getSessionId()));
			}
		} catch (SQLException e) {
			if (null == sqlerr) {
				sqlerr = new StringBuilder();
			}
			sqlerr.append("会话").append(getSessionId()).append("分布式事务回滚异常.").append(XContext.getInstance().getTID());
		}
		
		if (null != sqlerr) {
			log.error(sqlerr.toString());
		}
	}
	
	/**
	 * 连接关闭
	 */
	public void close() {
		if (log.isDebugEnabled())
			log.debug(String.format("会话 [%s] 准备关闭连接...", getSessionId()));
		
		StringBuilder sqlerr = null;
		
		Map<String, Connection> conns = connectionMap;
		Iterator<String> iter = conns.keySet().iterator();
		while (iter.hasNext()) {
			String dataSourceName = iter.next();
			Connection conn = conns.get(dataSourceName);
			if (null != conn) {
				try {
					if (!conn.isClosed()) {
						conn.close();
						
						if (log.isDebugEnabled()) {
							log.debug(String.format("会话 [%s] 关闭连接[%s]成功", getSessionId(), dataSourceName));
						}
					} else {
						if (log.isDebugEnabled()) {
							log.debug(String.format("会话 [%s] 关闭连接[%s]成功，已关闭", getSessionId(), dataSourceName));
						}
					}
				} catch (SQLException e) {
					if (null == sqlerr) {
						sqlerr = new StringBuilder();
						sqlerr.append("会话 [").append(getSessionId()).append("] 连接关闭异常.");
					}
					sqlerr.append("数据源").append(dataSourceName).append(":").append(e.getMessage());
					
					log.error(String.format("会话 [%s] 关闭连接[%s]失败", getSessionId(), dataSourceName), e);
				} finally {
					conn = null;
				}
			}
		}
		
		
		// 处理分步式事务
		if (DatabaseCfg.useDTM()) {
			String tid = XContext.getInstance().getTID();
			XContext.getInstance().destroyCTX();
		
			if (log.isDebugEnabled()) {
				log.debug(String.format("会话 [%s] 注销分布式事务成功", tid));
			}
		}
		
		if (null != sqlerr) {
			log.error(sqlerr.toString());
		}
	}

	
	/**
	 * 重置线程上下文
	 */
	public void reset() {
		if (log.isDebugEnabled()) {
			log.debug(String.format("会话 [%s] 重置完成", getSessionId()));
		}
		
		//清除锁
		cleanLocks();
		
		//清除锁
		cleanCaches();
		
		//清除会话对象
		connectionMap = new LinkedHashMap<String, Connection>(CONNECTION_INIT_SIZE);
		sessionId = UUID.randomUUID().toString();
		context = null;
		lock = new AppSessionLock();
		cache = new SessionShareObject<ISessionShareObject>();
		statusControl = new AppStatusControl();
		services = new Stack<Object>();
	}
	
}
