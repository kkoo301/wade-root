package com.wade.gateway.ftp.server;

import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.wade.gateway.ftp.server.cache.FtpSiteCache;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: FtpSiteFactory
 * @description: 
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public final class FtpSiteFactory {
	
	private FtpSiteFactory() {
		
	}
	
	/**
	 * 根据 siteId 找对应的 FTP 站点配置
	 * 
	 * @param siteId
	 * @return
	 * @throws Exception
	 */
	public static FtpSite getInstance(String siteId) throws Exception {
		IReadOnlyCache cache = CacheFactory.getReadOnlyCache(FtpSiteCache.class);
		FtpSite ftpSite = (FtpSite) cache.get(siteId);
		
		if (null == ftpSite) {
			throw new NullPointerException("根据siteId=" + siteId + "找不到配置信息!");
		}
		
		return ftpSite;
	}
}
