/**
 * $
 */
package com.wade.dsf.registry.entity;

import java.lang.reflect.Method;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: BaseEntity.java
 * @description: TODO
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-9-14
 */
public class BaseEntity implements IDsfEntity {

	private String name;
	private String center;
	private String implClass;
	private Method method;
	private String inputClass;
	private String outputClass;
	private int timeout;
	private int concurrency;
	private int status;

	public BaseEntity() {

	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCenter() {
		return this.center;
	}

	@Override
	public void setCenter(String center) {
		this.center = center;
	}

	@Override
	public String getImplClass() {
		return this.implClass;
	}

	@Override
	public void setImplClass(String implClass) {
		this.implClass = implClass;
	}

	@Override
	public String getInputClass() {
		return this.inputClass;
	}

	@Override
	public void setInputClass(String inputClass) {
		this.inputClass = inputClass;
	}

	@Override
	public String getOutputClass() {
		return this.outputClass;
	}

	@Override
	public void setOutputClass(String outputClass) {
		this.outputClass = outputClass;
	}

	@Override
	public int getTimeout() {
		return this.timeout;
	}

	@Override
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public int getConcurrency() {
		return this.concurrency;
	}

	@Override
	public void setConcurrency(int concurrency) {
		this.concurrency = concurrency;
	}

	@Override
	public Method getMethod() {
		return this.method;
	}

	@Override
	public void setMethod(Method method) {
		this.method = method;
	}

	@Override
	public int getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

}
