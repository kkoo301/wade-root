/*
 * Copyright: Copyright (c) 2015 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;

/**
 * 测试自定义扩展数据库方言的示例
 * 
 * @className: TestWrapper.java
 * @author: liaosheng
 * @date: 2015-2-3
 */
public class TestWrapper {

	public static void main(String[] args) {
		TestWrapper wrapper = new TestWrapper();
		wrapper.testSqliteDBCP();
	}
	
	
	public void testSqliteDBCP() {
		IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
		Connection conn = null;
		String psql = "select sysdate from dual";
		try {
			conn = manager.getConnection("sqlite");
			if (conn.getClass().getName().indexOf("SqliteConnection") != -1)
				System.out.println(">>>>>>>>> dbcp: Sqlite conn is " + conn.getClass().getName());
			
			PreparedStatement pstmt = conn.prepareStatement(psql);
			if (pstmt.getClass().getName().indexOf("SqlitePreparedStatement") != -1)
				System.out.println(">>>>>>>>> dbcp: Sqlite pstmt is " + pstmt.getClass().getName());
			
			//pstmt.setDate(1, new Date(new java.util.Date().getTime()));
			//pstmt.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.getClass().getName().indexOf("SqliteResultSet") != -1)
				System.out.println(">>>>>>>>> dbcp: Sqlite rs is " + rs.getClass().getName());
			while(rs.next()) {
				System.out.println(rs.getString(1));
			}
			
			Statement stmt = conn.createStatement();
			if (stmt.getClass().getName().indexOf("SqliteStatement") == -1)
				System.out.println(">>>>>>>>> dbcp: Sqlite stmt is " + stmt.getClass().getName());
			ResultSet rs2 = stmt.executeQuery("select sysdate as TT from dual");
			if (rs2.getClass().getName().indexOf("SqliteResultSet") == -1)
				System.out.println(">>>>>>>>> dbcp: Sqlite rs2 is " + rs2.getClass().getName());
			while (rs2.next()) {
				System.out.println(rs2.getString("TT"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//模拟初始化DatabaseCfg实例
	static {
		try {
			Class.forName(DatabaseCfg.class.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
