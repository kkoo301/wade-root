/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * TODO
 * 
 * @className: TestMySQL.java
 * @author: liaosheng
 * @date: 2014-6-24
 */
public class TestMySQL {
	
	public static void main(String[] args) throws Exception {
		String user = "UOP_CEN1";//console,UOP_CEN1
		String pass =  "UOP_CEN1";
		//String driver = "com.mysql.jdbc.Driver";
		//String url = "jdbc:mysql://10.200.130.83:3306/console?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&cacheServerConfiguration=true&useLocalSessionState=true";
		
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@10.200.130.62:1521:ngact";
		
		Connection conn = null;
		String sql =  "select * from wd_datasource_url";
		
		Class.forName(driver);
		
		try {
			conn = DriverManager.getConnection(url, user, pass);
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				System.out.println("".equals(rs.getString("SERVICE_NAME")));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}

}
