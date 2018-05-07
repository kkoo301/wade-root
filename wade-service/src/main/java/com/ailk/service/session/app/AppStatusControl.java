/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.service.session.app;

/**
 * 会话状态控制对象
 * 
 * @className: AppStatusControl.java
 * @author: liaosheng
 * @date: 2014-5-17
 */
public class AppStatusControl {
	private boolean destroyed = false;
	
	public AppStatusControl() {
	}
	
	/**
	 * @param destroyed the destroyed to set
	 */
	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}
	
	/**
	 * @return the destroyed
	 */
	public boolean isDestroyed() {
		return destroyed;
	}
}
