/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 用于将ByteBuf转换为ByteBuffer
 * 
 * @className:MessageDecoder.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-26 
 */
public class ByteBufferMessageDecoder extends ByteToMessageDecoder {
	private static final transient Logger log = Logger.getLogger(ByteBufferMessageDecoder.class);

	/**
	 *  
	 * @param ctx
	 * @param in
	 * @param out
	 * @throws Exception 
	 * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List) 
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readableBytes = in.readableBytes();
		if (log.isDebugEnabled()) {
			log.debug("Readabled bytes length : " + readableBytes + "; writerIndex : " + in.writerIndex());
		}
		if (readableBytes > 4) {
			in.markReaderIndex();
			int dataLength = in.readInt();
			if (log.isDebugEnabled()) {
				log.debug("Data length : " + dataLength + "; can read data length :" + (readableBytes - 4));
			}
			if (readableBytes - 4 - dataLength >= 0) {
				ByteBuffer data = ByteBuffer.allocate(dataLength);
				/*byte[] dataBytes = new byte[dataLength];
				in.readBytes(dataBytes);
				data.put(dataBytes);*/
				in.readBytes(data);
				data.flip();
				if (log.isDebugEnabled()) {
					log.debug("read data size :" + (data.limit()) + "; data position :" + data.position() + "; decoder time :" + System.currentTimeMillis());
				}
				
				out.add(data);
			} else {
				in.resetReaderIndex();
			}
		}
	}

}
