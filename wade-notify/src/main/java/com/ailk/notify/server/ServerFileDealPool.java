/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.ailk.notify.common.FileDealPool;
import com.ailk.notify.common.FileProxy;
import com.ailk.notify.common.NotifyUtility;

/**
 * 
 * 
 * @className:ServerFileDealPool.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-6 
 */
public class ServerFileDealPool extends FileDealPool {
	
	private static final transient Logger log = Logger.getLogger(ServerFileDealPool.class);

	//protected static final ExecutorService transferClientService = Executors.newFixedThreadPool(NotifyUtility.DEFAULT_TRANSFER_SERVER_DATA_THREAD_SIZE);
	// 缓存生产者发送的服务端的通道
	protected static final Map<String, BlockingQueue<ChannelMap>> producerMaps = new HashMap<String, BlockingQueue<ChannelMap>>();
	// 缓存消费者发送的服务端通道
	protected static final Map<String, BlockingQueue<ChannelMap>> consumerMaps = new HashMap<String, BlockingQueue<ChannelMap>>();

	@Override
	public synchronized FileProxy getProxy(short type, String queueName, String serverName) {
		
		String key = NotifyUtility.buildKey(queueName, serverName);
		// 生产者和消费者的数据流分开存放，便于针对生产者或消费者进行处理
		if (NotifyUtility.CLIENT_TYPE.PRODUCER.getType() == type) {
			if (log.isDebugEnabled()) {
				log.debug("Put queue to producermap!");
			}
			if (producerMaps.get(key) == null) {
				BlockingQueue<ChannelMap> queue = new LinkedBlockingQueue<ChannelMap>();
				producerMaps.put(key, queue);
			}
		} else if (NotifyUtility.CLIENT_TYPE.CONSUMER.getType() == type) {
			if (log.isDebugEnabled()) {
				log.debug("Put queue to producermap!");
			}
			if (consumerMaps.get(key) == null) {
				BlockingQueue<ChannelMap> queue = new LinkedBlockingQueue<ChannelMap>();
				consumerMaps.put(NotifyUtility.buildKey(queueName, serverName), queue);
			}
			
		}
		
		return new ServerFileProxy(type, queueName, serverName);
	}
	
	/**
	 * 接收生产者或消费者的通道流，并对其进行分类 
	 * @param channelMap
	 */
	public static void putChannelMap(ChannelMap channelMap) {
		ByteBuffer data = channelMap.getData();
		short type = data.getShort();
		Long sequence = data.getLong();
		channelMap.setSequnce(sequence);
		
		byte[] queueBytes = new byte[NotifyUtility.getMaxQueueNameLength()];
		data.get(queueBytes);
		String queueName = NotifyUtility.transferByteArrayToStr(queueBytes);
		byte[] serverAddrBytes = new byte[NotifyUtility.getMaxServerNameLength()];
		data.get(serverAddrBytes);
		String serverAddrName = NotifyUtility.transferByteArrayToStr(serverAddrBytes);
		
		if (log.isDebugEnabled()) {
			log.debug("Server receive data, type :" + type + "; queueName :" + queueName + "; serverName :" + serverAddrName + "; time :" + System.currentTimeMillis());
		}
		
		try {
			BlockingQueue<ChannelMap> queue = null;
			
			if (type == NotifyUtility.CLIENT_TYPE.PRODUCER.getType()) {
				if (log.isDebugEnabled()) {
					log.debug("Get producer request! queueName :" + queueName + "; serverName :" + serverAddrName);
				}
				queue = producerMaps.get(NotifyUtility.buildKey(queueName, serverAddrName));
			} else 	if (type == NotifyUtility.CLIENT_TYPE.CONSUMER.getType()) { 
				if (log.isDebugEnabled()) {
					log.debug("Get consumer request! queueName :" + queueName + "; serverName :" + serverAddrName);
				}
				queue = consumerMaps.get(NotifyUtility.buildKey(queueName, serverAddrName));
			}
			
			if (queue == null) {
				log.error("The queue which name is  " + queueName + " and server name is " + serverAddrName + " is not init on the server !!!");
			} else {
				if (log.isDebugEnabled()) {
					log.debug("file deal pool, put to queue! time :" + System.currentTimeMillis());
				}
				queue.put(channelMap);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	/**
	 * 通道处理进程通过此方法获取可处理的通道流
	 * @return
	 */
	public static BlockingQueue<ChannelMap> getChannelQueue(short type, String queueName, String serverName) {
		BlockingQueue<ChannelMap> queue = null;
		if (type == NotifyUtility.CLIENT_TYPE.PRODUCER.getType()) {
			if (log.isDebugEnabled()) {
				log.debug("Get queue from producermap!");
			}
			queue = producerMaps.get(NotifyUtility.buildKey(queueName, serverName));
		} else 	if (type == NotifyUtility.CLIENT_TYPE.CONSUMER.getType()) { 
			if (log.isDebugEnabled()) {
				log.debug("Get queue from consumermap!");
			}
			queue = consumerMaps.get(NotifyUtility.buildKey(queueName, serverName));
		}
		
		if (queue == null) {
			log.error("type :" + type + "; queueName :" + queueName + "; serverName :" + serverName);
		}
		
		return queue;
	}
	
}
