/**
 * $
 */
package com.wade.dsf.adapter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: JsonStringAdapter.java
 * @description: 基于JSON串的适配器
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-19
 */
public final class StringAdapter implements IDsfAdapter {

	private static final int BUFFER_SIZE = 1024;

	
	/*
	 * 将请求数据当做字符串处理
	 * @see com.wade.dsf.adapter.IDsfAdapter#streamToObject(java.util.Map, java.io.InputStream, java.lang.String)
	 */
	@Override
	public Serializable streamToObject(Map<String, String> requestHeader, InputStream in, String charset) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024);
		byte[] data = new byte[BUFFER_SIZE];

		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return new String(outStream.toByteArray(), charset);
	}

	/*
	 * 将字符串数据输出到指字的流里
	 * @see com.wade.dsf.adapter.IDsfAdapter#objectToStream(java.util.Map, java.io.OutputStream, java.io.Serializable, java.lang.String)
	 */
	@Override
	public int objectToStream(Map<String, String> requestHeader, OutputStream out, Serializable object, String charset) throws Exception {
		if (object instanceof String) {
			byte[] bytes = ((String)object).getBytes(charset);
			
			out.write(bytes);
			return bytes.length;
		} else {
			byte[] bytes = ("{X_RESULTCODE=-1,X_RESULTINFO='object is not string'}").getBytes(charset);
			out.write(bytes);
			return bytes.length;
		}
	}
	
	@Override
	public int exceptionToStream(Map<String, String> requestHeader, OutputStream out, Exception object, String charset) {
		return 0;
	}
}
