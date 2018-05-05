package com.wade.watermark;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: WaterMarkAddress
 * @description: 水印主机地址 
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-06-16
 */
public final class WaterMarkAddress {
	
	private static final Logger LOG = Logger.getLogger(WaterMarkAddress.class);
	
	private InetAddress inetAddress;
	
	private int port;

	public WaterMarkAddress(String address) throws UnknownHostException {
		
		String[] parts = StringUtils.split(address, ":");
		
		if (2 != parts.length) {
			throw new IllegalArgumentException("illegal address: " + address);
		}
		
		this.inetAddress = InetAddress.getByName(parts[0]);
		this.port = Integer.parseInt(parts[1]);
		
		LOG.info("watermark address: " + address);
		
	}
	
	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public int getPort() {
		return port;
	}
	
}
