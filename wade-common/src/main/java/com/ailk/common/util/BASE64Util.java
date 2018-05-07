package com.ailk.common.util;

import java.math.BigInteger;

import com.ailk.org.apache.commons.codec.binary.Base64;

/**
 * Base64编码解码工具类
 * 
 */
public class BASE64Util{
	
	public boolean isBase64(String base64){
		return Base64.isBase64(base64);
	}
	
	public boolean isBase64(byte[] arrayOctet){
		return Base64.isBase64(arrayOctet);
	}
	
	/*******************************************************************/

	public static byte[] encode(byte[] binaryData){
		return Base64.encodeBase64(binaryData);
	}
	
	public static byte[] encode(byte[] binaryData, boolean isChunked){
		return Base64.encodeBase64(binaryData, isChunked);
	}
	
	public static String encodeString(String plainText) {
		byte[] b = plainText.getBytes();    
        return Base64.encodeBase64String(b); 
	}
	
	public static String encodeString(byte[] binaryData){
		return Base64.encodeBase64String(binaryData);
	}
	
	public static String encodeURLSafeString(byte[] binaryData){
		return Base64.encodeBase64URLSafeString(binaryData);
	}
	
	public static byte[] encodeInteger(BigInteger bigInt){
		return Base64.encodeInteger(bigInt);
	}
	
	public static byte[] decode(byte[] binaryData){
		return Base64.decodeBase64(binaryData);
	}
	
	public static byte[] decode(String base64String){
		return Base64.decodeBase64(base64String);
	}
	
	public static String decodeString(String base64String){
		return new String(Base64.decodeBase64(base64String));    
	}
	
	public static String decodeString(byte[] binaryData){
		return new String(Base64.decodeBase64(binaryData));
	}
	
	public static BigInteger decodeInteger(byte[] pArray){
		return Base64.decodeInteger(pArray);
	}
}