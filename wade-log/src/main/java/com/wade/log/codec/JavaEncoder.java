package com.wade.log.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * Java 编码
 * @author Shieh
 *
 */
public class JavaEncoder{
	
	private static Logger log = Logger.getLogger(JavaEncoder.class);
	
	/**
	 * 整型转换为byte[]
	 * @param data
	 * @return
	 */
	public static byte[] getBytes(int data){
		byte[] targets = new byte[4];  
        targets[3] = (byte) (data & 0xFF);  
        targets[2] = (byte) (data >> 8 & 0xFF);  
        targets[1] = (byte) (data >> 16 & 0xFF);  
        targets[0] = (byte) (data >> 24 & 0xFF);  
        return targets; 
	}
	
	/**
	 * 对象编码
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public static byte[] encode(Serializable obj) throws Exception{
		byte[] data = null;

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;

		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);

			data = baos.toByteArray();
		} catch (IOException e) {
			log.error("对象编码时发生IOException错误！", e);
		} finally {
			  if (null != oos) { 
				  try { 
					  oos.close(); 
				  } catch (IOException e) {
					  log.error("对象编码，关闭ObjectOutputStream时发生错误！", e);
				  } 
			  }
		}
		
		return data;
	}
	
	/**
	 * 对象编码 (包含长度信息)
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeByLen(Serializable obj) throws Exception{
		byte[] data = encode(obj);	
		byte[] tmp  = getBytes(data.length);
		byte[] ret  = new byte[tmp.length + data.length];
		
		System.arraycopy(tmp, 0, ret, 0, tmp.length);
		System.arraycopy(data, 0, ret, tmp.length, data.length);
		
		return ret;
	}
}