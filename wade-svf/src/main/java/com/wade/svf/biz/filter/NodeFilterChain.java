/**
 * $
 */
package com.wade.svf.biz.filter;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.Node;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: FlowFilterChain.java
 * @description: 流程执行过滤器链
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public class NodeFilterChain {
	
	private static List<INodeFilter> filters = new ArrayList<INodeFilter>(5);
	
	/**
	 * 用来迭代Filters的游标
	 */
	private int filterIndex = 0;
	
	
	public NodeFilterChain() {
		
	}
	
	
	/**
	 * Node过滤器
	 * @param context
	 * @throws FlowException
	 */
	public void doFilter(FlowContext context, Node<ServiceRequest, ServiceResponse> node) throws FlowException {
		if (filterIndex < filters.size()) {
			INodeFilter filter = filters.get(filterIndex);
			
			filterIndex ++;
			
			filter.doFilter(context, node, this);
		}
	}
	
	
	static {
		filters.add(new TraceNodeFilter());
		filters.add(new NodeExecutorFilter());
	}
}
