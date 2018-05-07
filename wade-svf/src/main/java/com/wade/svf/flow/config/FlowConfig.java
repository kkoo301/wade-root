/**
 * $
 */
package com.wade.svf.flow.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.svf.flow.config.cache.XmlCache;
import com.wade.svf.flow.config.cache.XmlItem;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.node.Node;
import com.wade.svf.flow.node.NodeParam;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: XmlFlowConfig.java
 * @description: 基于Xml结构的流程配置
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2016-11-15
 */
public class FlowConfig implements IFlowConfig {

	private static final Logger log = LoggerFactory.getLogger(FlowConfig.class);

	/**
	 * 流程名，即文件名
	 */
	private String name;

	/**
	 * 节点数组
	 */
	private String[] nodes;

	/**
	 * 所有节点的入参，出参
	 */
	private Map<String, Map<String, NodeParam>> inparams = new HashMap<String, Map<String, NodeParam>>(20);
	private Map<String, Map<String, NodeParam>> outparams = new HashMap<String, Map<String, NodeParam>>(20);

	/**
	 * 保存Node的实现类
	 */
	private Map<String, Class<? extends com.wade.svf.flow.node.Node<?, ?>>> nodeImpls = new HashMap<String, Class<? extends com.wade.svf.flow.node.Node<?, ?>>>();

	/**
	 * switch节点
	 */
	private Map<String, List<Map<String, String>>> switchcases = new HashMap<String, List<Map<String, String>>>(20);

	/**
	 * 所有节点的next
	 */
	private Map<String, String> nexts = new HashMap<String, String>(20);

	/**
	 * 所有节点的callback
	 */
	private Map<String, String> callbacks = new HashMap<String, String>(20);

	public FlowConfig(String name) throws FlowException {
		this.name = name;
		try {
			XmlItem item = XmlCache.getInstance().getItem(name);
			
			this.nodes = item.getNodes();
			this.inparams.putAll(item.getInParamNodes());
			this.outparams.putAll(item.getOutParamNodes());
			this.callbacks.putAll(item.getCallbacks());
			this.nexts.putAll(item.getNexts());
			this.nodeImpls.putAll(item.getNodeImpls());
			this.switchcases.putAll(item.getSwitchcases());
			
		} catch (FlowException e) {
			throw e;
		} catch (Exception e) {
			throw new FlowException(FlowErr.flow10007.getCode(), FlowErr.flow10007.getInfo(name, e.getMessage()), e);
		}
	}

	
	@Override
	public String[] getNodes() {
		return this.nodes;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Map<String, NodeParam> getInParam(String nodeName) {
		return this.inparams.get(nodeName);
	}
	
	
	@Override
	public Map<String, NodeParam> getOutParam(String nodeName) {
		return this.outparams.get(nodeName);
	}
	
	
	@Override
	public String getNext(String nodeName) {
		return this.nexts.get(nodeName);
	}

	@Override
	public String getCallback(String nodeName) {
		return this.callbacks.get(nodeName);
	}

	@Override
	public List<Map<String, String>> getSwitchCase(String nodeName) {
		return this.switchcases.get(nodeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wade.svf.flow.config.IFlowConfig#getNodeImpl(java
	 * .lang.String)
	 */
	@Override
	public Class<? extends Node<?, ?>> getNodeImpl(String nodeName) {
		return this.nodeImpls.get(nodeName);
	}
	

	@Override
	public void print() {
		if (log.isDebugEnabled()) {
			log.debug(">>>>>>>>>>>>>>>>开始打印流程配置信息{}>>>>>>>>>>>>>>>>>>>", getName());
			String[] nodes = this.getNodes();
			for (String node : nodes) {
				int index = node.indexOf("/");
				String nodeType = node.substring(0, index);
				String nodeName = node.substring(index + 1);
				Class<?> clazz = getNodeImpl(nodeName);
				
				log.debug("节点类型：{},节点名称：{},实现类：{}", new String[] {nodeType, nodeName, null == clazz ? "null" : clazz.getName()});
				log.debug("节点{}输入参数:{}", new String[] {nodeName, getInParam(nodeName).toString()});
	
				if (CFG_SWITCH_NAME.equals(nodeType)) {
					log.debug("节点{}Case参数:{}", new String[] {nodeName, getSwitchCase(nodeName).toString()});
				} else {
					log.debug("节点{}输出参数:{}", new String[] {nodeName, getOutParam(nodeName).toString()});
					log.debug("节点{}的Next:{}", new String[] {nodeName, getNext(nodeName)});
					log.debug("节点{}的Callback:{}", new String[] {nodeName, getCallback(nodeName)});
				}
			}
			
			log.debug(">>>>>>>>>>>>>>>>结束打印流程配置信息{}>>>>>>>>>>>>>>>>>>>", getName());
		}
	}
}
