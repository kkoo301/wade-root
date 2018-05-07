/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc.conn;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.DBConnection;

/**
 * TODO
 * 
 * @className: TestConn.java
 * @author: liaosheng
 * @date: 2014-9-3
 */
public class TestConn {
	static {
		ConnectionManagerFactory.getConnectionManager();
	}
	
	public static void main(String[] args) throws Exception {
		
		
		try {
			DBConnection conn = new DBConnection("name1", true, false);
			
			TestConn tc = new TestConn();
			tc.update(conn);
			Thread.sleep(1000);
			tc.rollback(conn);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void rollback(final DBConnection conn) {
		new Thread() {
			public void run() {
				try {
					System.out.println(">>>>>rollback111");
					conn.rollback();
					System.out.println(">>>>>rollback222");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
				}
			};
		}.start();
	}
	
	public void update(final DBConnection conn) {
		new Thread() {
			public void run() {
				try {
					PreparedStatement stmt = conn.prepareStatement("update WD_STATIC set data_name = ? where data_id=? and type_id=?");
					stmt.setString(1, "2");
					stmt.setString(2, "1");
					stmt.setString(3, "1");
					System.out.println(">>>>>>>>>>>>update");
					stmt.executeUpdate();
					conn.commit();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
				}
			};
		}.start();
	}

}
