/**
 * $
 */
package com.wade.dsf.executor;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfThread.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-19
 */
public class DsfThread extends Thread {
	
	public DsfThread (ThreadGroup group, Runnable r, String name) {
		super(group, r, name, 0);
	}

}
