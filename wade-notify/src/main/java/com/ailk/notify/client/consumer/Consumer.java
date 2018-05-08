/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.client.consumer;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataInput;
import com.ailk.notify.common.ChannelData;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.SocketPool;

/**
 * @className:Consumer.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-25 
 */
public class Consumer {
	private static final transient Logger log = Logger.getLogger(Consumer.class);
	
	public static Map<String, LinkedBlockingQueue<ConsumerData>> msgQueueMap = new ConcurrentHashMap<String, LinkedBlockingQueue<ConsumerData>>();
	private String queueName;
	private static Map<String, Consumer> consumerMap = new HashMap<String, Consumer>();
	private static final int RETRIVE_MSG_LENGTH = 2 + 8 + NotifyUtility.getMaxQueueNameLength() + NotifyUtility.getMaxServerNameLength() + 2 + 8;
	private static final int BEGIN_CONSUMER_MSG_LENGTH = RETRIVE_MSG_LENGTH - 8 + NotifyUtility.getMaxIndexFileNameLength() + NotifyUtility.getMaxIndexOffsetLength();
	//private static Map<String, AtomicInteger> retriveChannelCounts = new ConcurrentHashMap<String, AtomicInteger>();
	//private static Map<String, LockCondition> addLocks = new ConcurrentHashMap<String, LockCondition>();
	private static long lastAddTime = System.currentTimeMillis();
	private static Set<String> signalSets = new HashSet<String>();
	private static Map<String, Lock> signalLock = new ConcurrentHashMap<String, Lock>();// ReentrantLock();
	private static Map<String, Condition> signalCondition = new ConcurrentHashMap<String, Condition>(); 
	private long signal = 0;
	
	private static long sendTime = 0;
	//private static Map<String, LockCondition> getLocks = new ConcurrentHashMap<String, LockCondition>();
	
	protected Consumer(String queueName) {
		this.queueName = queueName;
		LinkedBlockingQueue<ConsumerData> msgQueue = new LinkedBlockingQueue<ConsumerData>();
		msgQueueMap.put(queueName, msgQueue);
		
		Lock lock = new ReentrantLock();
		Condition condition = lock.newCondition();
		signalLock.put(queueName, lock);
		signalCondition.put(queueName, condition);
		
		SocketPool.init(queueName, ConsumerClient.class);
		// 预缓存待消费数据
		
		new Thread(new ReceiveMsgFromServer(queueName)).start();
		//retriveMsg();
	}
	
