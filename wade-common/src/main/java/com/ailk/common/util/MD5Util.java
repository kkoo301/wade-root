package com.ailk.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.ailk.common.BaseException;

public final class MD5Util {
	
	private static final char[] hexchar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static MessageDigest alg;

	static {
		try {
			alg = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new BaseException("MD5Util-10000", e);
		}
	}

	private MD5Util() {}
	
	public static final String hexdigest(byte[] bytes) {
		byte[] digest = alg.digest(bytes);
		return bytesToHex(digest);
	}

	public static final String hexdigest(String str) {
		byte[] digest = alg.digest(str.getBytes());
		return bytesToHex(digest);
	}
	
	public static final String hexdigest_3(String str) {
		byte[] digest = null;
		digest = alg.digest(str.getBytes());
		digest = alg.digest(digest);
		digest = alg.digest(digest);
		return bytesToHex(digest);
	}
	
	private static final String bytesToHex(byte[] digest) {
		StringBuilder sb = new StringBuilder(digest.length * 2);
		for (int i = 0, size = digest.length; i < size; i++) {
			sb.append(hexchar[(digest[i] & 0xf0) >>> 4]);
			sb.append(hexchar[digest[i] & 0x0f]);
		}
		return sb.toString();
	}
}
