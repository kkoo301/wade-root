package com.ailk.service.server.socket;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.ailk.rpc.org.jboss.netty.bootstrap.ServerBootstrap;
import com.ailk.rpc.org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.ailk.common.config.GlobalCfg;
import com.ailk.service.ServiceManager;
import com.ailk.service.server.socket.pipeline.ServerPipelineFactory;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ServerPipelineFactory
 * @description: 
 * 
 * @version: v1.0.0
 * @author: xiedx
 * @date: 2013-7-4
 */
public class SocketServer {
	
	private static final int LEN = 50;
	
	private static transient final Logger log = Logger.getLogger(SocketServer.class);
	
	private static String registerClazzName = GlobalCfg.getProperty("service.register.clazz","com.ailk.biz.service.BizServiceRegister");
	
	public static void main(String[] args) {
		if (args.length <= 0) {
			System.err.println("Usage: java com.ailk.service.server.socket.SocketServer [ip] port ");
			System.exit(255);
		}

		String hostname = null;
		int port = 0;
		
		if(args.length == 1){
			port = Integer.parseInt(args[0]);
		}else if(args.length >1){
			hostname = args[0];
			port = Integer.parseInt(args[1]);
		}		

		try{
			if (log.isDebugEnabled()) {
				log.debug("开始注册服务...");
			}
			// 注册服务
			ServiceManager.createRegister(registerClazzName);
			ServiceManager.register();
			if (log.isDebugEnabled()) {
				log.debug("服务注册完成[" + ServiceManager.isLoadFinish() + "]");
			}
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		ServerBootstrap bootstrap = new ServerBootstrap(
			new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()
			)
		);


		bootstrap.setPipelineFactory(new ServerPipelineFactory());
		bootstrap.bind(hostname!=null?new InetSocketAddress(hostname, port):new InetSocketAddress(port));

		System.out.println(StringUtils.rightPad("", LEN, '-'));
		System.out.println(StringUtils.center("socket service start success", LEN));
		System.out.println(StringUtils.center("listening " + port, LEN));
		System.out.println(StringUtils.rightPad("", LEN, '-'));
	
	}
}