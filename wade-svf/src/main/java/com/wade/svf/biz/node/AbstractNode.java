/**
 * $
 */
package com.wade.svf.biz.node;

import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.express.IValueExpress;
import com.wade.svf.flow.express.ValueExpressFactory;
import com.wade.svf.flow.node.Node;
import com.wade.svf.flow.node.NodeParam;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: AbstractNode.java
 * @description: 抽象流程服务节点对象
 * 
 * @version: $ Id $
 * @author: liaosheng
 * @date: 2016-11-15
 */
public abstract class AbstractNode implements Node<ServiceRequest, ServiceResponse> {
	
	public static final String _response = "response";
	public static final String _request = "request";
	
	/**
	 * 当前流程对象
	 */
	private IFlow<ServiceRequest, ServiceResponse> flow = null;
	
	/**
	 * 输入参数
	 */
	private Map<String, String> inParams = new HashMap<String, String>(10);
	
	/**
	 * 输出参数
	 */
	private Map<String, String> outParams = new HashMap<String, String>(10);
	
	/**
	 * 节点名＝服务名
	 */
	private String name;
	
	/**
	 * 结果集编码
	 */
	private int resultCode;
	
	/**
	 * 返销节点名称
	 */
	private String callback;
	
	/**
	 * 下个节点
	 */
	private String next;
	
	/**
	 * 节点类型
	 */
	private String type;
	
	
	/**
	 * 节点对象
	 * @param flow
	 * @param name
	 * @param callback
	 * @param next
	 */
	public AbstractNode(IFlow<ServiceRequest, ServiceResponse> flow, String name, String type, String callback, String next) {
		this.flow = flow;
		this.name = name;
		this.type = type;
		this.callback = callback;
		this.next = next;
	}
	
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public Map<String, String> getInParams() {
		return this.inParams;
	}
	
	@Override
	public Map<String, String> getOutParams() {
		return this.outParams;
	}
	
	@Override
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
	@Override
	public int getResultCode() {
		return this.resultCode;
	}
	
	@Override
	public IFlow<ServiceRequest, ServiceResponse> getFlow() {
		return this.flow;
	}
	
	
	@Override
	public Node<ServiceRequest, ServiceResponse> getCallback() {
		if (null == callback || callback.length() == 0)
			return null;
		
		return flow.getNode(callback);
	}
	
	@Override
	public Node<ServiceRequest, ServiceResponse> getNext() {
		if (null == next || next.length() == 0)
			return null;
		return flow.getNode(next);
	}
	
	
	@Override
	public String getType() {
		return this.type;
	}
	
	@Override
	public void executeAfter(ServiceRequest request, ServiceResponse response) throws FlowException {
	}
	
	@Override
	public void executeBefore(ServiceRequest request) throws FlowException {
	}
	
	
	/**
	 * 若配置值有ref时，则根据上下文解析字符串，获取参数值
	 */
	@Override
	public boolean parseValue(NodeParam param) throws FlowException {
		Object obj = param.getValue();
		if (null == obj) {
			return false;
		}
		
		if (obj instanceof String) {
			String value = (String) obj;
			
			if (value.length() == 0) {
				return false;
			}
			
			// 处理内置变量
			if (value.startsWith("{flow.")) {
				IValueExpress express = ValueExpressFactory.getFactory().getExpress(value);
				if (null != express) {
					return express.getValue(param, value);
				} else {
					param.setValue(value);
					return false;
				}
			}
			
			// 处理引用的字符串@，规则如下：@nodename|context:[inparam|outparam].key 或 @扩展:xxx
			if (value.startsWith(IFlowConfig.CFG_REF_TAG)) {
				// 避免@符后无内容
				int paramIdx = value.indexOf(IFlowConfig.CFG_PARAM_TAG);
				if (paramIdx == -1) {
					throw new FlowException(FlowErr.flow10003.getCode(), FlowErr.flow10003.getInfo(getFlow().getName(), getName(), value, "找不到参数类型分隔符"));
				}
				
				String refNodeName = value.substring(1, paramIdx);
				
				// 优先处理扩展表达式 @扩展:xxx，再处理内置引用对象
				IValueExpress express = ValueExpressFactory.getFactory().getExpress(refNodeName);
				if (null != express) {
					return express.getValue(param, value);
				} else {
					// 避免配置的内部嵌套引用
					if (getName().equals(refNodeName)) {
						throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(getFlow().getName(), getName() + "节点参数不允许自引用:" + value));
					}
					
					// 处理内置表达式@nodename|context:[inparam|outparam].key
					express = ValueExpressFactory.getFactory().getExpress(IFlowConfig.CFG_REF_TAG);
					if (null != express) {
						return express.getValue(param, value);
					} else {
						throw new FlowException(FlowErr.flow10005.getCode(), FlowErr.flow10005.getInfo(getFlow().getName(), getName(), value));
					}
				}
			} else {
				param.setValue(value);
				return true;
			}
		} else {
			return false;
		}
		
	}
	
	/**
	 * 创建服务调用对象，默认实现是String,其它数据类型请重载该方法
	 */
	@Override
	public ServiceRequest createRequest() throws FlowException {
		ServiceRequest serviceRequest = new ServiceRequest();
		return serviceRequest;
	}
	
}
