package com.ailk.service.server.socket.handler.codec.hessian;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffer;
import com.ailk.rpc.org.jboss.netty.buffer.ChannelBuffers;
import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.handler.codec.oneone.OneToOneEncoder;


import com.ailk.common.data.IDataOutput;
import com.ailk.service.hessian.io.Hessian2Output;
import com.ailk.service.hessian.io.SerializerFactory;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: HessianEncoder
 * @description: 服务端  Hessian 编码器
 * 
 * @version: v1.0.0
 * @author: xiedx
 * @date: 2013-7-4
 */
public class HessianEncoder extends OneToOneEncoder {
	
	private static Logger log = Logger.getLogger(HessianEncoder.class);
	private static SerializerFactory dataOutputFactory = new SerializerFactory(IDataOutput.class.getClassLoader());
	
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
	
		byte[] data = null;

		ByteArrayOutputStream baos = null;
		Hessian2Output out = null;

		try {
			baos = new ByteArrayOutputStream(1024);
			out = new Hessian2Output(baos);
	        out.setSerializerFactory(dataOutputFactory);
	        out.writeObject(msg);
			out.flush();
			data = baos.toByteArray();
		} catch (IOException e) {
			log.error("对象编码时发生IOException错误！", e);
		} catch(Exception e){
			log.error("对象编码时发生Exception错误！", e);
		}  finally {
			  if (null != out) { 
				  try { 
					  out.close();
				  } catch (IOException e) {
					  log.error("对象编码，关闭  AbstractHessianOutput 时发生错误！", e);
				  } 
			  }
		}

		ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
		buf.writeInt(data.length); // 写入报文长度信息
		buf.writeBytes(data); // 写入报文内容
		
		return buf;
		
	}
	
}