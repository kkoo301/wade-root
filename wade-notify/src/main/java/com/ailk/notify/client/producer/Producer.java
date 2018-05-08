/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.client.producer;

import io.netty.channel.Channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataInput;
import com.ailk.notify.common.ChannelData;
import com.ailk.notify.common.Client;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.SocketPool;

/**
 * @className:Producer.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-25 
 */
public class Producer implements Client {
	private static final transient Logger log = Logger.getLogger(Producer.class);
	
	private String queueName;
	
	private static Map<String, Producer> producerMap = new HashMap<String, Producer>();
	
	private static Map<Long, ProducerMessage> messagesCallback = new ConcurrentHashMap<Long, ProducerMessage>();
	// 保存Channel与 messagesCallback的序列号之间的对应关系
	private static Map<Channel, Set<Long>> messageRelation = new ConcurrentHashMap<Channel, Set<Long>>();
	
	private AtomicLong sequenceCount = new AtomicLong(0);
	
	static {
		new Thread(new CheckTimeoutMesssage()).start();
	}
	
	protected Producer(String queueName) {
		this.queueName = queueName;
		
		SocketPool.init(queueName, ProducerClient.class);
	}
	
	public static Producer getInstance(String queueName) {
		if (queueName.length() > NotifyUtility.getMaxQueueNameLength()) {
			throw new RuntimeException("Consumer : The length of queue name " + queueName + " must less than " + NotifyUtility.getMaxQueueNameLength());
		}
		Producer producer = producerMap.get(queueName);
		if (producer == null) {
			synchronized (Producer.class) {
				producer = producerMap.get(queueName);
				if (producer == null) {
					producer = new Producer(queueName);
					producerMap.put(queueName, producer);
				}
			}
		}
		return producer;
	}
	
	public Long createSequence() {
		return sequenceCount.incrementAndGet();
	}
	
	public static void putMessage(Channel channel, Long sequence, ProducerMessage message) {
		if (log.isDebugEnabled()) {
			log.debug("put message seq ! seq :" + sequence);
		}
		
		putMessageRelation(channel, sequence);
		messagesCallback.put(sequence, message);
	}
	
	public static ProducerMessage removeProducerMessage(Channel channel,Long sequence) {
		removeMessageRelation(channel, sequence);
		return messagesCallback.remove(sequence);
	}

	private static void removeMessageRelation(Channel channel, long sequence) {
		messageRelation.get(channel).remove(sequence);
	}
	
	public static void putMessageRelation(Channel channel, long sequence) {
		Set<Long> messageSequences = messageRelation.get(channel);
		if (messageSequences == null) {
			synchronized (Producer.class) {
				messageSequences = messageRelation.get(channel);
				if (messageSequences == null) {
					messageSequences = new HashSet<Long>();
					messageRelation.put(channel, messageSequences);
				}
			}
		}
		messageSequences.add(sequence);
	}
	
	public static List<ProducerMessage> getMessagesByChannel(Channel channel) {
		Set<Long> sequences = messageRelation.get(channel);
		List<ProducerMessage> messages = new ArrayList<ProducerMessage>();
		Iterator<Long> sequeIter = sequences.iterator();
		while (sequeIter.hasNext()) {
			Long sequence = sequeIter.next();
			sequeIter.remove();
			messages.add(removeProducerMessage(channel, sequence));
		}
		return messages;
	}
	
	/**
	 *  
	 * @return 
	 * @see com.ailk.notify.common.Client#sendAsyncMsg() 
	 */
	public boolean sendAsyncMsg(IDataInput input, CallBackHandler callback) {
		if (log.isDebugEnabled()) {
			log.debug("send msg, begin get channel, time :" + System.currentTimeMillis());
		}
		ChannelData channelData = SocketPool.getChannel(this.queueName);
		if (log.isDebugEnabled()) {
			log.debug("send msg, end get channel, time :" + System.currentTimeMillis());
		}
		if (channelData == null) {
			throw new RuntimeException("No server is running for queue " + this.queueName + "!");
		}
		callback.setDataInput(input);
		Long sequence = createSequence();
		
		ProducerMessage message = new ProducerMessage(queueName, channelData.getChannel(), sequence, callback);
		putMessage(channelData.getChannel(), sequence, message);
		
		ByteBuffer data = buildData(sequence, channelData.getQueueName(), channelData.getServerName(), input);
		if (log.isDebugEnabled()) {
			log.debug("Producer begin write:" + System.currentTimeMillis() + "; channel :" + channelData.getChannel().toString());
		}
		channelData.getChannel().writeAndFlush(data);
		if (log.isDebugEnabled()) {
			log.debug("Producer write over:" + System.currentTimeMillis());
		}
		SocketPool.returnChannel(channelData);
		NotifyUtility.releaseByteBuffer(data);
		return true;
	}
	
