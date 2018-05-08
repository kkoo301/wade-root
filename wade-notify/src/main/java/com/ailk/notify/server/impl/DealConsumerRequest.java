/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server.impl;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.ailk.notify.common.FileProxy;
import com.ailk.notify.common.FileUtility;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.server.CacheData;
import com.ailk.notify.server.ChannelMap;
import com.ailk.notify.server.DealRequest;
import com.ailk.notify.server.MessageCache;
import com.ailk.notify.server.NotifyServer;
import com.ailk.notify.server.ServerFileProxy;

/**
 * 处理消费者发送的请求，并返回信息
 * 
 * @className:DealConsumerRequest.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-17 
 */
public class DealConsumerRequest extends DealRequest {
	private static final transient Logger log = Logger.getLogger(DealConsumerRequest.class);
	
	private static BlockingQueue<CacheData> queue = null;
	// 保存被消费者获取但未接收到已经开始消费的消息
	private static Map<String, Set<Integer>> sendConsumerDataCache = new ConcurrentHashMap<String, Set<Integer>>();
	// 记录发送的消息的时间
	private static Map<String, Long> sendConsumerDataTime = new ConcurrentHashMap<String, Long>();
	private static ScheduledExecutorService checkConsumerTimeoutService = Executors.newScheduledThreadPool(1);
	private static ReentrantLock sendCacheLock = new ReentrantLock();
	
	private static AtomicInteger count = new AtomicInteger(0);
	private static AtomicInteger countReceive = new AtomicInteger(0);
	
	static {
		checkConsumerTimeoutService.schedule(new CheckCacheConsumerDataTimeout(), NotifyUtility.CHECK_CONSUMER_MESSAGE_TIMEOUT_INTERVAL, TimeUnit.SECONDS);
	}
	
	public DealConsumerRequest(String queueName, String serverName, ServerFileProxy fileProxy) {
		super(queueName, serverName, fileProxy);
		queue = MessageCache.getQueue(queueName);
	}
	
	/**
	 *  
	 * @param data
	 * @return 
	 * @see com.ailk.notify.server.DealRequest#execute(java.nio.ByteBuffer) 
	 */
	@Override
	public void execute(ChannelMap channelMap) {
		
		if (!NotifyServer.canAcceptData(queueName, serverName)) {
			
			if (log.isInfoEnabled()) {
				log.info("Notify server can not accept data now! queueName :" + queueName + "; serverName :" + serverName);
			}
			
			byte[] returnData = NotifyUtility.SERVER_CANNOT_RECEIVE_DATA.getBytes();
			
			ByteBuffer writeData = ByteBuffer.allocate(NotifyUtility.getMaxQueueNameLength() + returnData.length);
			writeData.put(NotifyUtility.getBytesWithSpecifyLength(channelMap.getQueueName(), NotifyUtility.getMaxQueueNameLength()));
			writeData.put(returnData);
			
			channelMap.write(writeData);
			NotifyUtility.releaseByteBuffer(writeData);
			return ;
		}
		
		short type = channelMap.getData().getShort();
		if (log.isDebugEnabled()) {
			log.debug("Get consumer request for retrive data! state :" + ((NotifyUtility.CONSUMER_MESSAGE_TYPE.RETRIVE_MESSAGE.getType() == type) ? "retrive" : "beginConsumer"));
		}
		
		if (NotifyUtility.CONSUMER_MESSAGE_TYPE.RETRIVE_MESSAGE.getType() == type) {
			long signal = channelMap.getData().getLong();
			channelMap.setSignal(signal);
			channelMap.releaseData();
			for (int i = 0; i < NotifyUtility.CONSUMER_CACHE_DATA_SIZE; i++) {
				returnMsgToConsumer(channelMap);
			}
		} else if (NotifyUtility.CONSUMER_MESSAGE_TYPE.BEGIN_CONSUMER.getType() == type) {
			changeMessageState(queueName, serverName, channelMap, NotifyUtility.MESSSAGE_STATE.BEGIN_CONSUMER.getState());
			/*ByteBuffer respBuffer = NotifyUtility.getKeepAliveRespBuffer();
			channelMap.write(respBuffer);
			NotifyUtility.releaseByteBuffer(respBuffer);
			*/
		} else {
			log.error("Receive data can not be dealed! data type:" + type);
		}
	}
	
