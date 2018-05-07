/**
 * $
 */
package com.wade.dsf.test.invoker;

import com.wade.dsf.executor.invoker.IDsfInvoker;
import com.wade.dsf.registry.entity.IDsfEntity;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: MyInvoker.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-20
 */
public class MyInvoker implements IDsfInvoker {


	@Override
	public DsfResponse invoke(IDsfEntity entity, DsfRequest request) throws Exception {
		//Object response = invokeMethod.invoke(invokeObject, request);
		return null;
	}

}
