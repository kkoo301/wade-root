package com.ailk.rpc.codec;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: Transporter
 * @description: RPC传输对象
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-4
 */
public class Transporter implements Serializable {

	private static final long serialVersionUID = -2906241316120911255L;

	/**
	 * 序号
	 */
	private long seq = 0;
	
	/**
	 * 类名
	 */
	private String clazzName = null;
	
	/**
	 * 函数名
	 */
	private String methodName = null;
	
	/**
	 * 参数
	 */
	private Object[] params = null;
	
	/**
	 * 参数类型
	 */
	private Class<?>[] paramTypes = null;
	
	/**
	 * 返回结果集
	 */
	private Object response = null;
	
	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	public String getClazzName() {
		return clazzName;
	}
	
	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public Object[] getParams() {
		return params;
	}
	
	public void setParams(Object[] params) {
		this.params = params;
	}
	
	public Class<?>[] getParamTypes() {
		return paramTypes;
	}
	
	public void setParamTypes(Class<?>[] paramTypes) {
		this.paramTypes = paramTypes;
	}
	
	public long getSeq() {
		return seq;
	}

	public void setSeq(long seq) {
		this.seq = seq;
	}
	
	public String toString() {
				
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		sb.append("seq : '" + this.seq + "', ");
		sb.append("clazzName : '" + this.clazzName + "', ");
		sb.append("methodName : '" + this.methodName + "', ");
		sb.append(" }");
		return sb.toString();
		
	}
}