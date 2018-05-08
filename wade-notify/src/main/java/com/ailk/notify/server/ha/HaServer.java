/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server.ha;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import org.apache.log4j.Logger;

import com.ailk.notify.common.MessageHandler;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.code.ByteBufferMessageDecoder;
import com.ailk.notify.common.code.ByteBufferMessageEncoder;
import com.ailk.notify.server.NotifyServer;
import com.ailk.notify.server.ServerFileProxy;
import com.ailk.notify.server.SocketServer;

/**
 * HA的服务端, 在Salve端调用
 * 
 * @className:HaServer.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-20 
 */
public class HaServer extends SocketServer{
	
	private static final transient Logger log = Logger.getLogger(HaServer.class);

	private static final EventLoopGroup bossGroup = new NioEventLoopGroup(NotifyUtility.EVENTLOOP_HA_SERVER_BOSS_SIZE);
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(NotifyUtility.EVENTLOOP_HA_SERVER_WORKER_SIZE);
	private static final HaMessageHandler handler = new HaMessageHandler();

	public static void init() throws InterruptedException {
		HaServer server = new HaServer();
		server.startServer();
		
		log.info("Ha server is open");
	}
	
	@Override
	public int getPort() {
		int haPort = NotifyUtility.getHaPort();
		if (haPort <= 0) {
			throw new RuntimeException("The port for Ha can not less than 0 !!!");
		}
		return haPort;
	}

	@Override
	public void closeChannel(Channel channel) {
		String key = HaProxy.removeChannel(channel);
		if (key != null) {
			// 停止向现有的文件中写入数据
			ServerFileProxy.setAllUnBlankFileUnAvailable();
			String[] queueAndServerName = NotifyUtility.splitKey(key);
			// 打开备机接收请求的功能
			NotifyServer.setCanAcceptData(queueAndServerName[0], queueAndServerName[1], true);
			NotifyServer.setCachePersist(queueAndServerName[0], queueAndServerName[1], true);
		}
		log.info("close channel ; channel remote address : " + channel.remoteAddress());
	}
	
	
	@Override
	public EventLoopGroup getBossGroup() {
		return bossGroup;
	}

	@Override
	public EventLoopGroup getWorkerGroup() {
		return workerGroup;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public MessageToByteEncoder getEncoder() {
		return new ByteBufferMessageEncoder();
	}

	@Override
	public ByteToMessageDecoder getDecoder() {
		return new ByteBufferMessageDecoder();
	}

	@Override
	public MessageHandler getHandler() {
		return handler;
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		System.setProperty("wade.server.ha.port", "10000");
		System.setProperty("wade.server.port", "9000");
		init();
	}
}
