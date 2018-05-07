/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.database.object;

import java.util.Iterator;
import java.util.Map;

/**
 * 表结构对象
 * 
 * @className: TableMetaObject.java
 * @author: liaosheng
 * @date: 2014-3-26
 */
public class TableMetaObject {
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 字段集合
	 */
	private Map<String, IColumnObject> columns;
	
	
	/**
	 * 主键
	 */
	private String[] keys;
	
	/**
	 * 字段名
	 */
	private String[] columnNames;

	/**
	 * 单表增删改查语句，不带where条件，只拼所有字段，避免单表查询拼SQL时多次遍历字段列表
	 */
	private String selectSQL;
	private String updateSQL;
	private String insertSQL;
	private String deleteSQL;
	
	/**
	 * 单表增删改查语句，只有ROWID
	 */
	private String selectRowIdSQL;
	
	private String countSQL;
	private String pageSQL;
	
	private String sequenceSQL;
	
	private String systimeSQL;
	
	private Boolean initialized = Boolean.FALSE;
	
	private boolean isOraclePagin = false;
	
	private boolean isSQLitePagin = false;
	
	public TableMetaObject(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * @return the initialized
	 * @return
	 */
	public Boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * @param initialized the initialized to set
	 */
	public void setInitialized(Boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * @return the deleteSQL
	 */
	public String getDeleteSQL() {
		return deleteSQL;
	}
	
	/**
	 * @param deleteSQL the deleteSQL to set
	 */
	public void setDeleteSQL(String deleteSQL) {
		this.deleteSQL = deleteSQL;
	}
	
	/**
	 * @return the columns
	 */
	public Map<String, IColumnObject> getColumns() {
		return columns;
	}
	
	/**
	 * @param columns the columns to set
	 */
	public void setColumns(Map<String, IColumnObject> columns) {
		this.columns = columns;
		
		Iterator<String> iter = columns.keySet().iterator();
		this.columnNames = new String[this.columns.size()];
		int index = 0;
		while (iter.hasNext()) {
			this.columnNames[index] = iter.next();
			index ++;
		}
	}
	
	/**
	 * @return the keys
	 */
	public String[] getKeys() {
		return keys;
	}
	
	/**
	 * @param keys the keys to set
	 */
	public void setKeys(String[] keys) {
		this.keys = keys;
	}
	
	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames() {
		return columnNames;
	}
	
	
	/**
	 * @param selectSQL the selectSQL to set
	 */
	public void setSelectSQL(String selectSQL) {
		this.selectSQL = selectSQL;
	}
	
	/**
	 * @return the selectSQL
	 */
	public String getSelectSQL() {
		return selectSQL;
	}
	
	/**
	 * @return the selectRowIdSQL
	 */
	public String getSelectRowIdSQL() {
		return selectRowIdSQL;
	}
	
	/**
	 * @param selectRowIdSQL the selectRowIdSQL to set
	 */
	public void setSelectRowIdSQL(String selectRowIdSQL) {
		this.selectRowIdSQL = selectRowIdSQL;
	}
	
	/**
	 * @return the updateSQL
	 */
	public String getUpdateSQL() {
		return updateSQL;
	}
	
	/**
	 * @param updateSQL the updateSQL to set
	 */
	public void setUpdateSQL(String updateSQL) {
		this.updateSQL = updateSQL;
	}
	
	/**
	 * @param insertSQL the insertSQL to set
	 */
	public void setInsertSQL(String insertSQL) {
		this.insertSQL = insertSQL;
	}
	
	/**
	 * @return the insertSQL
	 */
	public String getInsertSQL() {
		return insertSQL;
	}
	
	/**
	 * @return the countSQL
	 */
	public String getCountSQL() {
		return countSQL;
	}
	
	/**
	 * @param countSQL the countSQL to set
	 */
	public void setCountSQL(String countSQL) {
		this.countSQL = countSQL;
	}
	
	/**
	 * @return the sequenceSQL
	 */
	private String getSequenceSQL() {
		return sequenceSQL;
	}
	
	/**
	 * @param sequenceSQL the sequenceSQL to set
	 */
	public void setSequenceSQL(String sequenceSQL) {
		this.sequenceSQL = sequenceSQL;
	}
	
	/**
	 * @return the pageSQL
	 */
	private String getPageSQL() {
		return pageSQL;
	}
	
	/**
	 * @param pageSQL the pageSQL to set
	 */
	public void setPageSQL(String pageSQL) {
		this.pageSQL = pageSQL;
	}
	
	/**
	 * @return the systimeSQL
	 */
	public String getSystimeSQL() {
		return systimeSQL;
	}
	
	/**
	 * @param systimeSQL the systimeSQL to set
	 */
	public void setSystimeSQL(String systimeSQL) {
		this.systimeSQL = systimeSQL;
	}
	
	/**
	 * @param isOraclePagin the isOraclePagin to set
	 */
	public void setOraclePagin(boolean isOraclePagin) {
		this.isOraclePagin = isOraclePagin;
	}
	
	/**
	 * @return the isOraclePagin
	 */
	public boolean isOraclePagin() {
		return isOraclePagin;
	}
	
	/**
	 * @param isSQLitePagin the isSQLitePagin to set
	 */
	public void setSQLitePagin(boolean isSQLitePagin) {
		this.isSQLitePagin = isSQLitePagin;
	}
	
	/**
	 * @return the isSQLitePagin
	 */
	public boolean isSQLitePagin() {
		return isSQLitePagin;
	}
	
	
	/**
	 * 生成分页SQL
	 * @param sql
	 * @return
	 */
	public String createPageSQL(String sql, int begin, int end) {
		if (begin != end && end != 0)
			return String.format(getPageSQL(), sql);
		return sql;
	}
	
	public String createCountSQL(String sql) {
		return String.format(getCountSQL(), sql);
	}
	
	public String createSequenceSQL(String seqName, int increment) {
		return String.format(getSequenceSQL(), seqName, increment);
	}
}
