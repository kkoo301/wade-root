/**
 * $
 */
package com.wade.svf.flow.executor;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.express.InnerValueExpress;
import com.wade.svf.flow.express.ReferExpress;
import com.wade.svf.flow.express.ValueExpressFactory;
import com.wade.svf.flow.filter.FlowFilterChain;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: FlowExecutor.java
 * @description: 流程执行的入口类
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2016-11-15
 */
public final class FlowExecutor implements IFlowExecutor {
	
	private static final Logger log = LoggerFactory.getLogger(FlowExecutor.class);
	
	@Override
	public Map<String, Object> execute(IFlow<?,?> flow, Map<String, Object> request) throws FlowException {
		FlowContext context = null;
		
		try {
			long start = System.currentTimeMillis();
			String name = flow.getName();
			
			// 创建上下文对象
			context = FlowContext.newContext(flow);
			context.getInitParam().putAll(request);
			
			// 初始化值表达式
			String valueExpress = InnerValueExpress.class.getName();
			ValueExpressFactory.getFactory().createExpress(FlowContext.FLOW_START_TIME, valueExpress);
			ValueExpressFactory.getFactory().createExpress(FlowContext.FLOW_END_TIME, valueExpress);
			ValueExpressFactory.getFactory().createExpress(FlowContext.FLOW_COST_TIME, valueExpress);
			ValueExpressFactory.getFactory().createExpress(FlowContext.FLOW_NAME, valueExpress);
			ValueExpressFactory.getFactory().createExpress(FlowContext.FLOW_THREAD, valueExpress);
			ValueExpressFactory.getFactory().createExpress(FlowContext.FLOW_SESSION, valueExpress);
			
			// 初始化配置引用表达式
			ValueExpressFactory.getFactory().createExpress(IFlowConfig.CFG_REF_TAG, ReferExpress.class.getName());
			
			// 创建并执行过滤器链
			FlowFilterChain chain = new FlowFilterChain();
			chain.doFilter(context);
			
			if (log.isDebugEnabled()) {
				long cost = System.currentTimeMillis() - start;
				log.debug("流程{}执行完成，调用链{}，总耗时{}毫秒", new String[] {name, context.getLink(), String.valueOf(cost)});
			}
			
			Map<String, Object> response = new HashMap<String, Object>();
			response.put("head", context.getInParam(IFlowConfig.CFG_START_NAME));
			response.put("data", context.getOutParam(IFlowConfig.CFG_END_NAME));
			return response;
		} finally {
			if (null != context) {
				context.clear();
				context = null;
			}
		}
	}
}
