package com.wade.gateway.ftp.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IFTPClient
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-4-18
 */
public interface IFTPClient {
		
	/**
	 * 下载文件
	 * 
	 * @param filePath 文件路径。默认是相对于base_path的位置；如果使用绝对路径则忽略base_path
	 * @param os
	 * @return
	 * @throws Exception
	 */
	public boolean downloadFile(String filePath, OutputStream os) throws Exception;
	
	/**
	 * 下载文件
	 * 
	 * @param filePath 文件路径。默认是相对于base_path的位置；如果使用绝对路径则忽略base_path
	 * @param fileData
	 * @return
	 * @throws Exception
	 */
	public byte[] downloadFile(String filePath) throws Exception;
	
	/**
	 * 上传文件
	 * 
	 * @param is
	 * @param dstFilePath
	 * @return
	 * @throws Exception
	 */
	public boolean uploadFile(InputStream is, String dstFilePath) throws Exception;
	
	/**
	 * 上传文件
	 * 
	 * @param fileData
	 * @param dstFilePath
	 * @return
	 * @throws Exception
	 */
	public boolean uploadFile(byte[] fileData, String dstFilePath) throws Exception;
	
	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public boolean deleteFile(String filePath) throws Exception;
	
	/**
	 * 文件改名
	 * 
	 * @param srcFilePath
	 * @param dstFilePath
	 * @return
	 * @throws Exception
	 */
	public boolean move(String srcFilePath, String dstFilePath) throws Exception;
	
	/**
	 * 创建目录
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public boolean makeDirectory(String path) throws Exception;
	
	/**
	 * 删除目录
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public boolean removeDirectory(String path) throws Exception;
	
	/**
	 * 获取文件清单
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public List<String> listFiles(String path) throws Exception;
	
	
	/**
	 * 获取目录清单
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public List<String> listDirectorys(String path) throws Exception;
}
