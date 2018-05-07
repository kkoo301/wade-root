package com.ailk.common.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 日志记录器，统计类日志不会生成文件，接触类日志将生成文件以提供分析的源数据
 * 
 * @author $Id: AbstractLogger.java 195 2014-06-23 15:19:49Z liaos $
 * 
 */

public abstract class AbstractLogger implements ILogger {
	
	private static final Logger log = Logger.getLogger(AbstractLogger.class);
	private static Map<Class<ILogger>, ILogger> clazz = new HashMap<Class<ILogger>, ILogger>(100);
	private boolean isDebug = false;
	
	/**
	 * 页面统计日志
	 */
	public static final String LOG_STAT_PAGE = "1000";
	
	/**
	 * 服务统计日志
	 */
	public static final String LOG_STAT_SERVICE = "1001";
	
	/**
	 * SQL统计日志
	 */
	public static final String LOG_STAT_SQL = "1002";
	
	/**
	 * HttpHandler统计日志
	 */
	public static final String LOG_STAT_HANDLER = "1003";
	
	/**
	 * 后台进程统计日志
	 */
	public static final String LOG_STAT_PROCESS = "1004";
	
	/**
	 * 服务接触日志
	 */
	public static final String LOG_ACCESS_SERVICE = "1010";
	
	/**
	 * 获取默认的Key
	 * @param clazz
	 * @param stackLevel
	 * @param subkey
	 * @return
	 */
	public String getDefaultKey(Class<?> clazz, int stackLevel, String subkey) {
		String key = getStackInfo(stackLevel, clazz.getName(), isDebug);
		
		if (null == key || key.length() <= 0)
			return null;
		
		return key + ((null == subkey) ? "" : ":" + subkey);
	}
	
	/**
	 * 获取注入的ILogger实例
	 * @param annotationClass
	 */
	@SuppressWarnings("unchecked")
	public static ILogger getLogger(Class<?> annotationClass) {
		try {
			if (annotationClass.isAnnotationPresent(com.ailk.common.logger.Logger.class)) {
				
				com.ailk.common.logger.Logger logger = annotationClass.getAnnotation(com.ailk.common.logger.Logger.class);
				
				Class<ILogger> logClass = (Class<ILogger>) logger.logger();
				
				ILogger instance = clazz.get(logClass);
				
				if (null == instance) {
					instance = logClass.newInstance();
					clazz.put(logClass, instance);
				}
				
				return instance;
			}
		} catch (Exception e) {
			log.error("统计日志记录失败，不影响系统正常运行。" + e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * @param isDebug the isDebug to set
	 */
	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}
	
	/**
	 * @return the isDebug
	 */
	public boolean isDebug() {
		return isDebug;
	}
	
	protected String getStackInfo(int level, String clazz, boolean isDebug) {
		return getStackInfo(level, new String[] { clazz }, isDebug);
	}
	
	protected String getStackInfo(int level, String[] clazz, boolean isDebug) {
		if (level < 0)
			return null;

		Throwable t = new Throwable();
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		String stack = sw.toString();
		
		if (isDebug) {
			if (log.isDebugEnabled()) {
				log.debug(stack);
			}
		}
		
		// 当前栈里需要找的Class对象最底层的栈
		int beginIndex = -1;
		
		for (int i = 0; i < clazz.length; i++) {
			String string = clazz[i];
			beginIndex = stack.lastIndexOf(string + ".");
			if (beginIndex != -1) {
				//level = 1;
				break;
			}
		}
		
		if (beginIndex <= 0)
			return null;

		// 往上找Level层的栈信息
		for (int i = 0; i < level; i++) {
			int index = stack.indexOf("at ", beginIndex + 1);

			if (index <= 0)
				break;

			beginIndex = index;
		}

		// 取当前行的未尾索引
		int endIndex = stack.indexOf(")", beginIndex);
		if (endIndex < 0)
			return null;

		String substring = stack.substring(beginIndex, endIndex + 1);
		if (substring.startsWith("at"))
			substring = substring.substring(3);

		return substring;
	}
}
