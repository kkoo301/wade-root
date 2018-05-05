package com.wade.trace.sample.impl;

import com.wade.trace.sample.AbstractSample;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: WebSample
 * @description: Web采样
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class WebSample extends AbstractSample {
	
	/**
	 * 菜单ID
	 */
	private String menuId;

	public WebSample(String menuId, long sampleDenom) {
		super(sampleDenom);
		this.menuId = menuId;
	}
	
	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	
	@Override
	public String toString() {
		return "{ menuId : " + this.menuId + ", sample_denom : " + getSampleDenom() + "}";		
	}
	
}
