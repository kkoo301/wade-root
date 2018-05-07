/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月22日
 * 
 * Just Do IT.
 */
package com.wade.svf.flow.config.reader;

import com.wade.svf.flow.config.cache.XmlItem;

/**
 * @description
 * 读取流程配置
 */
public interface IFlowReader {
	
	/**
	 * 根据流程名读取配置信息
	 * @param name
	 * @return
	 */
	public XmlItem readXml(String name);

}
