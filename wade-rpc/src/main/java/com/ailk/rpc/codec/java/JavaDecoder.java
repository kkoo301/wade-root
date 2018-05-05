package com.ailk.rpc.codec.java;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import org.apache.log4j.Logger;

import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffer;
import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: JavaDecoder
 * @description: Java对象解码器
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-4
 */
public class JavaDecoder extends FrameDecoder {

	private static Logger log = Logger.getLogger(JavaDecoder.class);
	
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
		byte[] data = new byte[dataLen];
		buffer.readBytes(data);

		Object obj = null;

		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;

		try {
			bais = new ByteArrayInputStream(data);
			ois = new ObjectInputStream(bais);
			obj = ois.readObject();
		} catch (IOException e) {
			log.error("对象解码时发生IOException错误！", e);
		} catch (ClassNotFoundException e) {
			log.error("对象解码时发生ClassNotFoundException错误！", e);
		} finally {
			if (null != ois) {
				try {
					ois.close();
				} catch (IOException e) {
					log.error("对象解码，关闭ObjectInputStream时发生错误！", e);
				}
			}
		}

		return obj;

	}

}