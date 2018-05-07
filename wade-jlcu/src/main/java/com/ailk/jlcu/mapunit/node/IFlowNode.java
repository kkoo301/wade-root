/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com
 * http://www.wadecn.com
 */
package com.ailk.jlcu.mapunit.node;

/**
 * 流程节点(接口)
 * 
 * @author steven zhou
 * @since 1.0
 */
public interface IFlowNode {

	/**
	 * 流程节点类型枚举
	 * 
	 * @author steven zhou
	 * @since 1.0
	 */
	public static enum NodeType {
		SWITCH, START, END, ACTION,
	}

	/** 获取节点标识 */
	public abstract String getNodeId();

	/** 获取节点描述 */
	public abstract String getNodeDesc();

	/** 获取节点类型 */
	public abstract NodeType getNodeType();

	/** 设置节点类型 */
	public abstract void setNodeType(NodeType type);
}
