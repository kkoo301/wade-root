package com.ailk.service.server.socket.handler.codec.hessian;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;

import org.apache.log4j.Logger;

import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffer;
import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.ailk.common.data.IDataInput;
import com.ailk.service.hessian.io.Hessian2Input;
import com.ailk.service.hessian.io.SerializerFactory;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: HessianDecoder
 * @description:服务端  Hessian 解码器
 * 
 * @version: v1.0.0
 * @author: xiedx
 * @date: 2013-7-4
 */
public class HessianDecoder extends FrameDecoder {

	private static Logger log = Logger.getLogger(HessianDecoder.class);
	private static SerializerFactory dataInputFactory = new SerializerFactory(IDataInput.class.getClassLoader());
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {

		if (buffer.readableBytes() < 4) {
			return null;
		}

		/**
		 * 获取报文长度信息
		 */
		
		int dataLen = buffer.getInt(buffer.readerIndex());
		
		if (dataLen <= 0) {
			throw new StreamCorruptedException("无效的报文长度:" + dataLen);
		}

		if (buffer.readableBytes() < dataLen + 4) {
			return null;
		}

		buffer.skipBytes(4);
		byte[] data = new byte[buffer.readableBytes()];
		buffer.readBytes(data);

		Object obj = null;

		ByteArrayInputStream bais = null;
		Hessian2Input in = null;

		try {
			bais = new ByteArrayInputStream(data);
			in = new Hessian2Input(bais);
			in.setSerializerFactory(dataInputFactory);

			obj = in.readObject(IDataInput.class);
			
		} catch (IOException e) {
			log.error("对象解码时发生IOException错误！", e);
		} catch(Exception e){
			log.error("对象解码时发生Exception错误！", e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					log.error("对象解码，关闭  Hessian2Input 时发生错误！", e);
				}
			}
		}

		return obj;

	}

}