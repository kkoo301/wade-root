package com.ailk.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ThreadStackUtil
 * @description: 取当前线程栈的信息，一般用于记录日志
 * 
 * @version: v1.0.0
 * @author: yifur
 * @date: 2013-7-26
 */

public final class ThreadStackUtil {
	
	private static final Logger log = Logger.getLogger(ThreadStackUtil.class);
	
	public static final String getThreadStackInfo(int level, String clazz) {
		return getThreadStackInfo(level, clazz, false);
	}

	/**
	 * 从当前线程栈里取最底层的Clazz，并向底取Level层的行内容
	 * 
	 * @param level
	 *            需要向栈底层深入的层数，如果超出则返回null
	 * @param clazz
	 *            取线程栈最底层的clazz
	 * @return
	 */
	public static final String getThreadStackInfo(int level, String clazz, boolean isDebug) {
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
		int beginIndex = stack.lastIndexOf(clazz + ".");
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

	public static void main(String[] args) {
	}
}
