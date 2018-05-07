package com.wade.message.comet.server;

import static com.wade.message.comet.server.Config.COMET_CONNECTION_HEARTBEAT;
import static com.wade.message.comet.server.Config.COMET_CONNECTION_TIMEOUT;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.wade.message.comet.server.handler.MessageHandler;

public class Initializer extends ChannelInitializer<SocketChannel>{
	private final IWorker worker;
	
	public Initializer(IWorker wk){
		super();
		worker = wk;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		
		//p.addLast(new HttpServerCodec());
		p.addLast(new HttpRequestDecoder());
		p.addLast(new HttpObjectAggregator(10485760));
        p.addLast(new HttpResponseEncoder());
		p.addLast(new ChunkedWriteHandler());
		
		p.addLast(new IdleStateHandler(COMET_CONNECTION_TIMEOUT, COMET_CONNECTION_HEARTBEAT, -1, TimeUnit.MILLISECONDS));
		p.addLast(new MessageHandler(worker));
	}
}