package com.ailk.database.session;

import java.sql.SQLException;

import com.ailk.database.dbconn.DBConnection;
import com.ailk.service.session.ISessionConnection;

public interface IDBSession extends ISessionConnection {
	
	public DBConnection get(String name) throws SessionConnectionException;
	
	public DBConnection[] get(String[] names) throws SessionConnectionException;
	
	public DBConnection[] getAll() throws SessionConnectionException;
	
	public void remove(String name) throws SQLException;

}
