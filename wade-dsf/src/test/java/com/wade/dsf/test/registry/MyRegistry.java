/**
 * $
 */
package com.wade.dsf.test.registry;

import java.util.HashMap;
import java.util.Map;

import com.wade.dsf.registry.IDsfRegistry;
import com.wade.dsf.registry.entity.BaseEntity;
import com.wade.dsf.registry.entity.IDsfEntity;
import com.wade.dsf.test.invoker.MyRequest;
import com.wade.dsf.test.invoker.MyResponse;
import com.wade.dsf.test.service.MyService;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: MyRegistry.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-18
 */
public class MyRegistry implements IDsfRegistry {
	
	private Map<String, IDsfEntity> entities = null;
	

	/* (non-Javadoc)
	 * @see com.wade.dsf.registry.IDsfRegistry#regist()
	 */
	@Override
	public Map<String, IDsfEntity> regist() {
		
		if (null == entities) {
			synchronized (this) {
				
				if (null == entities) {
					entities = new HashMap<String, IDsfEntity>(100);
					entities.put("MyService", create1("MyService"));
					entities.put("MyService2", create2("MyService2"));
					
					return entities;
				}
			}
		}
		
		return entities;
	}

	
	private IDsfEntity create1(String serviceName) {
		try {
			
			IDsfEntity entity = new BaseEntity();
			entity.setName(serviceName);
			entity.setCenter("ord");
			
			entity.setImplClass(MyService.class.getName());
			entity.setInputClass(String.class.getName());
			entity.setMethod(MyService.class.getMethod("hello", new Class[] {String.class}));
			entity.setOutputClass(String.class.getName());
			
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private IDsfEntity create2(String serviceName) {
		try {
			
			IDsfEntity entity = new BaseEntity();
			entity.setName(serviceName);
			entity.setCenter("ord");
			
			entity.setImplClass(MyService.class.getName());
			entity.setInputClass(MyRequest.class.getName());
			entity.setMethod(MyService.class.getMethod("hello", new Class[] {MyRequest.class}));
			entity.setOutputClass(MyResponse.class.getName());
			
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
