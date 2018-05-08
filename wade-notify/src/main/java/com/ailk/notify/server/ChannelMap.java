/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server;

import io.netty.channel.Channel;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ailk.notify.common.NotifyUtility;

/**
 * 用于保存客户端发送的信息及对应的通道关系
 * 
 * @className:ChannelMap.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-16 
 */
public class ChannelMap {

	private static final transient Logger log = Logger.getLogger(ChannelMap.class);
	
	private ByteBuffer data;
	private Channel channel;
	private Long producerSequence;
	private String queueName;
	private String serverName;
	private long signal;
	
	public ChannelMap(ByteBuffer data, Channel channel) {
		this.data = data;
		this.channel = channel;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public String getQueueName() {
		return this.queueName;
	}
	
	public String getServerName() {
		return this.serverName;
	}
	
	public void setSignal(long signal) {
		this.signal = signal;
	}
	
	public long getSignal() {
		return this.signal;
	}
	
	public void setSequnce(Long producerSequence) {
		this.producerSequence = producerSequence;
	}
	
	public Long getSequence() {
		return this.producerSequence;
	}
	
	public ByteBuffer getData() {
		return this.data;
	}
	
	public void releaseData() {
		NotifyUtility.releaseByteBuffer(this.data);
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public boolean isChannelActive() {
		return this.channel.isActive();
	}
	
	/**
	 * 返回客户端信息
	 *  
	 * @param data
	 * @return 
	 */
	public boolean write(ByteBuffer data) {
		try {
			this.channel.writeAndFlush(data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return false;
		} finally {
			/*NotifyUtility.releaseByteBuffer(data);*/
		}
	}
	
}
