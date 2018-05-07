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

import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.filter.FlowFilterChain;
import com.wade.svf.flow.filter.IFlowFilter;

/**
 * @description
 * 跟踪流程的过滤器，用来记录日志信息
 */
public class TraceFlowFilter implements IFlowFilter {
	
	
	private static final Logger log = LoggerFactory.getLogger(TraceFlowFilter.class);

	
	@Override
	public void doFilter(FlowContext context, FlowFilterChain chain) throws FlowException {
		long start = System.currentTimeMillis();
		
		try {
			if (log.isDebugEnabled())
				log.debug("开始跟踪流程{}", new String[] {context.getFlow().getName()});
			
			chain.doFilter(context);
		} finally {
			if (log.isDebugEnabled()) {
				long cost = System.currentTimeMillis() - start;
				log.debug("结束跟踪流程{},总耗时{}", new String[] {context.getFlow().getName(), String.valueOf(cost)});
			}
		}
	}

}
