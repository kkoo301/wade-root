/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.client.producer;

import io.netty.channel.Channel;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataInput;
import com.ailk.notify.common.LogClient;
import com.ailk.notify.common.NotifyUtility;

/**
 * 用于记录生产者生产过程中的中间信息
 * 便于后续进行同步或回调
 * 
 * @className:ProducerMessage.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-27 
 */
public class ProducerMessage {
	private static final transient Logger log = Logger.getLogger(ProducerMessage.class);
	
	private String queueName;
	
	// 当消息为异步发送时需设置该值
	private CallBackHandler callback;
	
	// 当消息为同步发送时需设置该值
	private CountDownLatch countDown;
	
	// 消息偏移量，可通过该值查找消息
	private String index;
	
	private IDataInput input;
	
	private boolean isSync;
	
	private boolean isTimeout;
	
	private long producerTime;
	
	private Channel channel;
	private Long sequence;
	
	public ProducerMessage(String queueName, Channel channel, Long sequence, CountDownLatch countDown, IDataInput input) {
		this.queueName = queueName;
		this.countDown = countDown;
		this.input = input;
		isSync = true;
		producerTime = System.currentTimeMillis();
		this.channel = channel;
		this.sequence = sequence;
	}
	
	public ProducerMessage(String queueName, Channel channel, Long sequence, CallBackHandler callback) {
		this.queueName = queueName;
		this.callback = callback;
		isSync = false;
		producerTime = System.currentTimeMillis();
		this.channel = channel;
		this.sequence = sequence;
	}
	
	public Long getSequence() {
		return this.sequence;
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public boolean isSync() {
		return this.isSync;
	}
	
	public IDataInput getInput() {
		if (isSync) {
			return this.input;
		} else {
			return this.callback.getDataInput();
		}
	}
	
	public String getQueueName() {
		return this.queueName;
	}
	
	public void setTimeout() {
		if (log.isDebugEnabled()) {
			log.debug("message is set to timeout!!!");
		}
		this.isTimeout = true;
		//synchronized (this) {
		over(null);
		//}
	}
	
	public boolean isTimeout() {
		if (!this.isTimeout) {
			long time = System.currentTimeMillis();
			long useTime = time - producerTime;
			if (useTime >= NotifyUtility.PRODUCER_SEND_MESSAGE_TIMEOUT) {
				this.isTimeout = true;
				if (log.isDebugEnabled()) {
					log.debug("data is timeout !");
				}
				over(null);
			}
		}
		return this.isTimeout;
	}
	
	public void over(String index) {
		if (log.isDebugEnabled()) {
			log.debug("put index to message : " + index);
		}
		Producer.removeProducerMessage(channel, sequence);
		this.index = index;
		if (countDown != null) {
			countDown.countDown();
		}
		if (callback != null) {
			callback.callback(index);
		}
		
	}
	
	/**
	 * 当消息返回后，可通过该方法获取消息索引
	 * 
	 * @return
	 */
	public String getIndex() {
		return index;
	}
	
}
