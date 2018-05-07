/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.service.session.app;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.config.SystemCfg;
import com.ailk.common.data.IVisit;
import com.ailk.common.util.Utility;

/**
 * 主服务调用
 * 
 * @className: AppInvoker.java
 * @author: liaosheng
 * @date: 2014-3-27
 */
public final class AppInvoker {
	private static final Logger log = Logger.getLogger(AppInvoker.class);
	
	private static final int maximumPoolSize = SystemCfg.serviceInvokeMaxsize;
	private static ThreadFactory threadFactory = new AppThreadFactory();
	
	private static ExecutorService executor = Executors.newFixedThreadPool(maximumPoolSize, threadFactory);
	
	private static final String DEFAULT_TIMEOUT = SystemCfg.serviceInvokeTimeout;
	
	private AppInvoker() {
		
	}
	
	public static final void shutdown() {
		executor.shutdown();
	}
	
	public static final <T extends Object>T invoke(IVisit context, Object service, String methodName, Object[] params, long timeout) throws Exception {
		AppCallable<T> callable = new AppCallable<T>();
		
		callable.setContext(context);
		callable.setObject(service);
		callable.setMethodName(methodName);
		callable.setParams(params);
		
		return invokeService(callable, service.getClass().getName(), methodName, timeout);
	}
	
	
	public static final <T extends Object>T invoke(IVisit context, Object service, Method method, Object[] params, long timeout) throws Exception {
		AppCallable<T> callable = new AppCallable<T>();
		
		String methodName = method.getName();
		
		callable.setContext(context);
		callable.setObject(service);
		callable.setMethod(method);
		callable.setMethodName(methodName);
		callable.setParams(params);
		
		T t = null;
		
		try {
			t = invokeService(callable, service.getClass().getName(), methodName, timeout);
		} catch (ExecutionException e) {
			
			Throwable sqlEx = null;
			Throwable throwable = e;
			
			while(throwable.getCause() != null){
				throwable = throwable.getCause();
				if(throwable instanceof SQLException){
					sqlEx = throwable;
					break;
				}
			}

			if(sqlEx != null){
				String wadeServerName = System.getProperty("wade.server.name", "");
				String errmsg = e.getMessage();
				String msg = "";
				long time = System.currentTimeMillis();
				log.error(msg, e);
				
				//处理数据库SQL超时的异常
				if (null != errmsg && errmsg.indexOf("ORA-01013") > 0) {
					msg = "数据库超时请尝试重新办理业务!" + wadeServerName;
				} else {
					msg = "数据库操作异常请尝试重新办理业务!" + wadeServerName;
				}
				
				if (SystemCfg.isThrowSQLError) {
					throw new SQLException(msg + time, e);
				} else {
					throw new BaseException("DB1001", null, msg + time);
				}
			}else{
				throw e;
			}
		}catch(Exception e){
			throw e;
		}
		
		return t;
	}
	
	/**
	 * 
	 * @param callable
	 * @param serviceName
	 * @param methodName
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	private static final <T extends Object>T invokeService(AppCallable<T> callable, String serviceName, String methodName, long timeout) throws Exception {
		Future<T> future = null;
		
		if (timeout < 0) {
			throw new Exception(String.format("服务[%s]调用超时或系统开户自动保护", serviceName));
		}
		
		if (timeout == 0) {
			timeout = Long.parseLong(DEFAULT_TIMEOUT);
		}
		
		callable.setTimeout(timeout);
		
		try {
			future = executor.submit(callable);
			return future.get(timeout, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			
			try {
				// 如果事务已开始提交，则不触发cancel尝试干扰线程，直接等待执行完成后返回，否则callable.stop()
				if (!callable.isCommitted()) {
					callable.stop();
				} else {
					if (callable.isCommitted()) {
						// 等待子线程10秒，然后判断是否执行完成，未完成则异常退出
						Thread.sleep(10000);
						
						if (callable.isFinished()) {
							if (log.isInfoEnabled()) {
								log.info(String.format("TIMEOUT>>FIN:服务超时且事务已提交，线程执行完成，已耗时 %d ms, 当前状态%s", 10000, callable.toString()));
							}
							return callable.getResult();
						} else {
							log.error(String.format("TIMEOUT>>WAIT:服务超时且事务已提交，等待线程执行完成，已耗时 %d ms, 当前状态%s", 10000, callable.toString()));
							
							String message = String.format("服务调用[%s]超时[%d]秒，部分事务已提交且无法回滚", serviceName, timeout);
							log.error(message, e);
							throw new Exception(message);
						}
					}
				}
				
			} catch (Exception ex) {
				String message = String.format("服务调用[%s]超时[%d]秒", serviceName, timeout);
				log.error(message, ex);
				throw new Exception(message);
			} finally {
				try {
					//解决两个conn执行同一条Insert语句导致的锁
					callable.destroy();
					
					future.cancel(true);
				} catch (Exception ex) {
					String message = String.format("服务调用[%s]超时[%d]秒", serviceName, timeout);
					log.error(message, ex);
					throw new Exception(message);
				}
			}
			
			String message = String.format("服务调用[%s]超时[%d]秒", serviceName, timeout);
			log.error(message, e);
			throw new Exception(message);
		} catch (InterruptedException e) {
			String message = String.format("服务调用[%s]被中断", serviceName);
			log.error(message, e);
			throw e;
		} catch (ExecutionException e) {
			String message = String.format("服务调用[%s]执行异常", serviceName);
			log.error(message, e);
			
			Throwable t = Utility.getBottomException(e);
			if (t instanceof BaseException) {
				throw (BaseException)t;
			} else {
				throw e;
			}
		} catch (RejectedExecutionException e) {
			String message = String.format("服务调用[%s]执行异常", serviceName);
			log.error(message, e);
			throw new Exception(String.format("服务线程繁忙，请调整并发线程数[%d]", maximumPoolSize), e);
		} finally {
			future = null;
			callable = null;
		}
	}
	

	/**
	 * 返回方法反射的对象
	 * @param context 上下文对象
	 * @param service Class实例
	 * @param methodName 方法名
	 * @param params 方法入参
	 * @return
	 */
	public static final <T extends Object>T invoke(IVisit context, Object service, String methodName, Object[] params) throws Exception {
		AppCallable<T> callable = new AppCallable<T>();
		
		callable.setContext(context);
		callable.setObject(service);
		callable.setMethodName(methodName);
		callable.setParams(params);
		
		return invokeService(callable, service.getClass().getName(), methodName, Long.parseLong(DEFAULT_TIMEOUT));
	}
}
