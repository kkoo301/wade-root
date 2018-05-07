package com.ailk.common.util.parser;

import com.ailk.common.data.IData;

/**
 * 
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IValueFilter.java
 * @author: liaosheng
 * @date: 2015-7-1
 */
public interface IValueFilter {

	/**
	 * 根据cell的配置过滤数据，当cell配置filter时触发
	 * 需要过滤的数据则返回错误描述，否则返回null即可
	 * @param config cell的配置
	 * @param value cell的值
	 * @return 返回过滤的错误描述信息，返回null则不做过滤
	 */
	public String filter(IData config, String value);
}
