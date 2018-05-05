package com.ailk.rpc.server.pipeline;

import com.ailk.rpc.codec.java.JavaDecoder;
import com.ailk.rpc.codec.java.JavaEncoder;
import com.ailk.rpc.org.jboss.netty.channel.ChannelPipeline;
import com.ailk.rpc.org.jboss.netty.channel.ChannelPipelineFactory;
import com.ailk.rpc.org.jboss.netty.channel.Channels;
import com.ailk.rpc.server.handler.ServerHandler;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ServerPipelineFactory
 * @description: 
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-4
 */
public class ServerPipelineFactory implements ChannelPipelineFactory {

	public ChannelPipeline getPipeline() throws Exception {
		
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("decoder", new JavaDecoder());
		pipeline.addLast("encoder", new JavaEncoder());
		pipeline.addLast("handler", new ServerHandler());

		return pipeline;
		
	}
}