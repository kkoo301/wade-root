/**
 * $
 */
package com.ailk.common.exce;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: WadeExceptionWrapper.java
 * @description: 针对WADE定制的异常封装类
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-11
 */
public class WadeExceptionWrapper implements IExceptionWrapper {
	
	private String code;
	private String info;
	private String stack;
	private String target;
	private ExceptionLevel level = ExceptionLevel.ERROR; //默认为Error级别
	
	private boolean codeTranslateHit;
	private Throwable throwable;
	
	public WadeExceptionWrapper() {
		this.target = System.getProperty("wade.server.name", "");
	}
	
	@Override
	public Throwable getThrowable() {
		return this.throwable;
	}
	
	@Override
	public String getTarget() {
		return this.target;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public boolean isCodeTranslateHit(){
		return this.codeTranslateHit;
	}
	
	@Override
	public String getInfo() {
		return this.info;
	}

	@Override
	public String getStack() {
		return this.stack;
	}
	
	@Override
	public ExceptionLevel getLevel(){
		return this.level;
	}

	@Override
	public void overwrite(int type) {
		
	}
	
	/**
	 * @param throwable the throwable to set
	 */
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	
	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setCodeTranslateHit(boolean codeTranslateHit){
		this.codeTranslateHit = codeTranslateHit;
	}
	
	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info.replace("用户请求取消当前的操作", "数据库繁忙，请稍再试");
	}
	
	/**
	 * @param stack the stack to set
	 */
	public void setStack(String stack) {
		this.stack = stack;
	}
	
	public void setLevel(ExceptionLevel level){
		this.level = level;
	}

}
