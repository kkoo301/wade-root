/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.client.producer;

import io.netty.channel.Channel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.notify.common.ChannelData;
import com.ailk.notify.common.MessageHandler;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.SocketBucket;
import com.ailk.notify.common.SocketClient;
import com.ailk.notify.common.SocketPool;
import com.ailk.notify.common.code.ByteBufferMessageDecoder;
import com.ailk.notify.common.code.ByteBufferMessageEncoder;

/**
 * @className:ProducerClient.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-20 
 */
public class ProducerClient extends SocketClient {
	private static final transient Logger log = Logger.getLogger(ProducerClient.class);
	
	private ProducerHandler handler = new ProducerHandler();
	
	public ProducerClient(String queueName, String serverName, String ip, int port, SocketBucket bucket) {
		super(queueName, serverName, ip, port, bucket);
	}

	@Override
	public int getLoopGroupSize() {
		return NotifyUtility.EVENTLOOP_RRODUCER_CLIENT_SIZE;
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
	public MessageHandler getMessageHandler() {
		return handler;
	}

	@Override
	public void closeChannel(Channel channel) {
		// 当连接池内还有可用链接时, 将通过该通道且未返回的数据重新发送
		if (SocketPool.isServerWorking(queueName)) {
			List<ProducerMessage> producerMessages = Producer.getMessagesByChannel(channel);
			for (ProducerMessage message : producerMessages) {
				if (!message.isTimeout()) {
					Long sequence = message.getSequence();
					ByteBuffer sendData = Producer.buildData(sequence, message.getQueueName(), this.serverName, message.getInput());
					ChannelData channelData = SocketPool.getChannel(message.getQueueName());
					if (channelData == null) {
						log.info("No server data, queueName : " + message.getQueueName() + "; data input :" + message.getInput().toString());
						continue;
					}
					channelData.getChannel().writeAndFlush(sendData);
					message.setChannel(channelData.getChannel());
					Producer.putMessage(channelData.getChannel(), sequence, message);
					NotifyUtility.releaseByteBuffer(sendData);
				}
			}
		}
	}
}
