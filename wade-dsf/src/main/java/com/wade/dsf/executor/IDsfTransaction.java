/**
 * $
 */
package com.wade.dsf.executor;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDsfTransation.java
 * @description: 事务控制
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-26
 */
public interface IDsfTransaction {
	
	public void startTransaction() throws Exception;
	
	/**
	 * 是否已经开始提交事务
	 * @return
	 */
	public boolean isStartCommit();
	
	/**
	 * 提交事务
	 */
	public void commit() throws Exception;
	
	/**
	 * 关闭事务
	 */
	public void close() throws Exception;
	
	/**
	 * 回滚事务
	 */
	public void rollback() throws Exception;
	
	public boolean isTimeout();
	
	public void setTimeout(boolean isTimeout);

}
