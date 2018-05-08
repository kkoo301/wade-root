/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.notify.common.NotifyUtility;
import com.ailk.notify.common.SocketPool;

/**
 * 将ByteBuf类型转换为IDataInput
 * 
 * @className:DataInputMessageDecoder.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-26 
 */
public class DataInputMessageDecoder extends ByteToMessageDecoder {

	private static final transient Logger log = Logger.getLogger(DataInputMessageDecoder.class);
	
	/**
	 * @param ctx
	 * @param in
	 * @param out
	 * @throws Exception 
	 * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List) 
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		int readIndex = in.readerIndex();
		if (readIndex > 4) {
			in.markReaderIndex();
			int dataLength = in.readInt();
			if (readIndex - 4 - dataLength >= 0) {
				byte[] dataBytes = new byte[dataLength];
				byte[] queueNameBytes = new byte[NotifyUtility.getMaxQueueNameLength()];
				in.readBytes(queueNameBytes);
				in.readBytes(dataBytes);
				String queueName = new String(queueNameBytes);
				if (dataLength == NotifyUtility.SERVER_CANNOT_RECEIVE_DATA_LENGTH 
						&& NotifyUtility.SERVER_CANNOT_RECEIVE_DATA.equals(new String(dataBytes))) {
					if (log.isInfoEnabled()) {
						log.info("Can not get message because " + NotifyUtility.SERVER_CANNOT_RECEIVE_DATA);
					}
					
					SocketPool.connectMaster(queueName);
					out.add(null);
				} else {
					out.add(NotifyUtility.decodeHessian(dataBytes));
				}
				return;
			} else {
				in.resetReaderIndex();
			}
		}
		out.add(null);
	}

}
