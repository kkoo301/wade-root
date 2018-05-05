package com.wade.trace.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.wade.trace.cache.AppConfCache;
import com.wade.trace.cache.WebConfCache;
import com.wade.trace.cache.EcsConfCache;
import com.wade.trace.cache.IbsConfCache;
import com.wade.trace.cache.UipConfCache;
import com.wade.trace.cache.PfConfCache;
import com.wade.trace.conf.AppConf;
import com.wade.trace.conf.WebConf;
import com.wade.trace.conf.EcsConf;
import com.wade.trace.conf.IbsConf;
import com.wade.trace.conf.UipConf;
import com.wade.trace.conf.PfConf;
import com.wade.trace.sample.ISample;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: SampleUtil
 * @description: 采样控制
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public final class SampleUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(SampleUtil.class);
	
	private static IReadOnlyCache confCache;
	
	static {

		try {
			
			String serverName = SystemUtil.getServerName();
			
			if (serverName.startsWith(SystemUtil.WEB_PREFIX)) {
				confCache = CacheFactory.getReadOnlyCache(WebConfCache.class);
			} else if (serverName.startsWith(SystemUtil.APP_PREFIX)) {
				confCache = CacheFactory.getReadOnlyCache(AppConfCache.class);
			} else if (serverName.startsWith(SystemUtil.ECS_PREFIX)) {
				confCache = CacheFactory.getReadOnlyCache(EcsConfCache.class);
			} else if (serverName.startsWith(SystemUtil.IBS_PREFIX)) {
				confCache = CacheFactory.getReadOnlyCache(IbsConfCache.class);	
			} else if (serverName.startsWith(SystemUtil.PF_PREFIX)) {
				confCache = CacheFactory.getReadOnlyCache(PfConfCache.class);
			} else if (serverName.startsWith(SystemUtil.UIP_PREFIX)) {
				confCache = CacheFactory.getReadOnlyCache(UipConfCache.class);
			} else {
				LOG.error("unknow wade.server.name={}", serverName);
			}
			
		} catch (Exception e) {
			LOG.error("Local Config Cache Initialize Failure!", e);
		}
		
	}
	
	/**
	 * 判断是否对本次WEB调用进行采样
	 * 
	 * @param menuId
	 * @return
	 * @throws Exception
	 */
	public static final boolean isWebSample(String menuId) {
		
		WebConf webConf = null;
		
		try {
			webConf = (WebConf) confCache.get(menuId);
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		if (null == webConf) {
			return false;
		}
		
		ISample sample = webConf.getSample();
		return sample.isSample();
		
	}
	
	/**
	 * 判断是否对本次APP调用进行采样
	 * 
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public static final boolean isAppSample(String serviceName) {
		
		AppConf appConf = null;
		
		try {
			appConf = (AppConf) confCache.get(serviceName);
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		if (null == appConf) {
			return false;
		}
		
		ISample sample = appConf.getSample();
		return sample.isSample();
		
	}
	
	/**
	 * 判断是否需要对本次调用采样
	 * 
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public static final boolean isEcsSample(String serviceName) {
		
		EcsConf ecsConf = null;
		
		try {
			ecsConf = (EcsConf) confCache.get(serviceName);
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		if (null == ecsConf) {
			return false;
		}
		
		ISample sample = ecsConf.getSample();
		return sample.isSample();
		
	}
	
	/**
	 * 判断是否需要对本次调用采样
	 * 
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public static final boolean isIbsSample(String serviceName) {
		
		IbsConf ibsConf = null;
		
		try {
			ibsConf = (IbsConf) confCache.get(serviceName);
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		if (null == ibsConf) {
			return false;
		}
		
		ISample sample = ibsConf.getSample();
		return sample.isSample();
		
	}

	/**
	 * 判断是否需要对本次调用采样
	 * 
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public static final boolean isUipSample(String serviceName) {
		
		UipConf uipConf = null;
		
		try {
			uipConf = (UipConf) confCache.get(serviceName);
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		if (null == uipConf) {
			return false;
		}
		
		ISample sample = uipConf.getSample();
		return sample.isSample();
		
	}
	
	/**
	 * 判断是否需要对本次调用采样
	 * 
	 * @param serviceName
	 * @return
	 * @throws Exception
	 */
	public static final boolean isPfSample(String serviceName) {
		
		PfConf pfConf = null;
		
		try {
			pfConf = (PfConf) confCache.get(serviceName);
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		if (null == pfConf) {
			return false;
		}
		
		ISample sample = pfConf.getSample();
		return sample.isSample();
		
	}
	
}
