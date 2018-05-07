/**
 * $
 */
package com.wade.dsf.executor;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfTranscation.java
 * @description: 事务控制
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-27
 */
public class DsfTranscation {
	
	private ThreadLocal<IDsfTransaction> transcation = new ThreadLocal<IDsfTransaction>();
	
	public IDsfTransaction getTransaction() {
		return transcation.get();
	}
	
	public void startTransaction(IDsfTransaction transcation) {
		this.transcation.set(transcation);
	}

}
