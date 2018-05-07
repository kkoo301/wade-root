/**
 * $
 */
package com.ailk.common.exce;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IExceptionWrapper.java
 * @description: 异常包装类<br>
 * 1. 获取异常编码
 * 2. 获取异常描述
 * 3. 获取异常堆栈
 * 4. 获取异常源信息-Dwade.server.name
 * 4. 异常编码及描述的重定义
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-3-11
 */
public interface IExceptionWrapper {
	
	/**
	 * 异常级别枚举
	 * @author Shieh
	 *
	 */
	public static enum ExceptionLevel
	{
		/**
		 * 警示
		 */
		WARN("warn"),
		
		/**
		 * 异常
		 */
		ERROR("error");
		
		private final String level;
		private ExceptionLevel(final String level){
			this.level = level;
		}
		
		@Override
		public String toString(){
			return this.level;
		}
	}
	
	/**
	 * 获取异常源信息(通常是进程名)
	 * @return
	 */
	public String getTarget();
	
	/**
	 * 获取异常编码
	 * @return
	 */
	public String getCode();
	
	/**
	 * 错误编码是否有匹配的翻译项
	 * @return
	 */
	public boolean isCodeTranslateHit();
	
	/**
	 * 获取异常描述
	 * @return
	 */
	public String getInfo();	
	
	/**
	 * 获取异常堆栈
	 * @return
	 */
	public String getStack();
	
	/**
	 * 获取异常对象
	 * @return
	 */
	public Throwable getThrowable();
	
	
	/**
	 * 获取异常级别
	 * @return
	 */
	public ExceptionLevel getLevel();
	
	/**
	 * 重写异常编码及异常描述
	 */
	public void overwrite(int type);

}
