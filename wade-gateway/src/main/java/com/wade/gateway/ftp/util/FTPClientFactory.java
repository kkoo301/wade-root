package com.wade.gateway.ftp.util;

import com.wade.gateway.ftp.server.FtpSite;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: FTPClientFactory
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-4-18
 */
public class FTPClientFactory {
	public static final IFTPClient getInstance(FtpSite site) {
		IFTPClient client = null;
		
		if (site.isUseSftp()) {
			client = new SecureFTPClient(site);
		} else {
			client = new PlainFTPClient(site);
		}
	
		return client;
	}
}