/**
 * $
 */
package com.wade.svf.flow.filter;

import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.exception.FlowException;


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
public interface IFlowFilter {

	/**
	 * 过滤器
	 * @param request
	 * @param response
	 * @param chain
	 * @throws DsfException
	 */
	public void doFilter(FlowContext context, FlowFilterChain chain) throws FlowException;
	
}
