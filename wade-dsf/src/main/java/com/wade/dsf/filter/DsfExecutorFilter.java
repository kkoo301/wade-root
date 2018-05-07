/**
 * $
 */
package com.wade.dsf.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.dsf.config.DsfConfigure;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.executor.IDsfExecutor;
import com.wade.dsf.filter.IDsfFilter;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;
import com.wade.dsf.server.DsfContext;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: ServiceInfoFilter.java
 * @description: 服务调用
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public class DsfExecutorFilter implements IDsfFilter {
	
	private static final Logger log = LoggerFactory.getLogger(DsfExecutorFilter.class);
	
	
	/**
	 * 根据请求的序列化类型来调用对应的Invoker
	 */
	@Override
	public void doFilter(DsfRequest request, DsfResponse response, IDsfFilterChain chain) throws DsfException {
		long start = System.currentTimeMillis();
		boolean success = false;
		
		DsfConfigure configure = DsfConfigure.getInstance();
		
		try {
			IDsfExecutor executor = configure.getExecutor();
			
			log.debug(String.format("开始服务调用:%s,请求类型:%s", request.getServiceName(), request.getRequest().getClass().getName()));
			
			response = executor.execute(request);
			success = true;
		} catch (Exception e) {
			response = new DsfResponse();
			response.setError();
			response.setResponse(e);
			
			log.error("服务执行异常{}", request.getServiceName(), e);
		} finally {
			long cost = System.currentTimeMillis() - start;
			if (log.isDebugEnabled()) {
				log.debug(String.format("结束服务调用:%s, 成功标识:%s, 耗时:%dms", request.getServiceName(), success, cost));
			}
			
			DsfContext context = DsfContext.getContext();
			context.setResponse(response);
		}
	}

}
