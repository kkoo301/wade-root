package com.wade.message.websocket.server.util;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.wade.message.websocket.server.IConnection;
import static com.wade.message.websocket.server.Config.*;

public class Util{
	
	/**
	 * 获取参数
	 * @param key
	 * @param parameters
	 * @return
	 */
	public static String getParameter(String key, Map<String,List<String>> parameters){
		if(key != null && !"".equals(key) && parameters != null){
			List<String> values = parameters.get(key);
			if(values != null && values.size() > 0){
				return values.get(0);
			}
		}
		return null;
	}
	
	/**
	 * 输出消息内容
	 * @param channel
	 * @param conn
	 * @param content
	 * @throws Exception
	 */
	public static void outputContent(Channel channel, IConnection conn, String content) throws Exception{
		channel.write(new TextWebSocketFrame(content));
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