package com.ailk.service.server.socket.pipeline;

import com.ailk.rpc.org.jboss.netty.channel.ChannelPipeline;
import com.ailk.rpc.org.jboss.netty.channel.ChannelPipelineFactory;
import com.ailk.rpc.org.jboss.netty.channel.Channels;

import com.ailk.service.server.socket.handler.ServerHandler;

import com.ailk.service.server.socket.handler.codec.hessian.HessianDecoder;
import com.ailk.service.server.socket.handler.codec.hessian.HessianEncoder;
import com.ailk.service.server.socket.handler.codec.java.JavaDecoder;
import com.ailk.service.server.socket.handler.codec.java.JavaEncoder;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ServerPipelineFactory
 * @description: 
 * 
 * @version: v1.0.0
 * @author: xiedx
 * @date: 2013-7-4
 */
public class ServerPipelineFactory implements ChannelPipelineFactory {

	public ChannelPipeline getPipeline() throws Exception {
		return Channels.pipeline(
				new HessianDecoder(),
                new HessianEncoder(),
                new ServerHandler()
        );
	}
}