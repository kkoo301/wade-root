package com.wade.message.websocket.server.handler;

import org.apache.log4j.Logger;

import com.wade.message.websocket.server.IWorker;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class MessageHandler extends SimpleChannelInboundHandler<Object>{
	
	private transient static final Logger log = Logger.getLogger(MessageHandler.class);
			
	private final IWorker worker;
	
	public MessageHandler(IWorker wk){
		worker = wk;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		worker.active(ctx);
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		worker.inactive(ctx);
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception 
	{
		worker.work(ctx, msg);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
		ctx.flush();
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
           IdleStateEvent e = (IdleStateEvent) evt;
           worker.heartbeat(ctx, e);
       }
    }
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		ctx.channel().close();
		
		String s = cause.toString(); 
		if (!s.startsWith("scala.runtime.NonLocalReturnControl") &&
            !s.startsWith("java.nio.channels.ClosedChannelException") &&
            !s.startsWith("java.io.IOException") &&
            !s.startsWith("javax.net.ssl.SSLException") &&
            !s.startsWith("java.lang.IllegalArgumentException")){
        	
        	cause.printStackTrace();     
       }else{
    	   if(log.isDebugEnabled()){
    	   		log.debug("CometServerHandler:" + getClass().getName() + " -> BadClientSilencer", cause);   
    	   }
       }
	}
}