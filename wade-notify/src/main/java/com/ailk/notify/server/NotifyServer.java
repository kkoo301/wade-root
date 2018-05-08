/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.ailk.notify.common.FileUtility;
import com.ailk.notify.common.MessageHandler;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.code.ByteBufferMessageDecoder;
import com.ailk.notify.common.code.ByteBufferMessageEncoder;
import com.ailk.notify.server.ha.HaClient;
import com.ailk.notify.server.ha.HaServer;

/**
 * Notify的服务端入口， 接收生产者和消费者的请求并进行处理
 * 
 * @className:Server.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-6 
 */
public class NotifyServer extends SocketServer{

	private static final transient Logger log = Logger.getLogger(NotifyServer.class);
	
	private EventLoopGroup bossGroup = new NioEventLoopGroup(NotifyUtility.EVENTLOOP_SERVER_BOSS_SIZE);
	private EventLoopGroup workerGroup = new NioEventLoopGroup(NotifyUtility.EVENTLOOP_SERVER_WORKER_SIZE); 
	private MessageHandler handler = new NotifyServerMessageHandler();
	private static boolean isHaServer;
	
	// 标记当起那服务端是否可接受客户端修改数据的请求，当为false时，仅HA功能可用
	private static Map<String, AtomicBoolean> canAcceptData = new ConcurrentHashMap<String, AtomicBoolean>();
	// 标记是否可以将持久化中的数据加载到缓存中, 在服务刚启动且尚未同步完数据时不能进行加载
	private static Map<String, AtomicBoolean> canCachePersistData = new ConcurrentHashMap<String, AtomicBoolean>();
	// 保存服务在未接收请求的情况下，当前的持久化数据的待处理的消息索引集合
	private static Map<String, int[]> persistIndexRangeBeforeServer = new ConcurrentHashMap<String, int[]>();
	
	public static boolean canCachePersist(String queueName, String serverName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		AtomicBoolean data = canCachePersistData.get(key);
		return data == null ? false : data.get();
	}
	
	public static void setCachePersist(String queueName, String serverName, boolean canCache) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		AtomicBoolean data = canCachePersistData.get(key);
		if (data == null) {
			data = new AtomicBoolean(false);
			canCachePersistData.put(key, data);
		}
		data.compareAndSet(!canCache, canCache);
	}
	
	public static void setIndexRangeBeforeServer(String key, int[] range) {
		persistIndexRangeBeforeServer.put(key, range);
	}
	
	public static int[] getIndexRangeBeforeServer(String queueName, String serverName, long fileName) {
		String key = NotifyUtility.buildKey(NotifyUtility.buildKey(queueName, serverName), fileName);
		int[] range = persistIndexRangeBeforeServer.get(key);
		if (range == null) {
			FileUtility fileUtility = ServerFileProxy.getFileUtility(queueName, serverName, fileName);
			synchronized(fileUtility) {
				range = persistIndexRangeBeforeServer.get(key);
				if (range == null) {
					// 获取文件的待处理数据范围
					range = fileUtility.getUnOverDataRange();
					String fileUtilityKey = NotifyUtility.buildKey(NotifyUtility.buildKey(queueName, serverName), String.valueOf(fileName));
					NotifyServer.setIndexRangeBeforeServer(fileUtilityKey, new int[]{range[0], range[1]});
				}
			}
		}
		return range;
	}
	
	public static void updateIndexRangeBeforeServer(String key, int[] range) {
		int[] oldRange = persistIndexRangeBeforeServer.get(key);
		
		if (log.isInfoEnabled()) {
			log.info("key, start : " + range[0] + " ;end :" + range[1] + " ; curStart : " + oldRange == null ? null : oldRange[0] + "; oldEnd :" + oldRange == null ? null : oldRange[1]);
		}
		
		if (oldRange == null) {
			oldRange = range;
			persistIndexRangeBeforeServer.put(key, oldRange);
		}
		if (oldRange[0] > range[0]) {
			oldRange[0] = range[0];
		}
		if (oldRange[1] < range[1]) {
			oldRange[1] = range[1];
		}
	}
	
	/**
	 * 当主机启动，并向备机发出同步请求后，主机和备机都处于不可接收数据状态；
	 * 当主机接收到备机的状态数据或备机无法链接时，主机更新状态索引数据并设置可接收数据，
	 * 备机设置为不可接收数据状态后，可定时或根据是否有客户端发送数据判断主机是否存活，若主机存活，则保持不可接收数据；否则修改状态为可接收数据
	 * 
	 * 主机默认设置为不可接收；
	 * 备机默认设置为可接收。
	 * 
	 * @param queueName
	 * @param serverName
	 * @return
	 */
	public static boolean canAcceptData(String queueName, String serverName) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		AtomicBoolean data = canAcceptData.get(key);
		if (data == null) {
			synchronized (canAcceptData) {
				data = canAcceptData.get(key);
				if (data == null) {
					data = new AtomicBoolean(false);
					canAcceptData.put(key, data);
				}
			}
		}
		return data.get();
	}
	
	public static void setCanAcceptData(String queueName, String serverName,boolean canAccept) {
		String key = NotifyUtility.buildKey(queueName, serverName);
		AtomicBoolean data = canAcceptData.get(key);
		if (data == null) {
			synchronized (canAcceptData) {
				data = canAcceptData.get(key);
				if (data == null) {
					data = new AtomicBoolean(false);
					canAcceptData.put(key, data);
				}
			}
		}
		data.compareAndSet(!canAccept, canAccept);
	}
	
	/** 
	 * @param args 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		//test 
		//System.setProperty("wade.server.port", "8000");
		//System.setProperty("wade.server.ip", "127.0.0.1");
		// test 
		
		log.info("begin init files !");
		ServerFileProxy.init();
		
		// HA同步启动是，若为备机则异步启动即可，若为主机，则需与备机同步完数据后才能开启服务
		if (NotifyUtility.getHaPort() > 0) {
			isHaServer = true;
			// 标记该服务为备机服务
			HaServer.init();
		} else {
			log.info("begin int haClient !");
			isHaServer = false;
			// 标记该服务为主机服务
			HaClient.init();
		}
		
		ServerFileProxy.reloadUnConsumerData();
		
		log.info("Begin init deal request pool!");
		new ServerFileDealPool().init();
		
		while (true) {
			Set<String> keys = canAcceptData.keySet();
			boolean canAccept = true;
			for (String key : keys) {
				if (!canAcceptData.get(key).get()) {
					canAccept = false;
					log.info("Server can not accept data now!" + key);
					Thread.currentThread().sleep(1000);
					break;
				}
			}
			if (canAccept) {
				break;
			}
		}
		
		log.info("Notify server is open!");
		NotifyServer server = new NotifyServer();
		server.startServer();
	}

	public static boolean isHaServer() {
		return isHaServer;
	}
	
	@Override
	public int getPort() {
		return NotifyUtility.getServerPort();
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

}
