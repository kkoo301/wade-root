package com.ailk.service.client.socket.pipeline;

import com.ailk.rpc.org.jboss.netty.channel.ChannelPipeline;
import com.ailk.rpc.org.jboss.netty.channel.ChannelPipelineFactory;
import com.ailk.rpc.org.jboss.netty.channel.Channels;

import com.ailk.service.client.socket.handler.ClientHandler;

import com.ailk.service.client.socket.handler.codec.hessian.HessianDecoder;
import com.ailk.service.client.socket.handler.codec.hessian.HessianEncoder;
import com.ailk.service.client.socket.handler.codec.java.JavaDecoder;
import com.ailk.service.client.socket.handler.codec.java.JavaEncoder;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ClientPipelineFactory
 * @description: 
 * 
 * @version: v1.0.0
 * @author: xiedx
 * @date: 2013-5-4
 */
public class ClientPipelineFactory implements ChannelPipelineFactory {
	

	public ChannelPipeline getPipeline() throws Exception {
		
		return Channels.pipeline(
                new HessianEncoder(),
                new HessianDecoder(), 
                new ClientHandler()
         );
	}
	
}