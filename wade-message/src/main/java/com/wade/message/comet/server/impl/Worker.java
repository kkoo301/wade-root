package com.wade.message.comet.server.impl;

import static com.wade.message.comet.server.Config.COMET_CHARSET;
import static com.wade.message.comet.server.Config.COMET_SERVER_ACL;
import static com.wade.message.comet.server.Config.COMET_SERVER_HTTP_PUSH;
import static com.wade.message.comet.server.Config.RESULT_CODE_CONTINUE;
import static com.wade.message.comet.server.Config.RESULT_CODE_DATA;
import static com.wade.message.comet.server.Config.RESULT_CODE_ERROR;
import static com.wade.message.comet.server.Config.RESULT_CODE_OK;
import static com.wade.message.comet.server.util.Util.buildContent;
import static com.wade.message.comet.server.util.Util.getParameter;
import static com.wade.message.comet.server.util.Util.writeResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.wade.message.acl.SourceAddressControl;
import com.wade.message.comet.server.IWorker;
import com.wade.message.comet.server.codec.HttpPostRequestDecoder;
import com.wade.message.comet.server.codec.QueryStringDecoder;

public class Worker implements IWorker{
	
	public static final byte[] RUNNING = ("comet server is running").getBytes(COMET_CHARSET);
	public static final byte[] OK = ("OK").getBytes(COMET_CHARSET);
	public static final byte[] ERROR = (buildContent(RESULT_CODE_ERROR, "error")).getBytes(COMET_CHARSET);
	
	private static final boolean USE_ACL = COMET_SERVER_ACL != null && !"".equals(COMET_SERVER_ACL.trim());
	
	@Override
	public void start(String hostname, int port) throws Exception{
		if(USE_ACL){
			SourceAddressControl.initialize(COMET_SERVER_ACL);
		}
		System.out.println("server started " + (hostname != null ? hostname : "0.0.0.0") + ":" + port);
	}

	@Override
	public void active(ChannelHandlerContext ctx) throws Exception{
		Manager.active(ctx.channel());
	}
	
	@Override
	public void inactive(ChannelHandlerContext ctx) throws Exception{
		Manager.inactive(ctx.channel());
	}
	
	@Override
	public void work(ChannelHandlerContext ctx, HttpRequest request) throws Exception{
		 Channel channel = ctx.channel();
		 Connection conn = Manager.get(channel);
		 
		 conn.setIs100ContinueExpected( HttpHeaders.is100ContinueExpected(request) );
		 conn.setIsKeepAlive( HttpHeaders.isKeepAlive(request) );
		 
		 QueryStringDecoder decoder = new QueryStringDecoder(request.getUri()); 
		 String path = decoder.path();
		 Map<String, List<String>> parameters = decoder.parameters();
 
		 if("/message".equals(path)){
			 String callback = getParameter("callback", parameters);
			 String sessionId = getParameter("sessionId", parameters);
			 
			 conn.setCallback(callback);
			 
			 if(sessionId != null && !"".equals(sessionId)){
				 conn.setSessionId(sessionId);
				 Manager.addBySessionId(sessionId, conn);
			 }else{
				 writeResponse(channel, conn, buildContent(RESULT_CODE_ERROR, "sessionId is null"));
			 }
		 }else if("/push".equals(path)){
			 
			 if(!COMET_SERVER_HTTP_PUSH){
				 writeResponse(channel, conn, buildContent(RESULT_CODE_ERROR, "http push is disabled"));
				 return;
			 }
			 
			// 判断acl
			if(USE_ACL){
				String clientIP = request.headers().get("X-Forwarded-For");
				if (clientIP == null) {
					InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
					if(insocket != null)
						clientIP = insocket.getAddress().getHostAddress();
				}
				
				if(!SourceAddressControl.isPhyPermit(clientIP)){
					 writeResponse(channel, conn, buildContent(RESULT_CODE_ERROR, "acl valid faild"));
					return;
				}
			}
			 
			 HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
			 InterfaceHttpData sessionIdData =  postDecoder.getBodyHttpData("sessionId");
			 InterfaceHttpData contentData = postDecoder.getBodyHttpData("content");
			 
			 if(sessionIdData == null){
				 sessionIdData = postDecoder.getBodyHttpData("uid");
			 }
			 
			 Attribute sessionIdAttr = (Attribute) sessionIdData;
			 Attribute contentAttr = (Attribute) contentData;
			 
			 String sessionId = null;
			 if(sessionIdAttr != null){
        		sessionId = sessionIdAttr.getString();
        	 }

			 if(contentAttr != null){
				 JSONObject data = JSONObject.fromObject(contentAttr.getString(COMET_CHARSET));
				 if(sessionId != null && !"".equals(sessionId)){
					 Connection target = Manager.getBySessionId(sessionId);
					 if(target != null){
						 writeResponse(target.getChannel(), target, buildContent(RESULT_CODE_DATA, "ok", data));
						 target.getChannel().flush();
						 writeResponse(channel, conn, buildContent(RESULT_CODE_OK, "success"));
					 }else{
						 writeResponse(channel, conn, buildContent(RESULT_CODE_ERROR, "target is null"));
					 }
				 }else{
					 writeResponse(channel, conn, buildContent(RESULT_CODE_ERROR, "sessionId is null")); 
				 }
			 }	
			 writeResponse(channel, conn, buildContent(RESULT_CODE_ERROR, "data is null"));
		 }else if("/probe".equals(path)){
			 writeResponse(channel, conn, OK);
	     }else{
	    	 writeResponse(channel, conn, RUNNING);
		 }
	}

	@Override
	public void heartbeat(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		Channel channel = ctx.channel();
		if (evt.state() == IdleState.READER_IDLE){
			ctx.close();
		}else if (evt.state() == IdleState.WRITER_IDLE) {
        	if(channel.isWritable()){
            	writeResponse(channel, Manager.get(channel), buildContent(RESULT_CODE_CONTINUE, "continue"));
        		ctx.flush();
            }
        }
	}

}