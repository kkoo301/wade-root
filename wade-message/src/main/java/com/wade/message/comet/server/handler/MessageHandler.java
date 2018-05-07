package com.wade.message.comet.server.handler;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.timeout.IdleStateEvent;

import com.wade.message.comet.server.IWorker;

public class MessageHandler extends ChannelInboundHandlerAdapter{
	
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
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
	{
		 if ((msg instanceof HttpRequest)) {
			 HttpRequest request = (HttpRequest)msg;
			 worker.work(ctx, request);
		 }
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