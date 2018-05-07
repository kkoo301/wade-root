package com.wade.log;

import java.util.Map;


/**
 * 服务端监听
 * @author Shieh
 */
public interface ILogServerListener
{
	public int getPort();
	
	public Protocal getProtocal();
	
	public ILogHandler getHandler(String type);
	
	public void addHandler(String type, ILogHandler handler);
	
	public void removeHandler(String type);
	
	public Map<String, ILogHandler> handlers();
	
}