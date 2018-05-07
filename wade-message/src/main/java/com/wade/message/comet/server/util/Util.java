package com.wade.message.comet.server.util;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import static com.wade.message.comet.server.Config.*;

import com.wade.message.comet.server.Config;
import com.wade.message.comet.server.IConnection;

public class Util{
	
	/**
	 * 获取参数
	 * @param key
	 * @param parameters
	 * @return
	 */
	public static String getParameter(String key, Map<String,List<String>> parameters) throws Exception{
		if(key != null && !"".equals(key) && parameters != null){
			List<String> values = parameters.get(key);
			if(values != null && values.size() > 0){
				return values.get(0);
			}
		}
		return null;
	}
	
	/**
	 * 输出Response消息
	 * @param ctx
	 * @param content
	 */
	public static void writeResponse(Channel channel, IConnection conn, String content) throws Exception{
		if (content == null){
			throw new IllegalArgumentException("response content is null");
		}
		
		if(conn == null){
			throw new IllegalArgumentException("connection is null");
		}
		
		String callback = conn.getCallback();
		
		if(callback != null && !"".equals(callback)){
			content = callback + "(" + content + ")";
		}
		
		writeResponse(channel, conn, content.getBytes(COMET_CHARSET));
	}
	
	/**
	 * 输出Response消息
	 * @param ctx
	 * @param content
	 */
	public static void writeResponse(Channel channel, IConnection conn, byte[] content)  throws Exception{
		if (content == null){
			throw new IllegalArgumentException("response content is null");
		}
		
		if(conn == null){
			throw new IllegalArgumentException("connection is null");
		}
		
		if (conn.is100ContinueExpected()) {
			channel.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
		}

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(content));
		response.headers().set("Content-Type", "text/plain");
		response.headers().set("Content-Length", Integer.valueOf(response.content().readableBytes()));

		String allowOrign = Config.COMET_SERVER_ALLOW_ORIGIN;
		if( null != allowOrign && !"".equals(allowOrign)){
			response.headers().set("Access-Control-Allow-Origin", allowOrign);
		}
		if (!conn.isKeepAlive()) {
			channel.write(response).addListener(ChannelFutureListener.CLOSE);
		} else {
			response.headers().set("Connection", "keep-alive");
			channel.write(response);
		}
	}
	
	/**
	 * 
	 * @param code
	 * @param info
	 * @return
	 */
	public static String buildContent(String info){
		return buildContent(RESULT_CODE_CONTINUE, info, null);
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static String buildContent(Map<String, Object> data){
		return buildContent(RESULT_CODE_DATA, "", data);
	}
	
	/**
	 * 
	 * @param code
	 * @param info
	 * @return
	 */
	public static String buildContent(int code, String info){
		return buildContent(code, info, null);
	}
	
	/**
	 * 
	 * @param code
	 * @param info
	 * @param data
	 * @return
	 */
	public static String buildContent(int code, String info, Map<String, Object> data){
		if(info == null){
			info = "";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("[{\"");
		sb.append("X_RESULTCODE");
		sb.append("\":");
		sb.append( code );
		sb.append(",\"");
		sb.append("X_RESULTINFO");
		sb.append("\":\"");
		sb.append(info);
		sb.append("\"},");
		if(data != null){
			sb.append( JSONObject.fromObject(data).toString() );
		}else{
			sb.append("{}");
		}
		sb.append("]");
		return sb.toString();
	}
}