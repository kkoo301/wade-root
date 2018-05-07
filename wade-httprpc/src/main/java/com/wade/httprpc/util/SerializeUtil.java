/**
 * $
 */
package com.wade.httprpc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: Util.java
 * @description: 对象正反序列化工具类
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-14
 */
public final class SerializeUtil {
	
	
	/**
	 * 从Http请求里读取字节流, 并反序列化成 ServiceRequest
	 * @param req
	 * @return
	 * @throws IOException
	 */
	public static final Serializable deserialize(InputStream is) throws IOException {
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
	
	
	@SuppressWarnings("unchecked")
	public static final <T extends Serializable> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
		ObjectInputStream ois = null;
		String classA = null;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
			Object obj = ois.readObject();
			classA = obj.getClass().getName();
			return (T) obj;
		} catch (Exception e) {
			throw new IOException(String.format("类型转换异常%s->%s", classA, clazz.getName()), e);
		} finally {
			if (null != ois)
				ois.close();
		}
	}
	
	
	/**
	 * 获取可序列化对象的字节数组
	 * @param <I>
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static final <I> byte[] serialize(Serializable obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (Exception e) {
			throw new IOException("序列化异常", e);
		} finally {
			baos.close();
		}
	}

}
