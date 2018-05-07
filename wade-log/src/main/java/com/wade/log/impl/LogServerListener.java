package com.wade.log.impl;

import java.util.HashMap;
import java.util.Map;

import com.wade.log.ILogHandler;
import com.wade.log.ILogServerListener;
import com.wade.log.Protocal;

public class LogServerListener implements ILogServerListener{

	private int port;
	private Protocal protocal;
	private Map<String, ILogHandler> handlers = new HashMap<String, ILogHandler>();
	
	public LogServerListener(int port, Protocal protocal){
		this.port = port;
		this.protocal = protocal;
	}
	
	@Override
	public int getPort() {
		return port;
	}
	
	@Override
	public Protocal getProtocal() {
		return protocal;
	}

	@Override
	public ILogHandler getHandler(String type) {
		return handlers.get(type);
	}

	@Override
	public void addHandler(String type, ILogHandler handler) {
		handlers.put(type, handler);
	}

	@Override
	public void removeHandler(String type) {
		handlers.remove(type);
	}

	@Override
	public Map<String, ILogHandler> handlers(){
		return handlers;
	}
}