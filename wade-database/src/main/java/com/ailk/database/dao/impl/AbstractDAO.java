package com.ailk.database.dao.impl;

import com.ailk.database.dao.IBaseDAO;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: AbstractDAO
 * @description: DAO抽象类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-7-20
 */
public abstract class AbstractDAO implements IBaseDAO {
	
	/**
	 * 连接名
	 */
	protected String dataSourceName;
	
	public abstract void initial(String connName);
	
	public String getDataSourceName() {
		return dataSourceName;
	}
}