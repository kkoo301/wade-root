/**
 * $
 */
package com.wade.dsf.executor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.wade.dsf.config.DsfConfigure;
import com.wade.dsf.exception.DsfErr;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: ThreadPoolExecutor.java
 * @description: 通过线程池控制最大并发量，以及超时控制
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-19
 */
public final class DsfExecutor implements IDsfExecutor {
	
	private static final Logger log = Logger.getLogger(DsfExecutor.class);
	
	private static final DsfConfigure configure = DsfConfigure.getInstance();
	
	/**
	 * 默认最大线程数
	 */
	private static final int MAX_POOL_SIZE = 100;
	/**
	 * 默认服务超时阈值，10秒
	 */
	private static final int MAX_TIMEOUT = 10000;
	/**
	 * 服务超时后，等待事务提交的最大阈值
	 */
	private static final int MAX_WAIT = 2000;
	/**
	 * 线程池
	 */
	private static ExecutorService executor = null;
	/**
	 * 默认的线程工厂类
	 */
	private ThreadFactory threadFactory = new DsfThreadFactory();
	
	private int timeout = MAX_TIMEOUT;
	
	private int poolsize = MAX_POOL_SIZE;
	
	private int maxwait = MAX_WAIT;
	
	private boolean debug = false;
	
	public DsfExecutor() throws DsfException {
		String factory = configure.getProperty("//executor/factory");
		if (null != factory && factory.length() > 0) {
			try {
				threadFactory = (ThreadFactory) Class.forName(factory).newInstance();
			} catch (Exception e) {
				throw new DsfException(DsfErr.dsf10015.getCode(), DsfErr.dsf10015.getInfo(factory), e);
			}
		}
		
		String poolsize = configure.getProperty("//executor/poolsize");
		if (null != poolsize && poolsize.length() > 0) {
			this.poolsize = Integer.parseInt(poolsize);
		}
		
		String timeout = configure.getProperty("//executor/timeout");
		if (null != timeout && timeout.length() > 0) {
			this.timeout = Integer.parseInt(timeout);
		} else {
			this.timeout = MAX_TIMEOUT;
		}
		
		String maxwait = configure.getProperty("//executor/maxwait");
		if (null != maxwait && maxwait.length() > 0) {
			this.maxwait = Integer.parseInt(maxwait);
		} else {
			this.maxwait = MAX_WAIT;
		}
		
		
		String debug = configure.getProperty("//executor/debug");
		if (null != debug && debug.length() > 0) {
			this.debug = "true".equals(debug);
		} else {
			this.debug = false;
		}
		
		executor = Executors.newFixedThreadPool(this.poolsize, threadFactory);
		
		if (log.isInfoEnabled()) {
			log.info(String.format("启动服务线程池:最大线程数%d,线程实现类%s,等待事务提交时间%dms,默认线程耗时%dms,异常调试%s", 
					this.poolsize, 
					this.threadFactory.getClass().getName(), 
					this.maxwait, 
					this.timeout, 
					String.valueOf(this.debug)));
		}
	}
	
	/**
	 * 启动子线程执行请求逻辑，当子线程执行超时，处理逻辑如下：<br>
	 * 1、如果事务已提交，则主线程等待MAX_WAIT毫秒后，再判断子线程是否执行完成<br>
	 * a、若未完成则抛出强制中止异常，但事务存在不一致<br>
	 * b、若已完成则返回子线程结果<br>
	 * 2、如果事务未提交，则修改子线程状态为已超时
	 */
	@Override
	public DsfResponse execute(DsfRequest request) throws Exception {
		Future<DsfResponse> future = null;
		
		IDsfCallable callable = new DsfCallable();
		callable.setRequest(request);
		
		try {
			future = executor.submit(callable);
			DsfResponse response = future.get(timeout, TimeUnit.SECONDS);
			return response;
		} catch (TimeoutException e) {
			if (callable.isFinished()) {
				return callable.getResponse();
			} else {
				Thread.sleep(maxwait);
				if (callable.isFinished()) {
					return callable.getResponse();
				} else {
					callable.setTimeout(true);
					throw e;
				}
			}
		} catch (InterruptedException e) {
			throw e;
		} catch (ExecutionException e) {
			if (debug) {
				log.error(e.getMessage(), e);
			}
			if (null != e.getCause().getCause())
				throw (Exception)(e.getCause().getCause());
			else
				throw new Exception(e.getCause());
		} catch (RejectedExecutionException e) {
			throw new DsfException(DsfErr.dsf10020.getCode(), DsfErr.dsf10020.getInfo(String.valueOf(this.poolsize)));
		}
	}
}
