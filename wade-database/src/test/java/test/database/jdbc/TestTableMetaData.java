/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.jdbc.wrapper.DataSourceWrapper;
import com.ailk.database.jdbc.wrapper.TableMetaData;
import com.ailk.database.object.IColumnObject;

/**
 * 验证最佳获取表主键及列名的SQL
 * 验证多线程同时对同一个表做初始化操作，看是否会导致CPU100%
 * 
 * @className: TestMySQLTableMetaData.java
 * @author: liaosheng
 * @date: 2014-3-25
 */
public class TestTableMetaData {
	
	public static void main(String[] args) throws SQLException {
		TestTableMetaData meta = new TestTableMetaData();
		
		//meta.printTableMetaData();
		
		//meta.testMySQL();
		meta.testOracle();
	}
	
	/**
	 * 测试MySQL的表结构逻辑
	 */
	public void testMySQL() throws SQLException {
		testMySQLTableMetaData();
	}
	
	public void testOracle() throws SQLException {
		testOracleTableMetaData();
	}
	
	private void testOracleTableMetaData() throws SQLException {
		DataSourceWrapper wrapper = ConnectionManagerFactory.getConnectionManager().getDataSource("base");
		TableMetaData meta = wrapper.getTableMetaData();
		
		String tableName = "TF_F_CUST_VIP";
		//String tableName = "new_table";//没有主键
		
		if (meta.getClass().getName().indexOf("OracleTableMetaData") == -1) 
			System.out.println(">>>>>>ERROR Class Name " + meta.getClass().getName());
		
		String[] keys = meta.getTableMetaObject(tableName).getKeys();
		System.out.println("表主键：" + Arrays.toString(keys));
		
		Map<String, IColumnObject> columns = meta.getTableMetaObject(tableName).getColumns();
		Iterator<String> iter = columns.keySet().iterator();
		while (iter.hasNext()) {
			IColumnObject col = columns.get(iter.next());
			System.out.println("表结构：" + col);
		}
		
	}
	
	/**
	 * 测试MySQL表结构信息
	 */
	private void testMySQLTableMetaData() throws SQLException {
		DataSourceWrapper wrapper = ConnectionManagerFactory.getConnectionManager().getDataSource("base");
		TableMetaData meta = wrapper.getTableMetaData();
		
		String tableName = "sys_adm_menu_right";
		//String tableName = "new_table";//没有主键
		
		if (meta.getClass().getName().indexOf("MySQLTableMetaData") == -1) 
			System.out.println(">>>>>>ERROR Class Name " + meta.getClass().getName());
		
		String[] keys = meta.getTableMetaObject(tableName).getKeys();
		System.out.println("表主键：" + Arrays.toString(keys));
		
		Map<String, IColumnObject> columns = meta.getTableMetaObject(tableName).getColumns();
		Iterator<String> iter = columns.keySet().iterator();
		while (iter.hasNext()) {
			IColumnObject col = columns.get(iter.next());
			System.out.println("表结构：" + col);
		}
		
	}
	
	
	/**
	 * 通过desc tableName获取表结构
	 */
	public void printTableMetaData() {
		Connection conn = null;
		try {
			conn = ConnectionManagerFactory.getConnectionManager().getConnection("base");
			ResultSet rs = conn.getMetaData().getPrimaryKeys(null, "%", "WD_DATASOURCE_ACCT");
			while (rs.next()) {
				String field = rs.getString("COLUMN_NAME");
				System.out.println(">>>>" + field);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != conn) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
