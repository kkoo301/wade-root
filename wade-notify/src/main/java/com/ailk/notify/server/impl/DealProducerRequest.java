/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.server.impl;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ailk.notify.common.LogClient;
import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.server.ChannelMap;
import com.ailk.notify.server.DealRequest;
import com.ailk.notify.server.MessageCache;
import com.ailk.notify.server.NotifyServer;
import com.ailk.notify.server.ServerFileProxy;

/**
 * 处理消费者发送的请求，并返回信息
 * 
 * @className:DealProducerRequest.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-17 
 */
public class DealProducerRequest extends DealRequest {
	private static final transient Logger log = Logger.getLogger(DealProducerRequest.class);
	
	public DealProducerRequest(String queueName, String serverName, ServerFileProxy fileProxy) {
		super(queueName, serverName, fileProxy);
		initFileUtility();
	}

	/**
	 * @param data
	 * @return 
	 * @see com.ailk.notify.server.DealRequest#execute(java.nio.ByteBuffer) 
	 */
	@Override
	public void execute(ChannelMap channelMap) {
		
		long startTime = 0;
		if (log.isDebugEnabled()) {
			startTime = System.currentTimeMillis();
			log.debug("get by producer request deal, time :" + startTime);
		}
		if (!NotifyServer.canAcceptData(queueName, serverName)) {
			byte[] returnData = NotifyUtility.SERVER_CANNOT_RECEIVE_DATA.getBytes();
			ByteBuffer returnProducerBuffer = ByteBuffer.allocate(8 + returnData.length);
			returnProducerBuffer.putLong(channelMap.getSequence());
			returnProducerBuffer.put(returnData);
			channelMap.write(returnProducerBuffer);
			NotifyUtility.releaseByteBuffer(returnProducerBuffer);
			return ;
		}
		
		if (!this.fileUtility.isAvailable()) {
			if (log.isDebugEnabled()) {
				log.debug("deal producer request, begin init new file utility!");
			}
			initFileUtility();
		}
		write(channelMap);
		
		if (log.isDebugEnabled()) {
			long writeTime = System.currentTimeMillis();
			log.debug("write by producer request deal, time :" + "; begin execute time:" + startTime + 
				"; use time :" + (writeTime - startTime) + "; file obj :" + this.fileUtility.toString());
		}
	}
	
	/**
	 * 先创建索引偏移量，获取后直接返回给客户端，然后再进行消息的持久化操作 
	 * @param channelMap
	 */
	protected void write(ChannelMap channelMap) {
		ByteBuffer data = null;
		ByteBuffer indexOffset = null;
		ByteBuffer returnProducerBuffer = null;
		boolean isCreated= false;
		try {
			data = channelMap.getData();
			int offset = this.fileUtility.createIndexOffset();
			indexOffset =  NotifyUtility.buildMessageOffset(this.queueName, this.serverName, this.fileName, offset);
			returnProducerBuffer = ByteBuffer.allocate(8 + indexOffset.limit());
			returnProducerBuffer.putLong(channelMap.getSequence());
			returnProducerBuffer.put(indexOffset);
			returnProducerBuffer.flip();
			
			int pos = data.position();
			if (log.isDebugEnabled()) {
				log.debug("begin persist producer; time : " + System.currentTimeMillis());
			}
			isCreated = this.fileUtility.write(offset, data);
			data.rewind();
			data.position(pos);
			if (isCreated) {
				//addCache(indexOffset, data);
				LogClient.sendServerLog(indexOffset);
			}
			
			// 只要服务端接收成功, 都可以发送给消费者, 消费者需要能够处理重复的消息内容
			// 当成功发送客户端并且消息成功持久化后再添加待消费队列，否则消息无效
			/*if (isReturned && isCreated) {
				addCache(indexOffset, data);
				this.fileUtility.updateState(offset, NotifyUtility.MESSSAGE_STATE.RETURN_PRODUCER_SUCESS.getState());
			} else if (!isReturned) {
				indexOffset.rewind();
				indexOffset.position(NotifyUtility.INDEX_OFFSET_PREFIX_LENGTH);
				this.fileUtility.updateState(offset, NotifyUtility.MESSSAGE_STATE.RETURN_PRODUCER_FAILED.getState());
			}*/
			
			boolean isReturned = channelMap.write(returnProducerBuffer);
			if (log.isDebugEnabled()) {
				log.debug("Return data to producer ! queueName :" + queueName + "; serverName :" + serverName + "; fileName :" + fileName + 
						"; offset :" + offset + "; isReturned : " + isReturned + "; time :" + System.currentTimeMillis());
			}
			if (log.isDebugEnabled()) {
				log.debug("persist producer over; time : " + System.currentTimeMillis());
			}
		} finally {
			if (!isCreated) {
				NotifyUtility.releaseByteBuffer(data);
			}
			NotifyUtility.releaseByteBuffer(indexOffset);
			NotifyUtility.releaseByteBuffer(returnProducerBuffer);
		}
	}
	
	public void addCache(ByteBuffer indexOffset, ByteBuffer data) {
		if (log.isDebugEnabled()) {
			log.debug("Add producer data to cache! queueName :" + queueName + "; serverName :" + serverName + "; fileName :" + fileName);
		}
		MessageCache.addCache(queueName, serverName, fileName, indexOffset, -1, data, false, false);
	}
	
}
