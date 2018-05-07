/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import com.ailk.database.dbconn.DBConnection;

/**
 * 初始化数据库脚本
 * 
 * @className: DBInitScript.java
 * @author: liaosheng
 * @date: 2014-9-6
 */
public class DBInitScript {
	
	
	private DBInitScript () {}
	
	public static void initScript(String dataSourceName) {
		
	}
	
	private static void createTable(String dataSourceName) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("");
		
		Connection conn = null;
		
		try {
			conn = new DBConnection(dataSourceName, true, false);
			
			conn.commit();
		} catch (Exception e) {
			if (null != conn)
				conn.rollback();
			throw e;
		} finally {
			if (null != conn)
				conn.close();
		}
	}
	
	public static void main(String[] args) {
		
	}

}
