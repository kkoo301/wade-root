package com.wade.log;

import java.io.Serializable;

/**
 * 日志数据接口类
 * @author Shieh
 *
 */
public interface ILogData extends Serializable{
	
	/**
	 * 日志类型,匹配 ILogHandler类型
	 * @return
	 */
	public String getType();
	
	/**
	 * 时间戳
	 * @return
	 */
	public long getTimestamp();
	
	/**
	 * 日志数据
	 * @return
	 */
	public Serializable getContent();
	
}