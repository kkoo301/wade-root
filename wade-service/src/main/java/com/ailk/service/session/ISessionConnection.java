/**
 * 
 */
package com.ailk.service.session;

import com.ailk.database.dbconn.DBConnection;

/**
 * @author yifur
 *
 */
public interface ISessionConnection {
	
	public DBConnection getConnection(String name, boolean transaction) throws Exception;
	
	public DBConnection getAsyncConnection(String name, boolean transaction) throws Exception;
	
	public void setAutoCommit(boolean autoCommit);
	
	public void commitAll() throws Exception;
	
	public void commit(String name) throws Exception;
	
	public void close(String name) throws Exception;
	
	public void closeAll() throws Exception;
	
	public void rollback(String name) throws Exception;
	
	public void rollbackAll() throws Exception;
	
	public void destroy() throws Exception;
	
	public boolean isAutoCommit() ;

}
