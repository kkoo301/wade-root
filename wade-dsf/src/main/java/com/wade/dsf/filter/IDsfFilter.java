/**
 * $
 */
package com.wade.dsf.filter;

import com.wade.dsf.exception.DsfException;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfFilter.java
 * @description: 服务调用的过滤器接口定义
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public interface IDsfFilter {

	/**
	 * 过滤器
	 * @param request
	 * @param response
	 * @param chain
	 * @throws DsfException
	 */
	public void doFilter(DsfRequest request, DsfResponse response, IDsfFilterChain chain) throws DsfException;
	
}
