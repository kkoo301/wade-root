/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月20日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.bo;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.orm.err.BOException;
import com.ailk.database.orm.util.BOUtil;

/**
 * @description 
 * 业务实体对象的封装
 */
public class BOContainer implements BOEntity, Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 存放所有属性
	 */
	private IData properties = new DataMap(20);
	
	/**
	 * 存放备份数据
	 */
	private IData backup;
	
	/**
	 * 存放修改的字段列表
	 */
	private Set<String> changedProperties = null;
	
	/**
	 * 标识是否为新增
	 */
	private boolean isNew;
	
	/**
	 * 标识是否为物理删除
	 */
	private boolean isDelete;
	
	/**
	 * ROWID
	 */
	private String rowId;
	
	
	public BOContainer() {
		
	}
	
	public BOContainer(Map<String, Object> properties) {
		this.properties.putAll(properties);
	}
	
	@Override
	public IData toData() {
		return properties;
	}
	
	@Override
	public void initProperty(String key, Object value) {
		this.properties.put(key, value);
	}
	
	@Override
	public boolean containsKey(String key) {
		return this.properties.containsKey(key);
	}
	
	@Override
	public boolean isEmpty() {
		return null == this.properties || this.properties.isEmpty();
	}
	
	@Override
	public Set<String> getColumnNames() throws BOException {
		return new HashSet<String>(BOUtil.getColumns(getClass()).keySet());
	}
	
	
	@Override
	public void clear() {
		this.properties = new DataMap(20);
		this.backup = null;
		this.changedProperties = new HashSet<String>(20);
	}
	
	@Override
	public void removeKey(String key) {
		if (null == key) {
			return;
		}
		
		if (null != this.backup)
			this.backup.remove(key);
		
		if (null != this.changedProperties)
			this.changedProperties.remove(key);
		
		this.properties.remove(key);
	}
	
	@Override
	public void set(String key, Object value) {
		if (null == this.changedProperties) {
			this.changedProperties = new HashSet<String>(20);
		}
		
		// 将变更的属性添加到changedProperties
		this.changedProperties.add(key);
		
		this.properties.put(key, value);
	}
	
	/**
	 * 获取属性值
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return this.properties.get(key);
	}
	
	@Override
	public boolean isNull(String name) {
		return (get(name) == null);
	}
	
	@Override
	public Set<String> getPropertyNames() throws BOException {
		return toMap().keySet();
	}
	
	@Override
	public Set<String> getChangedProperties() {
		return this.changedProperties;
	}
	
	@Override
	public boolean isDelete() {
		return this.isDelete;
	}
	
	@Override
	public boolean isNew() {
		return this.isNew;
	}
	
	@Override
	public void setStsToDelete() {
		this.isNew = false;
		this.isDelete = true;
	}
	
	@Override
	public void setStsToNew() {
		this.isNew = true;
		this.isDelete = false;
	}
	
	@Override
	public Map<String, Object> backup(boolean rewrite) {
		if (null == backup) {
			this.backup = new DataMap(this.properties.size());
			this.backup.putAll(this.properties);
			return this.backup;
		}
		
		return this.backup;
	}
	
	@Override
	public String getRowId() {
		return rowId;
	}
	
	@Override
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	
	@Override
	public void fill(Map<String, Object> data) {
		for (Map.Entry<String, Object> item : data.entrySet()) {
			set(item.getKey(), item.getValue());
		}
	}
	
	@Override
	public Map<String, Object> toMap() {
		return this.properties;
	}
	
	
	@Override
	public String getAsString(String column) throws BOException {
		return DataType.getAsString(get(column));
	}
	
	@Override
	public boolean getAsBoolean(String column) throws BOException {
		return DataType.getAsBoolean(get(column));
	}
	
	
	@Override
	public char getAsChar(String column) throws BOException {
		return DataType.getAsChar(get(column));
	}
	
	@Override
	public short getAsShort(String column) throws BOException {
		return DataType.getAsShort(get(column));
	}
	
	
	@Override
	public Date getAsDate(String column) throws BOException {
		return DataType.getAsDate(get(column));
	}
	
	
	@Override
	public Timestamp getAsDateTime(String column) throws BOException {
		return DataType.getAsDateTime(get(column));
	}
	
	@Override
	public double getAsDouble(String column) throws BOException {
		return DataType.getAsDouble(get(column));
	}
	
	
	@Override
	public float getAsFloat(String column) throws BOException {
		return DataType.getAsFloat(get(column));
	}
	
	
	@Override
	public int getAsInt(String column) throws BOException {
		return DataType.getAsInt(get(column));
	}
	
	@Override
	public long getAsLong(String column) throws BOException {
		return DataType.getAsLong(get(column));
	}
	
	
	@Override
	public Time getAsTime(String column) throws BOException {
		return DataType.getAsTime(get(column));
	}

}
