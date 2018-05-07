/**   
* Copyright: Copyright (c) 2012 Asiainfo-Linkage
* 
* @ClassName: TreeBean.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: Ben
* @date: 2012-10-17 下午06:57:50 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2012-10-17     Ben           v1.0.0               修改原因
*/

package com.ailk.common.data.impl;

/**
 * @author Ben
 *
 */
public class TreeBean {
	
	private String id;
	
	private String code;
	
	private String parentId;
	
	private String label;
	
	private String value;
	
	private String href;
	
	private boolean showCheck;
	
	private int nodeCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public boolean isShowCheck() {
		return showCheck;
	}

	public void setShowCheck(boolean showCheck) {
		this.showCheck = showCheck;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}
	
}
