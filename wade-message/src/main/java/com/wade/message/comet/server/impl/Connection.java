package com.wade.message.comet.server.impl;

import io.netty.channel.Channel;

import java.util.UUID;

import com.wade.message.comet.server.IConnection;

public class Connection implements IConnection{
	
	private String id;
	private long creationTime;
	private Channel channel;
	private boolean b100ContinueExpected;
	private boolean bKeepAlive;
	private String callback;
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
	
	public boolean is100ContinueExpected(){
		return b100ContinueExpected;
	}
	
	public void setIs100ContinueExpected(boolean val){
		b100ContinueExpected = val;
	}
	
	public boolean isKeepAlive(){
		return bKeepAlive;
	}
	
	public void setIsKeepAlive(boolean val){
		bKeepAlive = val;
	}
	
	public String getCallback(){
		return callback;
	}
	
	public void setCallback(String val){
		callback = val;
	}
	
	public String getSessionId(){
		return sessionId;
	}

	public void setSessionId(String val){
		sessionId = val;
	}
}