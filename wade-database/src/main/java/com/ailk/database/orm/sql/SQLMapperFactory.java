/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月21日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.sql;

import com.ailk.database.orm.bo.BOEntity;
import com.ailk.database.orm.sql.oracle.OracleSQLMapper;
import com.ailk.database.orm.util.BOUtil;

/**
 * @description
 * SQL生成器工厂类
 */
public final class SQLMapperFactory {
	
	/**
	 * Oracle SQL　生成器
	 */
	private static ISQLMapper oracle = new OracleSQLMapper();
	
	
	/**
	 * 根据BO的数据库方言获取对应的SQL生成器
	 * @param entity
	 * @return
	 */
	public static ISQLMapper getMapper(BOEntity entity) {
		String dialect = BOUtil.getDialect(entity.getClass());
		
		if ("oracle".equals(dialect)) {
			return oracle;
		}
		
		return null;
	}

	
}
