/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月22日
 * 
 * Just Do IT.
 */
package com.wade.svf.biz.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.Node;

/**
 * @description
 * 节点执行trace过滤器
 */
public class TraceNodeFilter implements INodeFilter {

	private static final Logger log = LoggerFactory.getLogger(TraceNodeFilter.class);
	
	@Override
	public void doFilter(FlowContext context, Node<ServiceRequest, ServiceResponse> node, NodeFilterChain chain) throws FlowException {
		long start = System.currentTimeMillis();
		
		try {
			if (log.isDebugEnabled())
				log.debug("开始跟踪流程{}的节点{}", new String[] {context.getFlow().getName(), node.getName()});
			
			chain.doFilter(context, node);
		} finally {
			if (log.isDebugEnabled()) {
				long cost = System.currentTimeMillis() - start;
				log.debug("结束跟踪流程{}的节点{},总耗时{}毫秒", new String[] {context.getFlow().getName(), node.getName(), String.valueOf(cost)});
			}
		}
	}

}
