package com.wade.log.protocal.tcp.codec;

import java.util.List;

import com.wade.log.codec.JavaDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


/**
 * Netty java 解码
 * @author Shieh
 *
 */
public class JavaMessageDecoder extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		if (buf.readableBytes() < 4) { //长度信息为4,小于4则直接返回
            return;
        }
		
        buf.markReaderIndex(); //需要标记当前的readIndex的位置             
        int dataLength = buf.readInt(); //读取长度信息   
        if (dataLength < 0) { 
            return;
        }
 
        if (buf.readableBytes() < dataLength) { //判断消息长度,如果小于消息产度，则把readIndex重置到mark的地方
            buf.resetReaderIndex();
            return;
        }
 
        byte[] body = new byte[dataLength];  
        buf.readBytes(body); 
        
        Object o = JavaDecoder.decode(body);  
        out.add(o);  
	}
	
}