/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com
 * http://www.wadecn.com
 */
package com.ailk.jlcu;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ailk.jlcu.mapunit.Buffvar;
import com.ailk.jlcu.mapunit.node.IFlowNode;

/**
 * 流程图
 * 
 * @author steven zhou
 * @since 1.0
 */
public class FlowChart implements Serializable {

	private static final long serialVersionUID = -156337439285737687L;

	/** PageData对象键名 */
	public static final String CONTEXT = "Context";

	/** 流程图节点集合 */
	private Map<String, IFlowNode> nodes;

	/** 流程图开始节点 */
	private IFlowNode startNode;

	/** 输入参数 */
	private List<Buffvar> inDatas;

	/** 输出参数 */
	private Buffvar outData;
	
	/** buff变量 */
	private Map<String, Buffvar> buffs;
	
	private Map<String, Class> buffTypes;
	
	public final static String BUFF_TYPE = "BUFF_TYPE";

	public FlowChart(String xTransCode,String xTransPath) throws Exception {
		FlowChartXml config = new FlowChartXml(xTransCode,xTransPath);
		init(config);
	}

	private void init(FlowChartXml config) throws ClassNotFoundException {
		this.nodes = config.getNodes();
		this.startNode = config.getStartNode();
		this.inDatas = config.getInDatas();
		this.outData = config.getOutData();
		this.buffs = config.getBuffs();
		initBuffClass();
	}
	
	private void initBuffClass() throws ClassNotFoundException {
		
		// TODO Auto-generated method stub
		buffTypes = new HashMap<String, Class>();
		Iterator<Map.Entry<String, Buffvar>> it = this.buffs.entrySet().iterator();
		Map.Entry<String, Buffvar> e;
		while (it.hasNext()) {
			e = it.next();
			buffTypes.put(e.getKey(), EngineFactory.getClass(e.getValue().getType()));
		}
		
		// 在buffTypes中增加入参的类型
		for (Buffvar inData : inDatas) {
			buffTypes.put(inData.getName(), EngineFactory.getClass(inData.getType()));
		}
		buffTypes.put(this.outData.getName(), EngineFactory.getClass(this.outData.getType()));
	}
	
	/**
	 * 获取开始节点
	 */
	public IFlowNode getStartNode() {
		return startNode;
	}

	/**
	 * 获取流程图节点总数
	 */
	public int getNodeCount() {
		return nodes.size();
	}

	/**
	 * 获取流程输入参数
	 */
	public List<Buffvar> getInDatas() {
		return inDatas;
	}

	/**
	 * 获取流程输出参数
	 */
	public Buffvar getOutData() {
		return outData;
	}
	
	/**
	 * 获取buff变量
	 */
	public Map<String, Buffvar> getBuffs() {
		return buffs;
	}
	
	public String toString() {
		String display = "nodeCount" + nodes.size();
		return display;
	}
	
	/**
	 * 获取buffTypes变量
	 */
	public Map<String, Class> getBuffTypes() {
		return buffTypes;
	}
	
}
