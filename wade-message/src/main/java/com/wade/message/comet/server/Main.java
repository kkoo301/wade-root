package com.wade.message.comet.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import static com.wade.message.comet.server.Config.*;

public class Main{
	
	public static void main(String[] args) {
		if (args.length <= 0) {
			System.err.println("Usage: java com.wade.message.comet.Main [ip] port ");
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
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
	    EventLoopGroup workerGroup = new NioEventLoopGroup();
	    
		try {
			
			IWorker worker = null;
			Class<IWorker> workerClazz = (Class<IWorker>)Class.forName(COMET_SERVER_WORKER_CLAZZ);
			if(workerClazz != null){
				worker = workerClazz.newInstance();
			}

			ServerBootstrap b = new ServerBootstrap();
			
			b.option(ChannelOption.SO_BACKLOG, Integer.valueOf(1024));
			b.group(bossGroup, workerGroup)
			 .channel(NioServerSocketChannel.class)
			 .handler(new LoggingHandler(LogLevel.INFO))
			 .childHandler(new Initializer(worker));
			
			Channel ch = b.bind(hostname != null ? new InetSocketAddress(hostname, port) : new InetSocketAddress(port)).sync().channel();
			
			worker.start(hostname, port);
			
			ch.closeFuture().sync();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}