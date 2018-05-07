package com.wade.message.websocket.server;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public interface IConnection{
	
	public String getId();
	
	public long getCreationTime();
	
	public Channel getChannel();
	
	public WebSocketServerHandshaker getHandshaker();
	
}