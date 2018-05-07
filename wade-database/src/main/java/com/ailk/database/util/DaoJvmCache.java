package com.ailk.database.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ailk.database.object.IColumnObject;
import com.ailk.database.object.ITableObject;

public final class DaoJvmCache {
		
	/**
	 * 表的主键集合
	 * 
	 * @note: 一定要用ConcurrentHashMap，原先用HashMap，IBM-JDK环境下会导致CPU 100%
	 */
	private static final Map<String, String[]> pkeys = new ConcurrentHashMap<String, String[]>();
	
	/**
	 * 表对象集合
	 * 
	 * @note: 一定要用ConcurrentHashMap，原先用HashMap，IBM-JDK环境下会导致CPU 100%
	 */
	private static final Map<String, Map<String, ITableObject>> tables = new ConcurrentHashMap<String, Map<String, ITableObject>>();
	
	/**
	 * 列对象集合
	 * 
	 * @note: 一定要用ConcurrentHashMap，原先用HashMap，IBM-JDK环境下会导致CPU 100%
	 */
	private static final Map<String, Map<String, IColumnObject>> columns = new ConcurrentHashMap<String, Map<String, IColumnObject>>();
	
	/**
	 * 获取表的主键
	 * 
	 * @param key: OWNER_TABLENAME
	 * @return
	 */
	public static final String[] getPrimaryKeys(String key) {
		return (String[]) pkeys.get(key);
	}
	
	public static final void putPrimaryKeys(String key, String[] primaryKeys) {
		pkeys.put(key, primaryKeys);
	}
	
	public static final Map<String, ITableObject> getTable(String key) {
		return tables.get(key);
	}
	
	public static final void putTable(String key, Map<String, ITableObject> table) {
		tables.put(key, table);
	}
	
	public static final Map<String, IColumnObject> getColumn(String key) {
		return columns.get(key);
	}
	
	public static final void putColumn(String key, Map<String, IColumnObject> table) {
		columns.put(key, table);
	}
	
}