	/**
	 * 改变消息的消费状态
	 * 
	 * @param queueName
	 * @param serverName
	 * @param channelMap
	 * @param messageState
	 */
	private void changeMessageState(String queueName, String serverName, ChannelMap channelMap, short messageState) {
		long fileName = channelMap.getData().getLong();
		Integer indexOffset = channelMap.getData().getInt();
		channelMap.releaseData();
		
		if (log.isInfoEnabled()) {
			log.info("change consumer state size:" + countReceive.incrementAndGet() + "; offset :" + indexOffset + "; messageState :" + messageState);
		}
		
		removeConsumerDataCache(queueName, serverName, fileName, indexOffset);
		long indexFileSize = 0;
		long messageFileSize = 0;
		try {
			FileUtility fileUtility = this.fileProxy.getFileUtility(queueName, serverName, fileName);
			indexFileSize = fileUtility.getIndexFileSize();
			messageFileSize = fileUtility.getMsgFileSize();
			
			fileUtility.updateState(indexOffset, messageState);
		} catch (Exception e) {
			log.error("queueName :" + queueName + "; serverName :" + serverName + "; fileName :" + fileName +
					" ; indexOffset: " + indexOffset + "; messageState :" + messageState + "; " + 
						";index file size :" + indexFileSize + " ; message file size :" + messageFileSize);
		}
	}
	/**
	 * 返回客户端请求拉取的数据
	 *  
	 * @param channelMap
	 */
	private void returnMsgToConsumer(ChannelMap channelMap) {
		//for (int i = 0; i < NotifyUtility.CONSUMER_CACHE_DATA_SIZE; i++) {
		if (!channelMap.isChannelActive()) {
			if (log.isInfoEnabled()) {
				log.info("Channel is not active!!!" + channelMap.getChannel().remoteAddress());
			}
			//break;
		}
		
		if (log.isInfoEnabled()) {
			log.info("Get Client retrive msg request!!!" + channelMap.getChannel().remoteAddress());
		}
		
		CacheData cacheData = getData();
		
		if (!channelMap.isChannelActive() && cacheData != null) {
			MessageCache.addQueueFirst(queueName, serverName, cacheData.getFileName(), cacheData.getIndexOffset(), cacheData.getData());
			return;
			//break;
		}
		
		if (cacheData == null) {
			write(channelMap, null, -1, -1);
		} else {
			write(channelMap, cacheData.getData(), cacheData.getFileName(), cacheData.getIndexOffset());
		}
		//}
	}
	
	/**
	 * 更新消息状态
	 *  
	 * @param channelMap
	 * @param data
	 * @param fileName
	 * @param indexOffset
	 */
	protected void write(ChannelMap channelMap, ByteBuffer data, long fileName, int indexOffset) {
		long signal = channelMap.getSignal();
		//data.rewind();
		
		int writeDataLength = 8 + NotifyUtility.getMaxQueueNameLength() + NotifyUtility.getMaxServerNameLength() + 
				NotifyUtility.getMaxIndexFileNameLength() + NotifyUtility.getMaxIndexOffsetLength();
		
		if (data != null) {
			writeDataLength += data.limit() - data.position();
		}
		
		ByteBuffer writeData = ByteBuffer.allocate(writeDataLength);
		writeData.put(NotifyUtility.getBytesWithSpecifyLength(channelMap.getQueueName(), NotifyUtility.getMaxQueueNameLength()));
		writeData.put(NotifyUtility.getBytesWithSpecifyLength(channelMap.getServerName(), NotifyUtility.getMaxServerNameLength()));
		writeData.putLong(fileName);
		writeData.putInt(indexOffset);
		writeData.putLong(signal);
		//int dataPos = -1;
		if (data != null) {
			//dataPos = data.position();
			/*byte[] dataBytes = new byte[data.limit() - dataPos];
			data.get(dataBytes);*/
			data.mark();
			writeData.put(data);
		}
		
		if (log.isInfoEnabled()) {
			log.info("return consumer size:" + count.incrementAndGet() + "; limit :" + data == null ? null : data.limit() + " ; channel : " + channelMap.getChannel().remoteAddress());
		}
		
		writeData.flip();
		
		if (log.isDebugEnabled()) {
			log.debug("begin return consumer data; queueName : " + queueName + "; serverName :" + serverName + 
					"; fileName :" + fileName + "; indexOffset :" + indexOffset + "; queuesize :" + MessageCache.getQueue(queueName).size());
		}
		
		boolean isReturned = channelMap.write(writeData);
		NotifyUtility.releaseByteBuffer(writeData);
		if (data != null) {
			if (isReturned) {
				putConsumerDataCache(queueName, serverName, fileName, indexOffset);
				NotifyUtility.releaseByteBuffer(data);
			} else {
				data.reset();
				MessageCache.addQueueFirst(queueName, serverName, fileName, indexOffset, data);
			}
		}
	}

