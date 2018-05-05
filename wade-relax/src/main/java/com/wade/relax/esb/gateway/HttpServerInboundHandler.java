package com.wade.relax.esb.gateway;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map.Entry;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.relax.registry.consumer.ConsumerRuntime;
import com.wade.relax.registry.consumer.SockSite;
import com.wade.relax.esb.gateway.acl.SourceAddressControl;
import com.wade.relax.exception.NotFoundCenterException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.HttpHeaders.Values;

/**
 * Copyright: Copyright (c) 2016 Asiainfo
 *
 * @desc:
 * @auth: steven.zhou
 * @date: 2016-11-24
 */
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
	
	private static final Logger LOG = LoggerFactory.getLogger(HttpServerInboundHandler.class);

	/**
	 * URI信息
	 */
	private String uri;
	
	/**
	 * 服务名
	 */
	private String serviceName;
	
	/**
	 * 客户端IP,通常意义上是通过X-Forwarded-For透传过来的IP
	 */
	private String xff;
	
	/**
	 * 直接访问过来的客户端IP
	 */
	private String remoteClientIp;
	
	/**
	 * HTTP头部信息
	 */
	private HttpHeaders httpHeaders;
			
	/**
	 * 存放请求BODY数据
	 */	
	private ByteArrayOutputStream bodyOS = new ByteArrayOutputStream(1024);

	/**
	 * 连接刚建立时调用
	 */
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {

		/**
		 * 验证来源IP地址的合法性
		 */
		if (SourceAddressControl.isPhyCheckEnable()) {
			Channel channel = ctx.channel();
			SocketAddress socketAddress = channel.remoteAddress();
			String address = socketAddress.toString();
			this.remoteClientIp = address.substring(1, address.lastIndexOf(":"));
			if (!SourceAddressControl.isPhyPermit(this.remoteClientIp)) {
		        LOG.warn("deny illegal access, physical-address:" + this.remoteClientIp, new Object[0]);
		        ctx.close();
		        return;
			}
		}

	}
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        
    	if (msg instanceof HttpRequest) {
    		handleHttpRequest(ctx, (HttpRequest) msg);
        }
    	
        if (msg instanceof HttpContent) {
        	handleHttpContent(ctx, (HttpContent) msg);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	LOG.error("转发调用发生异常! ", cause);
        ctx.close();
    }
	
    /**
     * 处理请求头
     * 
     * @param ctx
     * @param request
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest request) {

    	this.uri = request.getUri();
    	this.serviceName = extractServiceName(uri); // 根据URL识别出服务名
    	this.httpHeaders = request.headers();
    	this.xff = this.httpHeaders.get("X-Forwarded-For"); // 取ESB来源IP地址
    	if (null == this.xff) {
    		this.xff = this.remoteClientIp;
    	}

		request.headers().set("X-Forwarded-For", this.xff);

    	if (LOG.isDebugEnabled()) {
    		
    		LOG.debug("------------- HTTP HEAD: -------------");
    		LOG.debug(request.getMethod().name() + " " + this.uri + " " + request.getProtocolVersion().text());
    		for (Iterator<Entry<String, String>> iter = this.httpHeaders.iterator(); iter.hasNext();) {
    			Entry<String, String> entry = iter.next();
    			LOG.debug(entry.getKey() + ": " + entry.getValue());
			}
            
            LOG.debug("--------------------------------------");
            
    	}

    }
    
    /**
     * 处理请求体
     * 
     * @param ctx
     * @param content
     * @throws Exception
     */
    private void handleHttpContent(ChannelHandlerContext ctx, HttpContent content) throws Exception {

    	if (DoorKeeper.isProbeAccess(this.uri, ctx)) { // 是否探针请求
    		return;
    	}
    	
    	EsbMonitor.incrementSrcCount(this.xff);
    	
		ByteBuf byteBuf = null;
		
		try {
			
			byteBuf = content.content();
			byte[] bytes = new byte[byteBuf.readableBytes()];
			byteBuf.readBytes(bytes);
			bodyOS.write(bytes);
			
		} finally {
			if (null != byteBuf) {
				byteBuf.release();
			}
		}
		
        if (content instanceof LastHttpContent) {
        	
        	SockSite sockSite = null;
        	
        	try {

        		sockSite = ConsumerRuntime.nextAvailableAddress(serviceName);

        	} catch (NotFoundCenterException e) {
        		String responseContent = "could not found center name by serviceName: " + serviceName + ", requestURI=" + this.uri;
        		LOG.error(responseContent);
        		
        		byte[] responseBody = responseContent.getBytes();
        		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST, Unpooled.wrappedBuffer(responseBody));
    	        response.headers().set(CONTENT_LENGTH, responseBody.length);
    	        response.headers().set(CONNECTION, Values.CLOSE);
    	        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    	        return;
        	}
        	
    		String url = sockSite.getUrl();
    		byte[] body = bodyOS.toByteArray();
    		Proxy.proxy(ctx, httpHeaders, body, url, this.serviceName, this.xff);
    		bodyOS.close();
    		
    		EsbMonitor.incrementDstCount(sockSite);
    		
        } else {
        	LOG.debug("请求BODY数据未完待续...  " + this.serviceName);
        }
		
    }
    
	/**
	 * 从requestURI里获取服务名，服务格式为: /service/{SERVICE_NAME} 
	 * 
	 * @param requestURI
	 * @return
	 * @throws IOException 
	 */
	private String extractServiceName(String requestURI) {
		
		if (requestURI.startsWith("/service/")) {
			return requestURI.substring(9);	
		}
		
		return "NULL";
		
	}

}
