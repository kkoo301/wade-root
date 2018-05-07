package com.ailk.database.sequence;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ISequence
 * @description: 序列接口
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-4-18
 */
public interface ISequence {

	/**
	 * 取序列的下一个值
	 * 
	 * @param connName 连接名
	 * @return
	 */
	public String getNextval(String connName) throws Exception;
	
	/**
	 * 取序列的下一个值
	 * 
	 * @param connName 连接名
	 * @param eparchyCode 地州编码
	 * @return
	 */
	public String getNextval(String connName, String eparchyCode) throws Exception;

}