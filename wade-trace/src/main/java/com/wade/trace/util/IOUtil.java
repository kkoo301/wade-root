package com.wade.trace.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IOUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(IOUtil.class);
	
	/**
	 * 将对象编码成byte数组
	 * 
	 * @param obj
	 * @return
	 */
	public static final byte[] encode(Object obj) {

		byte[] rtn = null;

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;

		try {
			
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			rtn = baos.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			  if (null != oos) { 
				  try { 
					  oos.close(); 
				  } catch (IOException e) {
				
				  } 
			  }
		}

		return rtn;
	}

	/**
	 * 将byte数组解码成对象
	 * 
	 * @param bytes
	 * @return
	 */
	public static final Object decode(byte[] bytes) {

		Object rtn = null;

		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;

		try {
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			rtn = ois.readObject();
		} catch (IOException e) {
			LOG.error("", e);
		} catch (ClassNotFoundException e) {
			LOG.error("", e);
		} finally {
			 if (null != ois) { 
				 try { 
					 ois.close(); 
				 } catch (IOException e) {
				 } 
			 }
		}

		return rtn;
	}
}
