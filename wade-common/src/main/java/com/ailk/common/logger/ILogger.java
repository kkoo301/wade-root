package com.ailk.common.logger;

/**
 * 
 * @author $Id: ILogger.java 1 2014-02-20 08:34:02Z huangbo $
 *
 */

public interface ILogger {

	/**
	 * 记录当前执行对象object，从start开始运行，并执行cost毫秒，其日志内容为content
	 * 
	 * @param object 当前执行的对象
	 * @param subkey 日志的子Key
	 * @param start 当前执行的开始时间(毫秒)
	 * @param cost 当前执行完成消耗的时间(毫秒)
	 */
	public void log(Object object, String subkey, long start, long cost, String content);
	
}