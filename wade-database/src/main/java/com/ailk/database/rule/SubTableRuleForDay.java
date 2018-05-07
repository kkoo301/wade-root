/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年5月4日
 * 
 * Just Do IT.
 */
package com.ailk.database.rule;

import com.ailk.database.rule.ISubTableRule;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;

/**
 * @description
 * 按天分表规则
 */
public class SubTableRuleForDay implements ISubTableRule {

	
	@Override
	public String getSplitTag() {
		return "_";
	}

	@Override
	public String getSubTable() {
		return DateFormatUtils.format(System.currentTimeMillis(), "dd");
	}

}
