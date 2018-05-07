package com.ailk.common.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA 加密解密类
 * @author Shieh
 *
 */
public class RSAUtil {

	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivateKey";
	
	public static final byte[] PUBLIC_KEY_BYTES = {
		(byte)0x30, (byte)0x81, (byte)0x9F, (byte)0x30, (byte)0x0D, (byte)0x06, (byte)0x09, (byte)0x2A, (byte)0x86, 
		(byte)0x48, (byte)0x86, (byte)0xF7, (byte)0x0D, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x05, (byte)0x00, 
		(byte)0x03, (byte)0x81, (byte)0x8D, (byte)0x00, (byte)0x30, (byte)0x81, (byte)0x89, (byte)0x02, (byte)0x81, 
		(byte)0x81, (byte)0x00, (byte)0xAA, (byte)0x0C, (byte)0x62, (byte)0xCE, (byte)0xD2, (byte)0x1B, (byte)0x3A, 
		(byte)0x62, (byte)0x62, (byte)0x7A, (byte)0x09, (byte)0x76, (byte)0x8A, (byte)0x68, (byte)0x73, (byte)0xB1, 
		(byte)0x8A, (byte)0x2F, (byte)0x04, (byte)0x73, (byte)0x00, (byte)0x1C, (byte)0x5B, (byte)0x23, (byte)0xE5, 
		(byte)0x54, (byte)0x6B, (byte)0x44, (byte)0x5B, (byte)0xE4, (byte)0x8E, (byte)0x79, (byte)0xDD, (byte)0x94, 
		(byte)0x65, (byte)0x5F, (byte)0xD0, (byte)0x0F, (byte)0x9B, (byte)0x4C, (byte)0x74, (byte)0xB2, (byte)0x12, 
		(byte)0xDF, (byte)0x9A, (byte)0x86, (byte)0xEA, (byte)0xE7, (byte)0xC0, (byte)0x4D, (byte)0x00, (byte)0x6B, 
		(byte)0xED, (byte)0xE8, (byte)0x45, (byte)0xC3, (byte)0x1C, (byte)0x6D, (byte)0x4B, (byte)0x16, (byte)0x96, 
		(byte)0x58, (byte)0xE6, (byte)0xB3, (byte)0x7B, (byte)0xA3, (byte)0xE5, (byte)0x10, (byte)0x7B, (byte)0xE7, 
		(byte)0x93, (byte)0xBC, (byte)0x81, (byte)0x5F, (byte)0xF1, (byte)0x32, (byte)0xD3, (byte)0xD4, (byte)0x1D, 
		(byte)0x44, (byte)0xF7, (byte)0xB3, (byte)0xF2, (byte)0xDE, (byte)0x12, (byte)0x46, (byte)0xC1, (byte)0x44, 
		(byte)0x70, (byte)0x83, (byte)0x3A, (byte)0x78, (byte)0x0E, (byte)0x3B, (byte)0x45, (byte)0xB7, (byte)0x2E, 
		(byte)0xE9, (byte)0x69, (byte)0x6A, (byte)0x53, (byte)0xAB, (byte)0xF5, (byte)0x7A, (byte)0xAF, (byte)0x7B, 
		(byte)0x5E, (byte)0x69, (byte)0x13, (byte)0xA0, (byte)0xD9, (byte)0x9A, (byte)0x39, (byte)0x02, (byte)0xE9, 
		(byte)0x6F, (byte)0x76, (byte)0xE1, (byte)0xA4, (byte)0x79, (byte)0xE6, (byte)0xFD, (byte)0x7F, (byte)0xED, 
		(byte)0x87, (byte)0x5B, (byte)0x1C, (byte)0x17, (byte)0x02, (byte)0x03, (byte)0x01, (byte)0x00, (byte)0x01
	};
	
