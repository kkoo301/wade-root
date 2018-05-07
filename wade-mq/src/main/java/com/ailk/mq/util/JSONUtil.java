package com.ailk.mq.util;

import java.util.Map;
import net.sf.json.JSONObject;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: JSONUtil
 * @description: JSON工具类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-8-2
 */
public final class JSONUtil {
	
	private JSONUtil() {
		
	}
	
	@SuppressWarnings("rawtypes")
	public static final String encode(Map map) {
		JSONObject obj = JSONObject.fromObject(map);
		return obj.toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final Map decode(String jsonString) {
		JSONObject obj = JSONObject.fromObject(jsonString);  
		Map<String, Object> map = JSONObject.fromObject(obj);
        return map;
	}
	
}
