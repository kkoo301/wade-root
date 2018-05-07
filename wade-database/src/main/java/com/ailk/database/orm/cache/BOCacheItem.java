/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月20日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.cache;

import java.util.HashMap;
import java.util.Map;

import com.ailk.database.jdbc.wrapper.TableMetaData;
import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.rule.ISubTableRule;

/**
 * @description
 * BO缓存对象
 */
class BOCacheItem {
	/**
	 * BO类
	 */
	private Class<? extends BOEntity> entity = null;
	
	/**
	 * BO的字段
	 */
	private Map<String, BOColumn> columns = new HashMap<String, BOColumn>(20);
	
	private TableMetaData metadata = null;
	
	/**
	 * 分表规则
	 */
	private ISubTableRule rule = null;
	
	BOCacheItem(Class<? extends BOEntity> entity) {
		this.entity = entity;
	}
	
	/**
	 * @return the rule
	 */
	public ISubTableRule getRule() {
		return rule;
	}
	
	/**
	 * @param rule the rule to set
	 */
	public void setRule(ISubTableRule rule) {
		this.rule = rule;
	}
	
	/**
	 * @return the entity
	 */
	public Class<? extends BOEntity> getEntity() {
		return entity;
	}
	
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Class<? extends BOEntity> entity) {
		this.entity = entity;
	}
	
	
	/**
	 * @return the columns
	 */
	public Map<String, BOColumn> getColumns() {
		return columns;
	}
	
	/**
	 * @param columns the columns to set
	 */
	public void setColumns(Map<String, BOColumn> columns) {
		this.columns = columns;
	}
	
	/**
	 * @return the metadata
	 */
	public TableMetaData getMetadata() {
		return metadata;
	}
	
	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(TableMetaData metadata) {
		this.metadata = metadata;
	}
}
