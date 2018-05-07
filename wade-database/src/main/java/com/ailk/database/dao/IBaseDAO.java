package com.ailk.database.dao;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: IBaseDAO
 * @description: DAO接口
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-7-20
 */
public interface IBaseDAO {

	/**
	 * 初始化
	 * 
	 * @param dataSourceName
	 * @param executeSqlStatckLevel
	 */
	public void initial(String dataSourceName) ;
	
	/**
	 * 获取连接名
	 * 
	 * @return
	 */
	public String getDataSourceName();
	
}