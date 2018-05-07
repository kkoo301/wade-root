/**
 * $
 */
package com.wade.svf.flow.config;

import java.util.List;
import java.util.Map;

import com.wade.svf.flow.node.Node;
import com.wade.svf.flow.node.NodeParam;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IFlowConfig.java
 * @description: 流程配置对象
 * 
 * @version: $ Id $
 * @author: liaosheng
 * @date: 2016-11-15
 */
public interface IFlowConfig {
	
	public static final String CFG_NODE_NAME = "name";
	public static final String CFG_KEY_NAME = "key";
	public static final String CFG_VALUE_NAME = "value";
	public static final String CFG_FLOW_NAME = "flow";
	public static final String CFG_EVENT_NAME = "event";
	public static final String CFG_START_NAME = "start";
	public static final String CFG_END_NAME = "end";
	public static final String CFG_SERVICE_NAME = "service";
	public static final String CFG_SWITCH_NAME = "switch";
	public static final String CFG_RULE_NAME = "rule";
	public static final String CFG_NEXT_NAME = "next";
	public static final String CFG_CALLBACK_NAME = "callback";
	public static final String CFG_PARAM_NAME = "param";
	
	public static final String CFG_NODE_IMPL = "impl";
	public static final String CFG_NODE_INPARAM = "inparam";
	public static final String CFG_NODE_OUTPARAM = "outparam";
	public static final String CFG_NODE_CONTEXT = "context";
	
	public static final String CFG_REF_TAG = "@";
	public static final String CFG_PARAM_TAG = ":";
	public static final String CFG_KEY_TAG = ".";

	public static final String CFG_SWITCH_CASE_VALUE = "value";
	public static final String CFG_SWITCH_CASE_NEXT = "next";
	
	public String getName();
	
	/**
	 * 获取流程配置的所有节点name的值的数组，返回格式如下：nodeType/name
	 * @return
	 */
	public String[] getNodes();
	
	/**
	 * 获取Node的实现类，必须是实现了Node接口的对象
	 * @param nodeName
	 * @return
	 */
	public Class<? extends Node<?,?>> getNodeImpl(String nodeName);
	
	/**
	 * 获取流程配置里指定节点的inparam结合集
	 * @param nodeName
	 * @return
	 */
	public Map<String, NodeParam> getInParam(String nodeName);
	
	/**
	 * 获取流程配置里指定节点的inparam结合集
	 * @param nodeName
	 * @return
	 */
	public Map<String, NodeParam> getOutParam(String nodeName);
	
	
	/**
	 * 获取流程配置里指定节点的next
	 * @param nodeName
	 * @return
	 */
	public String getNext(String nodeName);
	
	/**
	 * 获取流程配置里指定节点的callback
	 * @param nodeName
	 * @return
	 */
	public String getCallback(String nodeName);
	
	/**
	 * 获取switch节点的case集合
	 * @param nodeName
	 * @return
	 */
	public List<Map<String, String>> getSwitchCase(String nodeName);
	
	/**
	 * 格式化输出配置信息，仅做调试用
	 */
	public void print();
}
