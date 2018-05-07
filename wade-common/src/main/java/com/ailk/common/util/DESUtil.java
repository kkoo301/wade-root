package com.ailk.common.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.log4j.Logger;

import com.ailk.common.config.GlobalCfg;

/**
 * DES加密解密工具类
 * 
 */
public final class DESUtil {
	
	private static DESUtil	_DESUtil_inst	= null;
	private static transient final Logger log = Logger.getLogger(DESUtil.class);
	private static final String CIPHER_ALGORITHM = "DES"; //"DES/ECB/PKCS5Padding"; //CIPHER_ALGORITHM="DES" ,PKCS5Padding
	private static final String DES_KEY = GlobalCfg.getProperty("DES.key","wade_framwork");
	
	private DESUtil() {}

	/**
	 * str encrypt
	 * 
	 * @param key
	 * @param str
	 * @param random
	 * @return
	 */
	private String strEncrypt(String key, String str, boolean random) {
		try {
			// Encode the string into bytes using utf-8
			byte[] utf8 = str.getBytes("UTF8");

			// Encrypt
			Cipher ecipher = Cipher.getInstance(CIPHER_ALGORITHM);
			ecipher.init(Cipher.ENCRYPT_MODE, getKey(key));
			byte[] enc = ecipher.doFinal(utf8);
			
			return BASE64Util.encodeString(enc);
		} catch (javax.crypto.BadPaddingException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			log.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidKeyException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * decrypt
	 * 
	 * @param key
	 * @param str
	 * @param random
	 * @return
	 */
	private String strDecrypt(String key, String str, boolean random) {
		try {
			// Decode base64 to get bytes
			byte[] dec = BASE64Util.decode(str);
			
			// Decrypt
			Cipher dcipher = Cipher.getInstance(CIPHER_ALGORITHM);
			dcipher.init(Cipher.DECRYPT_MODE, getKey(key));
			byte[] utf8 = dcipher.doFinal(dec);

			// Decode using utf-8
			return new String(utf8, "UTF8");
		} catch (javax.crypto.BadPaddingException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		} catch (IllegalBlockSizeException e) {
			log.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		} catch (java.io.IOException e) {
			log.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidKeyException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	
	/**
	 * get key
	 * @param strKey
	 * @param random
	 * @return
	 */
	private static SecretKey getKey(String strKey) {
		try {
			/*if (random) {
				KeyGenerator _generator;
				_generator = KeyGenerator.getInstance("DES");
				_generator.init(new SecureRandom(strKey.getBytes()));
				return _generator.generateKey();
			} else {*/
				DESKeySpec desKeySpec = new DESKeySpec(strKey.getBytes());
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				return keyFactory.generateSecret(desKeySpec);
			//}
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidKeyException e) {
			log.error(e.getMessage(), e);
		} catch (InvalidKeySpecException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	private static DESUtil getInstance() {
		if (_DESUtil_inst == null) {
			_DESUtil_inst = new DESUtil();
		}
		return _DESUtil_inst;
	}

	public static String encrypt(String key, String str, boolean random) {
		return getInstance().strEncrypt(key, str, random);
	}

	public static String decrypt(String key, String str, boolean random) {
		return getInstance().strDecrypt(key, str, random);
	}

	public static String encrypt(String str) {
		return getInstance().strEncrypt(DES_KEY, str, false);
	}

	public static String decrypt(String str) {
		return getInstance().strDecrypt(DES_KEY, str, false);
	}
	
	public static void main(String[] args){
		//String s1="3V%2FBwMJ9DsIEUq%2B4NVXzSb0mXmWcncjmqRkBniE7jfx3Z3YGLlagoA%3D%3D";
		//String s2=URLDecoder.decode(s1,"UTF-8");
		
		//System.out.println(">>>>" + s2);
		
		//String str="7B97IHGSpobeTV8yvzqGYjZasfZGsYJyvP+HD7ZKRirT9q05R7b7pg==";
		//String str="qDdXXAMa+ebV30xOJ7VlJ6nMAewQ9UorlYvAiZWoRzCO4Qm5wWyAuQ==";
		
		String staffId="TST08704";
		String str1=encrypt(staffId);
		String str2=decrypt(str1);
		
		System.out.println(">>>>" + str1);
		System.out.println(">>>>" + str2);
		
		//System.out.println(">>>>" + decrypt(s2));
	}
	
}
