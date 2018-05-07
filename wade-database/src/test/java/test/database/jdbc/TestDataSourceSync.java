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
 * @className: TestDataSource.java
 * @author: liaosheng
 * @date: 2014-3-24
 */
public class TestDataSourceSync {
	public static void main(String[] args) {
		System.setProperty("wade.server.name", "app-node01-srv01");
		
		final IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
		for (int i = 0; i < 10; i++) {
			new Thread() {
				public void run() {
					while (true) {
						Connection conn = null;
						try {
							conn = manager.getConnection("cen1");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								Thread.sleep(1000);
								if (null != conn) {
									conn.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				};
			}.start();
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
