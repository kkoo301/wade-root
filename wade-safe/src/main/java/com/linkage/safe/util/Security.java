package com.linkage.safe.util;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 
 * @author $Id: Security.java 1 2014-02-20 08:34:02Z huangbo $
 *
 */
public class Security {  
	
	/*
	//这两个参数还不能全局,否则有并发性问题
	private static Cipher dcipher =null;
	private static MessageDigest md = null;
	static {
		try{
			dcipher = Cipher.getInstance("DES");
			md = MessageDigest.getInstance("MD5");
		}catch(Exception e){}
		
	}
	*/
	
	
	public static SecretKey getKey(String strKey) {
		try {
			DESKeySpec desKeySpec = new DESKeySpec(strKey.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey sk = keyFactory.generateSecret(desKeySpec);
			return sk;
		} catch (Exception e) {
			//log.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static String strDecrypt(SecretKey secretKey,String str) {
		try {
			Cipher dcipher = Cipher.getInstance("DES");
			byte[] dec = Base64Decoder.decodeToBytes(str); //new sun.misc.BASE64Decoder().decodeBuffer(str);
			dcipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] utf8 = dcipher.doFinal(dec);
			// Decode using utf-8
			return new String(utf8, "UTF8");
		} catch (Exception e) {
			//log.error(e.getMessage(), e);
		} 
		return null;
	}
	
	/*
	public static String strEncrypt(SecretKey secretKey, String str) {
		try {
			Cipher dcipher = Cipher.getInstance("DES");
			byte[] enc = str.getBytes("UTF8");
			dcipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] utf8 = dcipher.doFinal(enc);
			String dec = new sun.misc.BASE64Encoder().encode(utf8);
			return dec;
		} catch (Exception e) {
			//log.error(e.getMessage(), e);
		}
		return null;
	}
	*/
	
	public static String md5b(byte[] plainByte) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainByte);
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer(64);
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.substring(8, 24); //buf.toString();//
		} catch (Exception e) {
		return null;
		}
	}
	/*
	public static String md5b(byte[] plainByte,int offset, int len) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainByte, offset, len);
			byte b[]=md.digest();
			int i;
			StringBuffer buf = new StringBuffer(64);
			for (int index = 0; index < b.length; index++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.substring(8, 24); //buf.toString();//
		} catch (Exception e) {
		return null;
		}
	}
	*/
	
	public static String md5s(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer(64);
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.substring(8, 24); //buf.toString();//
		} catch (Exception e) {
		return null;
		}
	}
}