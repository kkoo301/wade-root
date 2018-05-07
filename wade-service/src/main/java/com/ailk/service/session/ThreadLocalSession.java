/**
 * 
 */
package com.ailk.service.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ailk.common.data.IVisit;
import com.ailk.database.dbconn.DBConnection;

/**
 * 线程对象
 * 
 * @author yifur
 * 
 */
public class ThreadLocalSession {
	
	private static final Logger log = Logger.getLogger(ThreadLocalSession.class);
	
	/**
	 * 每个线程的唯一序列
	 */
	private String uuid;

	/**
	 * 会话里的数据库连接
	 */
	private ISessionConnection sessionConn;

	/**
	 * 会话外的独立事务的连接池
	 */
	private Map<String, DBConnection> asyncConns = new HashMap<String, DBConnection>(10);

	/**
	 * 当前线程的上下文对象
	 */
	private IVisit visit;

	/**
	 * 是否为主线程事务
	 */
	private Boolean mainTranscation = Boolean.FALSE;

	/**
	 * 当前线程正在运行的对象实体,必须是同时实现了IResult,IRoute的对象
	 */
	private Stack<Object> instance = new Stack<Object>();
	
	/**
	 * 数据库连接实现类
	 */
	private String sessionConnClass = "com.ailk.database.session.DBSession";
	
	/**
	 * 会话超时时长
	 */
	private int timeout = 0;

	/**
	 * ThreadLocalSession
	 */
	public ThreadLocalSession() {
		this.uuid = UUID.randomUUID().toString();
	}
	
	/**
	 * @return the asyncConns
	 */
	public Map<String, DBConnection> getAsyncConns() {
		return asyncConns;
	}
	
	
	/**
	 * 根据连接名获取数据库连接
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public DBConnection getSessionConnection(String name, boolean transaction) throws Exception {
		DBConnection conn = getSessionConnection().getConnection(name, transaction);
		
		return conn;
	}
	
	
	/**
	 * 根据连接名获取异步连接
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public DBConnection getAsyncConnection(String name, boolean transaction) throws Exception {
		DBConnection conn = asyncConns.get(name);
		if (conn == null) {
			conn = getSessionConnection().getAsyncConnection(name, transaction);
			asyncConns.put(name, conn);
		}
		
		return conn;
	}
	
	
	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}
	
	/**
	 * is main transcation
	 */
	public boolean isMainTranscation() {
		return this.mainTranscation;
	}
	
	/**
	 * @param mainTranscation the mainTranscation to set
	 */
	public void setMainTranscation(Boolean mainTranscation) {
		this.mainTranscation = mainTranscation;
	}
	
	/**
	 * @return the visit
	 */
	public IVisit getVisit() {
		return this.visit;
	}
	
	/**
	 * @param visit the visit to set
	 */
	public void setVisit(IVisit visit) {
		this.visit = visit;
	}
	
	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
	/**
	 * Push当前执行的业务对象
	 * @param obj
	 * @return
	 */
	public Object pushInstance(Object obj) {
		return this.instance.push(obj);
	}
	
	/**
	 * Pop当前执行的业务对象
	 * @return
	 */
	public Object popInstance() {
		if (this.instance.size() > 1) {
			return this.instance.pop();
		}
		return null;
	}
	
	/**
	 *  Peek当前执行的业务对象
	 * @return
	 */
	public Object peekInstance() {
		return this.instance.peek();
	}
	
	
	/**
	 * 返回数据库连接对象,如果没有则创建一个
	 * @return
	 */
	public ISessionConnection getSessionConnection() {
		if (sessionConn == null) {
			sessionConn = newSessionInstance();
		}
		return sessionConn;
	}
	
	
	/**
	 * 创建会话连接对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ISessionConnection newSessionInstance() {
		try {
			Class<ISessionConnection> clazz = (Class<ISessionConnection>) Class.forName(sessionConnClass);
			return clazz.newInstance();
		} catch (InstantiationException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	/**
	 * @param sessionConnClass the sessionConnClass to set
	 */
	public void setSessionConnClass(String sessionConnClass) {
		this.sessionConnClass = sessionConnClass;
	}
	
	
	/**
	 * 会话失效
	 */
	public void invalidate() {
		this.visit = null;
	}
}
