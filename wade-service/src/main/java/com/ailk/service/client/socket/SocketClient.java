package com.ailk.service.client.socket;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.rpc.org.jboss.netty.bootstrap.ClientBootstrap;
import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.channel.ChannelFuture;
import com.ailk.rpc.org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataInput;
import com.ailk.service.client.socket.SocketIO;
import com.ailk.service.client.socket.pipeline.ClientPipelineFactory;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SocketClient
 * @description: 
 * 
 * @version: v1.0.0
 * @author: xiedx
 * @date: 2013-7-4
 */
public class SocketClient {
	
	private static transient Logger log = Logger.getLogger(SocketClient.class);
	
	/**
	 * 心跳休眠时间
	 */
	private int maintSleepSec = 10000;
	
	/**
	 * 每服务端最大连接数
	 */
	private int connNumEachServer = 1;

	/**
	 * 服务调用超时时间
	 */
   private int requestTimeoutSec = 10;	
	
	/**
	 * 连接池
	 */
	private LinkedBlockingQueue<SocketIO> socks = new LinkedBlockingQueue<SocketIO>(50);
	/**
	 * IP地址连接池
	 */
	private Map<String, LinkedBlockingQueue<SocketIO>> socksMap = new ConcurrentHashMap<String, LinkedBlockingQueue<SocketIO>>();
	
	public SocketClient(String serverAddress) { 
		if (StringUtils.isBlank(serverAddress)) {
			throw new IllegalArgumentException("服务端地址为空，客户端初始化失败！");
		}

		String[] addrs = StringUtils.split(serverAddress, ',');

		// 初始化连接池
		for (int i = 0; i < addrs.length; i++) {
			
			String addr = addrs[i];
			String[] tmp = StringUtils.split(addr, ':');
			String hostname = tmp[0];
			int port = Integer.parseInt(tmp[1]);
			
			Channel channel = createChannel(hostname, port);
			
			SocketIO sock = new SocketIO();
			sock.setChannel(channel);
			sock.setHostname(hostname);
			sock.setPort(port);
			
			socks.offer(sock);
		}
		
		/**
		 * 启动连接池心跳线程
		 */
		MaintTask task = new MaintTask();
		task.setDaemon(true);
		task.start();
	}
	
	private Channel createChannel(String hostname, int port) {
		
		Channel channel = null;
		
		ClientBootstrap bootstrap = new ClientBootstrap(
			new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()
			)
		);

		ClientPipelineFactory factory = new ClientPipelineFactory();
		bootstrap.setPipelineFactory(factory);

		ChannelFuture future = bootstrap.connect(new InetSocketAddress(hostname, port));
		channel = future.awaitUninterruptibly().getChannel();
			
		if (!future.isSuccess()) {
			bootstrap.releaseExternalResources();
			channel = null;
		}
		
		if (null == channel) {
			log.error("socket连接创建失败！host=" + hostname + " port=" + port);
		}
		return channel;
	}
	
	public IDataOutput call(String svcname, IDataInput input) {	
		IDataOutput ret = null;
		SocketIO sock = null;		
		try {
			sock = socks.take();
			Channel channel = sock.getChannel();
			SocketProxy proxy = new SocketProxy(channel);
			proxy.write(input);
			ret = proxy.read(this.getRequestTimeoutSec());
		} catch (InterruptedException e) {
			log.error("SocketClient 调用，从连接池取连接失败！", e);
		}finally {
			if (null != sock) {
				socks.offer(sock);
			}
		} 
		return ret;	
	}
	
	public IDataOutput call(String addr, String svcname, IDataInput input){
		if (StringUtils.isBlank(addr)) {
			return call(svcname, input);
		}
		
		IDataOutput ret = null;
		LinkedBlockingQueue<SocketIO> socks = null;
		SocketIO sock = null;
		
		try {
			socks = socksMap.get(addr);
			if(null == socks){
				String[] tmp = StringUtils.split(addr, ':');
				String hostname = tmp[0];
				int port = Integer.parseInt(tmp[1]);
				
				socks = new LinkedBlockingQueue<SocketIO>(1);
				
				Channel channel = createChannel(hostname, port);
				
				SocketIO socket = new SocketIO();
				socket.setChannel(channel);
				socket.setHostname(hostname);
				socket.setPort(port);
				
				socks.offer(socket);
				
				socksMap.put(addr, socks);
			}
			
			sock = socks.take();
			Channel channel = sock.getChannel();	
			SocketProxy proxy = new SocketProxy(channel);
			proxy.write(input);
			ret = proxy.read(this.getRequestTimeoutSec());
			
		} catch (InterruptedException e) {
			log.error("SocketClient 调用，从连接池取连接失败！", e);
		} catch(Exception ex){
			log.error("SocketClient 调用失败！", ex);
		}finally {
			if (null != sock) {
				socks.add(sock);
			}
		}
		 
		return ret;
	}

	public int getMaintSleepSec() {
		return maintSleepSec;
	}

	public void setMaintSleepSec(int maintSleepSec) {
		this.maintSleepSec = maintSleepSec * 1000;
	}

	public int getConnNumEachServer() {
		return connNumEachServer;
	}

	public void setConnNumEachServer(int connNumEachServer) {
		this.connNumEachServer = connNumEachServer;
	}
	
	public int getRequestTimeoutSec(){
		return requestTimeoutSec;
	}
	
	public void setRequestTimeoutSec(int requestTimeoutSec){
		this.requestTimeoutSec = requestTimeoutSec;
	}
	
	/**
	 * 连接心跳检测线程
	 */
	private class MaintTask extends Thread {
		
		private IDataInput input;
		
		public MaintTask() {
			input = new DataInput();
			input.getHead().put("_SOCKET_HEART_BEAT", 1);
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					checkSocketQueue(socks);
					
					for(Entry<String,LinkedBlockingQueue<SocketIO>> entry:socksMap.entrySet()){
						checkSocketQueue(entry.getValue());
					}
					
					Thread.sleep(maintSleepSec);
				} catch (InterruptedException e) {
					log.error("客户端，心跳检测线程，休眠时被中断！", e);
				}
			}
		}	
		
		
		void checkSocketQueue(LinkedBlockingQueue<SocketIO> sockets){
			SocketIO sock = null;
			try {
				sock = sockets.take();
			} catch (InterruptedException e) {
				log.error("客户端心跳检测线程，从连接池取连接失败！", e);
			}
	
			Channel channel = sock.getChannel();
			try {	
				SocketProxy proxy = new SocketProxy(channel);
				proxy.write(input);
				
				IDataOutput out = proxy.read(5);	
				if (out !=null && out.getHead().getInt("_SOCKET_HEART_BEAT")>0) { // 心跳成功
					sockets.offer(sock);
				} else { // 心跳失败，重新创建连接
					channel.close();
					channel = createChannel(sock.getHostname(), sock.getPort());
					System.out.println("heartbeat faild createChannel=" + channel);
					
					sock.setChannel(channel);
					sockets.offer(sock);
				}
			} catch (Exception e) { // 心跳失败，重新创建连接
				if(channel != null){
					channel.close();
				}
				channel = createChannel(sock.getHostname(), sock.getPort());
				System.out.println("heartbeat faild createChannel=" + channel);
				
				sock.setChannel(channel);
				sockets.offer(sock);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		/*
		SocketClient client= new SocketClient("localhost:8008");
		
		IDataInput input = new DataInput();
		
		IDataOutput out = client.call(input);
		
		System.out.println(">>>>>>>>>>>>>>>" + out.toString());	
		*/
	}
}
