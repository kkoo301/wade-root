/**
 * $
 */
package com.wade.dsf.executor;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfTransaction.java
 * @description: 事务管理
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-26
 */
public final class DsfTransactionManager implements IDsfTranscationManager {
	
	private ThreadLocal<IDsfTransaction> transcation = new ThreadLocal<IDsfTransaction>();
	
	private Class<IDsfTransaction> transactionImplClass = null; 
	
	public DsfTransactionManager() {
		
	}
	
	@Override
	public void setTranscationImplClass(Class<IDsfTransaction> transactionImplClass) {
		this.transactionImplClass = transactionImplClass;
	}
	
	@Override
	public IDsfTransaction startTranscation() {
		try {
			IDsfTransaction impl = transactionImplClass.newInstance();
			impl.startTransaction();
			
			transcation.set(impl);
			return impl;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public IDsfTransaction getTranscation() {
		return transcation.get();
	}
	
}
