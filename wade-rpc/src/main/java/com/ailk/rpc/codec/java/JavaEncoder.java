package com.ailk.rpc.codec.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffer;
import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffers;
import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: JavaEncoder
 * @description: Java对象编码器
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-4
 */
public class JavaEncoder extends OneToOneEncoder {
	
	private static Logger log = Logger.getLogger(JavaEncoder.class);
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
	
		byte[] data = null;

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;

		try {
			
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(msg);
			data = baos.toByteArray();
				
		} catch (IOException e) {
			log.error("对象编码时发生IOException错误！", e);
		} finally {
			  if (null != oos) { 
				  try { 
					  oos.close(); 
				  } catch (IOException e) {
					  log.error("对象编码，关闭ObjectOutputStream时发生错误！", e);
				  } 
			  }
		}

		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeInt(data.length); // 写入报文长度信息
		buf.writeBytes(data); // 写入报文内容
		
		return buf;
		
	}
	
}