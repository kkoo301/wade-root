/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年5月16日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.bo;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.database.orm.err.BOException;

/**
 * @description
 * 业务实体的接口，用来映射表名、表字段、主键等模型信息，并支持扩展其它字段，是单表增删改查的基础对象
 */
public interface BOEntity {
	
	/**
	 * 获取数据表的字段名集合，必须在BO的属性里添加注解[Column]，示例如下：<br>
	 * <code>
	 * Column(name="PARTITION_ID", type=Types.NUMERIC, length=4, desc ="desc")<br>
	 * public static final String PARTITION_ID = "PARTITION_ID";
	 * </code>
	 * @return
	 */
	public Set<String> getColumnNames() throws BOException;
	
	/**
	 * 清除所有数据，包括表字段和扩展的字段
	 */
	public void clear();
	
	/**
	 * 判断是否为空对象
	 * @return
	 */
	public boolean isEmpty();
	
	/**
	 * 删除指定的Key
	 * @param key
	 */
	public void removeKey(String key);
	
	/**
	 * 是否包含Key
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key);
	
	/**
	 * 获取Rowid
	 * @return
	 */
	public String getRowId();
	
	
	/**
	 * 设置RowId
	 * @param rowId
	 */
	public void setRowId(String rowId);
	
	/**
	 * 初始化BO的表字段属性，在查询结果集生成BO对象时使用，与set的区别在于该方法不会添加到属性修改列表里
	 * @param key
	 * @param value
	 */
	public void initProperty(String key, Object value);
	
	/**
	 * 批量设置字段，通过set来实现，修改的字段会添加到属性修改列表里
	 * @param data
	 * @throws BOException
	 */
	public void fill(Map<String, Object> data);
	
	/**
	 * 将BO的所有属性转换成Map对象
	 * @return
	 */
	public Map<String, Object> toMap();
	
	/**
	 * 将BO的所有属性转换成IData对象
	 * @return
	 */
	public IData toData();
	
	/**
	 * 是否为新增，在save操作时，用来判断是否执行InsertSQL
	 * @return
	 */
	public boolean isNew();
	
	/**
	 * 是否为物理删除，在save操作时，用来判断是否执行DeleteSQL
	 * @return
	 */
	public boolean isDelete();
	
	/**
	 * 设置状态为isNew，在save操作时，将执行InsertSQL
	 */
	public void setStsToNew();
	
	/**
	 * 设置状态为isDelete，在save操作时，将执行DeleteSQL
	 */
	public void setStsToDelete();
	
	/**
	 * 设置字段，并添加到修改列表里
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value);
	
	/**
	 * 备份BO的所有数据并返回，当rewrite为true时会强制把现有的数据再次备份
	 * @param rewrite
	 * @return
	 */
	public Map<String, Object> backup(boolean rewrite);
	
	/**
	 * 判断值是否为NULL
	 * @param name
	 * @return
	 */
	public boolean isNull(String name);

	/**
	 * 将name的值转换成String
	 * @param name
	 * @return
	 */
	public String getAsString(String name) throws BOException;

	/**
	 * 将name的值转换成short
	 * @param name
	 * @return
	 * @throws BOException
	 */
	public short getAsShort(String name) throws BOException;

	/**
	 * 将name的值转换成int
	 * @param name
	 * @return
	 * @throws BOException
	 */
	public int getAsInt(String name) throws BOException;

	/**
	 * 将name的值转换成long
	 * @param name
	 * @return
	 * @throws BOException
	 */
	public long getAsLong(String name) throws BOException;

	/**
	 * 将name的值转换成double
	 * @param name
	 * @return
	 * @throws BOException
	 */
	public double getAsDouble(String name) throws BOException;

	/**
	 * 将name的值转换成float
	 * @param name
	 * @return
	 * @throws BOException
	 */
	public float getAsFloat(String name) throws BOException;
	
	/**
	 * 将name的值转换成boolean
	 * @param name
	 * @return
	 * @throws BOException
	 */
	public boolean getAsBoolean(String name) throws BOException;

	/**
	 * 将name的值转换成char
	 * @param name
	 * @return
	 * @throws BOException
	 */
	public char getAsChar(String name) throws BOException;

	/**
	 * 将name的值转换成java.sql.Date
	 * @param name
	 * @return
	 * @throws BOException
	 */
	public Date getAsDate(String name) throws BOException;

	/**
	 * 将name的值转换成java.sql.Time
	 * @param name
	 * @return
	 * @throws BOException
	 */
	public Time getAsTime(String name) throws BOException;

	/**
	 * 将name的值转换成java.sql.Timestamp
	 * @param name
	 * @return
	 * @throws BOException
	 */
	public Timestamp getAsDateTime(String name) throws BOException;

	/**
	 * 获取所有的属性名称列表，包括映射的表字段名以及扩展的Key
	 * @return
	 */
	public Set<String> getPropertyNames() throws BOException;
	
	/**
	 * 获取所有修改的属性名称列表
	 * @return
	 */
	public Set<String> getChangedProperties();

}
