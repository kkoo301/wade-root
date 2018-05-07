package com.wade.gateway.ftp.util;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: AbstractFTPClient
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-4-18
 */
public abstract class AbstractFTPClient implements IFTPClient {

	/**
	 * 判断是否为绝对路径
	 * 
	 * @param filePath
	 * @return
	 */
	protected boolean isAbsolutePath(String filePath) {
		if (filePath.startsWith("/")) { // Unix、Linux绝对路径
			return true;
		}
		
		if (-1 != filePath.indexOf(":\\")) { // Windows 绝对路径
			return true;
		}
		
		if (-1 != filePath.indexOf(":/")) { // Windows 绝对路径
			return true;
		}
		
		return false;
	}
	
	protected boolean isRelativePath(String filePath) {
		return !isAbsolutePath(filePath);
	}
}
