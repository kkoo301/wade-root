package com.wade.gateway.ftp.server.op;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.wade.gateway.ftp.server.FtpSite;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: AbstractOperation
 * @description: FTP操作抽象类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public abstract class AbstractOperation implements IOperation {
	
	/**
	 * 非空断言
	 * 
	 * @param str
	 */
	protected void assertNotBlank(String str) {
		if (!StringUtils.isNotBlank(str)) {
			throw new IllegalArgumentException(str + " 参数不能为空!");
		}
	}

	/**
	 * 拼接绝对路径
	 * 
	 * @param ftpSite
	 * @param fileName
	 * @return
	 */
	protected String buildAbsolutePath(FtpSite ftpSite, String fileName) {
		String absolutePath = FilenameUtils.concat(ftpSite.getBasePath(), fileName);
		if (-1 != absolutePath.indexOf("..")) {
			// 防止基于相对路径，进行非法访问。
			throw new IllegalArgumentException("非法访问! " + absolutePath);
		}
		return absolutePath;
	}
	
	/**
	 * 格式化异常信息
	 * 
	 * @param throwable
	 * @return
	 */
	protected String formatException(Throwable throwable) {
		
		String rtn = null;
		
		Writer writer = null;
		PrintWriter printWriter = null;
		
		try {
			writer = new StringWriter();
			printWriter = new PrintWriter(writer);
			throwable.printStackTrace(printWriter);
			printWriter.flush();
			rtn = writer.toString();
		} finally {
			try {
				writer.flush();
				writer.close();		
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return rtn;
	}
	
}
