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
 * @className: EventNode.java
 * @description: 事件节点
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2016-11-15
 */
public class EventNode extends ServiceNode {

	
	/**
	 * 规则节点
	 * @param flow
	 * @param name
	 * @param callback
	 * @param next
	 */
	public EventNode(IFlow<ServiceRequest, ServiceResponse> flow, String name, String callback, String next) {
		super(flow, name, IFlowConfig.CFG_EVENT_NAME, callback, next);
	}


}
