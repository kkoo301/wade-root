package com.wade.gateway.ftp.server.cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;
import com.ailk.database.util.TripleDES;
import com.wade.gateway.ftp.server.FtpSite;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: FtpSiteCache
 * @description: FtpSite配置缓存
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public class FtpSiteCache extends AbstractReadOnlyCache {

	private static final Logger log = Logger.getLogger(FtpSiteCache.class);
	
	/**
	 * 缓存数据加载逻辑
	 */
	@Override
	public Map<String, Object> loadData() throws Exception {
		
		Map<String, Object> cache = new HashMap<String, Object>();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
		
			IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
			if (null != manager) {
				conn = manager.getConnection("cen1");
				if (null == conn) { 
					throw new NullPointerException("找不到数据库连接:cen1");
				}
			}
			
			stmt = conn.prepareStatement("SELECT SITE_ID, IP, PORT, USE_SFTP, USERNAME, PASSWORD, BASE_PATH FROM WD_GATEWAY_FTP WHERE STATE = 'U'");
			rs = stmt.executeQuery();
			
			while (rs.next()) {				
				FtpSite ftpSite = new FtpSite();
				
				String siteId = rs.getString("SITE_ID");
				String ip = rs.getString("IP");
				int port = rs.getInt("PORT");
				String useSftp = rs.getString("USE_SFTP");
				String username = rs.getString("USERNAME");
				String password = rs.getString("PASSWORD");
				String basePath = rs.getString("BASE_PATH");
				
				ftpSite.setSiteId(siteId);
				ftpSite.setIp(ip);
				ftpSite.setPort(port);
				
				if ("Y".equals(useSftp)) {
					ftpSite.setUseSftp(true);
				} else {
					ftpSite.setUseSftp(false);
				}
				
				ftpSite.setUsername(username);
				
				if (password.startsWith("{3DES}")) {
					password = TripleDES.decrypt(password.substring(6));
				}
				
				ftpSite.setPassword(password);
				ftpSite.setBasePath(basePath);
				
				cache.put(siteId, ftpSite);
			}
			
		} catch (Exception e ) {
			log.error("加载FTP网关配置发生错误!", e);
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}
				
				if (null != stmt) {
					stmt.close();
				}
				
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException sqle) {
				log.error("", sqle);
			}
		}
		
		return cache;
	}

}
