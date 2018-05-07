/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月22日
 * 
 * Just Do IT.
 */
package com.ailk.database.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.ailk.database.dao.IDAOSession;
import com.ailk.database.transaction.LocalMutilTransaction;

/**
 * @description
 * 本地DAO会话对象
 */
public class LocalDAOSession implements IDAOSession {

	
	@Override
	public Connection getConnection(String dataSourceName) throws SQLException {
		return LocalMutilTransaction.getConnection(dataSourceName);
	}

}
