package com.ailk.rpc.server.handler;

import java.lang.reflect.Method;
import java.net.SocketAddress;

import org.apache.log4j.Logger;

import com.ailk.rpc.codec.Transporter;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.channel.ChannelStateEvent;
import com.ailk.rpc.org.jboss.netty.channel.ExceptionEvent;
import com.ailk.rpc.org.jboss.netty.channel.MessageEvent;
import com.ailk.rpc.org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ServerHandler
 * @description: 
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-4
 */
public class ServerHandler extends SimpleChannelUpstreamHandler {

	private static final Logger log = Logger.getLogger(ServerHandler.class);	
	
	@Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelOpen(ctx, e);
        if (log.isInfoEnabled()) {
        	SocketAddress address = ctx.getChannel().getRemoteAddress();
        	log.info("客户端" + address + "连接上服务器");
        }
    }
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		
		Transporter transporter = (Transporter) e.getMessage();
		
		String clazzName = transporter.getClazzName();
		String methodName = transporter.getMethodName();
		Object[] params = transporter.getParams();
		Class<?> typeClass[] = transporter.getParamTypes();
		
		long seq = transporter.getSeq();
		
		try {
		
			Class<?> clazz = Class.forName(clazzName);
			Object instance = clazz.newInstance();
						
			Method method = clazz.getMethod(methodName, typeClass);
			Object response = method.invoke(instance, params);
		
			Transporter rtn = new Transporter();
			rtn.setSeq(seq); // 响应携带回请求流水号
			rtn.setResponse(response);
			
			e.getChannel().write(rtn);
			
		} catch (Exception ex) {
			log.error("调用" + clazzName + "." + methodName + "时发生错误！", ex);
			log.error("-------------------------------------------");
			log.error("调用类:" + clazzName);
			log.error("调用方法:" + methodName);
			log.error("方法参数:");
			for (Object o : params) {
				log.error(o.toString());
			}
			log.error("-------------------------------------------");
		}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		log.warn("Unexpected exception from downstream." + e.getCause());
		e.getChannel().close();
	}
	
}