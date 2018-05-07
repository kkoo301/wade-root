/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * TODO
 * 
 * @className: TestMySQL.java
 * @author: liaosheng
 * @date: 2014-6-24
 */
public class TestOracle {
	
	public static void main(String[] args) throws Exception {
		String user = "UOP_RES";//console,UOP_CEN1
		String pass =  "UOP_REStest";
		//String driver = "com.mysql.jdbc.Driver";
		//String url = "jdbc:mysql://10.200.130.83:3306/console?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&cacheServerConfiguration=true&useLocalSessionState=true";
		
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@10.200.130.86:1521:centest";
		
		Connection conn = null;
		StringBuilder sql =  new StringBuilder();
		sql.append(" SELECT * ");
		sql.append(" FROM (SELECT ROW_.*, ROWNUM ROWNUM_");
		sql.append(" FROM (SELECT *");
		sql.append(" FROM TF_R_MPHONECODE_IDLE");
		sql.append(" WHERE 1 = 1");
		sql.append(" AND POOL_CODE = '4'");
		sql.append(" AND EPARCHY_CODE = '0898'");
		sql.append(" AND RES_STATE IN ('1', '2', '3', '5')");
		sql.append(" AND SERIAL_NUMBER LIKE '%88'");
		sql.append(" AND INSTR(SERIAL_NUMBER, '88') > 0");
		sql.append(" ORDER BY SERIAL_NUMBER) ROW_");
		sql.append(" WHERE ROWNUM <= 10)");
		sql.append(" WHERE ROWNUM_ > 0");
		
		Class.forName(driver);
		
		try {
			conn = DriverManager.getConnection(url, user, pass);
			
			PreparedStatement stmt = conn.prepareStatement(sql.toString());
			
			//stmt.setString(1, "222");
			//stmt.setString(2, "U");
			
			ResultSet rs = stmt.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int size = rsmd.getColumnCount();
			for (int i = 1; i <= size; i++) {
				String name = rsmd.getColumnLabel(i);
				System.out.println(name);
			}
			while (rs.next()) {
				System.out.println(rs.getString(1));
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
