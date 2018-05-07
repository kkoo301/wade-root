/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月24日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.node;


/**
 * @description
 * 节点参数对象
 */
public class NodeParam {
	
	private String nodeName;
	
	/**
	 * 参数名称
	 * @return
	 */
	private String key;
	
	
	/**
	 * 参数类型
	 */
	private String type;
	
	/**
	 * 参数检查器
	 */
	private String inspector;
	
	/**
	 * 参数检查结果
	 */
	private String inspectMessage;
	
	/**
	 * 参数值
	 * @return
	 */
	private Object value;
	
	/**
	 * 是否已检查
	 */
	private boolean isInspected;
	
	/**
	 * 是否为输入参数
	 */
	private boolean isInparam;
	
	/**
	 * 是否为输出参数
	 */
	private boolean isOutparam;
	
	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}
	
	/**
	 * @param nodeName the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	
	/**
	 * @return the isInparam
	 */
	public boolean isInparam() {
		return isInparam;
	}
	
	/**
	 * @return the isOutparam
	 */
	public boolean isOutparam() {
		return isOutparam;
	}
	
	/**
	 * @param isInparam the isInparam to set
	 */
	public void setInparam(boolean isInparam) {
		this.isInparam = isInparam;
	}
	/**
	 * @param isOutparam the isOutparam to set
	 */
	public void setOutparam(boolean isOutparam) {
		this.isOutparam = isOutparam;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	
	/**
	 * @return the inspector
	 */
	public String getInspector() {
		return inspector;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param inspector the inspector to set
	 */
	public void setInspector(String inspector) {
		this.inspector = inspector;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
	/**
	 * @return the isInspected
	 */
	public boolean isInspected() {
		return isInspected;
	}
	/**
	 * @param isInspected the isInspected to set
	 */
	public void setInspected(boolean isInspected) {
		this.isInspected = isInspected;
	}
	
	
	/**
	 * @return the inspectMessage
	 */
	public String getInspectMessage() {
		return inspectMessage;
	}
	
	
	/**
	 * @param inspectMessage the inspectMessage to set
	 */
	public void setInspectMessage(String inspectMessage) {
		this.inspectMessage = inspectMessage;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(20).append("{\"key\":\"").append(key).append("\",\"value\":\"").append(value).append("\"}").toString();
	}
	
}
