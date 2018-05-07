/**
 * $
 */
package com.wade.dsf.executor;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDsfTranscationManager.java
 * @description: 事务管理器
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-27
 */
public interface IDsfTranscationManager {
	
	public void setTranscationImplClass(Class<IDsfTransaction> transactionImplClass);
	
	public IDsfTransaction startTranscation();
	
	public IDsfTransaction getTranscation();
	
}
