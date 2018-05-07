/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com
 * http://www.wadecn.com
 */
package com.ailk.jlcu.mapunit;

import com.ailk.jlcu.mapunit.node.IFlowNode;

/**
 * Case 语句
 * 
 * @author steven zhou
 * @since 1.0
 */
public class Case {

	/** case对应的连线 */
	private String linkId;

	/** 表达式 */
	private String expression;

	/** case对应的下一节点 */
	private IFlowNode next;

	public Case(String linkId, String expression) {
		this.linkId = linkId;
		this.expression = expression;
	}

	/**
	 * 获取表达式
	 * 
	 * @return
	 */
	public String getExpression() {
		return expression;
	}

	public String getLinkId() {
		return linkId;
	}
	
	public void setNext(IFlowNode next) {
		this.next = next;
	}

	public IFlowNode getNext() {
		return next;
	}
}