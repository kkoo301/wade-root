package com.ailk.rpc.client.io;

import com.ailk.rpc.org.jboss.netty.channel.Channel;

public class SockIO {
	
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
