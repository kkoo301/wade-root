/**
 * $
 */
package com.wade.dsf.executor.invoker;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.wade.dsf.exception.DsfErr;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.executor.invoker.IDsfInvoker;
import com.wade.dsf.registry.entity.IDsfEntity;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfInvoker.java
 * @description: 默认的服务调用实现类，仅输反射方法调用，不做任何逻辑处理
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-7-26
 */
public class DsfInvoker implements IDsfInvoker {
	
	@Override
	public DsfResponse invoke(IDsfEntity entity, DsfRequest request) throws Exception {
		Object response = null;
		Object object = null;
		Method method = null;
		
		DsfException err = null;
		try {
			object = Class.forName(entity.getImplClass()).newInstance();
			method = entity.getMethod();
			
			response = method.invoke(object, request.getRequest());
		} catch (IllegalAccessException e) {
			err = new DsfException(DsfErr.dsf10019.getCode(), DsfErr.dsf10019.getInfo(object.getClass().getName() + "@" + method.getName()), e);
		} catch (IllegalArgumentException e) {
			err = new DsfException(DsfErr.dsf10019.getCode(), DsfErr.dsf10019.getInfo(object.getClass().getName() + "@" + method.getName()), e);
		} catch (InvocationTargetException e) {
			err = new DsfException(DsfErr.dsf10019.getCode(), DsfErr.dsf10019.getInfo(e.getTargetException().getMessage()), e.getTargetException());
		}
		
		if (null != err) {
			DsfResponse errResponse = new DsfResponse();
			errResponse.setError();
			errResponse.setResponse(err);
			return errResponse;
		}
		
		return new DsfResponse((Serializable)response);
	}

}
