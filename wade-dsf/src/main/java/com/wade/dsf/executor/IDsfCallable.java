/**
 * $
 */
package com.wade.dsf.executor;

import java.util.concurrent.Callable;

import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDsfCallable.java
 * @description: 服务执行线程启动的Callable对象
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-19
 */
public interface IDsfCallable extends Callable<DsfResponse> {
	
	/**
	 * 标识是否执行完成
	 * @return
	 */
	public boolean isFinished();
	
	/**
	 * 获取事务对象
	 * @return
	 */
	public IDsfTransaction getTransation();
	
	/**
	 * 判断是否已经超时
	 * @return
	 */
	public boolean isTimeout();
	
	/**
	 * 设置超时标识
	 * @param isTimeout
	 */
	public void setTimeout(boolean isTimeout);
	
	/**
	 * @return the request
	 */
	public DsfRequest getRequest();
	
	/**
	 * @param request the request to set
	 */
	public void setRequest(DsfRequest request);
	
	/**
	 * @return the response
	 */
	public DsfResponse getResponse();
	
	/**
	 * @param response the response to set
	 */
	public void setResponse(DsfResponse response);

}
