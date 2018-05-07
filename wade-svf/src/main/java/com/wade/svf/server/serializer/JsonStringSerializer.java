/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月27日
 * 
 * Just Do IT.
 */
package com.wade.svf.server.serializer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ailk.common.json.JSONObject;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;

/**
 * @description
 * Json字符串的数据序列化实现
 */
public class JsonStringSerializer implements IDataSerializer {

	
	@Override
	public Serializable serialize(byte[] data) throws FlowException {
		return new String(data);
	}
	
	
	@Override
	public Map<String, Object> getData(Serializable source) throws FlowException {
		try {
			String jsonstr = (String) source;
			
			JSONObject json = JSONObject.fromObject(jsonstr);
			
			Map<String, Object> data = new HashMap<String, Object>(30);
		
			Iterator<String> iter = json.keys();
			while (iter.hasNext()) {
				String key = iter.next();
				Object obj = json.get(key);
				
				if (null == obj) {
					data.put(key, "");
				} else {
					data.put(key, obj);
				}
			}
			
			return data;
		} catch (Exception e) {
			throw new FlowException(FlowErr.flow10012.getCode(), FlowErr.flow10012.getInfo(e.getMessage()));
		}
	}
	
	@Override
	public Serializable toData(Map<String, Object> data) throws FlowException {
		try {
			JSONObject obj = JSONObject.fromObject(data);
			return obj.toString();
		} catch (Exception e) {
			throw new FlowException(FlowErr.flow10012.getCode(), FlowErr.flow10012.getInfo(e.getMessage()));
		}
	}

	
	@Override
	public byte[] deserialize(Serializable data) throws FlowException {
		try {
			return ((String) data).getBytes("utf-8");
		} catch (Exception e) {
			throw new FlowException(FlowErr.flow10010.getCode(), FlowErr.flow10010.getInfo("不支持的字符集"), e);
		}
	}

}
