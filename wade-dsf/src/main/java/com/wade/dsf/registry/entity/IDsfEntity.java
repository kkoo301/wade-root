/**
 * $
 */
package com.wade.dsf.registry.entity;

import java.lang.reflect.Method;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfEntity.java
 * @description: 服务实体对象，包括如下属性:<br>
 * 服务名<br>
 * 归属中心<br>
 * 服务实现类<br>
 * 服务执行方法<br>
 * 服务入参<br>
 * 服务出参<br>
 * 服务超时阈值<br>
 * 服务并发阈值<br>
 * 服务状态<br>
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-14
 */
public interface IDsfEntity {
	
	public String getName();
	
	public void setName(String name);
	
	public String getCenter();
	
	public void setCenter(String center);
	
	public String getImplClass();
	
	public void setImplClass(String className);
	
	public String getInputClass();
	
	public void setInputClass(String inputClass);
	
	public String getOutputClass();
	
	public void setOutputClass(String outputClass);
	
	public int getTimeout();
	
	public void setTimeout(int timeout);
	
	public int getConcurrency();
	
	public void setConcurrency(int concurrency);
	
	public Method getMethod();
	
	public void setMethod(Method method);
	
	public int getStatus();
	
	public void setStatus(int status);
}
