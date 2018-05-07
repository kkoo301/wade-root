package com.ailk.database.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.data.IVisit;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.service.session.SessionManager;

public class DBSession implements IDBSession {
	
	private static transient Logger log = Logger.getLogger(DBSession.class);
	
	private Map<String, DBConnection> connections = new HashMap<String, DBConnection>();
	private boolean autoCommit = false;
	
	public DBSession() {
		
	}
	
	@Override
	public DBConnection getConnection(String name, boolean transaction) throws SQLException {
		DBConnection dbc = get(name);
		return dbc;
	}
	
	@Override
	public DBConnection getAsyncConnection(String name, boolean transaction) throws SQLException {
		DBConnection dbc = getAsync(name);
		return dbc;
	}
	
	public boolean isAutoCommit() {
		return autoCommit;
	}
	
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
	
	public DBConnection get(String name) throws SessionConnectionException {
		DBConnection conn = connections.get(name);
		try {
			if (null == conn || conn.isClosed()) {
				conn = createConnection(name);
				connections.put(name, conn);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		
		return conn;
	}
	
	private DBConnection createConnection(String name) throws SessionConnectionException {
		DBConnection dbconn = null;
		try {
			dbconn = new DBConnection(name, false, isAutoCommit());
			connections.put(name, dbconn);
		} catch (SQLException e) {
			throw new SessionConnectionException("db.session.conn.sql", new String[] { name, e.getMessage() } );
		}
		
		return dbconn;
	}
	
	private DBConnection getAsync(String name) throws SQLException {
		DBConnection dbconn = new DBConnection(name, true, isAutoCommit());
		return dbconn;
	}
	
	public DBConnection[] get(String[] names) {
		DBConnection[] conns = new DBConnection[names.length];
		
		for(int i = 0, size = names.length; i < size; i++) {
			conns[i] = get(names[i]);
		}
		
		return conns;
	}
	
	public DBConnection[] getAll() {
		DBConnection[] conns = new DBConnection[connections.size()];
		
		Iterator<String> names = connections.keySet().iterator();
		int i = 0;
		while (names.hasNext()) {
			conns[i] = get(names.next());
			i++;
		}
		return conns;
	}
	
	public void commit(String name) throws SQLException {
		try {
			DBConnection conn = get(name);
			if (null != conn) {
				conn.commit();
			}
		} catch (SQLException e) {
			log.error("事务提交异常,dbname=" + name);
		}
	}
	
	public void commitAll() throws SQLException {		
		for (String name : connections.keySet()) {
			commit(name);
		}
	}
	
	public void close(String name) throws SQLException {
		try {
			DBConnection conn = this.connections.get(name);
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			log.error("连接关闭异常,dbname=" + name);
		}
	}

	public void closeAll() throws SQLException {		
		for (String name : connections.keySet()) {
			close(name);
		}
	}
	
	public void rollback(String name) throws SQLException {
		try {
			DBConnection conn = get(name);
			if (null != conn) {
				conn.rollback();
			}
		} catch (SQLException e) {
			log.error("事务回滚异常,dbname=" + name);
		}
	}
	
	public void rollbackAll() throws SQLException {
		for (String name : connections.keySet()) {
			rollback(name);
		}
	}
	
	public void destroy() throws Exception {
		for (String name : connections.keySet()) {
			rollback(name);
			close(name);
		}
	}
	
	
	@Override
	public void remove(String name) throws SQLException {
		try {
			DBConnection conn = connections.get(name);
			if (null != conn) {
				Connection connection = conn.getConnection();
				if (null != connection && !connection.isClosed()) {
					connection.close();
				}
				
				connections.remove(name);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug("close no transaction db conn " + name);
			}
		}
	}
	
	public static IVisit getVisit() {
		return SessionManager.getInstance().getVisit();
	}
}
