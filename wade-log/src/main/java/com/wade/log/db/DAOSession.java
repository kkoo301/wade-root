package com.wade.log.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.ailk.database.dao.IDAOSession;

public class DAOSession implements IDAOSession {

	private static ThreadLocal<DBSession> session = new ThreadLocal<DBSession>();
	
	@Override
	public Connection getConnection(String name) throws SQLException {
		if( null == session.get() ){
			synchronized(DAOSession.class){
				if( null == session.get() ){
					session.set(new DBSession());
				}
			}
		}
		return session.get().getConnection(name);
	}
	
	public static void commit() throws SQLException {		
		if( null == session.get() )
			return;
		session.get().commit();
	}
	
	public static void rollback() throws SQLException {
		if( null == session.get() )
			return;
		session.get().rollback();
	}
	
	public static void close() throws SQLException {
		if( null == session.get() )
			return;
		session.get().close();
	}
	
	public static void destroy() throws Exception {
		if( null == session.get() )
			return;
		
		session.get().rollback();
		session.get().close();
	}
}