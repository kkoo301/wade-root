package com.wade.message.websocket.server.impl;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.util.UUID;

import com.wade.message.websocket.server.IConnection;

public class Connection implements IConnection{
	
	private String id;
	private long creationTime;
	private Channel channel;
	private WebSocketServerHandshaker handshaker;
	private String sessionId;
	
	public Connection(Channel ch){
		id = UUID.randomUUID().toString();
		creationTime = System.currentTimeMillis();
		channel = ch;
	}
	
	public String getId(){
		return id;
	}
	
	public long getCreationTime() {
		return creationTime;
	}
	
	public Channel getChannel(){
		return channel;
	}
	
	public WebSocketServerHandshaker getHandshaker(){
		return handshaker;
	}

	public void setHandshaker(WebSocketServerHandshaker val){
		handshaker = val;
	}
	
	public String getSessionId(){
		return sessionId;
	}

	public void setSessionId(String val){
		sessionId = val;
	}
}