	static final byte[] PRIVATE_KEY_BYTES = { 
		(byte)0x30, (byte)0x82, (byte)0x02, (byte)0x77, (byte)0x02, (byte)0x01, (byte)0x00, (byte)0x30, (byte)0x0D, 
		(byte)0x06, (byte)0x09, (byte)0x2A, (byte)0x86, (byte)0x48, (byte)0x86, (byte)0xF7, (byte)0x0D, (byte)0x01, 
		(byte)0x01, (byte)0x01, (byte)0x05, (byte)0x00, (byte)0x04, (byte)0x82, (byte)0x02, (byte)0x61, (byte)0x30, 
		(byte)0x82, (byte)0x02, (byte)0x5D, (byte)0x02, (byte)0x01, (byte)0x00, (byte)0x02, (byte)0x81, (byte)0x81, 
		(byte)0x00, (byte)0xAA, (byte)0x0C, (byte)0x62, (byte)0xCE, (byte)0xD2, (byte)0x1B, (byte)0x3A, (byte)0x62, 
		(byte)0x62, (byte)0x7A, (byte)0x09, (byte)0x76, (byte)0x8A, (byte)0x68, (byte)0x73, (byte)0xB1, (byte)0x8A, 
		(byte)0x2F, (byte)0x04, (byte)0x73, (byte)0x00, (byte)0x1C, (byte)0x5B, (byte)0x23, (byte)0xE5, (byte)0x54, 
		(byte)0x6B, (byte)0x44, (byte)0x5B, (byte)0xE4, (byte)0x8E, (byte)0x79, (byte)0xDD, (byte)0x94, (byte)0x65, 
		(byte)0x5F, (byte)0xD0, (byte)0x0F, (byte)0x9B, (byte)0x4C, (byte)0x74, (byte)0xB2, (byte)0x12, (byte)0xDF, 
		(byte)0x9A, (byte)0x86, (byte)0xEA, (byte)0xE7, (byte)0xC0, (byte)0x4D, (byte)0x00, (byte)0x6B, (byte)0xED, 
		(byte)0xE8, (byte)0x45, (byte)0xC3, (byte)0x1C, (byte)0x6D, (byte)0x4B, (byte)0x16, (byte)0x96, (byte)0x58, 
		(byte)0xE6, (byte)0xB3, (byte)0x7B, (byte)0xA3, (byte)0xE5, (byte)0x10, (byte)0x7B, (byte)0xE7, (byte)0x93, 
		(byte)0xBC, (byte)0x81, (byte)0x5F, (byte)0xF1, (byte)0x32, (byte)0xD3, (byte)0xD4, (byte)0x1D, (byte)0x44, 
		(byte)0xF7, (byte)0xB3, (byte)0xF2, (byte)0xDE, (byte)0x12, (byte)0x46, (byte)0xC1, (byte)0x44, (byte)0x70, 
		(byte)0x83, (byte)0x3A, (byte)0x78, (byte)0x0E, (byte)0x3B, (byte)0x45, (byte)0xB7, (byte)0x2E, (byte)0xE9, 
		(byte)0x69, (byte)0x6A, (byte)0x53, (byte)0xAB, (byte)0xF5, (byte)0x7A, (byte)0xAF, (byte)0x7B, (byte)0x5E, 
		(byte)0x69, (byte)0x13, (byte)0xA0, (byte)0xD9, (byte)0x9A, (byte)0x39, (byte)0x02, (byte)0xE9, (byte)0x6F, 
		(byte)0x76, (byte)0xE1, (byte)0xA4, (byte)0x79, (byte)0xE6, (byte)0xFD, (byte)0x7F, (byte)0xED, (byte)0x87, 
		(byte)0x5B, (byte)0x1C, (byte)0x17, (byte)0x02, (byte)0x03, (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x02, 
		(byte)0x81, (byte)0x80, (byte)0x2A, (byte)0x74, (byte)0x51, (byte)0x2E, (byte)0xE5, (byte)0x91, (byte)0xDA, 
		(byte)0xAF, (byte)0xB9, (byte)0x7A, (byte)0x52, (byte)0x66, (byte)0x80, (byte)0x03, (byte)0xB7, (byte)0xEE, 
		(byte)0xB9, (byte)0x5B, (byte)0x27, (byte)0xB7, (byte)0x36, (byte)0x6D, (byte)0xDD, (byte)0xEB, (byte)0x46, 
		(byte)0x54, (byte)0x1F, (byte)0x3B, (byte)0xBF, (byte)0xDD, (byte)0x7E, (byte)0x1C, (byte)0xBA, (byte)0xCF, 
		(byte)0x53, (byte)0xC7, (byte)0xEA, (byte)0x00, (byte)0x1B, (byte)0x7B, (byte)0xA9, (byte)0x1E, (byte)0xD4, 
		(byte)0x4B, (byte)0x73, (byte)0x1F, (byte)0x00, (byte)0x02, (byte)0xC0, (byte)0x25, (byte)0xF9, (byte)0x56, 
		(byte)0xF8, (byte)0xDC, (byte)0xA6, (byte)0x9E, (byte)0x21, (byte)0xB8, (byte)0xBF, (byte)0xA8, (byte)0x71, 
		(byte)0xFC, (byte)0xDC, (byte)0x0B, (byte)0xC7, (byte)0x36, (byte)0xD2, (byte)0x85, (byte)0xBA, (byte)0x35, 
		(byte)0xC7, (byte)0x9C, (byte)0x74, (byte)0xA2, (byte)0x36, (byte)0x30, (byte)0x9B, (byte)0x3B, (byte)0x93, 
		(byte)0x9D, (byte)0xD4, (byte)0x51, (byte)0x21, (byte)0xF3, (byte)0x4F, (byte)0xB6, (byte)0xE3, (byte)0x3F, 
		(byte)0x19, (byte)0x67, (byte)0x1C, (byte)0x31, (byte)0x9E, (byte)0x4B, (byte)0x62, (byte)0x21, (byte)0x5E, 
		(byte)0x79, (byte)0x4A, (byte)0x4A, (byte)0x65, (byte)0x90, (byte)0xFE, (byte)0xC7, (byte)0xEE, (byte)0x30, 
		(byte)0xD1, (byte)0x7A, (byte)0x4A, (byte)0x3D, (byte)0x6B, (byte)0x66, (byte)0xD1, (byte)0xBB, (byte)0x40, 
		(byte)0x94, (byte)0xB8, (byte)0x6A, (byte)0x48, (byte)0x49, (byte)0x09, (byte)0xE7, (byte)0x85, (byte)0x16, 
		(byte)0xA5, (byte)0x78, (byte)0x0F, (byte)0xA1, (byte)0x02, (byte)0x41, (byte)0x00, (byte)0xD6, (byte)0xC4, 
		(byte)0x4C, (byte)0x85, (byte)0x65, (byte)0x89, (byte)0xE2, (byte)0x84, (byte)0xA9, (byte)0xFB, (byte)0x8D, 
		(byte)0x15, (byte)0x01, (byte)0x9B, (byte)0x51, (byte)0x71, (byte)0x36, (byte)0xAB, (byte)0x29, (byte)0x36, 
		(byte)0x4B, (byte)0x50, (byte)0xAA, (byte)0x51, (byte)0x58, (byte)0xF9, (byte)0x44, (byte)0x9F, (byte)0x2B, 
		(byte)0x4D, (byte)0x29, (byte)0x6D, (byte)0x76, (byte)0xBB, (byte)0x00, (byte)0x6A, (byte)0xA5, (byte)0x6C, 
		(byte)0x72, (byte)0x35, (byte)0x83, (byte)0xAD, (byte)0xB4, (byte)0xCD, (byte)0xBE, (byte)0xED, (byte)0xDE, 
		(byte)0x6A, (byte)0xF8, (byte)0x07, (byte)0x16, (byte)0xC0, (byte)0x72, (byte)0xFE, (byte)0xE4, (byte)0x93, 
		(byte)0x7B, (byte)0x74, (byte)0x89, (byte)0x46, (byte)0x72, (byte)0x9C, (byte)0x31, (byte)0x0F, (byte)0x02, 
		(byte)0x41, (byte)0x00, (byte)0xCA, (byte)0xB2, (byte)0x31, (byte)0xFD, (byte)0xA2, (byte)0xCF, (byte)0xF1, 
		(byte)0x34, (byte)0xE5, (byte)0x66, (byte)0x89, (byte)0x8F, (byte)0x6B, (byte)0x94, (byte)0xC8, (byte)0x0C, 
		(byte)0xD4, (byte)0xEB, (byte)0xC6, (byte)0x9C, (byte)0xF4, (byte)0x94, (byte)0xF9, (byte)0xB9, (byte)0x19, 
		(byte)0x01, (byte)0xA0, (byte)0x98, (byte)0xAA, (byte)0xE6, (byte)0xD8, (byte)0x17, (byte)0x44, (byte)0x6B, 
		(byte)0xFB, (byte)0x1B, (byte)0x28, (byte)0x43, (byte)0xE3, (byte)0x62, (byte)0x82, (byte)0x00, (byte)0x4C, 
		(byte)0x56, (byte)0xD6, (byte)0x48, (byte)0xB0, (byte)0x14, (byte)0x7B, (byte)0x3F, (byte)0x90, (byte)0xB8, 
		(byte)0x73, (byte)0x24, (byte)0xFB, (byte)0xDD, (byte)0xBF, (byte)0xCB, (byte)0xB1, (byte)0x9E, (byte)0xAF, 
		(byte)0xA1, (byte)0x54, (byte)0x79, (byte)0x02, (byte)0x41, (byte)0x00, (byte)0x9E, (byte)0x65, (byte)0x32, 
		(byte)0x86, (byte)0xA3, (byte)0xE7, (byte)0xB8, (byte)0xEF, (byte)0xDD, (byte)0x2A, (byte)0x50, (byte)0xD8, 
		(byte)0x30, (byte)0x52, (byte)0x2C, (byte)0x13, (byte)0xD2, (byte)0x9D, (byte)0x14, (byte)0x0D, (byte)0x1E, 
		(byte)0x29, (byte)0x05, (byte)0x24, (byte)0x6F, (byte)0xF9, (byte)0x8F, (byte)0xF9, (byte)0xD0, (byte)0x86, 
		(byte)0xDD, (byte)0x4A, (byte)0x05, (byte)0x6A, (byte)0x50, (byte)0x19, (byte)0x57, (byte)0x9E, (byte)0x0E, 
		(byte)0xF1, (byte)0x97, (byte)0x07, (byte)0x73, (byte)0x34, (byte)0xD7, (byte)0x5F, (byte)0x3A, (byte)0x4E, 
		(byte)0x2C, (byte)0x55, (byte)0x96, (byte)0x1B, (byte)0x23, (byte)0xF5, (byte)0x08, (byte)0x02, (byte)0x19, 
		(byte)0xE8, (byte)0x4E, (byte)0x22, (byte)0x6D, (byte)0xFE, (byte)0xF2, (byte)0x53, (byte)0x02, (byte)0x41, 
		(byte)0x00, (byte)0x98, (byte)0x80, (byte)0x91, (byte)0x94, (byte)0x1F, (byte)0x17, (byte)0x0C, (byte)0x87, 
		(byte)0x9B, (byte)0xC1, (byte)0x15, (byte)0xE6, (byte)0x4E, (byte)0x2E, (byte)0xD6, (byte)0x84, (byte)0xB0, 
		(byte)0xFD, (byte)0xE3, (byte)0xA4, (byte)0xDB, (byte)0x6F, (byte)0xEA, (byte)0xA3, (byte)0x14, (byte)0xE9, 
		(byte)0x60, (byte)0x86, (byte)0xFC, (byte)0xAE, (byte)0x4A, (byte)0x0E, (byte)0xD7, (byte)0x6F, (byte)0xD6, 
		(byte)0xB1, (byte)0x8C, (byte)0x0D, (byte)0xE7, (byte)0xDA, (byte)0x89, (byte)0xAC, (byte)0xE7, (byte)0xC2, 
		(byte)0xBD, (byte)0xDE, (byte)0x5A, (byte)0x7C, (byte)0x4E, (byte)0x6E, (byte)0x78, (byte)0xD8, (byte)0x0D, 
		(byte)0xE9, (byte)0xD9, (byte)0x85, (byte)0xF1, (byte)0x73, (byte)0xB3, (byte)0xE3, (byte)0x8C, (byte)0x88, 
		(byte)0x79, (byte)0x21, (byte)0x02, (byte)0x40, (byte)0x16, (byte)0x7F, (byte)0x8A, (byte)0xB0, (byte)0xF1, 
		(byte)0x6E, (byte)0xEE, (byte)0x60, (byte)0x99, (byte)0xED, (byte)0xB4, (byte)0xAE, (byte)0xD7, (byte)0x1C, 
		(byte)0xC3, (byte)0xA1, (byte)0x33, (byte)0x8D, (byte)0xFA, (byte)0xAC, (byte)0xC5, (byte)0x37, (byte)0x3E, 
		(byte)0xFA, (byte)0xF5, (byte)0x14, (byte)0xB8, (byte)0x77, (byte)0xF9, (byte)0x59, (byte)0x87, (byte)0x85, 
		(byte)0xDD, (byte)0x13, (byte)0x5A, (byte)0xFE, (byte)0x0A, (byte)0x23, (byte)0x69, (byte)0x54, (byte)0x4B, 
		(byte)0xFF, (byte)0xF2, (byte)0xCC, (byte)0xC4, (byte)0xF5, (byte)0x6C, (byte)0x6C, (byte)0x30, (byte)0x52, 
		(byte)0x51, (byte)0xFC, (byte)0xB1, (byte)0x64, (byte)0xF0, (byte)0x86, (byte)0x18, (byte)0xAD, (byte)0x9E, 
		(byte)0xA0, (byte)0xBE, (byte)0xE6, (byte)0x6C, (byte)0x8E
	};
	
	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, byte[] privateKey) throws Exception {
		
		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
		
		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		
		// 取私钥匙对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
		
		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);
		return BASE64Util.encodeString(signature.sign());
	}
	

	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		
		// 解密由base64编码的私钥
		byte[] keyBytes = BASE64Util.decode(privateKey);
		
		return sign(data, keyBytes);
	}
	
	/**
	 * 校验数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * @return 校验成功返回true 失败返回false
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, byte[] publicKey, String sign) throws Exception {
		
		// 构造X509EncodedKeySpec对象
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
		
		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		
		// 取公钥匙对象
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);
		
		// 验证签名是否正常
		return signature.verify(BASE64Util.decode(sign));
	}
	

	/**
	 * 校验数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * @return 校验成功返回true 失败返回false
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		
		// 解密由base64编码的公钥
		byte[] keyBytes = BASE64Util.decode(publicKey);
		
		return verify(data, keyBytes, sign);
	}
	
	/**
	 * 使用私钥解密
	 * @param data
	 *            加密数据
	 * @param key
	 *            私钥
	 * @return 返回解密数据 byte[]
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
		
		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		
		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	/**
	 * 使用私钥解密
	 * @param data
	 *            加密数据
	 * @param key
	 *            私钥
	 * @return 返回解密数据 byte[]
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
		
		// 对密钥解密
		byte[] keyBytes = BASE64Util.decode(key);
		
		return decryptByPrivateKey(data, keyBytes);
	}
	
	/**
	 * 使用私钥解密
	 * @param data
	 *            加密数据
	 * @param key
	 *            私钥
	 * @return 返回解密数据 String
	 * @throws Exception
	 */
	public static String decryptByPrivateKey(String data, byte[] key) throws Exception {
		
		// 对内容解密
		byte[] dataBytes = BASE64Util.decode(data);
		
		return new String( decryptByPrivateKey(dataBytes, key) );
	}

	/**
	 * 解密<br>
	 * 用私钥解密加密字符串
	 * 
	 * @param data
	 *            加密字符串
	 * @param key
	 *            私钥
	 * @return  返回解密数据  String
	 * @throws Exception
	 */
	public static String decryptByPrivateKey(String data, String key) throws Exception {
		
		// 对密钥解密
		byte[] keyBytes = BASE64Util.decode(key);
		
		return decryptByPrivateKey(data, keyBytes);
	}	
	
	/**
	 * 使用默认私钥解密
	 * @param data
	 *            加密数据
	 * @return    返回解密数据  String
	 * @throws Exception
	 */
	public static String decryptByPrivateKey(String data) throws Exception {
		//解密
		return decryptByPrivateKey(data, PRIVATE_KEY_BYTES);
	}
	
	/**
	 * 解密<br>
	 * 用公钥解密
	 * 
	 * @param data
	 *            加密数据
	 * @param key
	 *            公钥
	 * @return     返回解密数据byte[]
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {
		
		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicKey = keyFactory.generatePublic(x509KeySpec);
		
		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}

	/**
	 * 解密<br>
	 * 用公钥解密
	 * 
	 * @param data
	 *            加密数据
	 * @param key
	 *            公钥
	 * @return     返回解密数据 byte[]
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
		
		// 对密钥解密
		byte[] keyBytes = BASE64Util.decode(key);
		
		return decryptByPublicKey(data, keyBytes);
	}
	
	/**
	 * 解密<br>
	 * 用公钥解密
	 * @param data
	 *         加密数据
	 * @param key
	 *         公钥
	 * @return  返回解密数据 String
	 * @throws Exception
	 */
	public static String decryptByPublicKey(String data, byte[] key) throws Exception{
		
		// Base64解码 data
		byte[] dataBytes = BASE64Util.decode(data);
		
		return new String( decryptByPublicKey(dataBytes, key) );
	}
	
	/**
	 * 解密<br>
	 * 用公钥解密
	 * @param data
	 *         加密数据
	 * @param key
	 *         公钥
	 * @return  返回解密数据 String
	 * @throws Exception
	 */
	public static String decryptByPublicKey(String data, String key) throws Exception{
		
		// 对密钥解密
		byte[] keyBytes = BASE64Util.decode(key);
						
		return decryptByPublicKey(data, keyBytes);
	}
	
	/**
	 * 解密<br>
	 * 用公钥解密
	 * @param data 加密数据
	 * @return 返回解密数据 String
	 * @throws Exception
	 */
	public static String decryptByPublicKey(String data) throws Exception{
		return decryptByPublicKey(data, PUBLIC_KEY_BYTES);
	}
	
	/**
	 * 加密<br>
	 * 用公钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {
		
		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicKey = keyFactory.generatePublic(x509KeySpec);
		
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}
	
	/**
	 * 加密<br>
	 * 用公钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String data, byte[] key) throws Exception {
		// 使用Base64编码
		return BASE64Util.encodeString( encryptByPublicKey(data.getBytes(), key) );
	}

	
	/**
	 * 加密<br>
	 * 用公钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String data, String key) throws Exception {
		
		// 对公钥转码
		byte[] keyBytes = BASE64Util.decode(key);
		
		return encryptByPublicKey(data, keyBytes);
	}
	
	/**
	 * 加密<br>
	 * 用公钥加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String data) throws Exception {
		return encryptByPublicKey(data, PUBLIC_KEY_BYTES);
	}
	
	
	/**
	 * 加密<br>
	 * 用私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {
		
		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	/**
	 * 加密<br>
	 * 用私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
		
		// 对密钥解密
		byte[] keyBytes = BASE64Util.decode(key);

		return encryptByPrivateKey(data, keyBytes);
	}
	
	/**
	 * 加密<br>
	 * 用私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPrivateKey(String data, byte[] key) throws Exception {
		//加密
		return BASE64Util.encodeString( encryptByPrivateKey(data.getBytes(), key) );
	}
	
	/**
	 * 加密<br>
	 * 用私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPrivateKey(String data, String key) throws Exception {
		
		// 对密钥解密
		byte[] keyBytes = BASE64Util.decode(key);

		return encryptByPrivateKey(data, keyBytes);
	}
	
	/**
	 * * 加密<br>
	 * 用私钥加密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPrivateKey(String data) throws Exception {
		//加密
		return encryptByPrivateKey(data, PRIVATE_KEY_BYTES);
	}	
	

	/**
	 * 取得私钥
	 * 
	 * @param keyMap
	 * @return 返回私钥
	 * @throws Exception
	 */
	public static String getPrivateKey(Map<String, Key> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return BASE64Util.encodeString(key.getEncoded());
	}

	/**
	 * 取得公钥
	 * 
	 * @param keyMap
	 * @return 
	 * @throws Exception
	 */
	public static String getPublicKey(Map<String, Key> keyMap) throws Exception {
		Key key = keyMap.get(PUBLIC_KEY);
		return BASE64Util.encodeString(key.getEncoded());
	}

	/**
	 * 初始化密钥
	 * 
	 * @return 返回keyMap
	 * @throws Exception
	 */
	public static Map<String, Key> initKey() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		Map<String, Key> keyMap = new HashMap<String, Key>(2);
		keyMap.put(PUBLIC_KEY, keyPair.getPublic());     //公钥
		keyMap.put(PRIVATE_KEY, keyPair.getPrivate());   //私钥
		return keyMap;
	}

	public static void main(String[] args) throws Exception {
		/*
		 * Map<String, Key> keyMap = RSAUtil.initKey(); String publicKey =
		 * RSAUtil.getPublicKey(keyMap); String privateKey =
		 * RSAUtil.getPrivateKey(keyMap);
		 * 
		 * System.out.println("公钥: \n\r" + publicKey);
		 * System.out.println("私钥： \n\r" + privateKey);
		 */

		//String enStr = BASE64Util.encodeString( encryptByPublicKey("ewqeq") );
		//System.out.println(enStr);
		
		String enStr = "om7SwXvmhdIN3mzu0EMf2jAeps8ojUfbp+gEBDUxbjLnGWd1p0XFPEEzDuiDzw43o/KxYBwte43dQzh5shoTKBca2Lm+4OawGhjFC9Nbi9tfOCAXBxELi4ZlVkUV67mkUqm28tCb15eXhNKJ5qZpORbeXFIZQ4bFkJFfq+NJuyg=";
		//String privKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKoMYs7SGzpiYnoJdopoc7GKLwRzABxbI+VUa0Rb5I553ZRlX9APm0x0shLfmobq58BNAGvt6EXDHG1LFpZY5rN7o+UQe+eTvIFf8TLT1B1E97Py3hJGwURwgzp4DjtFty7paWpTq/V6r3teaROg2Zo5AulvduGkeeb9f+2HWxwXAgMBAAECgYAqdFEu5ZHar7l6UmaAA7fuuVsntzZt3etGVB87v91+HLrPU8fqABt7qR7US3MfAALAJflW+NymniG4v6hx/NwLxzbShbo1x5x0ojYwmzuTndRRIfNPtuM/GWccMZ5LYiFeeUpKZZD+x+4w0XpKPWtm0btAlLhqSEkJ54UWpXgPoQJBANbETIVlieKEqfuNFQGbUXE2qyk2S1CqUVj5RJ8rTSltdrsAaqVscjWDrbTNvu3eavgHFsBy/uSTe3SJRnKcMQ8CQQDKsjH9os/xNOVmiY9rlMgM1OvGnPSU+bkZAaCYqubYF0Rr+xsoQ+NiggBMVtZIsBR7P5C4cyT73b/LsZ6voVR5AkEAnmUyhqPnuO/dKlDYMFIsE9KdFA0eKQUkb/mP+dCG3UoFalAZV54O8ZcHczTXXzpOLFWWGyP1CAIZ6E4ibf7yUwJBAJiAkZQfFwyHm8EV5k4u1oSw/eOk22/qoxTpYIb8rkoO12/WsYwN59qJrOfCvd5afE5ueNgN6dmF8XOz44yIeSECQBZ/irDxbu5gme20rtccw6EzjfqsxTc++vUUuHf5WYeF3RNa/gojaVRL//LMxPVsbDBSUfyxZPCGGK2eoL7mbI4=";

		System.out.println( new String( decryptByPrivateKey(enStr) ) );
		
	}
}