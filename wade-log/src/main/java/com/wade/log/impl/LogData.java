package com.wade.log.impl;

import java.io.Serializable;

import com.wade.log.ILogData;

/**
 * 日志数据类
 * @author Shieh
 *
 */
public class LogData implements ILogData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7006873535750651824L;
	private String type;
	private long timestamp;
	private Serializable content;
	
	public LogData(){
		
	}
	
	public LogData(String logType){
		this(logType, null);
	}
	
	public LogData(String logType, Serializable logContent){
		type = logType;
		content = logContent;
		timestamp = System.currentTimeMillis();
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public void setType(String logType){
		type = logType;
	}
	
	@Override
	public long getTimestamp(){
		return timestamp;
	}
	
	public void setTimestamp(long val){
		timestamp = val;
	}
	
	@Override
	public Serializable getContent() {
		return content;
	}
	
	public void setContent(Serializable logContent){
		content = logContent;
	}
}