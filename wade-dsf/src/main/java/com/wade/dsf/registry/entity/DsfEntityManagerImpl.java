/**
 * $
 */
package com.wade.dsf.registry.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfEntityManagerImpl.java
 * @description: 管理注册的所有服务实体对象
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-18
 */
public final class DsfEntityManagerImpl implements IDsfEntityManager {

	private Map<String, IDsfEntity> entities = new HashMap<String, IDsfEntity>(10000);
	
	@Override
	public IDsfEntity getEntity(String name) {
		return entities.get(name);
	}

	
	@Override
	public boolean addEntity(IDsfEntity entity) {
		String name = entity.getName();
		
		IDsfEntity existsEntity = entities.get(name);
		if (null == existsEntity) {
			entities.put(name, entity);
			return true;
		}
		
		return false;
	}
}
