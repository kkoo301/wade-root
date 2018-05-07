package com.wade.message.comet.server;

import io.netty.channel.Channel;

public interface IConnection{
	
	public String getId();
	
	public long getCreationTime();
	
	public Channel getChannel();
	
	public boolean is100ContinueExpected();
	
	public boolean isKeepAlive();
	
	public String getCallback();
}