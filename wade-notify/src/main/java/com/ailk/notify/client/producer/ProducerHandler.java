/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.client.producer;

import io.netty.channel.Channel;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ailk.notify.common.ChannelData;
import com.ailk.notify.common.MessageHandler;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.SocketPool;

/**
 * @className:ProducerHandler.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-25 
 */
public class ProducerHandler implements MessageHandler {

	private static final transient Logger log = Logger.getLogger(ProducerHandler.class);
	
	@SuppressWarnings("static-access")
	public void handler(Channel channel, Object msg) {
		
		ByteBuffer data = (ByteBuffer)msg;
		
		if (log.isDebugEnabled()) {
			log.debug("Producer handle data : " + data);
		}
		
		Long sequence = data.getLong();
		String index = NotifyUtility.analyMessageOffset(data);
		ProducerMessage message = Producer.removeProducerMessage(channel, sequence);
		// 当备机为SERVER_CANNOT_RECEIVE_DATA该状态时表示，主机正在启动且正在与备机同步文件索引，
		// 客户端可主动链接主机，然后继续发送消息
		// 对于同步消息，若无法链接到主机，则停止发送服务端
		// 对于异步消息，不论链接是否成功，继续发送至服务端，重复该流程直到超时或被执行
		if (NotifyUtility.SERVER_CANNOT_RECEIVE_DATA.equals(index)) {
			index = null;
			ChannelData channelData = SocketPool.getChannel(message.getQueueName());
			if (channelData == null) {
				throw new RuntimeException("No server is running for queue " + message.getQueueName() + "!");
			}
			
			log.error("Server cannot receive data! queueName :" + message.getQueueName() + "; remote address :" + channel.remoteAddress());
			
			if (!channelData.isMaster()) {
				SocketPool.connectMaster(message.getQueueName());
				try {
					Thread.currentThread().sleep(NotifyUtility.PRODUCER_WAIT_CONNECT_MASTER_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
				channelData = SocketPool.getChannel(message.getQueueName());
			}
			
			if (!channelData.isMaster() && message.isSync()) {
				log.error("Message send timeout! queueName : " + message.getQueueName() + " ; input : " + message.getInput().toString());
				message.setTimeout();
			}
			
			if (!message.isTimeout()) {
				ByteBuffer sendData = Producer.buildData(sequence, message.getQueueName(), channelData.getServerName(), message.getInput());
				message.setChannel(channelData.getChannel());
				Producer.putMessage(channelData.getChannel(), sequence, message);
				channelData.getChannel().writeAndFlush(sendData);
				NotifyUtility.releaseByteBuffer(sendData);
			}
			return;
		}
		if (message == null) {
			log.error("The callback message with sequence of " + sequence + " is null !");
			return ;
		}
		if (!message.isTimeout()) {
			message.over(index);
		}
		
		NotifyUtility.releaseByteBuffer(data);
	}

}
