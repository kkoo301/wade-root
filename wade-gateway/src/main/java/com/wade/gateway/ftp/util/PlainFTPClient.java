package com.wade.gateway.ftp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.gateway.ftp.server.FtpSite;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: PlainFTPClient
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-4-18
 */
public class PlainFTPClient extends AbstractFTPClient {

	private static final Logger log = Logger.getLogger(PlainFTPClient.class);
	private FtpSite ftpSite;
	
	public PlainFTPClient(FtpSite ftpSite) {
		this.ftpSite = ftpSite;
	}
		
	@Override
	public boolean downloadFile(String filePath, OutputStream os) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("download...");
			log.debug(ftpSite);
			log.debug("filePath: " + filePath);
		}

		boolean rtn = false;
		FTPClient client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(filePath)) { // 使用相对路径时，首先变更当前工作目录
				client.changeWorkingDirectory(ftpSite.getBasePath());	
			}
			
			rtn = client.retrieveFile(filePath, os);
			if (!rtn) {
				throw new FileNotFoundException("filePath:" + filePath);
			}
			
		} finally {
			if (null != client) {
				client.logout();
				client.disconnect();
			}
		}
		
		return rtn;
	}
	
	@Override
	public byte[] downloadFile(String filePath) throws Exception {
		
		byte[] rtn = null;
		ByteArrayOutputStream baos = null;
		
		try {
			baos = new ByteArrayOutputStream();
			downloadFile(filePath, baos);
			rtn = baos.toByteArray();
		} finally {
			baos.close();
		}
		
		return rtn;
	}
		
	@Override
	public boolean uploadFile(InputStream is, String dstFilePath) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("uploadFile...");
			log.debug(ftpSite);
			log.debug("dstFilePath: " + dstFilePath);
		}
		
		boolean rtn = false;
		FTPClient client = null;
		
		try {
			client = beReady(ftpSite);

			/** 确保目录存在 */
			makeSureDirExist(client, dstFilePath);
			
			rtn = client.storeFile(dstFilePath, is);
			if (!rtn) {
				throw new FileNotFoundException("dstFilePath=" + dstFilePath);
			}
			
		} finally {
			if (null != client) {
				client.logout();
				client.disconnect();
			}
		}
		
		return rtn;
	}

	/**
	 * 确保目录存在
	 * 
	 * @param client
	 * @param dstFilePath
	 * @return
	 */
	private void makeSureDirExist(FTPClient client, String dstFilePath) throws Exception {
		
		String dirname = FilenameUtils.getFullPath(dstFilePath);
		if (!client.changeWorkingDirectory(dirname)) {
			String basePath = ftpSite.getBasePath();
			client.changeWorkingDirectory(basePath);
			String subdir = StringUtils.removeStart(dirname, basePath);
			subdir = StringUtils.removeStart(subdir, "/");
			for (String dir : StringUtils.split(subdir, "/")) {
				client.makeDirectory(dir);
				client.changeWorkingDirectory(dir);
			}
		}
		
	}
	
	@Override
	public boolean uploadFile(byte[] fileData, String dstFilePath) throws Exception {
		
		boolean rtn = false;
		ByteArrayInputStream bais = null;
		
		try {
			bais = new ByteArrayInputStream(fileData);			
			rtn = uploadFile(bais, dstFilePath);
		} finally {
			bais.close();
		}
		
		return rtn;
	}
	
	@Override
	public boolean deleteFile(String filePath) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("deleteFile...");
			log.debug(ftpSite);
			log.debug("filePath: " + filePath);
		}

		boolean rtn = false;
		FTPClient client = null;
		
		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(filePath)) { // 使用相对路径时，首先变更当前工作目录
				client.changeWorkingDirectory(ftpSite.getBasePath());
			}
			
			rtn = client.deleteFile(filePath);
			if (!rtn) {
				throw new FileNotFoundException("filePath=" + filePath);
			}
			
		} finally {
			if (null != client) {
				client.logout();
				client.disconnect();
			}
		}
		
		return rtn;
	}

	@Override
	public boolean move(String srcFilePath, String dstFilePath) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("move...");
			log.debug(ftpSite);
			log.debug("srcFilePath: " + srcFilePath);
			log.debug("dstFilePath: " + dstFilePath);
		}
		
		boolean rtn = false;
		FTPClient client = null;
		
		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(srcFilePath)) {
				srcFilePath = ftpSite.getBasePath() + '/' + srcFilePath;
			}
			
			if (isRelativePath(dstFilePath)) {
				dstFilePath = ftpSite.getBasePath() + '/' + dstFilePath;
			}
			
			rtn = client.rename(srcFilePath, dstFilePath);
			if (!rtn) {
				throw new FileNotFoundException("srcFilePath:" + srcFilePath + ",dstFilePath:" + dstFilePath);
			}
			
		} finally {
			if (null != client) {
				client.logout();
				client.disconnect();
			}
		}
		
		return rtn;
	}

	@Override
	public boolean makeDirectory(String path) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("makeDirectory...");
			log.debug(ftpSite);
			log.debug("path: " + path);
		}
		
		boolean rtn = false;
		FTPClient client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(path)) { // 使用相对路径时，首先变更当前工作目录
				client.changeWorkingDirectory(ftpSite.getBasePath());
			}
			
			rtn = client.makeDirectory(path);
			if (!rtn) {
				throw new FileNotFoundException(path);
			}
			
		} finally {
			if (null != client) {
				client.logout();
				client.disconnect();
			}
		}
		
		return rtn;
	}

	@Override
	public List<String> listDirectorys(String path) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("listDirectorys");
			log.debug(ftpSite);
			log.debug("path: " + path);
		}
		
		List<String> rtn = new ArrayList<String>();
		FTPClient client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(path)) { // 使用相对路径时，首先变更当前工作目录
				client.changeWorkingDirectory(ftpSite.getBasePath());
			}
			
			FTPFile[] files = client.listFiles(path);
			for (FTPFile file : files) {
				if (file.isDirectory()) {
					rtn.add(file.getName());
				}
			}
		} finally {
			if (null != client) {
				client.logout();
				client.disconnect();
			}
		}
		
		return rtn;
	}
	
	@Override
	public List<String> listFiles(String path) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("listFiles...");
			log.debug(ftpSite);
			log.debug("path: " + path);
		}
		
		List<String> rtn = new ArrayList<String>();
		FTPClient client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(path)) { // 使用相对路径时，首先变更当前工作目录
				client.changeWorkingDirectory(ftpSite.getBasePath());
			}
			
			FTPFile[] files = client.listFiles(path);
			for (FTPFile file : files) {
				
				if (file.isFile()) {
					rtn.add(file.getName());
				}
			}
		} finally {
			if (null != client) {
				client.logout();
				client.disconnect();
			}
		}
		
		return rtn;
	}
	
	@Override
	public boolean removeDirectory(String path) throws Exception {
		
		path = StringUtils.stripEnd(path, "/");
		path = StringUtils.stripEnd(path, "\\");
		
		if (log.isDebugEnabled()) {
			log.debug("removeDirectory...");
			log.debug(ftpSite);
			log.debug("path: " + path);
		}
		
		boolean rtn = false;
		FTPClient client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(path)) { // 使用相对路径时，首先变更当前工作目录
				client.changeWorkingDirectory(ftpSite.getBasePath());
			}
			
			rtn = removeDirectoryRecursion(client, path);
			if (!rtn) {
				throw new FileNotFoundException("directory is not exists! " + path);
			}
			
		} finally {
			if (null != client) {
				client.logout();
				client.disconnect();
			}
		}
		
		return rtn;
	}
	
	private static final FTPClient beReady(FtpSite ftpSite) throws Exception {
		FTPClient client = new FTPClient();
		client.connect(ftpSite.getIp(), ftpSite.getPort());
		client.setControlEncoding("UTF-8");
		
		boolean success = client.login(ftpSite.getUsername(), ftpSite.getPassword());
		if (!success) {
			throw new IllegalStateException("ftp login failure!");
		}
		
		if (ftpSite.isPasvMode()) {
			client.enterLocalPassiveMode();
		}
		
		client.setFileType(FTPClient.BINARY_FILE_TYPE);
		return client;
	}
	
	private static final boolean removeDirectoryRecursion(FTPClient client, String path) throws IOException {
		
		FTPFile[] files = client.listFiles(path);
				
		if (files == null || files.length == 0) {
			return client.removeDirectory(path);
		} else {
	    	for (FTPFile file : files) {
	    		if (file.isDirectory()) {
	    			removeDirectoryRecursion(client, path + '/' + file.getName());
	    		} else {
	    			client.deleteFile(path + '/' + file.getName());
	    		}
	    	}
	    	
	    	client.removeDirectory(path);
		}
		
		return true;
	}

}
