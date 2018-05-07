/**
 * $
 */
package com.ailk.database.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.ailk.database.dao.IDAOSession;
import com.ailk.service.session.app.AppSession;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DefaultDAOSession.java
 * @description: 默认的数据访问层会话对象
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2017-2-1
 */
public class DefaultDAOSession implements IDAOSession {

	
	@Override
	public Connection getConnection(String dataSourceName) throws SQLException {
		return AppSession.getSession().getConnection(dataSourceName);
	}

}
