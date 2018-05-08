/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.client.consumer;

import io.netty.channel.Channel;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataInput;
import com.ailk.notify.common.MessageHandler;
import com.ailk.notify.common.NotifyUtility;

/**
 * @className:ConsumerHandler.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-25 
 */
public class ConsumerHandler implements MessageHandler {

	private static final transient Logger log = Logger.getLogger(ConsumerHandler.class);
	
	public ConsumerHandler() {
	}
	
	public void handler(Channel channel, Object msg) {
		ByteBuffer data = (ByteBuffer)msg;
		int pos = data.position();
		int limit = data.limit();
		
		byte[] queueNameBytes = new byte[NotifyUtility.getMaxQueueNameLength()];
		data.get(queueNameBytes);
		String queueName = new String(queueNameBytes);
		byte[] serverNameBytes = new byte[NotifyUtility.getMaxServerNameLength()];
		data.get(serverNameBytes);
		String serverName = new String(serverNameBytes);
		long fileName = data.getLong();
		
		if (fileName == -1) {
			NotifyUtility.releaseByteBuffer(data);
			//Consumer.reduceRetriveCount(queueName);
			return ;
		}
		
		int indexOffset = data.getInt();
		long signal = data.getLong();
		
		int dataLength = data.limit() - data.position();
		byte[] inputBytes = new byte[dataLength];
		data.get(inputBytes);
		IDataInput input = null;
		try {
			if (inputBytes.length > 0) {
				input = NotifyUtility.decodeHessian(inputBytes);
			} 
		} catch (Throwable e) {
			e.printStackTrace();
			log.error("queueName :" + queueName + "; serverName : " + serverName + " ; fileName: " + fileName + 
					"; data length :" + dataLength + "; inputstr :" + new String(inputBytes));
		} finally {
			NotifyUtility.releaseByteBuffer(data);
		}
		
		if (input != null) {
			Consumer.addMsg(queueName, new ConsumerData(queueName, serverName, fileName, indexOffset, input), signal);
		} else {
			log.info("Retrived msg from server , but msg is null! pos : " + pos + "; limit :" + limit);
			//Consumer.reduceRetriveCount(queueName);
			//Consumer.getInstance(queueName).retriveMsg();
		}
	}


}
