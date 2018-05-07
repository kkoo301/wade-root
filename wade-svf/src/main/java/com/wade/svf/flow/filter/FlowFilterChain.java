/**
 * $
 */
package com.wade.svf.flow.filter;

import com.wade.svf.flow.FlowConfigure;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.exception.FlowException;

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
public class FlowFilterChain {
	
	private static IFlowFilter[] filters = FlowConfigure.getFilters();
	
	/**
	 * 用来迭代Filter的游标
	 */
	private int filterIndex = 0;
	
	
	public FlowFilterChain() {
		
	}
	
	public void doFilter(FlowContext context) throws FlowException {
		if (filterIndex < filters.length) {
			IFlowFilter filter = filters[filterIndex];
			
			filterIndex ++;
			
			filter.doFilter(context, this);
		}
	}
	
}
