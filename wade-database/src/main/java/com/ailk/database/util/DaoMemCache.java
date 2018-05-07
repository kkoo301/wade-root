package com.ailk.database.util;

import java.util.Map;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.config.SystemCfg;
import com.ailk.database.object.IColumnObject;
import com.ailk.database.object.ITableObject;

public class DaoMemCache {
	
	private static String DAO_TABLE_PRIMARY_KEY = "WADE-DB-KEY-";
	private static String DAO_TABLE_COLUMNS = "WADE-DB_COL-";
	private static String DAO_TABLE_TABLES = "WADE-DB_TAB-";
	private static final int CACHE_TIMEOUT = 60;
	static final boolean isDaoUseMemCache = SystemCfg.isDaoUseMemCache;
	
	/**
	 * 获取表的主键
	 * 
	 * @param key: OWNER_TABLENAME
	 * @return
	 */
	public static String[] getPrimaryKeys(String key) {
		if (!isDaoUseMemCache)
			return null;
		return (String[]) SharedCache.get(DAO_TABLE_PRIMARY_KEY + key);
	}
	
	public static void putPrimaryKeys(String key, String[] primaryKeys) {
		if (isDaoUseMemCache)
			SharedCache.set(DAO_TABLE_PRIMARY_KEY + key, primaryKeys, CACHE_TIMEOUT);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, ITableObject> getTable(String key) {
		if (!isDaoUseMemCache)
			return null;
		return (Map<String, ITableObject>) SharedCache.get(DAO_TABLE_TABLES + key);
	}
	
	public static void putTable(String key, Map<String, ITableObject> table) {
		if (isDaoUseMemCache)
			SharedCache.set(DAO_TABLE_TABLES + key, table, CACHE_TIMEOUT);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, IColumnObject> getColumn(String key) {
		if (!isDaoUseMemCache)
			return null;
		return (Map<String, IColumnObject>) SharedCache.get(DAO_TABLE_COLUMNS + key);
	}
	
	public static void putColumn(String key, Map<String, IColumnObject> table) {
		if (isDaoUseMemCache)
			SharedCache.set(DAO_TABLE_COLUMNS + key, table, CACHE_TIMEOUT);
	}
	
}
