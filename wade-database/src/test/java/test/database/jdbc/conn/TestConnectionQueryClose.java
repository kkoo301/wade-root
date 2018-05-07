/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc.conn;

import java.sql.Connection;
import java.sql.SQLException;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.dao.impl.TableDAO;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.object.TableMetaObject;

/**
 * TODO
 * 
 * @className: TestConnectionQueryClose.java
 * @author: liaosheng
 * @date: 2014-9-8
 */
public class TestConnectionQueryClose {
	
	static {
		ConnectionManagerFactory.getConnectionManager();
	}

	
	public static void main(String[] args) throws SQLException {
		TestConnectionQueryClose tcqc = new TestConnectionQueryClose();
		
		//tcqc.testQueryTwice();
		//tcqc.testQueryAfterUpdateWithCommit();
		//tcqc.testQueryAfterUpdateWithRollback();
		tcqc.testQueryAfterUpdateWithRollbackAndCommit();
	}
	
	public void testQueryAfterUpdateWithRollbackAndCommit() throws SQLException {
		DataSourceWrapper ds = ConnectionManagerFactory.getConnectionManager().getDataSource("name1");
		Connection conn = null;
		String tableName = "TD_S_STATIC";
		try {
			TableMetaObject table = ds.getTableMetaData().getTableMetaObject(tableName);
			conn = ds.getConnection();
			
			update(conn, table);
			conn.rollback();
			
			update(conn, table);
			//query(conn, table);
			
			conn.commit();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}
	
	public void testQueryAfterUpdateWithRollback() throws SQLException {
		DataSourceWrapper ds = ConnectionManagerFactory.getConnectionManager().getDataSource("name1");
		Connection conn = null;
		String tableName = "TD_S_STATIC";
		try {
			TableMetaObject table = ds.getTableMetaData().getTableMetaObject(tableName);
			conn = ds.getConnection();
			
			update(conn, table);
			conn.rollback();
			
			query(conn, table);
		} catch (SQLException e) {
			throw e;
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}
	
	public void testQueryAfterUpdateWithCommit() throws SQLException {
		DataSourceWrapper ds = ConnectionManagerFactory.getConnectionManager().getDataSource("name1");
		Connection conn = null;
		String tableName = "TD_S_STATIC";
		try {
			TableMetaObject table = ds.getTableMetaData().getTableMetaObject(tableName);
			conn = ds.getConnection();
			
			update(conn, table);
			query(conn, table);
			
			conn.commit();
		} catch (SQLException e) {
			throw e;
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}
	
	
	public void testQueryTwice() throws SQLException {
		DataSourceWrapper ds = ConnectionManagerFactory.getConnectionManager().getDataSource("name1");
		Connection conn = null;
		String tableName = "TD_S_STATIC";
		try {
			TableMetaObject table = ds.getTableMetaData().getTableMetaObject(tableName);
			
			conn = ds.getConnection();
			
			query(conn, table);
			//query(conn, table);
			
		} catch (SQLException e) {
			throw e;
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}
	
	private void query(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		String[] keys = {"TYPE_ID", "DATA_ID"};
		IData source = new DataMap();
		source.put("TYPE_ID", "");
		source.put("DATA_ID", "");
		
		dao.queryByPK(conn, table, keys, source);
	}
	
	private void update(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		String[] keys = {"TYPE_ID", "DATA_ID"};
		IData source = new DataMap();
		source.put("TYPE_ID", "COP_ENT_TYPE");
		source.put("DATA_ID", "F");
		source.put("DATA_NAME", "国有联营");
		
		String[] cols = {"DATA_NAME"};
		String[] values = {"COP_ENT_TYPE", "F"};
		
		//dao.updateByPK(conn, table, cols, source, keys, values);
		dao.saveByPK(conn, table, source, keys, values);
	}
}
