package com.wade.message.websocket.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

public interface IWorker{
	
	public void start(String hostname, int port) throws Exception;
	
	public void active(ChannelHandlerContext ctx) throws Exception ;
	
	public void inactive(ChannelHandlerContext ctx) throws Exception ;
	
	public void work(ChannelHandlerContext ctx, Object msg) throws Exception;
	
	public void heartbeat(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception ;
	
}
