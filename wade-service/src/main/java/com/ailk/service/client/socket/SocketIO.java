package com.ailk.service.client.socket;

import com.ailk.rpc.org.jboss.netty.channel.Channel;

public class SocketIO {
	
	private Channel channel;
	private String hostname;
	private int port;
	
	public Channel getChannel() {
		return channel;
	}
	
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
}
