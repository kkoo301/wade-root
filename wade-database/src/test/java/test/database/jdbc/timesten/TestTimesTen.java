/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc.timesten;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;

import com.timesten.jdbc.TimesTenDataSource;

/**
 * 
 * @className: TestTimesTen.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class TestTimesTen {

	public static void main(String[] args) throws Exception {
		TestTimesTen tt = new TestTimesTen();
		TimesTenDataSource ds = tt.getDataSource();
		Connection conn = null;
		try {
			conn = ds.getConnection();
			tt.printTableMetaData(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (null != conn) {
				conn.close();
			}
		}

	}

	public void printTableMetaData(Connection conn) throws SQLException {
		String sql = "select * from SYS.COLUMNS";
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.setQueryTimeout(10);
			ResultSet rs = stmt.executeQuery(sql);
			rs.setFetchSize(10);
			
			while (rs.next()) {
				System.out.println(">>>>"+rs.getString(1));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (null != stmt) {
				stmt.close();
			}
		}
	}

	public TimesTenDataSource getDataSource() throws Exception {
		String dsn = "TT_80_CS";
		String uid = "BILLING";
		String url = "jdbc:timesten:client:dsn=%s;UID=%s";

		TimesTenDataSource ds = null;
		try {
			Class.forName("com.timesten.jdbc.TimesTenDriver");

			ds = new TimesTenDataSource();
			ds.setUrl(String.format(url, dsn, uid));

			return ds;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public BasicDataSource getDBCPDataSource() throws Exception {
		BasicDataSource datasource = new BasicDataSource();
		int initialSize = 1;
		int maxActive = 5;
		int maxIdle = 5;
		int maxWait = 10000;
		String driver = "com.timesten.jdbc.TimesTenDriver";
		String user = "BILLING";
		String passwd = "BILLING";
		String url = "jdbc:timesten:client:dsn=%s;UID=%s;LogFileSize=1024;LogBuffSize=1048576;WaitForConnect=0;PermSize=10240;TempSize=2048;Connections=1800;PrivateCommands=1;RecoveryThreads=40;TypeMode=0;";
		String dsn = "TTClient-Billing";
		datasource.setDriverClassName(driver);
		datasource.setUsername(user);
		datasource.setPassword(passwd);
		datasource.setUrl(String.format(url, dsn, user));
		datasource.setInitialSize(initialSize);
		datasource.setMaxActive(maxActive);
		datasource.setMaxIdle(maxIdle);
		datasource.setMaxWait(maxWait);
		
		datasource.setTestWhileIdle(true);
		datasource.setTestOnBorrow(false);
		datasource.setTestOnReturn(false);
		datasource.setValidationQuery("");
		datasource.setTimeBetweenEvictionRunsMillis(2000);
		datasource.setNumTestsPerEvictionRun(2);
		datasource.setMinEvictableIdleTimeMillis(10000);
		
		return datasource;
	}
}
