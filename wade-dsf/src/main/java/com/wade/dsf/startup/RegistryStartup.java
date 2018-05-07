/**
 * $
 */
package com.wade.dsf.startup;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wade.dsf.config.DsfConfigure;
import com.wade.dsf.exception.DsfErr;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.registry.IDsfRegistry;
import com.wade.dsf.registry.entity.IDsfEntity;
import com.wade.dsf.registry.entity.IDsfEntityManager;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: RegistryStartup.java
 * @description: 启动时触发服务注册
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-18
 */
public final class RegistryStartup implements IDsfStartup {
	
	private static final Logger log = Logger.getLogger(RegistryStartup.class);

	@Override
	public void startup() throws DsfException {
		DsfConfigure configure = DsfConfigure.getInstance();

		IDsfEntityManager manager = configure.getEntityManager();
		IDsfRegistry[] registries = configure.getRegistrys();

		for (IDsfRegistry registry : registries) {
			Map<String, IDsfEntity> entities = registry.regist();

			log.info("注册服务：" + registry.getClass().getName());
			
			Iterator<String> iter = entities.keySet().iterator();
			while (iter.hasNext()) {
				String name = iter.next();
				IDsfEntity entity = entities.get(name);

				if (!manager.addEntity(entity)) {
					throw new DsfException(DsfErr.dsf10000.getCode(),
							DsfErr.dsf10000.getInfo("服务名重复:" + name
									+ ", Registry:"
									+ registry.getClass().getName()));
				}
			}
		}
	}

}
