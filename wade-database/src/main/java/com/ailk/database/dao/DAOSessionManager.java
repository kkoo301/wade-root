/**
 * $
 */
package com.ailk.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dao.impl.DefaultDAOSession;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DBSessionManager.java
 * @description: 获取DBSession对象
 * 
 * @version: v1.0
 * @author: liaosheng
 * @date: 2017-2-1
 */
public final class DAOSessionManager {
	
	private static final Logger log = LoggerFactory.getLogger(DAOSessionManager.class);

	private IDAOSession session = null;
	
	private static final String sessionImplClass = DatabaseCfg.getSession();
	private Object lock = new Object();
	
	private static DAOSessionManager manager = new DAOSessionManager();
	private DAOSessionManager() {
		
	}
	
	public static DAOSessionManager getManager() {
		return manager;
	}
	
	public IDAOSession getSession() {
		if (null == session) {
			synchronized (lock) {
				if (null == session) {
					try {
						log.info("创建DAOSession实例{}", sessionImplClass);
						session = (IDAOSession) Class.forName(sessionImplClass).newInstance();
					} catch (Exception e) {
						session = new DefaultDAOSession();
						log.error("实例化DAOSession失败{}", sessionImplClass, e);
					}
				}
			}
		}
		
		return session;
	}
	
}
