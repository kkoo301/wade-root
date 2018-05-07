/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月20日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.cache;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.lang.StringUtils;

import com.ailk.database.orm.annotation.meta.Column;
import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.orm.err.BOException;
import com.ailk.database.orm.util.BOUtil;

/**
 * @description
 * 缓存BO元数据
 */
public final class BOCacheFactory {
	
	
	/**
	 * 缓存BO配置信息
	 */
	private static Map<Class<? extends BOEntity>, BOCacheItem> cache = new WeakHashMap<Class<? extends BOEntity>, BOCacheItem>(10000);
	
	
	/**
	 * 解析BO的class，通过注解获取相关元数据
	 * @param clazz
	 */
	private static void parse(Class<? extends BOEntity> clazz) throws BOException {
		BOCacheItem item = cache.get(clazz);
		if (null == item) {
			synchronized (clazz) {
				if (null == item) {
					item = new BOCacheItem(clazz);
					
					// 还未处理isXXX的
					item.setColumns(parseColumn(clazz));
					cache.put(clazz, item);
				}
			}
		}
	}
	
	
	/**
	 * 解析字段与注解的关系
	 * @param clazz
	 * @return
	 */
	private static Map<String, BOColumn> parseColumn(Class<? extends BOEntity> clazz) {
		Map<String, BOColumn> columns = new HashMap<String, BOColumn>(30);
		
		Set<String> keys = new HashSet<String>();
		String[] primarys = BOUtil.getPrimary(clazz);
		for(String primary : primarys) {
			keys.add(primary);
		}
		
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			
			Column column = field.getAnnotation(Column.class);
			if (null == column) {
				continue;
			}
			
			String defval = column.defval();
			if (null != defval && defval.indexOf("'") != 0) {
				defval = StringUtils.strip(defval, "'");
			}
			
			BOColumn bc = new BOColumn();
			bc.setName(column.name());
			bc.setDefval(defval);
			bc.setDesc(column.desc());
			bc.setType(column.type());
			bc.setLength(column.length());
			
			// 是否为主键字段
			bc.setPrimary(keys.contains(column.name()));
			
			columns.put(bc.getName(), bc);
		}
		
		//添加ROWID
		BOColumn bc = new BOColumn();
		bc.setName("ROWID");
		bc.setDefval("");
		bc.setDesc("rowid");
		bc.setType(Types.ROWID);
		bc.setLength(0);
		bc.setRowId(true);
		
		columns.put(bc.getName(), bc);
		
		
		return columns;
	}
	
	
	/**
	 * 返回所有字段
	 * @param clazz
	 * @return
	 */
	public static Map<String, BOColumn> getColumns(Class<? extends BOEntity> clazz) throws BOException {
		parse(clazz);
		
		BOCacheItem item = cache.get(clazz);
		if (null == item)
			return null;
		
		return item.getColumns();
	}
	
	/**
	 * 获取字段信息
	 * @param clazz
	 * @param column
	 * @return
	 */
	public static BOColumn getColumn(Class<? extends BOEntity> clazz, String column) throws BOException {
		parse(clazz);
		
		BOCacheItem item = cache.get(clazz);
		if (null == item)
			return null;
		
		return item.getColumns().get(column);
	}
	
}
