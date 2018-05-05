package com.ailk.rpc.client.pipeline;

import com.ailk.rpc.client.handler.ClientHandler;
import com.ailk.rpc.codec.java.JavaDecoder;
import com.ailk.rpc.codec.java.JavaEncoder;
import com.ailk.rpc.org.jboss.netty.channel.ChannelPipeline;
import com.ailk.rpc.org.jboss.netty.channel.ChannelPipelineFactory;
import com.ailk.rpc.org.jboss.netty.channel.Channels;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ClientPipelineFactory
 * @description: 
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-4
 */
public class ClientPipelineFactory implements ChannelPipelineFactory {

	private JavaDecoder decoder; // 解码器
	private JavaEncoder encoder; // 编码器
	private ClientHandler handler; // 业务逻辑处理器
	
	public ChannelPipeline getPipeline() throws Exception {
		
		ChannelPipeline pipeline = Channels.pipeline();

		this.decoder = new JavaDecoder();
		this.encoder = new JavaEncoder();
		this.handler = new ClientHandler();
		
		pipeline.addLast("decoder", this.decoder);
		pipeline.addLast("encoder", this.encoder);
		pipeline.addLast("handler", this.handler);

		return pipeline;
		
	}
	
}