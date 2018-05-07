package com.ailk.mq.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ErrorUtil
 * @description: 异常工具类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-8-2
 */
public final class ErrorUtil {
	
	/**
	 * 将异常堆栈转成字符串
	 * 
	 * @param t
	 * @return
	 */
	public static final String exceptionDetail(Throwable t) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		t.printStackTrace(out);
		String detail = baos.toString();
		out.close();
		return detail;
	}
}
