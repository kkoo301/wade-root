/**
 * $
 */
package com.wade.dsf.adapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: JavaAdapter.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-20
 */
public class ObjectAdapter implements IDsfAdapter {

	
	@Override
	public Serializable streamToObject(Map<String, String> requestHeader, InputStream is, String charset) throws Exception {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(is);
			Object obj = ois.readObject();
			return (Serializable) obj;
		} catch (Exception e) {
			throw new IOException("类型转换异常", e);
		} finally {
			if (null != ois)
				ois.close();
		}
	}

	
	@Override
	public int objectToStream(Map<String, String> requestHeader, OutputStream out, Serializable object, String charset) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		
		byte[] bytes = baos.toByteArray();
		out.write(bytes);
		return bytes.length;
	}
	
	@Override
	public int exceptionToStream(Map<String, String> requestHeader, OutputStream out, Exception object, String charset) {
		return 0;
	}

}
