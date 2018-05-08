/**  
*
* Copyright: Copyright (c) 2015 Asiainfo-Linkage
*
*/
package com.ailk.notify.common;

import io.netty.channel.Channel;

/**
 * @className:ChannelData.java
 *
 * @version V1.0  
 * @author lvchao
 * @date 2015-3-25 
 */
public class ChannelData {
	
	private String queueName;
	private String serverName;
	private Channel channel;
	private boolean isMaster;
	private long version;
	
	public ChannelData(String queueName, String serverName, Channel channel, boolean isMaster, long version) {
		this.queueName = queueName;
		this.serverName = serverName;
		this.channel = channel;
		this.isMaster = isMaster;
		this.version = version;
	}

	public boolean isMaster() {
		return isMaster;
	}
	
	public String getQueueName() {
		return this.queueName;
	}
	
	public String getServerName() {
		return this.serverName;
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public long getVersion() {
		return this.version;
	}
}
