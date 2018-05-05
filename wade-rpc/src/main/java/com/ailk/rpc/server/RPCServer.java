package com.ailk.rpc.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.rpc.org.jboss.netty.bootstrap.ServerBootstrap;
import com.ailk.rpc.org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import com.ailk.rpc.server.pipeline.ServerPipelineFactory;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: RPCServer
 * @description: 
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-4
 */
public class RPCServer {
	
	private static final Logger log = Logger.getLogger(RPCServer.class);
	
	private static final int LEN = 50;
	
	public static void main(String[] args) {
	
		if (2 != args.length) {
			log.error("Usage: java com.ailk.rpc.server.RPCServer ip port ");
			System.exit(255);
		}

		String hostname = args[0];
		int port = Integer.parseInt(args[1]);

		ServerBootstrap bootstrap = new ServerBootstrap(
			new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool(), 1000
			)
		);

		bootstrap.setPipelineFactory(new ServerPipelineFactory());
		bootstrap.bind(new InetSocketAddress(hostname, port));

		System.out.println(StringUtils.rightPad("", LEN, '-'));
		System.out.println(StringUtils.center("server start success", LEN));
		System.out.println(StringUtils.center("listening " + port, LEN));
		System.out.println(StringUtils.rightPad("", LEN, '-'));
	}
	
}
