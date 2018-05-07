package com.wade.dfs.client.proto;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DFSFile
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-2-5
 */
public class DFSFile {
	
	/**
	 * 组名
	 */
	private String group;
	
	/**
	 * 路径名
	 */
	private String localtion;
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getLocaltion() {
		return localtion;
	}
	
	public void setLocaltion(String localtion) {
		this.localtion = localtion;
	}
	
	public String toString() {
		return "group: " + this.group + ", localtion: " + this.localtion;
	}
}
