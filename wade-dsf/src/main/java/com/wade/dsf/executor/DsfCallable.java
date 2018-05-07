/**
 * $
 */
package com.wade.dsf.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.dsf.config.DsfConfigure;
import com.wade.dsf.exception.DsfErr;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.executor.invoker.IDsfInvoker;
import com.wade.dsf.registry.entity.IDsfEntity;
import com.wade.dsf.registry.entity.IDsfEntityManager;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfCallable.java
 * @description: 子线程执行对象
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-20
 */
public class DsfCallable implements IDsfCallable {
	
	private static final Logger log = LoggerFactory.getLogger(DsfCallable.class);
	
	/**
	 * Dsf全局配置对象
	 */
	private static final DsfConfigure configure = DsfConfigure.getInstance();
	/**
	 * 服务实体管理器
	 */
	private static final IDsfEntityManager manager = configure.getEntityManager();
	
	
	/**
	 * 服务方法反射类
	 */
	private static final IDsfInvoker invoker = configure.getInvoker();
	
	/**
	 * 请求对象
	 */
	private DsfRequest request;
	/**
	 * 响应对象
	 */
	private DsfResponse response = new DsfResponse();
	
	/**
	 * 标识是否执行完成
	 */
	private boolean isFinished = false;
	
	/**
	 * 事务控制器
	 */
	private IDsfTransaction transaction = null;
	
	/**
	 * 每次都需要创建新的事务控制对象
	 */
	public DsfCallable() {
		
	}
	
	public boolean isStartCommit() {
		return this.transaction.isStartCommit();
	}
	
	@Override
	public DsfResponse call() throws Exception {
		String name = null;
		long start = System.currentTimeMillis();
		try {
			
			this.transaction = configure.getTranscationManager().startTranscation();

			name = request.getServiceName();
			
			IDsfEntity entity = manager.getEntity(name);
			response = invoker.invoke(entity, request);
			
			long cost = System.currentTimeMillis() - start;
			if (isTimeout()) {
				log.error(String.format("服务已超时, %s, %dms", name, cost));
				throw new DsfException(DsfErr.dsf10021.getCode(), DsfErr.dsf10021.getInfo(name, cost));
			}
			
			if (response.isOk())
				this.transaction.commit();
			else {
				this.transaction.rollback();
				
				log.error("服务{}调用异常", name, (Exception) response.getResponse());
			}
		} catch (Exception e) {
			
			log.error("服务{}调用异常", name, e);
			
			response.setError();
			response.setResponse(e);
			try {
				this.transaction.rollback();
			} catch (Exception e2) {
				response.setResponse(e2);
			}
		} finally {
			this.isFinished = true;
			this.transaction.close();
			this.transaction = null;
		}
		
		return response;
	}
	
	@Override
	public IDsfTransaction getTransation() {
		return this.transaction;
	}
	
	@Override
	public boolean isFinished() {
		return this.isFinished;
	}
	
	@Override
	public boolean isTimeout() {
		return this.transaction.isTimeout();
	}
	
	@Override
	public void setTimeout(boolean isTimeout) {
		this.transaction.setTimeout(true);
	}
	
	@Override
	public DsfRequest getRequest() {
		return request;
	}
	
	@Override
	public void setRequest(DsfRequest request) {
		this.request = request;
	}
	
	@Override
	public DsfResponse getResponse() {
		return response;
	}
	
	@Override
	public void setResponse(DsfResponse response) {
		this.response = response;
	}
}
