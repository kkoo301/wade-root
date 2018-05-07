/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月20日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.database.orm.annotation.meta.Table;
import com.ailk.database.orm.bo.BOContainer;
import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.orm.cache.BOCacheFactory;
import com.ailk.database.orm.cache.BOColumn;
import com.ailk.database.orm.err.BOError;
import com.ailk.database.orm.err.BOException;
import com.ailk.database.rule.ISubTableRule;
import com.ailk.database.rule.TableRuleContext;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * @description
 * BO工具类，提供获取BO对象的API
 */
public final class BOUtil {
	
	
	/**
	 * 创建BO对象
	 * @param clazz
	 * @return
	 * @throws BOException
	 */
	public static <T extends BOEntity> T create(Class<T> clazz, Map<String, Object> data) throws BOException {
		T bo = null;
		
		try {
			bo = clazz.newInstance();
			if (null != data && !data.isEmpty()) {
				bo.fill(data);
			}
		} catch (Exception e) {
			throw new BOException(BOError.bo10001.getCode(), BOError.bo10001.getInfo(e.getMessage(), clazz.getName()), e);
		}
		
		return bo;
	}
	
	/**
	 * 复制BO对象，包括修改的字段列表
	 * @param clazz
	 * @param entity
	 * @return
	 * @throws BOException
	 */
	public static <T extends BOEntity> T createByEntity(Class<T> clazz, BOEntity entity) throws BOException {
		T bo = null;
		
		try {
			bo = clazz.newInstance();
			Map<String, Object> data = entity.toMap();
			if (null != data && !data.isEmpty()) {
				bo.fill(data);
			}
			
			// 复制修改的字段列表
			Set<String> changedProperties = entity.getChangedProperties();
			if (null != changedProperties && changedProperties.size() > 0) {
				Map<String, BOColumn> columns = BOUtil.getColumns(clazz);
				for (String column : changedProperties) {
					if (columns.containsKey(column)) {
						bo.set(column, data.get(column));
					}
				}
			}
		} catch (Exception e) {
			throw new BOException(BOError.bo10001.getCode(), BOError.bo10001.getInfo(e.getMessage(), clazz.getName()), e);
		}
		
		return bo;
	}
	
	/**
	 * 根据BO的主键字段创建BO对象<br>
	 * 1.当values为NULL时，则直接返回新生成的BO<br>
	 * 2.否则，判断主键字段与所传values的个数是否一致，不一致则抛出异常
	 * @param clazz
	 * @param data
	 * @return
	 * @throws BOException
	 */
	public static <T extends BOEntity> T createByPrimary(Class<T> clazz, Object... values) throws BOException {
		Map<String, Object> data = new HashMap<String, Object>(10);
		
		if (null != values && values.length > 0) {
			String[] primary = getPrimary(clazz);
			
			int size = primary.length;
			if (size == values.length) {
				for (int i = 0; i < size; i++) {
					data.put(primary[i], values[i]);
				}
			} else {
				throw new BOException(BOError.bo10008.getCode(), BOError.bo10008.getInfo(clazz.getName()));
			}
			
		}
		return create(clazz, data);
	}
	
	/**
	 * 根据BO的主键字段创建BO对象<br>
	 * 1.当parameter为NULL时，则直接返回新生成的BO<br>
	 * 2.否则，判断主键字段值是否为NULL，为NULL则抛出异常
	 * @param clazz
	 * @param parameter
	 * @return
	 * @throws BOException
	 */
	public static <T extends BOEntity> Object[] getPrimaryValues(Class<T> clazz, Map<String, Object> parameter) throws BOException {
		List<Object> values = new ArrayList<Object>(10);
		
		if (null != parameter) {
			String[] primary = getPrimary(clazz);
			
			int size = primary.length;
			for (int i = 0; i < size; i++) {
				Object value = parameter.get(primary[i]);
				if (null == value) {
					throw new BOException(BOError.bo10016.getCode(), BOError.bo10016.getInfo(clazz.getName(), primary[i]));
				}
				values.add(value);
			}
			
		}
		return values.toArray(new Object[]{});
	}
	
	
	/**
	 * 获取表名
	 * @param clazz BO的Class
	 * @return
	 */
	public static String getTableName(BOEntity entity) throws BOException {
		Class<? extends BOEntity> clazz = entity.getClass();
		Table table = clazz.getAnnotation(Table.class);
		
		if (null != table) {
			String name = table.name();
			if (name.startsWith("{")) {
				String tableName = StringUtils.strip(name, "{}");
				ISubTableRule rule = null;
				
				try {
					rule = TableRuleContext.getRule(tableName);
					return tableName + rule.getSplitTag() + rule.getSubTable();
				} catch (SQLException e) {
					throw new BOException(BOError.bo10014.getCode(), BOError.bo10014.getInfo(name), e);
				}
			} else {
				return name;
			}
		}
		
		throw new BOException(BOError.bo10015.getCode(), BOError.bo10015.getInfo(clazz.toString()));
	}
	