	public static void addMsg(String queueName, ConsumerData consumerData, long signalReturn) {
		try {
			if (log.isInfoEnabled()) {
				log.info("get msg, queueName :" + queueName);
			}
			msgQueueMap.get(queueName).put(consumerData);
			//reduceRetriveCount(queueName, true);
			lastAddTime = System.currentTimeMillis();
			
			String signalKey = queueName + "_" + signalReturn;
			signalLock.get(queueName).lock();
			if (signalSets.contains(signalKey)) {
				signalSets.remove(signalKey);
				signalCondition.get(queueName).signalAll();
			}
			signalLock.get(queueName).unlock();
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	/**
	 * 需通过此方法获取Consumer实例对象
	 * 
	 * 可在系统预热时加载此方法，以加速数据处理
	 * 
	 * @param queueName
	 * @return
	 */
	public static Consumer getInstance(String queueName) {
		if (queueName.length() > NotifyUtility.getMaxQueueNameLength()) {
			throw new RuntimeException("Consumer : The length of queue name " + queueName + " must less than " + NotifyUtility.getMaxQueueNameLength());
		}
		Consumer consumer = consumerMap.get(queueName);
		if (consumer == null) {
			synchronized (Consumer.class) {
				consumer = consumerMap.get(queueName);
				if (consumer == null) {
					consumer = new Consumer(queueName);
					consumerMap.put(queueName, consumer);
				}
			}
		}
		return consumer;
	}
	
	/**
	 * 发起请求，从服务端获取数据
	 */
	public void retriveMsg() {
		//while (true) {
			if (msgQueueMap.get(this.queueName).size() > NotifyUtility.CONSUMER_CACHE_DATA_SIZE) {
				return ;
			}
			ChannelData channelData = SocketPool.getChannel(this.queueName);
			if (channelData == null) {
				log.error("No server is Running! queueName :" + queueName);
				try {
					Thread.currentThread().sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//Consumer.reduceRetriveCount(queueName);
				//retriveMsg();
				return;
			}
			ByteBuffer buffer = getRetriveMessage(channelData.getQueueName(), channelData.getServerName());
			
			channelData.getChannel().writeAndFlush(buffer);
			//retriveChannelCounts.get(queueName).incrementAndGet();
			SocketPool.returnChannel(channelData);
			NotifyUtility.releaseByteBuffer(buffer);
		//}
	}
	
	public void beginConsumerMessage(String queueName, String serverName, long fileName, int indexOffset) {
		ChannelData channelData = SocketPool.getChannel(queueName, serverName);
		if (channelData != null) {
			ByteBuffer message = getBeginConsumerMessage(queueName, serverName, fileName, indexOffset);
			channelData.getChannel().writeAndFlush(message);
			SocketPool.returnChannel(channelData);
			NotifyUtility.releaseByteBuffer(message);
		} else {
			if (log.isInfoEnabled()) {
				log.info("No server is running for queueName :" + queueName + "; serverName :" + serverName);
			}
		}
	}
	
	public ByteBuffer getBeginConsumerMessage(String queueName, String serverName, long fileName, int indexOffset) {
		ByteBuffer buffer = ByteBuffer.allocate(BEGIN_CONSUMER_MSG_LENGTH);
		buffer.putShort(NotifyUtility.CLIENT_TYPE.CONSUMER.getType());
		buffer.putLong(-1);
		buffer.put(NotifyUtility.getBytesWithSpecifyLength(queueName, NotifyUtility.getMaxQueueNameLength()));
		buffer.put(NotifyUtility.getBytesWithSpecifyLength(serverName, NotifyUtility.getMaxServerNameLength()));
		buffer.putShort(NotifyUtility.CONSUMER_MESSAGE_TYPE.BEGIN_CONSUMER.getType());
		buffer.putLong(fileName);
		buffer.putInt(indexOffset);
		buffer.flip();
		return buffer;
	}
	
	public ByteBuffer getRetriveMessage(String queueName, String serverName) {
		ByteBuffer buffer = ByteBuffer.allocate(RETRIVE_MSG_LENGTH);
		buffer.putShort(NotifyUtility.CLIENT_TYPE.CONSUMER.getType());
		buffer.putLong(-1);
		buffer.put(NotifyUtility.getBytesWithSpecifyLength(queueName, NotifyUtility.getMaxQueueNameLength()));
		buffer.put(NotifyUtility.getBytesWithSpecifyLength(serverName, NotifyUtility.getMaxServerNameLength()));
		buffer.putShort(NotifyUtility.CONSUMER_MESSAGE_TYPE.RETRIVE_MESSAGE.getType());
		buffer.putLong(signal);
		buffer.flip();
		return buffer;
	}

	/**
	 *  获取待处理的消息
	 *  
	 * @return 
	 * @throws InterruptedException 
	 * @see com.ailk.notify.common.Client#sendAsyncMsg() 
	 */
	public IDataInput getMsg() throws InterruptedException {
		ConsumerData consumerData = msgQueueMap.get(this.queueName).poll(10, TimeUnit.MILLISECONDS);
		//retriveMsg();
		if (consumerData == null) {
			if (log.isDebugEnabled()) {
				log.debug("no msg is in local cache!");
			}
			return null;
		}
		/*
		getLocks.get(queueName).getLock().lock();
		getLocks.get(queueName).getCondition().signalAll();
		getLocks.get(queueName).getLock().unlock();
		*/
		beginConsumerMessage(consumerData.getQueueName(), consumerData.getServerName(), consumerData.getFileName(), consumerData.getIndexOffset());
		return consumerData.getInput();
	}
	
	class ReceiveMsgFromServer implements Runnable {
		private String queueName;
		private BlockingQueue<ConsumerData> msgQueue;
		//private LockCondition addLockCondition;
		//private LockCondition getLockCondition;
		
		public ReceiveMsgFromServer(String queueName) {
			this.queueName = queueName;
			this.msgQueue = msgQueueMap.get(this.queueName);
			/*addLockCondition = new LockCondition();
			//getLockCondition = new LockCondition();
			addLocks.put(queueName, addLockCondition);
			AtomicInteger retriveCount = retriveChannelCounts.get(queueName);
			if (retriveCount == null) {
				retriveCount = new AtomicInteger(0);
				retriveChannelCounts.put(queueName, retriveCount);
			}*/
			//getLocks.put(queueName, getLockCondition);
		}
		
		public void run() {
			int beginRetriveCount = NotifyUtility.CONSUMER_CACHE_DATA_SIZE -  NotifyUtility.CONSUMER_CACHE_DATA_SIZE/3;
			while (true) {
				/*if (this.msgQueue.size() > NotifyUtility.CONSUMER_CACHE_DATA_SIZE / 2) {
					continue;
				}
				*/
				try {
					if (this.msgQueue.size() < beginRetriveCount) {
						/*AtomicInteger retriveCount = retriveChannelCounts.get(queueName);
						addLockCondition.getLock().lock();*/
						String signalKey = queueName + "_" + signal;
						signalLock.get(queueName).lock();
						if (signalSets.contains(signalKey)) {
							signalCondition.get(queueName).await(20, TimeUnit.MILLISECONDS);
						}
						signalLock.get(queueName).unlock();
						signal = System.currentTimeMillis();
						signalKey = queueName + "_" + signal;
						signalSets.add(signalKey);
						if (log.isInfoEnabled()) {
							log.info("begin retrive msg, queueName :" + queueName);
						}
						retriveMsg();
					}
					
					if (this.msgQueue.size() == 0 && (System.currentTimeMillis() - lastAddTime) >= 2000) {
						Thread.currentThread().sleep(1000);
					}
					
					if (log.isDebugEnabled()) {
						log.debug("msg in cache is enough, msg cache size :" + this.msgQueue.size());
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
