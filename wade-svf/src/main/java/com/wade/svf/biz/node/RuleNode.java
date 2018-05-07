/**
 * $
 */
package com.wade.svf.biz.node;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.config.IFlowConfig;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: RuleNode.java
 * @description: 规则节点
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2016-11-15
 */
public class RuleNode extends ServiceNode {

	
	/**
	 * 规则节点
	 * @param flow
	 * @param name
	 * @param callback
	 * @param next
	 */
	public RuleNode(IFlow<ServiceRequest, ServiceResponse> flow, String name, String callback, String next) {
		super(flow, name, IFlowConfig.CFG_RULE_NAME, callback, next);
	}


}
