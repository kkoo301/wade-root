/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月22日
 * 
 * Just Do IT.
 */
package com.wade.svf.biz.filter;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.Node;

/**
 * @description
 * 节点执行过滤器
 */
public interface INodeFilter {

	
	/**
	 * 节点执行过滤器
	 * @param context
	 * @param node
	 * @param chain
	 * @throws FlowException
	 */
	public void doFilter(FlowContext context, Node<ServiceRequest, ServiceResponse> node, NodeFilterChain chain) throws FlowException;
}
