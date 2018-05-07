package com.wade.message.websocket.server.impl;

import static com.wade.message.websocket.server.Config.RESULT_CODE_CONTINUE;
import static com.wade.message.websocket.server.Config.RESULT_CODE_DATA;
import static com.wade.message.websocket.server.Config.RESULT_CODE_ERROR;
import static com.wade.message.websocket.server.Config.RESULT_CODE_OK;
import static com.wade.message.websocket.server.Config.WEBSOCKET_CHARSET;
import static com.wade.message.websocket.server.Config.WEBSOCKET_SERVER_ACL;
import static com.wade.message.websocket.server.Config.WEBSOCKET_SERVER_HTTP_PUSH;
import static com.wade.message.websocket.server.util.Util.buildContent;
import static com.wade.message.websocket.server.util.Util.getParameter;
import static com.wade.message.websocket.server.util.Util.outputContent;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.wade.message.acl.SourceAddressControl;
import com.wade.message.websocket.server.IConnection;
import com.wade.message.websocket.server.IWorker;
import com.wade.message.websocket.server.util.Util;
import com.wade.message.websocket.server.codec.HttpPostRequestDecoder;
import com.wade.message.websocket.server.codec.QueryStringDecoder;

public class Worker implements IWorker{
	
	public static final String RUNNING = "websocket server is running";
	private static final String WEBSOCKET_PATH = "/message";
	
	private static final boolean USE_ACL = WEBSOCKET_SERVER_ACL != null && !"".equals(WEBSOCKET_SERVER_ACL.trim());
	
	@Override
	public void start(String hostname, int port) throws Exception{
		if(USE_ACL){
			SourceAddressControl.initialize(WEBSOCKET_SERVER_ACL);
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
	public void work(ChannelHandlerContext ctx, Object msg) throws Exception{
		 Channel channel = ctx.channel();
		 Connection conn = Manager.get(channel);

		 if ((msg instanceof FullHttpRequest)) {
			 FullHttpRequest request = (FullHttpRequest)msg;
			 handleHttpRequest(ctx, request, conn);
		 }else if (msg instanceof WebSocketFrame) {
			 WebSocketFrame frame = (WebSocketFrame)msg;
			 handleWebSocketFrame(ctx, frame, conn);
		 }
	}

	@Override
	public void heartbeat(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		Channel channel = ctx.channel();
		if (evt.state() == IdleState.READER_IDLE){
			ctx.close();
		}else if (evt.state() == IdleState.WRITER_IDLE) {
        	if(channel.isWritable()){
        		ctx.writeAndFlush(new TextWebSocketFrame(Util.buildContent(RESULT_CODE_CONTINUE, "continue")));
            }
        }
	}
	
	protected void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request, IConnection conn) throws Exception {
		Channel channel = ctx.channel();
		Connection connection = (Connection)conn;

		if (!request.getDecoderResult().isSuccess()) {
			sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
			return;
		}

		if ("/push".equals(request.getUri())) {
			
			// 处理http push
			if(!WEBSOCKET_SERVER_HTTP_PUSH || !POST.equals(request.getMethod())){
				sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
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
					sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
					return;
				}
			}
			
			String responseContent = "";
			
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
				 JSONObject data = JSONObject.fromObject(contentAttr.getString(WEBSOCKET_CHARSET));
				 if(sessionId != null && !"".equals(sessionId)){
					 Connection target = Manager.getBySessionId(sessionId);
					 if(target != null){
						 outputContent(target.getChannel(), target, buildContent(RESULT_CODE_DATA, "ok", data));
						 target.getChannel().flush();
						 
						 responseContent = buildContent(RESULT_CODE_OK, "success");
					 }else{
						 
						 responseContent =  buildContent(RESULT_CODE_ERROR, "target is null");
					 }
				 }
			 }else{
				 responseContent = buildContent(RESULT_CODE_ERROR, "data is null");
			 }
			
			ByteBuf content = Unpooled.copiedBuffer(responseContent, WEBSOCKET_CHARSET);
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,OK, content);

			response.headers().set(CONTENT_TYPE, "text/html; charset=" + WEBSOCKET_CHARSET);
			HttpHeaders.setContentLength(response, content.readableBytes());

			sendHttpResponse(ctx, request, response);

			return;
		}

		if (request.getMethod() != GET) {
			sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
			return;
		}

		if ("/".equals(request.getUri())) {
			//sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
			ByteBuf content = Unpooled.copiedBuffer(RUNNING, WEBSOCKET_CHARSET);
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,OK, content);

			response.headers().set(CONTENT_TYPE, "text/html; charset=" + WEBSOCKET_CHARSET);
			HttpHeaders.setContentLength(response, content.readableBytes());

			sendHttpResponse(ctx, request, response);
			return;
		}

		if ("/favicon.ico".equals(request.getUri())) {
			sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
			return;
		}

		if ("/probe".equals(request.getUri())) {
			ByteBuf content = Unpooled.copiedBuffer("OK", WEBSOCKET_CHARSET);
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,OK, content);

			response.headers().set(CONTENT_TYPE, "text/html; charset=" + WEBSOCKET_CHARSET);
			HttpHeaders.setContentLength(response, content.readableBytes());

			sendHttpResponse(ctx, request, response);
			return;
		}

		// Handshake
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(request), null, true);
		WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), request);
			connection.setHandshaker(handshaker);
		}
	}

	protected void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame, IConnection conn) throws Exception {
		
		Connection connection = (Connection)conn;
		
		if (frame instanceof CloseWebSocketFrame) {
			WebSocketServerHandshaker handshaker = conn.getHandshaker();
			if (handshaker != null)
				handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}

		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}

		// 只处理TextFrame
		if (frame instanceof TextWebSocketFrame) {
			
			String paramString = ((TextWebSocketFrame) frame).text();
			
			if(paramString == null || "".equals(paramString.trim()))
				return;
			
			//解析参数
			QueryStringDecoder decoder = new QueryStringDecoder(paramString); 
			Map<String, List<String>> parameters = decoder.parameters();

			String sessionId = getParameter("sessionId", parameters);
			if(sessionId != null && !"".equals(sessionId)){
				connection.setSessionId(sessionId);
				Manager.addBySessionId(sessionId, connection);
			}
			
		}
	}

	public void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) throws Exception {
		if (response.getStatus().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(response.getStatus().toString(), WEBSOCKET_CHARSET);
			response.content().writeBytes(buf);
			buf.release();
			HttpHeaders.setContentLength(response, response.content().readableBytes());
		}

		ChannelFuture f = ctx.channel().writeAndFlush(response);
		if (!HttpHeaders.isKeepAlive(response) || response.getStatus().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public String getWebSocketLocation(FullHttpRequest request) {
		String location = request.headers().get(HOST) + WEBSOCKET_PATH;
		return "ws://" + location;
	}
}