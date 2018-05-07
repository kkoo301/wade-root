package com.wade.message.websocket.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.wade.message.websocket.server.handler.MessageHandler;
import static com.wade.message.websocket.server.Config.*;

public class Initializer extends ChannelInitializer<SocketChannel>{
	private final IWorker worker;
	
	public Initializer(IWorker wk){
		super();
		worker = wk;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		
		p.addLast(new HttpRequestDecoder());
		p.addLast(new HttpObjectAggregator(10485760));
        p.addLast(new HttpResponseEncoder());
        p.addLast(new ChunkedWriteHandler());
        
		p.addLast(new IdleStateHandler(-1, WEBSOCKET_CONNECTION_HEARTBEAT, -1, TimeUnit.MILLISECONDS));
		p.addLast(new MessageHandler(worker));
	}
}