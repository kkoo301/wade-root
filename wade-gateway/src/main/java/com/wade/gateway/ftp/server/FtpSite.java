package com.wade.gateway.ftp.server;

import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: FtpSiteConfigure
 * @description: FTP站点配置
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public final class FtpSite {
	
	/**
	 * 站点ID
	 */
	private String siteId;
	
	/**
	 * 站点IP
	 */
	private String ip;
	
	/**
	 * 站点端口
	 */
	private int port;
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 基目录
	 */
	private String basePath;

	/**
	 * 是否使用sftp
	 */
	private boolean useSftp;
	
	/**
	 * 是否使用被动模式
	 */
	private boolean pasvMode = false;
	
	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isUseSftp() {
		return useSftp;
	}

	public void setUseSftp(boolean useSftp) {
		this.useSftp = useSftp;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password.trim();
	}
	
	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		basePath.trim();
		
		// 剔除尾部目录分隔符
		this.basePath = StringUtils.stripEnd(basePath, "/");
		this.basePath = StringUtils.stripEnd(this.basePath, "\\");
	}

	public boolean isPasvMode() {
		return pasvMode;
	}

	public void setPasvMode(boolean pasvMode) {
		this.pasvMode = pasvMode;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" siteId:" + getSiteId());
		sb.append(" ip:" + getIp());
		sb.append(" port:" + getPort());
		sb.append(" basePath:" + getBasePath());
		sb.append(" useSftp:" + isUseSftp());
		return sb.toString();
	}
}
