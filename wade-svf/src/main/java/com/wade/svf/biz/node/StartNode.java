/**
 * $
 */
package com.wade.svf.biz.node;

import java.util.Map;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.IParamInspector;
import com.wade.svf.flow.node.NodeParam;
import com.wade.svf.flow.node.ParamInspectorFactory;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: StartNode.java
 * @description: 系统自带开始节点
 * 
 * @version: $ Id $
 * @author: liaosheng
 * @date: 2016-11-15
 */
public class StartNode extends AbstractNode {

	/**
	 * 开始节点
	 * @param flow
	 * @param name
	 * @param callback
	 * @param next
	 */
	public StartNode(IFlow<ServiceRequest, ServiceResponse> flow, String name, String callback, String next) {
		super(flow, name, IFlowConfig.CFG_START_NAME, callback, next);
	}

	
	/**
	 * 流程输入参数合法性效验
	 */
	@Override
	public void executeBefore(ServiceRequest request) throws FlowException {
		FlowContext context = FlowContext.getContext();
		
		// 处理流程配置输入参数里的变量，并效验参数类型
		IFlowConfig config = getFlow().getConfig();
		for (Map.Entry<String, NodeParam> item : config.getInParam(getName()).entrySet()) {
			// 内置变量替换
			NodeParam param = item.getValue();
			parseValue(param);
			
			// 参数效验
			String className = param.getInspector();
			IParamInspector inspect = ParamInspectorFactory.getInstance().get(className);
			if (null != inspect) {
				if (!inspect.inspect(this, param)) {
					throw new FlowException(FlowErr.flow10008.getCode(), FlowErr.flow10008.getInfo(getFlow().getName(), getName(), param.getInspectMessage()));
				}
			}
			
			context.getInitParam().put(item.getKey(), param.getValue());
		}
	}
	
	
	@Override
	public ServiceResponse execute(ServiceRequest request) throws Exception {
		return new ServiceResponse();
	}
	
}
