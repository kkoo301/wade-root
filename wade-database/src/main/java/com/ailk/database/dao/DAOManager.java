package com.ailk.database.dao;

import com.ailk.common.BaseException;
import com.ailk.database.DBException;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: DAOManager
 * @description: DAO工厂类
 * 
 * @version: v1.0.0
 * @author: $Id: DAOManager.java 1 2014-02-20 08:34:02Z huangbo $
 * @date: 2013-7-20
 */
public class DAOManager {
	
	private DAOManager() {
		
	}
	
	
	/**
	 * 创建指定连接的DAO
	 * 
	 * @param clazzName
	 * @param connName
	 * @return
	 * @throws Exception
	 */
	public static final <Type extends IBaseDAO>Type createDAO(Class<Type> clazz, String connName) throws Exception {
		String clazzName = clazz.getName();
		try {
			Type dao = (Type) clazz.newInstance();
			dao.initial(connName);
			return dao;
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new DBException("daoinstance can not created[" + clazzName + "]");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new DBException("daoinstance access exception[" + clazzName + "]");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(e);
		}
	}

	
	
	/**
	 * 创建不带连接名称的DAO对象，该DAO只能执行带Connection的API
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static final <Type extends IBaseDAO>Type createDAO(Class<Type> clazz) throws Exception {
		String clazzName = clazz.getName();
		try {
			Type dao = (Type) clazz.newInstance();
			return dao;
		} catch (InstantiationException e) {
			throw new DBException("daoinstance can not created[" + clazzName + "]");
		} catch (IllegalAccessException e) {
			throw new DBException("daoinstance access exception[" + clazzName + "]");
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}
	
}
