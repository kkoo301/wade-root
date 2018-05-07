/**
 * $
 */
package com.wade.svf.flow.filter;

import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: FlowExecutorFliter.java
 * @description: 流程执行的过滤器
 * 
 * @version: $ Id $
 * @author: liaosheng
 * @date: 2016-11-15
 */
public class FlowExecutorFliter implements IFlowFilter {
	
	@Override
	public void doFilter(FlowContext context, FlowFilterChain chain) throws FlowException {
		IFlow<?, ?> flow = context.getFlow();
		
		try {
			flow.execute(context);
		} catch (FlowException e) {
			throw e;
		} catch (Exception e) {
			throw new FlowException(FlowErr.flow10001.getCode(), FlowErr.flow10001.getInfo(flow.getName()), e);
		}
		
		chain.doFilter(context);
	}

}
