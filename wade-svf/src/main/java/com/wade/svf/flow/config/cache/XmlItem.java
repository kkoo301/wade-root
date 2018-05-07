/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月18日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.config.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wade.svf.flow.config.IFlowConfig;
import com.wade.svf.flow.node.NodeParam;

/**
 * @description
 * 流程配置对象
 */
public class XmlItem {
	
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
	private Map<String, Map<String, Map<String, String>>> inparams = new HashMap<String, Map<String, Map<String, String>>>(20);
	private Map<String, Map<String, Map<String, String>>> outparams = new HashMap<String, Map<String, Map<String, String>>>(20);
	

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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the nodes
	 */
	public String[] getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(String[] nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the inparams
	 */
	public Map<String, Map<String, Map<String, String>>> getInparams() {
		return inparams;
	}

	/**
	 * @param inparams the inparams to set
	 */
	public void setInparams(Map<String, Map<String, Map<String, String>>> inparams) {
		this.inparams = inparams;
	}

	/**
	 * @return the outparams
	 */
	public Map<String, Map<String, Map<String, String>>> getOutparams() {
		return outparams;
	}

	/**
	 * @param outparams the outparams to set
	 */
	public void setOutparams(Map<String, Map<String, Map<String, String>>> outparams) {
		this.outparams = outparams;
	}

	
	/**
	 * @return the nodeImpls
	 */
	public Map<String, Class<? extends com.wade.svf.flow.node.Node<?, ?>>> getNodeImpls() {
		return nodeImpls;
	}

	/**
	 * @param nodeImpls the nodeImpls to set
	 */
	public void setNodeImpls(Map<String, Class<? extends com.wade.svf.flow.node.Node<?, ?>>> nodeImpls) {
		this.nodeImpls = nodeImpls;
	}

	/**
	 * @return the switchcases
	 */
	public Map<String, List<Map<String, String>>> getSwitchcases() {
		return switchcases;
	}

	/**
	 * @param switchcases the switchcases to set
	 */
	public void setSwitchcases(Map<String, List<Map<String, String>>> switchcases) {
		this.switchcases = switchcases;
	}

	/**
	 * @return the nexts
	 */
	public Map<String, String> getNexts() {
		return nexts;
	}

	/**
	 * @param nexts the nexts to set
	 */
	public void setNexts(Map<String, String> nexts) {
		this.nexts = nexts;
	}

	/**
	 * @return the callbacks
	 */
	public Map<String, String> getCallbacks() {
		return callbacks;
	}

	/**
	 * @param callbacks the callbacks to set
	 */
	public void setCallbacks(Map<String, String> callbacks) {
		this.callbacks = callbacks;
	}
	
	/**
	 * 获取所有节点的输入的NodeParam对象
	 * @return
	 */
	public Map<String, Map<String, NodeParam>> getInParamNodes() {
		Map<String, Map<String, NodeParam>> nodes = new HashMap<String, Map<String,NodeParam>>(30);
		
		for (Map.Entry<String, Map<String, Map<String, String>>> inparams : getInparams().entrySet()) {
			String nodeName = inparams.getKey();
			Map<String, Map<String, String>> inparam = inparams.getValue();
			
			Map<String, NodeParam> nodeparams = new HashMap<String, NodeParam>(10);
			
			for (Map.Entry<String, Map<String, String>> param : inparam.entrySet()) {
				String key = param.getKey();
				Map<String, String> config = param.getValue();
				
				NodeParam nodeparam = new NodeParam();
				nodeparam.setNodeName(nodeName);
				nodeparam.setInparam(true);
				nodeparam.setOutparam(false);
				nodeparam.setKey(config.get(IFlowConfig.CFG_KEY_NAME));
				nodeparam.setValue(config.get(IFlowConfig.CFG_VALUE_NAME));
				
				nodeparams.put(key, nodeparam);
			}
			
			nodes.put(nodeName, nodeparams);
		}
		
		return nodes;
	}
	
	/**
	 * 获取所有节点的输出的NodeParam对象
	 * @return
	 */
	public Map<String, Map<String, NodeParam>> getOutParamNodes() {
		Map<String, Map<String, NodeParam>> nodes = new HashMap<String, Map<String,NodeParam>>(30);
		
		for (Map.Entry<String, Map<String, Map<String, String>>> inparams : getOutparams().entrySet()) {
			String nodeName = inparams.getKey();
			Map<String, Map<String, String>> inparam = inparams.getValue();
			
			Map<String, NodeParam> nodeparams = new HashMap<String, NodeParam>(10);
			
			for (Map.Entry<String, Map<String, String>> param : inparam.entrySet()) {
				String key = param.getKey();
				Map<String, String> config = param.getValue();
				
				NodeParam nodeparam = new NodeParam();
				nodeparam.setNodeName(nodeName);
				nodeparam.setInparam(false);
				nodeparam.setOutparam(true);
				nodeparam.setKey(config.get(IFlowConfig.CFG_KEY_NAME));
				nodeparam.setValue(config.get(IFlowConfig.CFG_VALUE_NAME));
				
				nodeparams.put(key, nodeparam);
			}
			
			nodes.put(nodeName, nodeparams);
		}
		
		return nodes;
	}

}
