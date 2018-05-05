package com.wade.trace.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.trace.cache.AppConfCache;
import com.wade.trace.cache.WebConfCache;
import com.wade.trace.conf.AppConf;
import com.wade.trace.conf.WebConf;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DynaBindUtil
 * @description: 动态参数绑定工具类
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public final class DynaBindUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(DynaBindUtil.class);
	
	private static IReadOnlyCache confCache;
	
	static {
		try {
			
			String serverName = SystemUtil.getServerName();
			
			if (serverName.startsWith(SystemUtil.WEB_PREFIX)) {
				confCache = CacheFactory.getReadOnlyCache(WebConfCache.class);
			} else if (serverName.startsWith(SystemUtil.APP_PREFIX)) {
				confCache = CacheFactory.getReadOnlyCache(AppConfCache.class);
			} 
			
		} catch (Exception e) {
			LOG.error("Local Config Cache initialize failure!", e);
		}
	}
	
	/**
	 * Web动态参数绑定
	 * 
	 * @param menuId
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static final Map<String, String> webBinding(String menuId, IData param) {
		
		Map<String, String> rtn = new HashMap<String, String>();
		
		WebConf webConf = null;
		
		try {
			webConf = (WebConf) confCache.get(menuId);
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		if (null == webConf) {
			return rtn;
		}
		
		for (String key : webConf.getKeys()) {
			String value = param.getString(key);
			if (StringUtils.isNotBlank(value)) {
				rtn.put(key, value);
			}
		}
		
		return rtn;
		
	}
	
	/**
	 * App动态参数绑定
	 * 
	 * @param servieName
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static final Map<String, String> appBinding(String servieName, IData param) {
		
		Map<String, String> rtn = new HashMap<String, String>();
		
		AppConf appConf = null;
		try {
			appConf = (AppConf) confCache.get(servieName);
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		if (null == appConf) {
			return rtn;
		}
		
		for (String key : appConf.getKeys()) {
			String value = param.getString(key);
			if (StringUtils.isNotBlank(value)) {
				rtn.put(key, value);
			}
		}
		
		return rtn;
		
	}
	
}
