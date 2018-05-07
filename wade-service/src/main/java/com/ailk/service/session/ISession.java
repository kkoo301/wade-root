/**
 * 
 */
package com.ailk.service.session;

import java.sql.Connection;

/**
 * @author yifur
 *
 */
public interface ISession {
	
	public Connection getConnection(String name) throws Exception;
	
	public Connection getAsyncConnection(String name) throws Exception;
	
	public void setAutoCommit(boolean autoCommit);
	
	public void commitAll() throws Exception;
	
	public void commit(String name) throws Exception;
	
	public void commit(String[] names) throws Exception;
	
	public void close(String name) throws Exception;
	
	public void close(String[] names) throws Exception;

	public void closeAll() throws Exception;
	
	public void rollback(String name) throws Exception;
	
	public void rollback(String[] names) throws Exception;
	
	public void rollbackAll() throws Exception;
	
	public void destroy() throws Exception;
	
	public boolean isAutoCommit() ;

}
