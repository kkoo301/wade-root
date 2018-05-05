package com.ailk.rpc.client.handler;

import org.apache.log4j.Logger;

import com.ailk.rpc.client.SockProxy;
import com.ailk.rpc.codec.Transporter;
import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.channel.ExceptionEvent;
import com.ailk.rpc.org.jboss.netty.channel.MessageEvent;
import com.ailk.rpc.org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ClientHandler
 * @description:
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-4
 */
public class ClientHandler extends SimpleChannelUpstreamHandler {

	private static final Logger log = Logger.getLogger(ClientHandler.class);
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		
		Transporter transporter = (Transporter) e.getMessage();
	
		long seq = transporter.getSeq();
		if (SockProxy.REQUEST_QUEUE.containsKey(seq)) {
			SockProxy sp = (SockProxy) SockProxy.REQUEST_QUEUE.get(seq);
			sp.setResponse(transporter);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		log.warn("Unexpected exception from downstream.", e.getCause());
		e.getChannel().close();
	}

}