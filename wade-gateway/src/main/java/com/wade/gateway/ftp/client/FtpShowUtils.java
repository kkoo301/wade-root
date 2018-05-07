package com.wade.gateway.ftp.client;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.config.GlobalCfg;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: FtpShowUtils
 * @description: Ftp文件显示工具类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-01-12
 */
public final class FtpShowUtils {
		
	private FtpShowUtils() {}
	
	/**
	 * 从某FTP主机上拷贝一个文件至静态资源服务器，并返回静态资源服务器的相对地址
	 * 
	 * @param siteId
	 * @param fileName
	 * @param suffix
	 * @return
	 * @throws Exception
	 */
	public static final String fetchFileUrl(String siteId, String fileName) throws Exception {
		return fetchFileUrl(siteId, fileName, "");
	}
	
	/**
	 * 从某FTP主机上拷贝一个文件至静态资源服务器，并返回静态资源服务器的相对地址
	 * 
	 * @param siteId
	 * @param fileName
	 * @param suffix
	 * @return
	 * @throws Exception 
	 */
	public static final String fetchFileUrl(String siteId, String fileName, String suffix) throws Exception {
		
		if (null == suffix) {
			suffix = "";
		}
		
		if ((!"".equals(suffix)) && (!suffix.startsWith("."))) {
			throw new IllegalArgumentException("suffix must start with .");
		}
		
		String rtn = null;
		
		// 创建目标文件名
		String dstFileName = siteId + "_" + fileName + suffix;
		dstFileName = StringUtils.replaceChars(StringUtils.replaceChars(dstFileName, "/", "-"), "\\", "-");
		
		String cacheKey = FtpShowUtils.class.getName() + ":" + dstFileName;
		rtn = (String) SharedCache.get(cacheKey);
		if (null != rtn) {
			return rtn;
		}
		
		// 获取配置 global.properties 中配置的静态资源服务器的SiteId标识，多个以逗号分隔。
		String staticFtpSites = GlobalCfg.getProperty("static.ftp.sites", "static-node01,static-node02");
		String[] dstSiteIds = StringUtils.split(staticFtpSites, ",");
		
		boolean isNeedCache = true;
		for (String dstSiteId : dstSiteIds) {
			// 如果有多台静态资源服务器，只要有一台上传不成功就不缓存地址。
			if (!FTPClient.remoteCopyFile(siteId, fileName, dstSiteId, dstFileName)) {
				isNeedCache = false;
			}
		}
		
		rtn = GlobalCfg.getProperty("static.ftpshow.path", "/static/ftpshow/") + dstFileName;
		if (isNeedCache) {
			SharedCache.set(cacheKey, rtn, 60);
		}
		
		return rtn;
	}
	
	public static void main(String[] args) throws Exception {
		//for (int i = 0; i < 10; i++) {
			long start = System.currentTimeMillis();
			String url = FtpShowUtils.fetchFileUrl("TEST_SITE", "BES.jpg");
			long cost = System.currentTimeMillis() - start;
			System.out.println(url);
			System.out.println("cost: " + cost);
		//}
	}
}
