/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月27日
 * 
 * Just Do IT.
 */
package com.wade.svf.server.serializer;

import java.io.Serializable;
import java.util.Map;

import com.wade.svf.flow.exception.FlowException;

/**
 * @description
 * 序列化接口
 */
public interface IDataSerializer {
	
	
	/**
	 * 将输入数据序列化成Java对象
	 * @param input
	 * @return
	 * @throws FlowException
	 */
	public Serializable serialize(byte[] data) throws FlowException;
	
	
	/**
	 * 将数据转换成Map<String, Object>格式
	 * @param source
	 * @return
	 * @throws FlowException
	 */
	public Map<String, Object> getData(Serializable source) throws FlowException;
	
	
	/**
	 * 将Map<String, Object>转换成JSON字符串
	 * @param data
	 * @return
	 * @throws FlowException
	 */
	public Serializable toData(Map<String, Object> data) throws FlowException;
	
	/**
	 * 将数据反序列化成字节数组
	 * @param data
	 * @return
	 * @throws FlowException
	 */
	public byte[] deserialize(Serializable data) throws FlowException;

}
