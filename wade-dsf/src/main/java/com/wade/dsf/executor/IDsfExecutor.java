/**
 * $
 */
package com.wade.dsf.executor;

import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.response.DsfResponse;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IDsfExecutor.java
 * @description: 线程执行接口
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-19
 */
public interface IDsfExecutor {
	
	public DsfResponse execute(DsfRequest request) throws Exception;

}
