/**
 * $
 */
package com.wade.svf.biz.node;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.Node;
import com.wade.svf.flow.node.NodeParam;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: SwitchNode.java
 * @description: 分支节点
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2016-11-15
 */
public class SwitchNode extends AbstractNode {
	
	private static final Logger log = LoggerFactory.getLogger(ServiceNode.class);
	
	private String caseNext;
	
	/**
	 * 分支节点
	 * @param flow
	 * @param name
	 * @param callback
	 * @param next
	 */
	public SwitchNode(IFlow<ServiceRequest, ServiceResponse> flow, String name, String callback, String next) {
		super(flow, name, IFlowConfig.CFG_SWITCH_NAME, callback, next);
	}

	
	/**
	 * 以switch的第一个inparam的Key为判断值，依次匹配所有的case条件，所有case都不匹配时，next指向end节点
	 */
	@Override
	public ServiceResponse execute(ServiceRequest request) throws Exception {
		Map<String, NodeParam> inparams = getFlow().getConfig().getInParam(getName());
		if (null == inparams || inparams.isEmpty()) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), getName() + "分支节点未配置输入参数!"));
		}
		
		if (inparams.size() > 2) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), getName() + "分支节点输入参数不能配置多个!"));
		}
		
		// 获取第一个输入参数，并处理内部变量
		Iterator<String> iter = inparams.keySet().iterator();
		String key = iter.next();
		NodeParam param = inparams.get(key);
		parseValue(param);
		
		Object obj = param.getValue();
		if (null == obj) {
			throw new FlowException(FlowErr.flow10010.getCode(), FlowErr.flow10010.getInfo(getFlow().getName(), getName() + "分支节点执行异常，输入参数值为空"));
		}
		
		if (obj instanceof String) {
			String value = (String) obj;
			
			if (log.isDebugEnabled()) 
				log.debug("流程{}分支节点{}判断参数{}={}", new String[] {getFlow().getName(), getName(), key, value});
			
			List<Map<String, String>> items = getFlow().getConfig().getSwitchCase(getName());
			int index = 0;
			for (Map<String, String> item : items) {
				
				NodeParam caseparam = new NodeParam();
				caseparam.setKey("case");
				caseparam.setValue(item.get(IFlowConfig.CFG_SWITCH_CASE_VALUE));
				parseValue(caseparam);
				
				Object temp = caseparam.getValue();
				String caseValue = null;
				if (temp instanceof String) {
					caseValue = (String) temp;
				}
				
				if (null == caseValue) {
					caseValue = "";
				}
				
				if (log.isDebugEnabled())
					log.debug("流程{}分支节点{}开始匹配第{}个case，case值为:{}", new String[] {getFlow().getName(), getName(), String.valueOf(index), caseValue});
				
				if (caseValue.equals(value)) {
					caseNext = item.get(IFlowConfig.CFG_SWITCH_CASE_NEXT);
					return new ServiceResponse();
				}
				
				index ++;
			}
			
			caseNext = "end";
			
			return new ServiceResponse();
		} else {
			throw new FlowException(FlowErr.flow10010.getCode(), FlowErr.flow10010.getInfo(getFlow().getName(), getName() + "分支节点执行异常，输入参数值必须是字符串"));
		}
		
	}
	
	
	@Override
	public Node<ServiceRequest, ServiceResponse> getNext() {
		if (null == caseNext || caseNext.length() == 0)
			return null;
		return getFlow().getNode(caseNext);
	}

}
