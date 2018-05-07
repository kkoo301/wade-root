package com.wade.gateway.ftp.server;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.wade.container.server.Server;

import com.wade.gateway.ftp.server.FtpGatewayHandler;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: Main
 * @description: 网关主入口
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public class Main {
	
	private static final Logger log = Logger.getLogger(Main.class);
	
	public static void main(String[] args) throws Exception {
		
		if (1 != args.length) {
			System.err.println("Usage: java " + Main.class.getName() +" PORT");
			System.exit(1);
		}
		
		int port = Integer.parseInt(args[0]);
		
		Server server = new Server(port);
		server.setSendServerVersion(false);
		server.setHandler(new FtpGatewayHandler());
		server.start();
		
		log.info("ftp gateway started! listen on port:" + port);
		
		// 把预热成功开关打开，本地自动缓存刷新依赖于是否预热。
		System.setProperty("isPrepared", "StartTime:" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		server.join();
	}
}
