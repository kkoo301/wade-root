/**
 * $
 */
package com.wade.httprpc.test.obj;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TestRequest.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-18
 */
public class TestRequest implements Serializable {

	private static final long serialVersionUID = 6028278834722187084L;
	
	private byte[] bytes = null;
	
	/**
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}
	
	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
