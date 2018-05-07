/**
 * $
 */
package com.ailk.database.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDAOSession.java
 * @description: 获取数据库连接
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2017-2-1
 */
public interface IDAOSession {
	
	public Connection getConnection(String name) throws SQLException;

}
