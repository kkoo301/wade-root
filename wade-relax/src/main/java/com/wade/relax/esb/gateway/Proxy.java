package com.wade.relax.esb.gateway;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright: Copyright (c) 2016 Asiainfo
 *
 * @desc: 代理访问后端APP服务
 * @auth: steven.zhou
 * @date: 2016-11-24
 */
public class Proxy {
	
	private static final Logger LOG = LoggerFactory.getLogger(Proxy.class);
	
	/**
	 * 代理访问后端服务
	 * 
	 * @param ctx
	 * @param httpHeaders
	 * @param body
	 * @param url
	 * @param serviceName
	 * @throws Exception
	 */
	public static final void proxy(ChannelHandlerContext ctx, HttpHeaders httpHeaders, byte[] body, String url, String serviceName, String xff) throws Exception {

		long start = System.currentTimeMillis();
		PostMethod post = null;
		
		try {
			
			post = new PostMethod(url + "/service/" + serviceName);
			
			Iterator<Entry<String, String>> iter = httpHeaders.iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				post.setRequestHeader(entry.getKey(), entry.getValue());
			}

			post.setRequestHeader(CONNECTION, Values.CLOSE);
			
			RequestEntity entity = new ByteArrayRequestEntity(body);
			post.setRequestEntity(entity);
			
			HttpClient httpclient = new HttpClient();
			httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
			httpclient.getHttpConnectionManager().getParams().setSoTimeout(600000);
			httpclient.executeMethod(post);
			
			Header[] responseHeaders = post.getResponseHeaders();
			byte[] responseBody = post.getResponseBody();
			
	        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(responseBody));
	        HttpHeaders headers = response.headers();
	        for (Header header : responseHeaders) {
	        	String name = header.getName();
	        	String value = header.getValue();
	        	headers.set(name, value);
	        }
	        
	        response.headers().set(CONTENT_LENGTH, responseBody.length);
	        response.headers().set(CONNECTION, Values.CLOSE);
	        
	        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	        
		} finally {			
			if (null != post) {
				post.releaseConnection();
			}
		}

        long cost = System.currentTimeMillis() - start;
		String info = String.format("%-14s -> %-50s -> %-27s %4d ms", xff, serviceName, url, cost);
        LOG.info(info);

    }
}
