package com.ailk.database.util;

import java.util.HashMap;
import java.util.Map;

import com.ailk.database.object.IColumnObject;
import com.ailk.database.object.ITableObject;

public class DaoCache {
		
	/**
	 * 表的主键集合
	 */
	private static Map<String, String[]> pkeys = new HashMap<String, String[]>();
	
	/**
	 * 表对象集合
	 */
	private static Map<String, Map<String, ITableObject>> tables = new HashMap<String, Map<String, ITableObject>>();
	
	/**
	 * 列对象集合
	 */
	private static Map<String, Map<String, IColumnObject>> columns = new HashMap<String, Map<String, IColumnObject>>();
	
	/**
	 * 获取表的主键
	 * 
	 * @param key: OWNER_TABLENAME
	 * @return
	 */
	public static String[] getPrimaryKeys(String key) {
		return (String[]) pkeys.get(key);
	}
	
	public static void putPrimaryKeys(String key, String[] primaryKeys) {
		pkeys.put(key, primaryKeys);
	}
	
	public static Map<String, ITableObject> getTable(String key) {
		return tables.get(key);
	}
	
	public static void putTable(String key, Map<String, ITableObject> table) {
		tables.put(key, table);
	}
	
	public static Map<String, IColumnObject> getColumn(String key) {
		return columns.get(key);
	}
	
	public static void putColumn(String key, Map<String, IColumnObject> table) {
		columns.put(key, table);
	}
	
}
