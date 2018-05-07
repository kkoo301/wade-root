package com.ailk.database.dialect;

import org.apache.log4j.Logger;

import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dialect.impl.OracleDialect;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: DBDialectFactory
 * @description: 数据库方言工厂类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-1
 */
public class DBDialectFactory {

	private static transient Logger log = Logger.getLogger(DBDialectFactory.class);
	
	/**
	 * 数据库方言编码
	 */
	public static final int ORACLE = 1;
	public static final int DB2    = 2;
	public static final int MYSQL  = 3;
	
	/**
	 * 数据库方言实例
	 */
	private static IDBDialect DBDIALECT = null;
	
	private DBDialectFactory() {
		
	}
	
	public static IDBDialect getDBDialect() {
		return DBDIALECT;
	}
	
	static {
		
		String dialect = null;
		try {
			dialect = DatabaseCfg.getDialect().trim();
			DBDIALECT = (IDBDialect) Class.forName(dialect).newInstance();
		} catch (Throwable e) {
			log.error("数据库方言实例化时发生错误，database.xml中定义的方言实现类为：" + dialect, e);
			DBDIALECT = new OracleDialect();
		} finally {
			log.info("数据库方言实现类:" + DBDIALECT.getClass().getName());
		}
		
	}
	
}
