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
 * @className: EndNode.java
 * @description: TODO
 * 
 * @version: $ Id $
 * @author: liaosheng
 * @date: 2016-11-15
 */
public class EndNode extends AbstractNode {

	
	/**
	 * 结束节点
	 * @param flow
	 * @param name
	 * @param callback
	 * @param next
	 */
	public EndNode(IFlow<ServiceRequest, ServiceResponse> flow, String name, String callback, String next) {
		super(flow, name, IFlowConfig.CFG_END_NAME, callback, next);
	}

	
	@Override
	public ServiceResponse execute(ServiceRequest request) throws Exception {
		return new ServiceResponse();
	}

	
	/**
	 * 1、效验输出参数；
	 * 2、替换内置变量；
	 * 3、添加到响应对象
	 */
	@Override
	public void executeAfter(ServiceRequest request, ServiceResponse response) throws FlowException {
		IFlowConfig config = getFlow().getConfig();
		for (Map.Entry<String, NodeParam> item : config.getOutParam(getName()).entrySet()) {
			String key = item.getKey();
			
			// 内置变量替换
			NodeParam param = item.getValue();
			boolean parsed = parseValue(param);
			
			// 参数效验
			String className = param.getInspector();
			IParamInspector inspect = ParamInspectorFactory.getInstance().get(className);
			if (null != inspect) {
				if (!inspect.inspect(this, param)) {
					throw new FlowException(FlowErr.flow10009.getCode(), FlowErr.flow10009.getInfo(getFlow().getName(), getName(), param.getInspectMessage()));
				}
			}
			
			// 添加到响应对象
			if (parsed)
				response.getBody().put(key, param.getValue());
		}
		
		// 添加到流程上下文
		FlowContext context = FlowContext.getContext();
		context.getOutParam(getName()).putAll(response.getBody());
	}
	
}