	/**
	 * 根据BO获取表名，支持分表
	 * @param clazz
	 * @return
	 * @throws BOException
	 */
	public static String getTableName(Class<? extends BOEntity> clazz) throws BOException {
		Table table = clazz.getAnnotation(Table.class);
		
		if (null != table) {
			String name = table.name();
			if (name.startsWith("{")) {
				String tableName = StringUtils.strip(name, "{}");
				ISubTableRule rule = null;
				
				try {
					rule = TableRuleContext.getRule(tableName);
					
					if (null == rule) {
						return null;
					}
					
					return tableName + rule.getSplitTag() + rule.getSubTable();
				} catch (SQLException e) {
					throw new BOException(BOError.bo10014.getCode(), BOError.bo10014.getInfo(name), e);
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * 判断是否存在字段
	 * @param clazz
	 * @param column
	 * @return
	 * @throws BOException
	 */
	public static boolean existsColumn(Class<? extends BOEntity> clazz, String column) throws BOException {
		return BOCacheFactory.getColumns(clazz).containsKey(column);
	}
	
	/**
	 * 获取数据库方言
	 * @param clazz
	 * @return
	 */
	public static String getDialect(Class<? extends BOEntity> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		
		if (null != table) {
			return table.dialect();
		}
		
		return null;
	}
	
	/**
	 * 获取主键字段列表
	 * @param clazz	BO的Class
	 * @return
	 */
	public static String[] getPrimary(Class<? extends BOEntity> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		
		if (null != table) {
			return table.primary().replaceAll(" ", "").split(",");
		}
		
		return null;
	}
	
	/**
	 * 获取唯一索引字段列表
	 * @param clazz	BO的Class
	 * @return
	 */
	public static String[] getUnique(Class<? extends BOEntity> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		
		if (null != table) {
			return table.unique().split(",");
		}
		
		return null;
	}
	
	
	/**
	 * 获取所有字段
	 * @param clazz
	 * @return
	 * @throws BOException
	 */
	public static Map<String, BOColumn> getColumns(Class<? extends BOEntity> clazz) throws BOException {
		return BOCacheFactory.getColumns(clazz);
	}
	
	
	/**
	 * 获取字段类型
	 * @param clazz
	 * @param column
	 * @return
	 * @throws BOException
	 */
	public static int getColumnType(Class<? extends BOEntity> clazz, String column) throws BOException {
		BOColumn bc = BOCacheFactory.getColumn(clazz, column);
		if (null == bc) {
			return -1;
		}
		
		return bc.getType();
	}
	
	
	/**
	 * 获取字段定义的长度
	 * @param clazz
	 * @param column
	 * @return
	 * @throws BOException
	 */
	public static int getColumnLength(Class<? extends BOEntity> clazz, String column) throws BOException {
		BOColumn bc = BOCacheFactory.getColumn(clazz, column);
		if (null == bc) {
			return -1;
		}
		
		return bc.getLength();
	}
	
	/**
	 * 将BoEntity数组转换成IDataset对象
	 * @param entities
	 * @return
	 */
	public static IDataset toDataset(BOEntity[] entities) {
		if (DataUtils.isEmpty(entities)) {
			return null;
		}
		
		IDataset ds = new DatasetList();
		for (BOEntity entity : entities) {
			if (null == entity) {
				ds.add(new DataMap());
			} else {
				ds.add(entity.toData());
			}
		}
		return ds;
	}
	
	/**
	 * 将IDataset转换成BOContainer对象数组
	 * @param clazz
	 * @param ds
	 * @return
	 * @throws BOException
	 */
	public static BOContainer[] toArray(Class<BOContainer> clazz, IDataset ds) throws BOException {
		if (DataUtils.isEmpty(ds)) {
			return null;
		}
		
		int size = ds.size();
		List<BOContainer> list = new ArrayList<BOContainer>(size);
		for (int i = 0; i < size; i++) {
			BOContainer entity = BOUtil.create(clazz, null);
			entity.fill(ds.getData(i));
			
			list.add(entity);
		}
		
		return list.toArray(new BOContainer[]{});
	}
}
