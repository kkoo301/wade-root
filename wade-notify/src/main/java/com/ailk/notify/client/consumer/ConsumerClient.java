/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.client.consumer;

import io.netty.channel.Channel;
import io.netty.handler.codec.ByteToMessageDecoder;

import com.ailk.notify.common.MessageHandler;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.SocketBucket;
import com.ailk.notify.common.SocketClient;
import com.ailk.notify.common.code.ByteBufferMessageDecoder;
import com.ailk.notify.common.code.ByteBufferMessageEncoder;

/**
 * @className:ConsumerClient.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-20 
 */
public class ConsumerClient extends SocketClient {

	private ConsumerHandler handler;
	
	public ConsumerClient(String queueName, String serverName, String ip, int port, SocketBucket bucket) {
		super(queueName, serverName, ip, port, bucket);
		handler = new ConsumerHandler();
	}

	@Override
	public int getLoopGroupSize() {
		return NotifyUtility.EVENTLOOP_CONSUMER_CLIENT_SIZE;
	}

	@Override
	public ByteBufferMessageEncoder getEncoder() {
		return new ByteBufferMessageEncoder();
	}

	@Override
	public ByteToMessageDecoder getDecoder() {
		return new ByteBufferMessageDecoder();
		//return new DataInputMessageDecoder();
	}

	@Override
	public MessageHandler getMessageHandler() {
		return handler;
	}
	
	@Override
	public void closeChannel(Channel channel) {
		//Consumer.reduceRetriveCount(queueName);
	}
}
