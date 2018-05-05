package com.ailk.rpc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.rpc.client.io.SockIO;
import com.ailk.rpc.client.pipeline.ClientPipelineFactory;
import com.ailk.rpc.codec.Transporter;
import com.ailk.rpc.org.jboss.netty.bootstrap.ClientBootstrap;
import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.channel.ChannelFuture;
import com.ailk.rpc.org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import com.ailk.rpc.server.HeartBeat;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: RPCClient
 * @description: 
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-4
 */
public class RPCClient extends Thread {
	
	private static final Logger log = Logger.getLogger(RPCClient.class);
	
	/**
	 * 心跳休眠时间
	 */
	private int maintSleepSec = 10000;
	
	/**
	 * 每服务端最大连接数
	 */
	private int ConnNumEachServer = 1;

	/**
	 * 可用连接
	 */
	private LinkedBlockingQueue<SockIO> liveSocks = new LinkedBlockingQueue<SockIO>();

	/**
	 * 注意: 不可反复构造ClientBootstrap，容易造成线程溢出。
	 */
	private ClientBootstrap bootstrap;
	
	/**
	 * 不可用连接
	 */
	private LinkedBlockingQueue<SockIO> deadSocks = new LinkedBlockingQueue<SockIO>();
	
	public RPCClient(String serverAddress) { 
		
		// 在构造函数构造一次ClientBootstrap!
		this.bootstrap = new ClientBootstrap(
			new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool(),
				2
			)
		);

		ClientPipelineFactory factory = new ClientPipelineFactory();
		bootstrap.setPipelineFactory(factory);
		
		if (StringUtils.isBlank(serverAddress)) {
			throw new IllegalArgumentException("RPC服务端地址为空，客户端初始化失败！");
		}
		
		String[] addrs = StringUtils.split(serverAddress, ',');

		// 初始化连接池
		for (int i = 0; i < addrs.length; i++) {
			
			String addr = addrs[i];
			String[] tmp = StringUtils.split(addr, ':');
			String hostname = tmp[0];
			int port = Integer.parseInt(tmp[1]);
			
			Channel channel = createChannel(hostname, port);
			
			SockIO sock = new SockIO();
			sock.setChannel(channel);
			sock.setHostname(hostname);
			sock.setPort(port);
			
			liveSocks.add(sock);
		}
		
		/**
		 * 启动连接池心跳线程
		 */
		MaintTask task = new MaintTask();
		task.setDaemon(true);
		task.start();
		log.info("搜索心跳线程启动!");
	}
	
	private Channel createChannel(String hostname, int port) {
		
		Channel channel = null;
		
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(hostname, port));
		channel = future.awaitUninterruptibly().getChannel();
			
		if (!future.isSuccess()) {
			channel = null;
		}
		
		if (null == channel) {
			log.error("创建socket连接失败！hostname=" + hostname + ", port=" + port);
		}
		return channel;
	}
	
	public int getMaintSleepSec() {
		return maintSleepSec;
	}

	public void setMaintSleepSec(int maintSleepSec) {
		this.maintSleepSec = maintSleepSec * 1000;
	}

	public int getConnNumEachServer() {
		return ConnNumEachServer;
	}

	public void setConnNumEachServer(int connNumEachServer) {
		ConnNumEachServer = connNumEachServer;
	}
	
	/**
	 * 连接心跳检测线程
	 */
	private class MaintTask extends Thread {
		
		private Transporter transporter;
		
		public MaintTask() {
			transporter = new Transporter();
			transporter.setClazzName("com.ailk.rpc.server.HeartBeat");
			transporter.setMethodName("isAlive");
			transporter.setParams(new Object[]{});
			transporter.setParamTypes(new Class[]{});
		}
		
		/**
		 * 心跳检查
		 */
		private void socketHeartbeat() {
			SockIO sock = null;
			try {
				sock = liveSocks.poll();
				if (null == sock) {
					return;
				}
				
				if (isConnected(sock)) {
					liveSocks.offer(sock); // 心跳成功
				} else {
					deadSocks.offer(sock); // 心跳失败
				}
				
			} catch (Throwable e) {
				deadSocks.offer(sock); // 心跳失败
				log.error("search长连接心跳失败！" + sock.getHostname() + ":" + sock.getPort(), e);
			}

		}

		/**
		 * 心跳重试
		 */
		private void socketReconnect() {
			SockIO sock = null;
			Channel channel = null;
			try {
				sock = deadSocks.poll();
				if (null == sock) {
					return;
				}
				
				channel = createChannel(sock.getHostname(), sock.getPort());
				if (null != channel) {
					log.info("RPC长连接重新创建成功! hostname=" + sock.getHostname() + ", port=" + sock.getPort());
				}
				
				sock.setChannel(channel);
				
				if (isConnected(sock)) {
					liveSocks.offer(sock); // 心跳成功
				} else {
					deadSocks.offer(sock); // 心跳失败
				}
								
			} catch (Throwable e) {
				deadSocks.offer(sock); // 心跳失败
				log.error("search socket重连失败！" + sock.getHostname() + ":" + sock.getPort(), e);
			}
		}
		
		/**
		 * Socket心跳检查
		 * 
		 * @param sock
		 * @return
		 */
		private boolean isConnected(SockIO sock) {
			Channel channel = sock.getChannel();
			SockProxy proxy = new SockProxy(channel);
			proxy.write(transporter);
			
			String isAlive = (String)proxy.read(5).getResponse();	
			if (HeartBeat.ALIVE.equals(isAlive)) {
				return true;
			} else {
				channel.close();
				log.error("search长连接心跳失败!" + sock.getHostname() + ":" + sock.getPort());
				return false;
			}
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(maintSleepSec);
					socketHeartbeat();
					socketReconnect();
				} catch (Throwable e) {
					log.error("搜索连接池心跳线程发生异常!", e);
				}
					
			}
		}
		
	}
	
	public Object rpcCall(Transporter transporter) {
		
		Object rtn = null;
		SockIO sock = null;
		Channel channel = null;
		
		try {
			sock = liveSocks.poll(5, TimeUnit.SECONDS);
			if (null == sock) {
				throw new IOException("搜索连接池中已没有可用的连接!");
			}
			channel = sock.getChannel();
			SockProxy proxy = new SockProxy(channel);
			proxy.write(transporter);
			rtn = proxy.read(20).getResponse();
			liveSocks.offer(sock);
		} catch (Throwable e) {
			if (null != channel) {
				channel.close();
			}
			
			deadSocks.offer(sock);
			log.error("rpcCall调用失败！", e);
		}

		return rtn;
		
	}
	
	public static void main(String[] args) throws Exception {
		RPCClient client = new RPCClient("localhost:8888");
		client.setMaintSleepSec(5);
		Thread.sleep(1000 * 1000);
	}
	
}
