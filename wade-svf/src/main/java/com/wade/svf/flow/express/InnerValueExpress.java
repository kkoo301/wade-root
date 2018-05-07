/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年8月1日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.express;

import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.NodeParam;

/**
 * @description
 * 处理流程内置变量
 */
public class InnerValueExpress implements IValueExpress {

	/**
	 * 处理{flow.start.time}
	 * 处理{flow.end.time}
	 * 处理{flow.cost.time}
	 * 处理{flow.name}
	 * 处理{flow.thread}
	 * 处理{flow.session}
	 */
	@Override
	public boolean getValue(NodeParam node, String config) throws FlowException {
		FlowContext context = FlowContext.getContext();
		
		// 处理{flow.start.time}
		if (FlowContext.FLOW_START_TIME.equals(config)) {
			node.setValue(String.valueOf(context.getStartTime()));
			return true;
		}
		
		// 处理{flow.end.time}
		if (FlowContext.FLOW_END_TIME.equals(config)) {
			node.setValue(String.valueOf(System.currentTimeMillis()));
			return true;
		}
		
		// 处理{flow.cost.time}
		if (FlowContext.FLOW_COST_TIME.equals(config)) {
			node.setValue(String.valueOf(context.getCostTime()));
			return true;
		}
		
		// 处理{flow.name}
		if (FlowContext.FLOW_NAME.equals(config)) {
			node.setValue(context.getFlow().getName());
			return true;
		}
		
		// 处理{flow.thread}
		if (FlowContext.FLOW_THREAD.equals(config)) {
			node.setValue(context.getThread());
			return true;
		}
		
		// 处理{flow.session}
		if (FlowContext.FLOW_SESSION.equals(config)) {
			node.setValue(context.getSessionId());
			return true;
		}
		return false;
	}

}
