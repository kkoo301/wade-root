package com.wade.message.comet.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.timeout.IdleStateEvent;

public interface IWorker{
	
	public void start(String hostname, int port) throws Exception;
	
	public void active(ChannelHandlerContext ctx) throws Exception;
	
	public void inactive(ChannelHandlerContext ctx) throws Exception ;
	
	public void work(ChannelHandlerContext ctx, HttpRequest request) throws Exception;
	
	public void heartbeat(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception ;
	
}
