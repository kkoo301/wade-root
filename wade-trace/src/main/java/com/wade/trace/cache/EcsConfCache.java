package com.wade.trace.cache;

import java.util.HashMap;
import java.util.Map;

import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.wade.trace.TraceContext;
import com.wade.trace.conf.EcsConf;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: EcsConfCache
 * @description: 电渠探针配置缓存
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class EcsConfCache extends AbstractReadOnlyCache {

	@Override
	public Map<String, Object> loadData() throws Exception {
		
		Map<String, Object> rtn = new HashMap<String, Object>();
		Map<String, EcsConf> confs = TraceContext.getEcsConf();
		
		for (String k : confs.keySet()) {
			rtn.put(k, confs.get(k));
		}
		
		return rtn;
		
	}

}
