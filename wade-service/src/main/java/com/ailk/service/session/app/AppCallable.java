/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.service.session.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.ailk.common.BaseException;
import com.ailk.common.config.SystemCfg;
import com.ailk.common.data.IVisit;

/**
 * 线程真正执行的对象，主要完成对服务方法的反射及事务控制，它将引用线程上的数据库连接对象及线程状态控制对象。
 * 当线程超时时，主线程会执行它的destroy方法，释放资源，确保超时后的连接及事务正常处理
 * 
 * @className: AppCallable.java
 * @author: liaosheng
 * @date: 2014-3-27
 */
public class AppCallable<T> implements Callable<T> {
	
	private static final Logger log = Logger.getLogger(AppCallable.class);
	
	//支持按工号写Log4j日志到Memcache
	private static final String MDC_STAFF_KEY = "STAFF_ID";
	//支持按会话实现垂直跟踪
	private static final String MDC_SESSION_KEY = "SESSION_ID";
	private static final boolean THROW_SQL_ERROR = SystemCfg.isThrowSQLError;
	
	/**
	 * 服务对象实例反射的相关参数
	 */
	private Object object = null;
	private Method executeMethod = null;
	private String methodName;
	private Object[] params = new Object[] {};
	private Class<?> clazz = null;
	
	
	/**
	 * 反射返回的结果集
	 */
	private T result = null;
	
	/**
	 * 线程上下文对象
	 */
	private IVisit context;
	
	/**
	 * 会话标识，即主线程标识
	 */
	private String sessionId = null;
	
	private boolean committed = false;
	private boolean stoped = false;
	private boolean finished = false;
	
	private long timeout = 0L;
	
	/**
	 * 引用主线程的数据库连接集对象
	 */
	private Map<String, Connection> connections = null;
	
	/**
	 * 引用主线程的会话状态控制对象
	 */
	private AppStatusControl appobj = null;
	
