package com.wade.relax.esb.gateway;

import com.wade.relax.registry.SystemUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Copyright: Copyright (c) 2016 Asiainfo
 *
 * @desc:
 * @auth: steven.zhou
 * @date: 2016-11-24
 */
public class ServiceGatewayChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	public void initChannel(SocketChannel channel) throws Exception {

		ChannelPipeline p = channel.pipeline();
		p.addLast(new HttpResponseEncoder()); // server端发送httpResponse，使用HttpResponseEncoder进行编码 
		p.addLast(new HttpRequestDecoder(4096, 8192, 32768));  // server端接收到httpRequest，使用HttpRequestDecoder进行解码
		p.addLast(new HttpObjectAggregator(SystemUtil.getMaxContentLength())); // 用于支持HttpChunks 1M: 10485760, 20M: 20971520
		p.addLast(new ChunkedWriteHandler()); // 用于支持HttpChunks
		p.addLast(new HttpServerInboundHandler());
	}

}
