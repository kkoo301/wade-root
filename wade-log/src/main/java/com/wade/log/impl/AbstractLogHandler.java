package com.wade.log.impl;

import com.wade.log.ILogData;
import com.wade.log.ILogHandler;

/**
 * 日志执行Handler 抽象基类
 * @author Shieh
 *
 */
public abstract class AbstractLogHandler implements ILogHandler{

	private String type;
	private String cron;
	private int spf = 0;
	
	@Override
	public String getType(){
		return type;
	}

	@Override
	public void setType(String val) {
		type = val;
	}
	
	@Override
	public String getCron() {
		return cron;
	}
	
	@Override
	public void setCron(String val) {
		cron = val;
	}
	
	@Override
	public int getSPF(){
		return spf;
	}
	
	@Override
	public void setSPF(int val){
		spf = val;
	}
	
	@Override
	public abstract void execute(ILogData data) throws Exception;
}