	public AppCallable() {
	}
	
	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return this.sessionId;
	}
	
	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}
	
	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) throws NoSuchMethodError {
		if ("callback".equals(methodName)) {
			throw new NoSuchMethodError("不允许的方法名[callback]");
		}
		this.methodName = methodName;
	}
	
	/**
	 * @return the context
	 */
	public IVisit getContext() {
		return context;
	}
	
	/**
	 * @param context the context to set
	 */
	public void setContext(IVisit context) {
		this.context = context;
	}
	
	/**
	 * @return the params
	 */
	public Object[] getParams() {
		return params;
	}
	
	/**
	 * @param params the params to set
	 */
	public void setParams(Object[] params) {
		if (null != params )
			this.params = params;
	}
	
	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * @return the timeout
	 */
	public long getTimeout() {
		return timeout;
	}
	
	/**
	 * 
	 * @throws InterruptedException
	 */
	public void stop() {
		synchronized (this) {
			this.stoped = true;
			
			if (null != this.appobj)
				this.appobj.setDestroyed(true);
		}
	}
	
	/**
	 * @return the stoped
	 */
	public synchronized boolean isStoped() {
		synchronized (this) {
			return stoped;
		}
	} 
	
	/**
	 * 执行服务调用
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T call() throws Exception {
		String wadeServerName = System.getProperty("wade.server.name", "");
		AppSession session = AppSession.getSession();
		
		sessionId = session.getSessionId();
		
		putMDC();
		
		session.pushService(object);
		
		//将会话上的对象引用到Callable对象上
		connections = session.connectionMap;
		appobj = session.statusControl;
		
		long start = System.currentTimeMillis();
		if (log.isDebugEnabled())
			log.debug(String.format("会话[%s]调用开始,超时阀值[%d]秒", session.getSessionId(), timeout));
		
		session.setContext(getContext());
		
		if (null == executeMethod)
			initMethod();
		
		
		try {
			result = (T) executeMethod.invoke(object, params);
			
			if (isStoped()) {
				throw new TimeoutException("事务已回滚");
			}
			setCommitted();
			session.commit();
		} catch (InvocationTargetException e) {
			session.rollback();
			
			Throwable sqlEx = null;
			Throwable throwable = e;
			
			//ojdbc8 SQLException不是最底层的异常
			while(throwable.getCause() != null){
				throwable = throwable.getCause();
				if(throwable instanceof SQLException){
					sqlEx = throwable;
					break;
				}
			}
			
			if(sqlEx != null){
				if (THROW_SQL_ERROR) {
					throw e;
				} else {
					String errmsg = e.getMessage();
					String msg = "";
					//处理数据库SQL超时的异常
					if (null != errmsg && errmsg.indexOf("ORA-01013") > 0) {
						msg = "数据库操作超时已强制中断," + wadeServerName;
					} else {
						msg = "数据库操作异常," + wadeServerName;
					}
					log.error(msg + "," + sessionId, e);
					throw new BaseException("DB1001", null, msg);
				}
			}else if(throwable instanceof BaseException){
				throw (BaseException)throwable;
			}else{
				throw e;
			}
		} catch (Exception e) {
			session.rollback();
			throw e;
		} catch (Throwable e) {
			session.rollback();
			throw new Exception(e);
		} finally {
			//关闭连接
			session.close();
			
			//清空线程对象
			session.reset();
			
			finished = true;
			
			removeMDC();
			
			
			if (log.isDebugEnabled())
				log.debug(String.format("会话[%s]调用完成,耗时[%d]ms", getSessionId(), (System.currentTimeMillis() - start)));
		}
		
		return result;
	}
	
	
	private void putMDC() {
		IVisit visit = getContext();
		if (null != visit) {
			Object obj = visit.get(MDC_STAFF_KEY);
			if (null != obj) {
				MDC.put(MDC_STAFF_KEY, obj);
			}
		}
		
		if (null != sessionId) {
			MDC.put(MDC_SESSION_KEY, sessionId);
		}
	}
	private void removeMDC() {
		MDC.remove(MDC_STAFF_KEY);
		MDC.remove(MDC_SESSION_KEY);
	}
	
	public void destroy() {
		if (log.isInfoEnabled())
			log.info(String.format("会话 [%s]超时，尝试关闭数据库连接...", sessionId));
		
		if (null == connections) {
			return ;
		}
		
		StringBuilder sqlerr = null;
		
		Iterator<String> iter = connections.keySet().iterator();
		while (iter.hasNext()) {
			String dataSourceName = iter.next();
			Connection conn = connections.get(dataSourceName);
			if (null != conn) {
				try {
					conn.rollback();
				} catch (SQLException e) {
					if (null == sqlerr) {
						sqlerr = new StringBuilder();
						sqlerr.append("会话").append(sessionId).append("事务回滚异常.");
					}
					sqlerr.append("数据源").append(dataSourceName).append(":").append(e.getMessage());
					
					log.error(String.format("会话[%s]回滚[%s]失败", sessionId, dataSourceName), e);
				} finally {
					try {
						conn.close();
					} catch (SQLException e) {
						if (null == sqlerr) {
							sqlerr = new StringBuilder();
							sqlerr.append("会话").append(sessionId).append("连接关闭异常.");
						}
						sqlerr.append("数据源").append(dataSourceName).append(":").append(e.getMessage());
						
						log.error(String.format("会话[%s]关闭连接[%s]失败", sessionId, dataSourceName), e);
					} finally {
						conn = null;
					}
				}
			}
		}
		
		if (null != appobj)
			appobj.setDestroyed(true);
	}
	
	/**
	 * 初始化方法、参数等
	 * @throws NoSuchMethodException
	 */
	private void initMethod() throws NoSuchMethodException {
		try {
			clazz = object.getClass();
			Method[] methods = clazz.getMethods();
			for (int i = 0, cnt = methods.length; i < cnt; i++) {
				String name = methods[i].getName();
				
				if (methodName.equals(name)) {
					executeMethod = methods[i];
					executeMethod.setAccessible(true);
					break;
				}
			}
			if (null == executeMethod) 
				throw new NoSuchMethodException(String.format("找不到方法名[%s.%s]", clazz.getName(), methodName));
			
			Class<?>[] types = executeMethod.getParameterTypes();
			
			int typesLen = types.length;
			if (typesLen != params.length) 
				throw new NoSuchMethodException(String.format("找不到方法名，参数个数不匹配[%s.%s.%s]", clazz.getName(), methodName, Arrays.toString(params)));
			
			for (int i = 0; i < typesLen; i++) {
				if (null == params[i])
					continue;
				
				if (!types[i].equals(params[i].getClass()) && !types[i].isAssignableFrom(params[i].getClass()))
					throw new NoSuchMethodException(String.format("找不到方法名，第[%d]个参数类型[%s]不匹配[%s.%s.%s]", i, params[i].getClass().getName(), clazz.getName(), methodName, Arrays.toString(types)));
			}
		} catch (NoSuchMethodException e) {
			throw e;
		}
	}
	
	/**
	 * @param method the method to set
	 */
	public void setMethod(Method method) {
		this.executeMethod = method;
	}
	
	/**
	 * @return the method
	 */
	public Method getMethod() {
		return executeMethod;
	}
	
	/**
	 * @return the committed
	 */
	public boolean isCommitted() {
		synchronized (this) {
			return committed;
		}
	}
	
	/**
	 * @param committed the committed to set
	 */
	public void setCommitted() {
		synchronized (this) {
			this.committed = true;
		}
	}
	
	
	/**
	 * @return the finished
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * @return the result
	 */
	public T getResult() {
		return result;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("当前线程:").append(sessionId);
		sb.append("状态:").append(null == appobj ? null : appobj.isDestroyed());
		sb.append("上下文:").append(null == context ? null : context.toString());
		sb.append("实例:").append(clazz).append("->").append(null == object ? null : object.toString());
		sb.append("函数:").append(null == executeMethod ? null : executeMethod.getName());
		sb.append("函数名:").append(methodName);
		sb.append("入参:").append(null == params ? null : Arrays.toString(params));
		sb.append("使用连接:").append(null == connections ? null : connections.toString());
		
		return sb.toString();
	}
}