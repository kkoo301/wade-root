/**
 * $
 */
package com.wade.httprpc.test.obj;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TestResponse.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-18
 */
public class TestResponse implements Serializable {

	private static final long serialVersionUID = 3247353448147900124L;
	
	private int code;
	
	private String info;
	
	private byte[] bytes = null;
	
	public TestResponse() {
		this(0, "OK");
	}
	
	public TestResponse(int code, String info) {
		this.code = code;
		this.info = info;
	}
	
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}
	
	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}
	
	
	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	
	
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
