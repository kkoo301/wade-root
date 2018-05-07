package com.ailk.privm.cache;

import com.ailk.privm.PConstants;

public class KeyCreator {
	
	public static String createKey(String staffId, String privilageType) {
		return PConstants.CacheParam.REDIS_PRIV_KEY_PREFIX + staffId + "_" + privilageType;
	}
	
	public static String createKey(String staffId, String privilageType, int index) {
		return PConstants.CacheParam.REDIS_PRIV_KEY_PREFIX + staffId + "_" + privilageType + index;
	}
	
}
