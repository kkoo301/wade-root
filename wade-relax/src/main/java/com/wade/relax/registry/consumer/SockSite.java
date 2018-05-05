package com.wade.relax.registry.consumer;

import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 *
 * @desc: Sock地址
 * @auth: steven.zhou
 * @date: 2015-3-26
 */
public final class SockSite {
	
	private String ip;
	private int port;
	private String url;
	
	public SockSite(String ip , int port) {
		this.ip = ip;
		this.port = port;
		this.url = "http://" + this.ip + ":" + this.port;
	}
	
	public SockSite(String address) {
		String[] slice = StringUtils.split(address, ":");
		
		if (2 != slice.length) {
			throw new IllegalArgumentException("地址格式不合法! address: " + address);
		}
		
		this.ip = slice[0];
		this.port = Integer.parseInt(slice[1]);
		this.url = "http://" + this.ip + ":" + this.port;
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
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return this.url;
	}
	
	@Override
	public boolean equals(Object anObject) {
     
		if (this == anObject) {
            return true;
        }
        
        if (anObject instanceof SockSite) {
        	SockSite anotherSockSite = (SockSite) anObject;
        	if (this.url.equals(anotherSockSite.url)) {
        		return true;
        	}
        }
        
        return false;
	}
}
