/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月21日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.sql;

import java.sql.SQLException;

import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.orm.err.BOException;

/**
 * @description
 * 单表操作的SQL代码生成器
 */
public interface ISQLMapper {
	
	/**
	 * 获取Count语句
	 * @param entity
	 * @return
	 */
	public String count(BOEntity entity) throws BOException ;
	
	/**
	 * 获取分页语句
	 * @param entity
	 * @return
	 */
	public StringBuilder pagin(String sql);
	
	
	/**
	 * 获取按主键查询的语句
	 * @param entity
	 * @param columns
	 * @param isNeedRowId
	 * @return
	 * @throws SQLException
	 */
	public String select(BOEntity entity, String[] columns, boolean isNeedRowId) throws SQLException;
	
	
	/**
	 * 获取按主键删除的语句
	 * @param entity
	 * @return
	 */
	public String delete(BOEntity entity) throws BOException ;
	
	
	/**
	 * 获取按主键修改的语句
	 * @param entity
	 * @return
	 */
	public String update(BOEntity entity) throws BOException ;
	
	/**
	 * 获取新增的语句
	 * @param entity
	 * @return
	 */
	public String insert(BOEntity entity) throws BOException ;
}
