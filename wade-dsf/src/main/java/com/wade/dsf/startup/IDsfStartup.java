/**
 * $
 */
package com.wade.dsf.startup;

import com.wade.dsf.exception.DsfException;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDsfStartup.java
 * @description: 服务启动处理逻辑
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-7-28
 */
public interface IDsfStartup {

	public void startup() throws DsfException;
}
