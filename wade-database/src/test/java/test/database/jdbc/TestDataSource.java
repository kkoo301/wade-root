/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;


/**
 * database.xml里定义connector=com.ailk.database.jdbc.manager.DataSourceManager
 * 测试数据库方言Connection,Prestatement,CallableStatement,ResultSet
 * 
 * @className: TestDataSource.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class TestDataSource {
	public static void main(String[] args) {
		System.setProperty("wade.server.name", "database-test");
		
		TestDataSource tds = new TestDataSource();
		//tds.testOracleDBCP();
		tds.testTimesTenDBCP();
		//tds.testMySQLDBCP();
	}
	
	/**
	 * 测试database.xml@name1,type=dbcp,dialect=oracle
	 */
	public void testOracleDBCP() {
		IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
		Connection conn = null;
		String psql = "select sysdate - ? from dual";
		try {
			conn = manager.getConnection("oracle");
			if (conn.getClass().getName().indexOf("OracleConnection") == -1)
				System.out.println(">>>>>>>>> dbcp: Oracle conn is " + conn.getClass().getName());
			
			PreparedStatement pstmt = conn.prepareStatement(psql);
			if (pstmt.getClass().getName().indexOf("OraclePreparedStatement") == -1)
				System.out.println(">>>>>>>>> dbcp: Oracle pstmt is " + pstmt.getClass().getName());
			
			pstmt.setDate(1, new Date(new java.util.Date().getTime()));
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.getClass().getName().indexOf("OracleResultSet") == -1)
				System.out.println(">>>>>>>>> dbcp: Oracle rs is " + rs.getClass().getName());
			while(rs.next()) {
				rs.getString(1);
			}
			
			Statement stmt = conn.createStatement();
			if (stmt.getClass().getName().indexOf("OracleStatement") == -1)
				System.out.println(">>>>>>>>> dbcp: Oracle stmt is " + stmt.getClass().getName());
			ResultSet rs2 = stmt.executeQuery("select sysdate as TT from dual");
			if (rs2.getClass().getName().indexOf("OracleResultSet") == -1)
				System.out.println(">>>>>>>>> dbcp: Oracle rs2 is " + rs2.getClass().getName());
			while (rs2.next()) {
				rs2.getString("TT");
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
	
	/**
	 * 测试database.xml@name3,type=jdbc,dialect=mysql
	 */
	public void testMySQLDBCP() {
		IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
		Connection conn = null;
		String psql = "select sysdate() - ?";
		try {
			conn = manager.getConnection("mysql");
			if (conn.getClass().getName().indexOf("MySQLConnection") == -1)
				System.out.println(">>>>>>>>> dbcp: MySQL conn is " + conn.getClass().getName());
			
			PreparedStatement pstmt = conn.prepareStatement(psql);
			if (pstmt.getClass().getName().indexOf("MySQLPreparedStatement") == -1)
				System.out.println(">>>>>>>>> dbcp: MySQL pstmt is " + pstmt.getClass().getName());
			
			pstmt.setDate(1, new Date(new java.util.Date().getTime()));
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.getClass().getName().indexOf("MySQLResultSet") == -1)
				System.out.println(">>>>>>>>> dbcp: MySQL rs is " + rs.getClass().getName());
			while(rs.next()) {
				rs.getString(1);
			}
			
			Statement stmt = conn.createStatement();
			if (stmt.getClass().getName().indexOf("MySQLStatement") == -1)
				System.out.println(">>>>>>>>> dbcp: MySQL stmt is " + stmt.getClass().getName());
			ResultSet rs2 = stmt.executeQuery("select sysdate() as TT");
			if (rs2.getClass().getName().indexOf("MySQLResultSet") == -1)
				System.out.println(">>>>>>>>> dbcp: MySQL rs2 is " + rs2.getClass().getName());
			while (rs2.next()) {
				rs2.getString("TT");
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
	
	
	public void testTimesTenDBCP() {
		IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
		Connection conn = null;
		String psql = "select sysdate from dual";
		try {
			conn = manager.getConnection("timesten");
			if (conn.getClass().getName().indexOf("TimesTenConnection") == -1)
				System.out.println(">>>>>>>>> dbcp: TimesTen conn is " + conn.getClass().getName());
			
			PreparedStatement pstmt = conn.prepareStatement(psql);
			if (pstmt.getClass().getName().indexOf("TimesTenPreparedStatement") == -1)
				System.out.println(">>>>>>>>> dbcp: TimesTen pstmt is " + pstmt.getClass().getName());
			
			//pstmt.setDate(1, new Date(new java.util.Date().getTime()));
			//pstmt.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
			
			ResultSet rs = pstmt.executeQuery();
			if (rs.getClass().getName().indexOf("TimesTenResultSet") == -1)
				System.out.println(">>>>>>>>> dbcp: TimesTen rs is " + rs.getClass().getName());
			while(rs.next()) {
				System.out.println(rs.getString(1));
			}
			
			Statement stmt = conn.createStatement();
			if (stmt.getClass().getName().indexOf("TimesTenStatement") == -1)
				System.out.println(">>>>>>>>> dbcp: TimesTen stmt is " + stmt.getClass().getName());
			ResultSet rs2 = stmt.executeQuery("select sysdate as TT from dual");
			if (rs2.getClass().getName().indexOf("TimesTenResultSet") == -1)
				System.out.println(">>>>>>>>> dbcp: TimesTen rs2 is " + rs2.getClass().getName());
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
