/**
 * $
 */
package com.wade.dsf.registry;

import java.util.Iterator;
import java.util.Map;

import com.wade.dsf.config.DsfConfigure;
import com.wade.dsf.exception.DsfErr;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.registry.entity.IDsfEntity;
import com.wade.dsf.registry.entity.IDsfEntityManager;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfRegistryFactory.java
 * @description: 按dsf.xml@registry的配置顺序，一次性注册所有服务名，发现服务名重复时异常终止
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-18
 */
public final class DsfRegistryFactory {
	
	
	/**
	 * 注册所有服务
	 * @throws DsfException
	 */
	public static void regist() throws DsfException {
		DsfConfigure configure = DsfConfigure.getInstance();
		
		IDsfEntityManager manager = configure.getEntityManager();
		
		IDsfRegistry[] registrys = configure.getRegistrys();
		for (IDsfRegistry registry : registrys) {
			Map<String, IDsfEntity> entity = registry.regist();
			Iterator<String> iter = entity.keySet().iterator();
			while (iter.hasNext()) {
				String name = iter.next();
				
				if (manager.addEntity(entity.get(name))) {
					throw new DsfException(DsfErr.dsf10013.getCode(), DsfErr.dsf10013.getInfo(registry.getClass().getName(), name));
				}
			}
		}
	}

}
