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
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.NodeParam;

/**
 * @description
 * 配置引用
 */
public class ReferExpress implements IValueExpress {

	/**
	 * 处理@context:
	 * 处理@nodename:inparam.xxxx
	 * 处理@nodename:outparam.xxxx
	 */
	@Override
	public boolean getValue(NodeParam node, String config) throws FlowException {
		FlowContext context = FlowContext.getContext();
		
		String flowName = context.getFlow().getName();
		String nodeName = node.getNodeName();
		
		if (config.startsWith(IFlowConfig.CFG_REF_TAG)) {
			int paramIdx = config.indexOf(IFlowConfig.CFG_PARAM_TAG);
			if (paramIdx == -1) {
				throw new FlowException(FlowErr.flow10003.getCode(), FlowErr.flow10003.getInfo(flowName, nodeName, config, "找不到参数类型分隔符"));
			}
			
			String refNodeName = config.substring(1, paramIdx);
			
			// 处理@context:
			if (IFlowConfig.CFG_NODE_CONTEXT.equals(refNodeName)) {
				String key = config.substring(paramIdx + 1);
				
				// 针对start节点特殊处理
				if (IFlowConfig.CFG_START_NAME.equals(nodeName)) {
					if (!context.getInitParam().containsKey(key)) {
						throw new FlowException(FlowErr.flow10008.getCode(), FlowErr.flow10008.getInfo(flowName, nodeName, "请求参数未指定" + key));
					}
				}
				
				node.setValue(context.getInitParam().get(key));
				return true;
			}
			
			int keyIdx = config.indexOf(IFlowConfig.CFG_KEY_TAG, paramIdx);
			if (keyIdx == -1) {
				throw new FlowException(FlowErr.flow10004.getCode(), FlowErr.flow10004.getInfo(flowName, nodeName, "找不到参数名分隔符，" + config));
			}
			
			
			String refParamType = config.substring(paramIdx + 1, keyIdx);
			String refParamKey = config.substring(keyIdx + 1);
			
			// 处理@nodename:inparam.xxxx
			if (IFlowConfig.CFG_NODE_INPARAM.equals(refParamType)) {
				if (context.getInParam(refNodeName).containsKey(refParamKey)) {
					Object temp = context.getInParam(refNodeName).get(refParamKey);
					node.setValue(temp);
					return true;
				} else {
					return false;
				}
			}
			
			// 处理@nodename:outparam.xxxx
			else if (IFlowConfig.CFG_NODE_OUTPARAM.equals(refParamType)) {
				if (context.getOutParam(refNodeName).containsKey(refParamKey)) {
					Object temp = context.getOutParam(refNodeName).get(refParamKey);
					node.setValue(temp);
					return true;
				} else {
					return false;
				}
			} else {
				throw new FlowException(FlowErr.flow10005.getCode(), FlowErr.flow10005.getInfo(flowName, nodeName, refParamType, config));
			}
		
		}
		
		return false;
	}

}
