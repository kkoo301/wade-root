/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com
 * http://www.wadecn.com
 */
package com.ailk.jlcu.mapunit.node;

import java.util.List;
import com.ailk.jlcu.mapunit.method.IMethod;

/**
 * 动作节点
 * 
 * @author steven zhou
 * @since 1.0
 */
public class ActionNode extends AbstractFlowNode {

	/** 下一节点 */
	private IFlowNode next;

	/** 节点内的方法集 */
	private List<IMethod> methods;

	/** 节点内undo方法集 */
	private List<IMethod> undoMethods;

	public ActionNode(String nodeId, String nodeDesc) {
		super(nodeId, nodeDesc);
	}

	public List<IMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<IMethod> actions) {
		methods = actions;
	}

	public IFlowNode getNext() {
		return next;
	}

	public void setNext(IFlowNode next) {
		this.next = next;
	}

	public List<IMethod> getUndoMethods() {
		return undoMethods;
	}

	public void setUndoMethods(List<IMethod> undoMethods) {
		this.undoMethods = undoMethods;
	}
}