	public static ByteBuffer buildData(Long sequence, String queueName, String serverName, IDataInput input) {
		byte[] datas = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug("Producer begin encode hessian, time :" + System.currentTimeMillis());
			}
			datas = NotifyUtility.encodeHessian(input);
			
			if (log.isDebugEnabled()) {
				log.debug("Producer end encode hessian, time :" + System.currentTimeMillis());
			}
			
			/*if (log.isInfoEnabled()) {
				IDataInput inputTest = NotifyUtility.decodeHessian(datas);
				log.info("decode after encode :" + inputTest.toString());
				//log.info("Data after encode hessian :" + new String(datas));
			}*/
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		int dataLength = datas.length;
		int size = 2 + 8 + NotifyUtility.getMaxQueueNameLength() + NotifyUtility.getMaxServerNameLength() + dataLength;
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.putShort(NotifyUtility.CLIENT_TYPE.PRODUCER.getType());
		buffer.putLong(sequence);
		byte[] queueNameBytes = NotifyUtility.getBytesWithSpecifyLength(queueName, NotifyUtility.getMaxQueueNameLength());
		byte[] serverNameBytes = NotifyUtility.getBytesWithSpecifyLength(serverName, NotifyUtility.getMaxServerNameLength()); 
 		buffer.put(queueNameBytes);
		buffer.put(serverNameBytes);
		buffer.put(datas);	
		buffer.rewind();
		
		/*if (log.isInfoEnabled()) {
			log.info("producer, pos : " + buffer.position() + " limit: " + buffer.limit() + "; dataLength :" + dataLength);
			byte[] bufferBytes = new byte[buffer.limit()];
			buffer.get(bufferBytes);
			buffer.rewind();
			log.info("producer, sequence: " + sequence + "; pos : " + buffer.position() + "; limit: " + buffer.limit() + 
					"; queueName :" + new String(queueNameBytes) + "; serverName :" + new String(serverNameBytes)
					+ "; data :" + new String(bufferBytes));
		}*/
		
		return buffer;
	}

	/**
	 *  同步发送消息
	 *  
	 * @return 返回消息的偏移量, 当返回为null的时候表示操作异常
	 * 
	 * @see com.ailk.notify.common.Client#sendMsg() 
	 */
	public String sendMsg(IDataInput input) {
		ChannelData channelData = SocketPool.getChannel(this.queueName);
		if (channelData == null) {
			throw new RuntimeException("No server is running for queue " + this.queueName + "!");
		}
		if (log.isDebugEnabled()) {
			log.debug("Get Channel : queueName :" + channelData.getQueueName() + "; serverName : " + channelData.getServerName());
		}
		String queueName = channelData.getQueueName();
		String serverName = channelData.getServerName();
		
		CountDownLatch countDown = new CountDownLatch(1);
		Long sequence = createSequence();
		
		ProducerMessage message = new ProducerMessage(queueName, channelData.getChannel(), sequence, countDown, input);
		putMessage(channelData.getChannel(), sequence, message);
		ByteBuffer data = buildData(sequence, queueName, serverName, input);
		channelData.getChannel().writeAndFlush(data);
		SocketPool.returnChannel(channelData);
		NotifyUtility.releaseByteBuffer(data);
		try {
			boolean awaitResult = countDown.await(NotifyUtility.PRODUCER_SEND_SYNC_TIMEOUT, TimeUnit.SECONDS);
			if (!awaitResult) {
				message.setTimeout();
				log.error("Message send timeout ! queueName : " + queueName + "; serverName : "  + serverName + " ; data : " + input.toString());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return message.getIndex();
	}

	// 校验message是否超时, 若超时则删除保存的回调及通道关系数据
	static class CheckTimeoutMesssage implements Runnable {

		public void run() {
			while (true) {
				try {
					Iterator<Long> messageIterator = messagesCallback.keySet().iterator();
					while (messageIterator.hasNext()) {
						Long sequence = messageIterator.next();
						ProducerMessage message = messagesCallback.get(sequence);
						if (message != null) {
							if (message.isTimeout()) {
								log.info("Time out, queueName : " + message.getQueueName() + "; data input :" + message.getInput().toString());
								messageIterator.remove();
								removeProducerMessage(message.getChannel(), sequence);
							}
						}
					}
				} catch (Throwable e) {
					log.error(e.getMessage());
				}
				
				try {
					Thread.currentThread().sleep(NotifyUtility.PRODUCER_SEND_MESSAGE_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