	public static void removeConsumerDataCache(String queueName, String serverName, long fileName, Integer indexOffset) {
		String key = NotifyUtility.buildKey(NotifyUtility.buildKey(queueName, serverName), fileName);
		sendConsumerDataTime.remove(NotifyUtility.buildKey(key, indexOffset));
		sendCacheLock.lock();
		Set<Integer> indexOffsetSet = sendConsumerDataCache.get(key);
		if (indexOffsetSet != null) {
			indexOffsetSet.remove(indexOffset);
		}
		if (indexOffsetSet != null && indexOffsetSet.isEmpty()) {
			sendConsumerDataCache.remove(key);
		}
		sendCacheLock.unlock();
	}
	
	public static void putConsumerDataCache(String queueName, String serverName, long fileName, int indexOffset) {
		String key = NotifyUtility.buildKey(NotifyUtility.buildKey(queueName, serverName), fileName);
		sendCacheLock.lock();
		Set<Integer> indexOffsetSet = sendConsumerDataCache.get(key);
		if (indexOffsetSet == null) {
			indexOffsetSet = new HashSet<Integer>();
			sendConsumerDataCache.put(key, indexOffsetSet);
		}
		indexOffsetSet.add(indexOffset);
		sendCacheLock.unlock();
		sendConsumerDataTime.put(NotifyUtility.buildKey(key, indexOffset), System.currentTimeMillis());
	}
	
	/**
	 * 获取缓存的数据供消费者消费 
	 * @return
	 */
	public CacheData getData() {
		try {
			return MessageCache.getData(queue, queueName);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 检测缓存的已经发送到消费者的数据是否已经消费过期，如果过期则重新消费
	 *  
	 * @className:DealConsumerRequest.java
	 *
	 * @version V1.0  
	 * @author lvchao
	 * @date 2015-4-11
	 */
	static class CheckCacheConsumerDataTimeout implements Runnable {

		public void run() {
			Set<String> keys = sendConsumerDataTime.keySet();
			long time = System.currentTimeMillis();
			for (String key : keys) {
				long sendTime = sendConsumerDataTime.get(key);
				if (NotifyUtility.CONSUMER_MESSAGE_TIMEOUT <= (time - sendTime)) {
					// 消费超时，需重新消费
					try {
						String[] datas = NotifyUtility.splitKey(key);
						String queueName = datas[0];
						String serverName = datas[1];
						long fileName = Long.valueOf(datas[2]);
						int indexOffset = Integer.valueOf(datas[3]);
						
						removeConsumerDataCache(queueName, serverName, fileName, indexOffset);
						ByteBuffer data = FileProxy.getFileUtility(queueName, serverName, fileName).getMessageByIndexOffset(indexOffset);
						//data.rewind();
						MessageCache.addQueueFirst(queueName, serverName, fileName, indexOffset, data);
					} catch (Throwable e) {
						log.error("key :" + key + "; sendTime :" + sendTime + "; errorMessage :" + e.getMessage());
					}
				}
			}
		}
		
	}

}
