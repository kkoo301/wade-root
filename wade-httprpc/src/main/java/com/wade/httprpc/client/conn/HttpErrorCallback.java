/**
 * $
 */
package com.wade.httprpc.client.conn;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: HttpCallback.java
 * @description: Http回调接口
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-10-25
 */
public interface HttpErrorCallback {
	
	public Serializable callback(Exception e);

}
