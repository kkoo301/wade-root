/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月22日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.sql;

/**
 * @description
 * 增删改查SQL
 */
public class CURDSql {
	
	public String selectRowId;
	
	public String select;
	
	private String delete;
	
	private String update;
	
	private String insert;
	
	private String count;
	
	public CURDSql() {
		
	}
	
	/**
	 * @return the delete
	 */
	public String getDelete() {
		return delete;
	}
	
	/**
	 * @return the insert
	 */
	public String getInsert() {
		return insert;
	}
	
	/**
	 * @return the select
	 */
	public String getSelect(boolean isNeedRowId) {
		if (isNeedRowId) {
			return selectRowId;
		} else {
			return select;
		}
	}
	
	/**
	 * @return the update
	 */
	public String getUpdate() {
		return update;
	}
	/**
	 * @param delete the delete to set
	 */
	public void setDelete(String delete) {
		this.delete = delete;
	}
	
	/**
	 * @param insert the insert to set
	 */
	public void setInsert(String insert) {
		this.insert = insert;
	}
	
	/**
	 * @param select the select to set
	 */
	public void setSelect(String select) {
		this.select = select;
	}
	
	/**
	 * @param selectRowId the selectRowId to set
	 */
	public void setSelectRowId(String selectRowId) {
		this.selectRowId = selectRowId;
	}
	
	/**
	 * @param update the update to set
	 */
	public void setUpdate(String update) {
		this.update = update;
	}
	
	/**
	 * @return the count
	 */
	public String getCount() {
		return count;
	}
	
	/**
	 * @param count the count to set
	 */
	public void setCount(String count) {
		this.count = count;
	}

}
