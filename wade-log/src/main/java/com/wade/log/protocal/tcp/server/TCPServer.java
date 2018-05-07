package com.wade.log.protocal.tcp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.apache.log4j.Logger;

import com.wade.log.ILogServer;
import com.wade.log.ILogServerListener;
import com.wade.log.protocal.tcp.codec.JavaMessageDecoder;
import com.wade.log.protocal.tcp.codec.JavaMessageEncoder;
import com.wade.log.protocal.tcp.server.handler.ServerHandler;

/**
 * Socket服务端
 * @author Shieh
 *
 */
public class TCPServer implements ILogServer{
	
	private static transient final Logger log = Logger.getLogger(TCPServer.class);
	
	final ILogServerListener serverListener;
	
	public TCPServer(ILogServerListener listener){
		serverListener = listener;
	}
	
	@Override
	public void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							
							p.addLast(
									new JavaMessageDecoder(),
									new JavaMessageEncoder(),
									new ServerHandler(serverListener));
							
							/*
							p.addLast(
									new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
									new LengthFieldPrepender(4),
									new ObjectEncoder(), 
									new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
									new ServerHandler(serverListener));
							*/
						}
					});

			try {
				b.bind(serverListener.getPort()).sync().channel().closeFuture().sync();
			} catch (InterruptedException e) {
				log.error(e);
			}
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}