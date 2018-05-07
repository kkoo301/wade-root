/**
 * $
 */
package com.wade.dsf.executor.invoker;


import com.wade.dsf.registry.entity.IDsfEntity;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDsfInvoker.java
 * @description: 服务调用接口，提供正反序列化及服务方法反射调用功能
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @param <Req>
 * @param <Res>
 * @date: 2016-7-26
 */
public interface IDsfInvoker {
	
	public DsfResponse invoke(IDsfEntity entity, DsfRequest request) throws Exception;
	
}
