/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月26日
 * 
 * Just Do IT.
 */
package com.ailk.database.rule;

/**
 * @description
 * 分表规则接口，最终生成的表名格式为主表名+分隔符+子表名
 */
public interface ISubTableRule {
	
	/**
	 * 获取分隔符
	 * @return
	 */
	public String getSplitTag();
	
	/**
	 * 获取子表
	 * @return
	 */
	public String getSubTable();

}
