package com.ailk.database.dbconn;

import org.apache.log4j.Logger;

import com.ailk.database.config.DatabaseCfg;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ConnectionManagerFactory
 * @description: 连接管理工厂类（类名不能改！！！）
 * 
 * @version: v1.0.0
 * @author: zhoulin
 * @date: 2013-7-20
 */
public final class ConnectionManagerFactory {
	
	private static final Logger log = Logger.getLogger(ConnectionManagerFactory.class);
	
	private static IConnectionManager manager = null;
	private static IDBPasswordCreator pwdcreator = null;
	private static ConnectionManagerFactory connector = new ConnectionManagerFactory();
	
	public synchronized static ConnectionManagerFactory getInstance() {
		if (null == manager) 
			initFactory();
		return connector;
	}
	
	/**
	 * 获取连接管理器
	 * 
	 * @return
	 */
	public static synchronized IConnectionManager getConnectionManager() {
		if (null == manager) 
			initFactory();
		
		return manager;
	}
    
	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public static synchronized IDBPasswordCreator getDBPwdCreator() {
		if (null == manager) 
			initFactory();
		
		return pwdcreator;
	}
	
	private static void initFactory() {
		synchronized (connector) {
			if (null == manager) {
				try {
					Class<?> clazz = Class.forName(DatabaseCfg.getConnector());
					manager = (IConnectionManager) clazz.newInstance();
				} catch (InstantiationException e) {
					log.error(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					log.error(e.getMessage(), e);
				} catch (ClassNotFoundException e) {
					log.error(e.getMessage(), e);
				} finally {
					log.info("实例化数据库连接管理类:" + manager);
				}
			}
			
			if (null == pwdcreator) {
				try {
					pwdcreator = (IDBPasswordCreator) Class.forName(DatabaseCfg.getPwdCreator()).newInstance();
				} catch (InstantiationException e) {
					log.error(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					log.error(e.getMessage(), e);
				} catch (ClassNotFoundException e) {
					log.error(e.getMessage(), e);
				} finally {
					log.info("实例化数据库密码管理类:" + pwdcreator);
				}
			}
			
			if (null != manager) {
				manager.initConnectionManager();
			}
		}
	}
	
	public static void main(String[] args) {
		System.setProperty("wade.server.name", "app-10.200.51.99");
		ConnectionManagerFactory.getConnectionManager();
	}
	
}