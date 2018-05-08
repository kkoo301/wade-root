/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.ByteBuffer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 构建Socket通信客户端
 * 
 * @className:SocketClient.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-20 
 */
public abstract class SocketClient {
	
	private static final transient Logger log = Logger.getLogger(SocketClient.class);
		
	private Bootstrap bootstrap;
	
	private String ip;
	private int port;
	private SocketBucket bucket;
	protected String queueName;
	protected String serverName;
	
	public SocketClient(String queueName, String serverName, String ip, int port, SocketBucket bucket) {
		this.queueName = queueName;
		this.serverName = serverName;
		this.ip = ip;
		this.port = port;
		this.bucket = bucket;
	}
	
	public void initBootstrap() {
		bootstrap = new Bootstrap();
		bootstrap.group(new NioEventLoopGroup(getLoopGroupSize()))
			  .channel(NioSocketChannel.class)
			  .option(ChannelOption.SO_KEEPALIVE, true)
			  .option(ChannelOption.SO_TIMEOUT, 20)
			  .handler(getChannelInitializer());
	}
	
	public Channel createChannel() throws InterruptedException {
		if (bootstrap == null) {
			synchronized (this) {
				if (bootstrap == null) {
					initBootstrap();
				}
			}
		}
		
		Channel channel = bootstrap.connect(ip, port).sync().channel();
		ByteBuffer respBuffer = NotifyUtility.getKeepAliveRespBuffer();
		channel.writeAndFlush(respBuffer);
		NotifyUtility.releaseByteBuffer(respBuffer);
		return channel;
	}
	
	/**
	 * 该客户端要设置的LoopGroup的大小 
	 * @return
	 */
	public abstract int getLoopGroupSize();

	/**
	 * 设置编码方式，不需要返回null即可 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public abstract MessageToByteEncoder getEncoder();
	
	/**
	 * 设置解码方式，不需要设置null即可 
	 * @return
	 */
	public abstract ByteToMessageDecoder getDecoder();
	
	/**
	 * 用于处理获取到的数据 
	 * @return
	 */
	public abstract MessageHandler getMessageHandler();
	
	/**
	 * 通道关闭的回调方法
	 *  
	 * @param channel
	 */
	public void closeChannel(Channel channel) {
		
	}
	
	/**
	 * 通道打开的回调方法
	 * 
	 * @param channel
	 */
	public void openChannel(Channel channel) {
		
	}
	
	public ChannelInitializer<Channel> getChannelInitializer() {
		return new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeLine = ch.pipeline();
				@SuppressWarnings("rawtypes")
				MessageToByteEncoder encoder = getEncoder();
				ByteToMessageDecoder decoder = getDecoder();
				if (encoder != null) {
					pipeLine.addLast("encoder", getEncoder());
				}
				if (decoder != null) {
					pipeLine.addLast("decoder", getDecoder());
				}
				pipeLine.addLast("idle", new IdleStateHandler(NotifyUtility.CLIENT_READER_IDLE_TIME, NotifyUtility.CLIENT_WRITE_IDLE_TIME, NotifyUtility.CLIENT_ALL_IDLE_TIME));
				pipeLine.addLast("handler", new SimpleChannelInboundHandler<ByteBuffer>() {

					@Override
					protected void channelRead0(ChannelHandlerContext ctx, ByteBuffer msg)
							throws Exception {
						int dataLength = msg.limit();
						
						if (log.isDebugEnabled()) {
							log.debug("receive data from server!");
						}
						
						// 处理获取的与关键字一致的数据, 若发现数据内容与关键字不一致, 则交给实际数据处理控制器处理
						if (dataLength == NotifyUtility.KEEP_ALIVE_KEY_WORD_LENGTH || dataLength == NotifyUtility.KEEP_ALIVE_RESP_KEY_WORD_LENGTH) {
							byte[] dataBytes = new byte[dataLength];
							msg.get(dataBytes);
							String msgStr = new String(dataBytes).trim();
							if (NotifyUtility.KEEP_ALIVE_KEY_WORD.equals(msgStr)) {
								if (log.isDebugEnabled()) {
									log.debug("Get keepalive data from server : " + ctx.channel().remoteAddress());
								}
								ByteBuffer respBuffer = NotifyUtility.getKeepAliveRespBuffer();
								ctx.channel().writeAndFlush(respBuffer);
								NotifyUtility.releaseByteBuffer(respBuffer);
								NotifyUtility.releaseByteBuffer(msg);
								return;
							} else if (NotifyUtility.KEEP_ALIVE_RESP_KEY_WORD.equals(msgStr)) {
								if (log.isDebugEnabled()) {
									log.debug("Get response data for keepalive from server : " + ctx.channel().remoteAddress());
								}
								NotifyUtility.releaseByteBuffer(msg);
								return;
							} else if (StringUtils.isBlank(msgStr)) {
								if (log.isDebugEnabled()) {
									log.debug("Don't handle empty data!");
								}
								NotifyUtility.releaseByteBuffer(msg);
								return;
							}
						}
						msg.rewind();
						if (log.isDebugEnabled()) {
							log.debug("Get data from server : " + ctx.channel().remoteAddress());
						}
						getMessageHandler().handler(ctx.channel(), msg);
					}
					
					@Override
					public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
						if (evt instanceof IdleStateEvent) {
							IdleStateEvent event = (IdleStateEvent)evt;
							if (IdleState.READER_IDLE.equals(event.state())) {
								log.info("client read idle, " + ctx.channel().remoteAddress() + ", begin close the channel!");
								ctx.close();
								//closeChannel(ctx.channel());
								bucket.removeChannel(ctx.channel());
							} else if (IdleState.ALL_IDLE.equals(event.state())) {
								if (log.isDebugEnabled()) {
									log.debug("client all idle, begin send keepalive data! " + ctx.channel().remoteAddress());
								}
								
								ByteBuffer keepAliveBuffer = NotifyUtility.getKeepAliveBuffer();
								ctx.channel().writeAndFlush(keepAliveBuffer);
								NotifyUtility.releaseByteBuffer(keepAliveBuffer);
							}
						}
					}
					
				    @Override
				    public void channelActive(ChannelHandlerContext ctx) throws Exception {
				    	if (log.isDebugEnabled()) {
				    		log.debug("Channel Active : " + ctx.channel().remoteAddress() + "; local address :" + ctx.channel().localAddress() + " ; time :" + System.currentTimeMillis());
				    	}
				    	super.channelActive(ctx);
				    	openChannel(ctx.channel());
				    }

				    @Override
				    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
				    	if (log.isDebugEnabled()) {
				    		log.debug("Channel InActive : remote address :" + ctx.channel().remoteAddress() + "; local address :" + ctx.channel().localAddress() + "; time : " + System.currentTimeMillis());
				    	}
				    	
				    	super.channelInactive(ctx);
				    	closeChannel(ctx.channel());
				    	bucket.removeChannel(ctx.channel());
				    }
				});
			}
			
		};
	}

	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
}
