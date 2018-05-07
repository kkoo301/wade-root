/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月21日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.cache;

/**
 * @description
 * BO字段对象
 */
public class BOColumn {
	
	private String defval;
	private String desc;
	private String name;
	private int type;
	private int length;
	private boolean isRowId;
	private boolean isPrimary;
	
	public BOColumn() {
		
	}
	
	/**
	 * @return the defval
	 */
	public String getDefval() {
		return defval;
	}
	
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param defval the defval to set
	 */
	public void setDefval(String defval) {
		this.defval = defval;
	}
	
	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	/**
	 * @return the isRowId
	 */
	public boolean isRowId() {
		return isRowId;
	}
	
	/**
	 * @param isRowId the isRowId to set
	 */
	public void setRowId(boolean isRowId) {
		this.isRowId = isRowId;
	}
	
	
	/**
	 * @return the isPrimary
	 */
	public boolean isPrimary() {
		return isPrimary;
	}
	
	/**
	 * @param isPrimary the isPrimary to set
	 */
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
}
