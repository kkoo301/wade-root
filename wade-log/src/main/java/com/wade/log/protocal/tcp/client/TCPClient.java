package com.wade.log.protocal.tcp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetAddress;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.wade.log.ILogClient;
import com.wade.log.ILogData;
import com.wade.log.protocal.tcp.client.handler.ClientHandler;
import com.wade.log.protocal.tcp.codec.JavaMessageDecoder;
import com.wade.log.protocal.tcp.codec.JavaMessageEncoder;

/**
 * TCP 客户端
 * @author Shieh
 *
 */
public class TCPClient implements ILogClient
{
	private static transient final Logger log = Logger.getLogger(TCPClient.class);
	
	private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
	
	/**
	 * 重连尝试间隔，单位秒
	 */
	private static final int RETRY_INTERVAL = 5;
	
	private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
	
	public TCPClient(final InetAddress addr,final int port){
		EventLoopGroup group = new NioEventLoopGroup();

		final Bootstrap b = new Bootstrap();
		
		 final ChannelFutureListener fl = new ChannelFutureListener() {
	            @Override
	            public void operationComplete(ChannelFuture future) {
	            	if (!future.isSuccess()) {
	                	try {
	                    	Channel channel = future.channel();
	                    	channel.close();
	                    	
	                    	if(channelGroup.contains(channel)){
	                    		channelGroup.remove(channel);
	                    	}
	                    } catch (Exception e) {
	                    	log.error(e.getMessage(), e);
	                    }
	                }else{
	                	try {
	                		Channel channel = future.channel();
	                		channelGroup.add(channel);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
	                }
	            }
	        };
	    
    
		b.group(group).channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						
						p.addLast(
								new JavaMessageDecoder(),
								new JavaMessageEncoder(), 
								new ClientHandler(){									
									@Override  
								    public void channelInactive(ChannelHandlerContext ctx) throws Exception {  
										super.channelInactive(ctx);
										
										Channel channel = ctx.channel();
										
										if( channelGroup.contains(channel) ){
											channelGroup.remove(ctx.channel());
										}
									}
								});
						
						/*
						p.addLast(
								new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
								new LengthFieldPrepender(4),
								new ObjectEncoder(), 
								new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
								new ClientHandler());
						*/
					}
				});			
		
	  
        
        b.connect(addr, port).addListener(fl);
        
        /**
         * 定时重连
         */
        executor.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run() {
				if(channelGroup.isEmpty()){
					log.info("正在尝试连接到日志服务端[" + addr.getHostName() + ":" + port + "]...");
					b.connect(addr, port).addListener(fl);
				}
			}	
		}, 0, RETRY_INTERVAL, TimeUnit.SECONDS);
	}
	
	@Override
	public void sendLog(ILogData logData) {		
		if(!channelGroup.isEmpty() && logData != null){
			channelGroup.writeAndFlush(logData);
		}
	}
}