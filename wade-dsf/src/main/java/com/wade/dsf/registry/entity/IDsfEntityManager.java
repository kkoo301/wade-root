/**
 * $
 */
package com.wade.dsf.registry.entity;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDsfEntityManager.java
 * @description: DsfEntity的管理器，实现根据名称获取Entity对象
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-18
 */
public interface IDsfEntityManager {
	
	public IDsfEntity getEntity(String name);
	
	public boolean addEntity(IDsfEntity entity);
	
}
