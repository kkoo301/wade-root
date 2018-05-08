/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.ByteBuffer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.notify.common.MessageHandler;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.code.ByteBufferMessageDecoder;
import com.ailk.notify.common.code.ByteBufferMessageEncoder;

/**
 * 构建Socket服务端公用方法
 * 
 * @className:SocketServer.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-20 
 */
public abstract class SocketServer {
	
	private static final transient Logger log = Logger.getLogger(SocketServer.class);
	
	public void startServer() throws InterruptedException {
		ServerBootstrap serverBoot = new ServerBootstrap();
		serverBoot.group(getBossGroup(), getWorkerGroup())
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_REUSEADDR, true)
					.childHandler(getChannelInitializer());
		
		serverBoot.bind("0.0.0.0", getPort()).sync();
		log.fatal("Server start, port : " + getPort());
	}
	
	public abstract int getPort();
	
	public abstract EventLoopGroup getBossGroup();
	public abstract EventLoopGroup getWorkerGroup();
	@SuppressWarnings("rawtypes")
	public abstract MessageToByteEncoder getEncoder();
	public abstract ByteToMessageDecoder getDecoder();
	// 获取对通道信息处理的handler
	public abstract MessageHandler getHandler();

	public void closeChannel(Channel channel) {
		
	}
	
	public void openChannel(Channel channel) {
		
	}
	
	public ChannelInitializer<Channel> getChannelInitializer() {
		
		return new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast("encoder", new ByteBufferMessageEncoder());
				ch.pipeline().addLast("decoder", new ByteBufferMessageDecoder());
				ch.pipeline().addLast("idle", new IdleStateHandler(NotifyUtility.SERVER_READER_IDLE_TIME, NotifyUtility.SERVER_WRITER_IDLE_TIME, NotifyUtility.SERVER_ALL_IDLE_TIME));
				ch.pipeline().addLast("handler", new ServerHandler());
			}
		};
	}
	
	class ServerHandler extends SimpleChannelInboundHandler<ByteBuffer> {

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, ByteBuffer msg)
				throws Exception {
			int dataLength = msg.limit();
			// 处理获取的与关键字一致的数据, 若发现数据内容与关键字不一致, 则交给实际数据处理控制器处理
			if (dataLength == NotifyUtility.KEEP_ALIVE_KEY_WORD_LENGTH || dataLength == NotifyUtility.KEEP_ALIVE_RESP_KEY_WORD_LENGTH) {
				byte[] dataBytes = new byte[dataLength];
				msg.get(dataBytes);
				String msgStr = new String(dataBytes).trim();
				if (NotifyUtility.KEEP_ALIVE_KEY_WORD.equals(msgStr)) {
					if (log.isDebugEnabled()) {
						log.debug("Get keepalive data from client : " + ctx.channel().remoteAddress());
					}
					ByteBuffer respBuffer = NotifyUtility.getKeepAliveRespBuffer();
					ctx.channel().writeAndFlush(respBuffer);
					NotifyUtility.releaseByteBuffer(respBuffer);
					NotifyUtility.releaseByteBuffer(msg);
					return;
				} else if (NotifyUtility.KEEP_ALIVE_RESP_KEY_WORD.equals(msgStr)) {
					if (log.isDebugEnabled()) {
						log.debug("Get response data for keepalive from client : " + ctx.channel().remoteAddress());
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
			if (log.isDebugEnabled()) {
				log.debug("---------------msg  : " + msg);
			}
			msg.rewind();
			if (log.isDebugEnabled()) {
				log.debug("---------------msg after flip : " + msg);
			}
			if (log.isDebugEnabled()) {
				log.debug("Get remote data  : " + ctx.channel().remoteAddress());
			}
			getHandler().handler(ctx.channel(), msg);
		}
		
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
			if (evt instanceof IdleStateEvent) {
				IdleStateEvent event = (IdleStateEvent)evt;
				if (IdleState.READER_IDLE.equals(event.state())) {
					log.info("read idle");
					ctx.close();
					//closeChannel(ctx.channel());
				} else if (IdleState.ALL_IDLE.equals(event.state())) {
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
	    }
	    
	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    	cause.printStackTrace();
	    	log.error("Channel exception : " + cause.getMessage());
	    	super.channelInactive(ctx);
	    }
	}

	
}
