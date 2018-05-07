package com.wade.message.websocket.server.impl;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Manager{
	
	private static final Map<Channel, Connection> connections = new ConcurrentHashMap<Channel, Connection>(1000);
	private static final Map<String, Connection> sessionMap = new ConcurrentHashMap<String, Connection>(1000);
	
	public static Connection get(Channel channel){
		return connections.get(channel);
	}
	
	public static void add(Channel channel, Connection conn){
		connections.put(channel, conn);
	}
	
	public static void remove(Channel channel){
		connections.remove(channel);
	}
	
	public static Connection getBySessionId(String sessionId){
		return sessionMap.get(sessionId);
	}
	
	public static void addBySessionId(String sessionId, Connection conn){
		if(sessionId == null || "".equals(sessionId))
			return;
		
		if(sessionMap.containsKey(sessionId))
			return;
		
		sessionMap.put(sessionId, conn);
	}
	
	public static void removeBySessionId(String sessionId){
		if(sessionId == null || "".equals(sessionId))
			return;
		sessionMap.remove(sessionId);
	}
	
	public static void active(Channel channel){
		Connection conn = Manager.get(channel);
		if(null == conn){
			conn = new Connection(channel);
			add(channel, conn);
		}
	}
	
	public static void inactive(Channel channel){
		Connection conn = get(channel);
		if(null != conn){
			removeBySessionId(conn.getSessionId());
		}
		remove(channel);
	}
}