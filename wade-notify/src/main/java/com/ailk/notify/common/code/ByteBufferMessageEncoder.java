/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ailk.notify.common.NotifyUtility;

/**
 * 将ByteBuffer类型转换为ByteBuf
 * 
 * @className:MessageEncoder.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-26 
 */
public class ByteBufferMessageEncoder extends MessageToByteEncoder<ByteBuffer> {
	//private static final transient Logger log = Logger.getLogger(ByteBufferMessageEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuffer msg, ByteBuf out)
			throws Exception {
		out.writeInt(msg.limit() - msg.position());
		out.writeBytes(msg);
		/*if (log.isInfoEnabled()) {
			msg.rewind();
			byte[] msgBytes = new byte[msg.limit()];
			msg.get(msgBytes);		
			log.info("limit :" + msg.limit() + "; str :" + new String(msgBytes));
		}*/
		NotifyUtility.releaseByteBuffer(msg);
	}

}
