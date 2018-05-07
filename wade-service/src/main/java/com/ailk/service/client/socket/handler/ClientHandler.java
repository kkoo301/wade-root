package com.ailk.service.client.socket.handler;

import org.apache.log4j.Logger;

import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.channel.ExceptionEvent;
import com.ailk.rpc.org.jboss.netty.channel.MessageEvent;
import com.ailk.rpc.org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.ailk.common.data.IDataOutput;
import com.ailk.service.client.socket.SocketProxy;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ClientHandler
 * @description:
 * 
 * @version: v1.0.0
 * @author: xiedx
 * @date: 2013-7-4
 */
public class ClientHandler extends SimpleChannelUpstreamHandler {

	private static Logger log = Logger.getLogger(ClientHandler.class);

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		IDataOutput output = (IDataOutput)e.getMessage();
		long seq = output.getHead().getLong("_SOCKET_PROXY_SEQ");
		if(SocketProxy.REQUEST_QUEUE.containsKey(seq)){
			SocketProxy sp = (SocketProxy) SocketProxy.REQUEST_QUEUE.get(seq);
			sp.setOutput(output);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		log.warn("Unexpected exception from downstream.", e.getCause());
		
		e.getChannel().close();
	}

}