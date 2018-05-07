/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年5月19日
 * 
 * Just Do IT.
 */
package com.wade.dsf.filter;

import com.wade.dsf.exception.DsfException;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * @description
 * 过滤器链
 */
public interface IDsfFilterChain {

	/**
	 * 执行过滤器
	 * @param request
	 * @param response
	 * @throws DsfException
	 */
	public void doFilter(DsfRequest request, DsfResponse response) throws DsfException;
}
