package com.ailk.privm.cache;

import java.util.HashMap;
import java.util.Map;

import com.ailk.cache.redis.RedisFactory;
import com.ailk.cache.redis.client.RedisClient;
import com.ailk.org.apache.commons.lang3.SerializationUtils;
import com.ailk.privm.PConstants;


public class PrivilageDataDest {
	
	public static void load(String key, Map<String, String> data)throws Exception{
		if(null == data) {
			data = new HashMap<String, String>();
			data.put(PConstants.CacheParam.REDIS_EMPTY_KEY, PConstants.CacheParam.REDIS_EMPTY_KEY);
		} else if(data.size() == 0){
			data.put(PConstants.CacheParam.REDIS_EMPTY_KEY, PConstants.CacheParam.REDIS_EMPTY_KEY);
		}
		RedisClient redis = RedisFactory.getRedisClient(PConstants.CacheParam.REDIS_GROUP_NAME);
		redis.del(key);
		redis.hmset(key, data);
	}
	
	public static void loadSerial(String key, HashMap<String, String> data) throws Exception {
		if(null == data) {
			data = new HashMap<String, String>();
			data.put(PConstants.CacheParam.REDIS_EMPTY_KEY, PConstants.CacheParam.REDIS_EMPTY_KEY);
		} else if(data.size() == 0){
			data.put(PConstants.CacheParam.REDIS_EMPTY_KEY, PConstants.CacheParam.REDIS_EMPTY_KEY);
		}
		RedisClient redis = RedisFactory.getRedisClient(PConstants.CacheParam.REDIS_GROUP_NAME);
		redis.del(key.getBytes());
		redis.set(key.getBytes(), SerializationUtils.serialize(data));
	}
	
	public static void delete(String key) throws Exception{
		RedisClient redis = RedisFactory.getRedisClient(PConstants.CacheParam.REDIS_GROUP_NAME);
		redis.del(key);
	}
	
	public static void deleteSerial(String key) throws Exception{
		RedisClient redis = RedisFactory.getRedisClient(PConstants.CacheParam.REDIS_GROUP_NAME);
		redis.del(key.getBytes());
	}
}
