/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com
 * http://www.wadecn.com
 */
package com.ailk.jlcu.mapunit.node;

import java.util.List;

import com.ailk.jlcu.mapunit.Case;

/**
 * 分支节点
 * 
 * @author steven zhou
 * @since 1.0
 */
public class SwitchNode extends AbstractFlowNode {
	
	private List<Case> cases;

	public SwitchNode(String nodeId, String nodeDesc) {
		super(nodeId, nodeDesc);
	}

	/**
	 * 设置Case集合
	 * 
	 * @param cases
	 */
	public void setCases(List<Case> cases) {
		this.cases = cases;
	}

	/**
	 * 获取Case集合
	 * 
	 * @return
	 */
	public List<Case> getCases() {
		return cases;
	}
}