/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com
 * http://www.wadecn.com
 */
package com.ailk.jlcu.mapunit.node;

/**
 * 流程节点(抽象类)
 * 
 * @author steven zhou
 * @since 1.0
 */
public abstract class AbstractFlowNode implements IFlowNode {

	/** 节点标识 */
	private String nodeId;

	/** 节点描述 */
	private String nodeDesc;

	/** 节点类型 */
	private NodeType nodeType;

	public AbstractFlowNode(String nodeId, String nodeDesc) {
		this.nodeId = nodeId;
		this.nodeDesc = nodeDesc;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public String getNodeId() {
		return nodeId == null ? "" : nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeDesc() {
		return nodeDesc == null ? "" : nodeDesc;
	}

	public void setNodeDesc(String nodeDesc) {
		this.nodeDesc = nodeDesc;
	}
}
