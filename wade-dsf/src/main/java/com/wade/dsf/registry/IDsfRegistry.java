/**
 * $
 */
package com.wade.dsf.registry;

import java.util.Map;

import com.wade.dsf.exception.DsfException;
import com.wade.dsf.registry.entity.IDsfEntity;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDsfRegistry.java
 * @description: 服务注册接口
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-14
 */
public interface IDsfRegistry {
	
	public Map<String, IDsfEntity> regist() throws DsfException;

}
