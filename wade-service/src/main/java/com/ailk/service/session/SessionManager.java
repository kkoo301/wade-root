package com.ailk.service.session;

import com.ailk.common.data.IVisit;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.service.session.app.AppSession;

public class SessionManager {

	private static SessionManager mananger = new SessionManager();
	
	private SessionManager() {
		
	}
	
	/**
	 * 获取当前会话的唯一标识
	 * @return
	 */
	public String getId() {
		return AppSession.getSession().getSessionId();
	}
	
	/**
	 * 获取会话里name对应的数据库连接
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public DBConnection getSessionConnection(String dataSourceName) throws Exception {
		return (DBConnection) AppSession.getSession().getConnection(dataSourceName);
	}
	
	/**
	 * 获取会话里name对应的数据库连接
	 * @param name
	 * @param transaction
	 * @return
	 * @throws Exception
	 */
	public DBConnection getSessionConnection(String dataSourceName, boolean transaction) throws Exception {
		DBConnection conn = (DBConnection) AppSession.getSession().getConnection(dataSourceName);
		conn.setTransaction(transaction);
		return conn;
	}
	
	
	/**
	 * 获取异步的数据库连接,该连接的事务需自己控制,不在线程上下文统一控制
	 * @param name
	 * @param transaction
	 * @return
	 * @throws Exception
	 */
	public DBConnection getAsyncConnection(String name, boolean transaction) throws Exception {
		DBConnection conn = (DBConnection) AppSession.getSession().getAsyncConnection(name);
		conn.setTransaction(transaction);
		return conn;
	}
	
	/**
	 * 获取异步的数据库连接,该连接的事务需自己控制,不在线程上下文统一控制
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public DBConnection getAsyncConnection(String dataSourceName) throws Exception {
		return (DBConnection) AppSession.getSession().getAsyncConnection(dataSourceName);
	}

	/**
	 * get instance
	 * 
	 * @return
	 */
	public static SessionManager getInstance() {
		return mananger;
	}
	
	
	/**
	 * 启动会话
	 */
	public void start() {
		
	}
	
	
	/**
	 * 设置上下文对象
	 * @param instance
	 * @param visit
	 */
	public void setContext(Object instance, IVisit visit) {
		
	}

	/**
	 * 获取会话上下文对象,该对象不可修改
	 * @return
	 */
	public IVisit getVisit() {
		final IVisit visit = AppSession.getSession().getContext();
		return visit;
	}

	/**
	 * 判断当前会话是否为活动的，即是否有启动主事务
	 * @return
	 */
	public boolean isActive() {
		return Thread.currentThread() instanceof AppSession;
	}
	
	
	/**
	 * 获取当前会话的实体对象
	 * @return
	 */
	public Object peek() {
		return AppSession.getSession().getCurrentService();
	}
	
	/**
	 * 获取主服务名
	 * @return
	 */
	public Object getMainService() {
		return AppSession.getSession().getService();
	}
	

	/**
	 * 会话提交，如果是主线程则真实提交，否则模拟二阶段事务提交
	 * @throws Exception
	 */
	public void commit() throws Exception {
		AppSession.getSession().commit();
	}

	/**
	 * rollback
	 * 
	 * @throws Exception
	 */
	public void rollback() throws Exception {
		AppSession.getSession().rollback();
	}

	/**
	 * destroy session
	 */
	public void destroy() throws Exception {
		
	}
	
	/**
	 * 添加线程锁对象的锁
	 * @param clazz
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public boolean lock(Class<?> clazz, Object[] params) throws Exception {
		return AppSession.getSession().lock(clazz, params);
	}
	
	/**
	 * 释放线程锁对象的锁
	 * @param clazz
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public boolean unlock(Class<?> clazz, Object[] params) throws Exception {
		return AppSession.getSession().unlock(clazz, params);
	}
	
	/**
	 * 获取线程共享对象，并返回其实例
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T getShareObject(Class<?> clazz) throws Exception {
		return (T) AppSession.getSession().getShareObject(clazz);
	}
}
