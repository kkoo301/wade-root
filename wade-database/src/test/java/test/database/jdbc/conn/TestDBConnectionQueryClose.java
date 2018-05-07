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
public class TestDBConnectionQueryClose {
	
	static int count = 30;
	
	static {
		ConnectionManagerFactory.getConnectionManager();
	}

	
	public static void main(String[] args) throws Exception {
		DataSourceWrapper ds = ConnectionManagerFactory.getConnectionManager().getDataSource("name1");
		String tableName = "TD_S_STATIC";
		final TableMetaObject table = ds.getTableMetaData().getTableMetaObject(tableName);
		
		final TestDBConnectionQueryClose tcqc = new TestDBConnectionQueryClose();
		
		for (int i = 1; i <= count; i++) {
			new Thread() {
				public void run() {
					try {
						int index = 0;
						while (index <= 100) {
							//tcqc.testQueryTwice(table);
							tcqc.testQueryAfterUpdateWithCommit(table);
							//tcqc.testQueryAfterUpdateWithRollback(table);
							//tcqc.testQueryAfterUpdateWithRollbackAndCommit(table);
							index ++;
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						count--;
						if (count == 0) {
							System.out.println(ConnectionManagerFactory.getConnectionManager().traceInfo("name1"));
						}
					}
				};
			}.start();
		}
		
		
	}
	
	public void testQueryAfterUpdateWithRollbackAndCommit(TableMetaObject table) throws Exception {
		Connection conn = null;
		try {
			conn = new DBConnection("name1", true, false);
			
			update(conn, table);
			conn.rollback();
			
			update(conn, table);
			query(conn, table);
			
			conn.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != conn) {
				conn.close();
			}
			
		}
	}
	
	public void testQueryAfterUpdateWithRollback(TableMetaObject table) throws Exception {
		Connection conn = null;
		try {
			conn = new DBConnection("name1", true, false);
			
			update(conn, table);
			conn.rollback();
			
			query(conn, table);
			conn.commit();
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != conn) {
				conn.close();
			}
			
		}
	}
	
	public void testQueryAfterUpdateWithCommit(TableMetaObject table) throws Exception {
		Connection conn = null;
		try {
			conn = new DBConnection("name1", false, false);
			
			update(conn, table);
			query(conn, table);
			
			conn.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}
	
	
	public void testQueryTwice(TableMetaObject table) throws Exception {
		Connection conn = null;
		try {
			conn = new DBConnection("name1", false, false);
			
			query(conn, table);
			query(conn, table);
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (null != conn) {
				conn.close();
			}
			System.out.println(ConnectionManagerFactory.getConnectionManager().traceInfo("name1"));
		}
	}
	
	private void query(Connection conn, TableMetaObject table) throws Exception {
		TableDAO dao = new TableDAO();
		String[] keys = {"TYPE_ID", "DATA_ID"};
		IData source = new DataMap();
		source.put("TYPE_ID", "");
		source.put("DATA_ID", "");
		
		dao.queryByPK(conn, table, keys, source);
	}
	
	private void update(Connection conn, TableMetaObject table) throws Exception {
		TableDAO dao = new TableDAO();
		String[] keys = {"TYPE_ID", "DATA_ID"};
		IData source = new DataMap();
		source.put("TYPE_ID", "COP_ENT_TYPE");
		source.put("DATA_ID", "F");
		source.put("DATA_NAME", "国有联营2");
		
		String[] cols = {"DATA_NAME"};
		String[] values = {"COP_ENT_TYPE", "F"};
		
		//dao.updateByPK(conn, table, cols, source, keys, values);
		dao.saveByPK(conn, table, source, keys, values);
	}
}
