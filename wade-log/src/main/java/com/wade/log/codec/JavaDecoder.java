package com.wade.log.codec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;

/**
 * Java解码
 * @author Shieh
 *
 */
public class JavaDecoder{
	
	private static Logger log = Logger.getLogger(JavaDecoder.class);
	
	/**
	 * byte[] 转换为整型
	 * @param bytes 
	 * @param off 开始位置
	 * @return
	 */
	public static int getInt(byte[] bytes, int off) {
		int b0 = bytes[off] & 0xFF;  
        int b1 = bytes[off + 1] & 0xFF;  
        int b2 = bytes[off + 2] & 0xFF;  
        int b3 = bytes[off + 3] & 0xFF;  
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;  
	}
	
	/**
	 * 对象解码 (解码byte数组)
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static Object decode(byte[] data) throws Exception{
		Object obj = null;

		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		
		try {
			bais = new ByteArrayInputStream(data);
			ois = new ObjectInputStream(bais);
			obj = ois.readObject();
		} catch (IOException e) {
			log.error("对象解码时发生IOException错误！", e);
		} catch (ClassNotFoundException e) {
			log.error("对象解码时发生ClassNotFoundException错误！", e);
		} finally {
			if (null != ois) {
				try {
					ois.close();
				} catch (IOException e) {
					log.error("对象解码，关闭ObjectInputStream时发生错误！", e);
				}
			}
		}
		
		return obj;	
	}
	
	/**
	 * 对象解码 （解码带长度信息的byte数组）
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static Object decodeByLen(byte[] data)  throws Exception{
		if(data.length < 4)
			return null;
		
		byte[] len = new byte[4];
		System.arraycopy(data, 0, len, 0, 4);
		
		int dataLen = getInt(len, 0);
		if(dataLen != data.length - 4){
			log.error("长度信息不匹配");
		}
		
		byte[] bytes = new byte[dataLen];
		System.arraycopy(data, 4, bytes, 0, dataLen);
		
		Object obj = null;

		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		
		try {
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			obj = ois.readObject();
		} catch (IOException e) {
			log.error("对象解码时发生IOException错误！", e);
		} catch (ClassNotFoundException e) {
			log.error("对象解码时发生ClassNotFoundException错误！", e);
		} finally {
			if (null != ois) {
				try {
					ois.close();
				} catch (IOException e) {
					log.error("对象解码，关闭ObjectInputStream时发生错误！", e);
				}
			}
		}
		
		return obj;	
	}
	
}