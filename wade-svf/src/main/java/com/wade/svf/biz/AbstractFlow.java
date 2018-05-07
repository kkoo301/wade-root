/**
 * $
 */
package com.wade.svf.biz;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.wade.svf.biz.express.SelectListExpress;
import com.wade.svf.biz.express.SelectStringExpress;
import com.wade.svf.biz.filter.NodeFilterChain;
import com.wade.svf.biz.node.EndNode;
import com.wade.svf.biz.node.EventNode;
import com.wade.svf.biz.node.RuleNode;
import com.wade.svf.biz.node.ServiceNode;
import com.wade.svf.biz.node.StartNode;
import com.wade.svf.biz.node.SwitchNode;
import com.wade.svf.flow.FlowContext;
import com.wade.svf.flow.IFlow;
import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.express.ValueExpressFactory;
import com.wade.svf.flow.config.FlowConfig;
import com.wade.svf.flow.node.Node;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: AbstraceFlow.java
 * @description: 抽象流程对象
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2016-11-15
 */
public abstract class AbstractFlow implements IFlow<ServiceRequest, ServiceResponse> {
	
	public static final String FLOW_EXPRESS_SELLIST = "sellist";
	public static final String FLOW_EXPRESS_SELSTR = "selstr";
	
	/**
	 * 流程名称
	 */
	private String name;
	
	/**
	 * 流程配置对象
	 */
	private IFlowConfig config = null;
	
	
	/**
	 * 流程节点集
	 */
	private Map<String, Node<ServiceRequest, ServiceResponse>> nodes = new HashMap<String, Node<ServiceRequest, ServiceResponse>>();
	
	public AbstractFlow(String name) throws FlowException {
		this.name = name;
		this.config = new FlowConfig(name);
		
		this.config.print();
		
		createNodes();
		
		// 添加自定义的表达式
		ValueExpressFactory.getFactory().createExpress(FLOW_EXPRESS_SELLIST, SelectListExpress.class.getName());
		ValueExpressFactory.getFactory().createExpress(FLOW_EXPRESS_SELSTR, SelectStringExpress.class.getName());
	}
	
	/**
	 * 创建流程节点实例对象
	 */
	private void createNodes() throws FlowException {
		String[] names = getConfig().getNodes();
		for (String name : names) {
			int index = name.indexOf("/");
			
			String type = name.substring(0, index);
			String nodeName = name.substring(index + 1);
			String callback = config.getCallback(nodeName);
			String next = config.getNext(nodeName);
			
			if (IFlowConfig.CFG_START_NAME.equals(type)) {
				Node<ServiceRequest, ServiceResponse> node = createNode(type, nodeName, callback, next);
				nodes.put(nodeName, node);
				continue;
			} else if (IFlowConfig.CFG_END_NAME.equals(type)) {
				next = null;
				Node<ServiceRequest, ServiceResponse> node = createNode(type, nodeName, callback, next);
				nodes.put(nodeName, node);
				continue;
			} else if (IFlowConfig.CFG_SERVICE_NAME.equals(type)) {
				Node<ServiceRequest, ServiceResponse> node = createNode(type, nodeName, callback, next);
				nodes.put(nodeName, node);
				continue;
			} else if (IFlowConfig.CFG_RULE_NAME.equals(type)) {
				Node<ServiceRequest, ServiceResponse> node = createNode(type, nodeName, callback, next);
				nodes.put(nodeName, node);
				continue;
			} else if (IFlowConfig.CFG_SWITCH_NAME.equals(type)) {
				Node<ServiceRequest, ServiceResponse> node = createNode(type, nodeName, callback, next);
				nodes.put(nodeName, node);
				continue;
			} else if (IFlowConfig.CFG_EVENT_NAME.equals(type)) {
				Node<ServiceRequest, ServiceResponse> node = createNode(type, nodeName, callback, next);
				nodes.put(nodeName, node);
				continue;
			} else {
				continue;
			}
		}
	}
	

	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * 获取流程节点对象
	 */
	public Node<ServiceRequest, ServiceResponse> getNode(String nodeName) {
		return nodes.get(nodeName);
	}
	
	/**
	 * 创建Node实例
	 * @param nodeName
	 * @param callback
	 * @param next
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Node<ServiceRequest, ServiceResponse> createNode(String type, String nodeName, String callback, String next) throws FlowException {
		Node<ServiceRequest, ServiceResponse> node = null;
		
		Class<Node<ServiceRequest, ServiceResponse>> nodeClass = (Class<Node<ServiceRequest, ServiceResponse>>) getConfig().getNodeImpl(nodeName);
		if (null == nodeClass) {
			return createDefaultNode(type, nodeName, callback, next);
		}
		
		Constructor<Node<ServiceRequest, ServiceResponse>> constructor = null;
		try {
			constructor = nodeClass.getConstructor(IFlow.class, String.class, String.class, String.class);
			node = constructor.newInstance(this, nodeName, callback, next);
		} catch (Exception e) {
			throw new FlowException(FlowErr.flow10000.getCode(), FlowErr.flow10000.getInfo(getName() + "根据配置创建流程节点" + nodeName + "异常"), e);
		}
		
		return node;
	}
	
	/**
	 * 创建默认的节点实例
	 * @param type
	 * @param nodeName
	 * @param callback
	 * @param next
	 * @return
	 */
	private Node<ServiceRequest, ServiceResponse> createDefaultNode(String type, String nodeName, String callback, String next) {
		if (IFlowConfig.CFG_START_NAME.equals(type)) {
			return new StartNode(this, nodeName, callback, next);
		} else if (IFlowConfig.CFG_END_NAME.equals(type)) {
			return new EndNode(this, nodeName, callback, next);
		} else if (IFlowConfig.CFG_SERVICE_NAME.equals(type)) {
			return new ServiceNode(this, nodeName, callback, next);
		} else if (IFlowConfig.CFG_EVENT_NAME.equals(type)) {
			return new EventNode(this, nodeName, callback, next);
		} else if (IFlowConfig.CFG_RULE_NAME.equals(type)) {
			return new RuleNode(this, nodeName, callback, next);
		} else if (IFlowConfig.CFG_EVENT_NAME.equals(type)) {
			return new EventNode(this, nodeName, callback, next);
		} else if (IFlowConfig.CFG_SWITCH_NAME.equals(type)) {
			return new SwitchNode(this, nodeName, callback, next);
		} else {
			return null;
		}
	}

	/**
	 * 按流程配置的节点顺序执行每个节点的excute方法
	 */
	@Override
	public void execute(FlowContext context) throws Exception {
		// 获取start节点
		Node<ServiceRequest, ServiceResponse> node = getNode(IFlowConfig.CFG_START_NAME);
		
		// 递归执行节点
		while (null != node) {
			node = executeNode(context, node);
		}
	}
	
	
	/**
	 * 节点执行
	 * @param context
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private Node<ServiceRequest, ServiceResponse> executeNode(FlowContext context, Node<ServiceRequest, ServiceResponse> node) throws Exception {
		String nodeName = node.getName();
		
		// 添加到调用链
		context.addNode(nodeName);
		
		// 添加过滤器逻辑
		NodeFilterChain chain = new NodeFilterChain();
		chain.doFilter(context, node);
		
		return node.getNext();
	}
	
	@Override
	public IFlowConfig getConfig() {
		return this.config;
	}
	
}
