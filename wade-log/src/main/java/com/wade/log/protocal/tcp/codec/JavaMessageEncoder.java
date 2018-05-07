package com.wade.log.protocal.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.wade.log.codec.JavaEncoder;
import com.wade.log.impl.LogData;

/**
 * Netty java 编码
 * @author Shieh
 *
 */
public class JavaMessageEncoder extends MessageToByteEncoder<LogData>{

	@Override
	protected void encode(ChannelHandlerContext ctx, LogData data, ByteBuf buf) throws Exception {
		byte[] body = JavaEncoder.encode(data);
		int dataLength = body.length;
		buf.writeInt(dataLength);
		buf.writeBytes(body);
	}
	
}