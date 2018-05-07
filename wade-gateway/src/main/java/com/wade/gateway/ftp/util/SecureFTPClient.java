package com.wade.gateway.ftp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelSftp.LsEntrySelector;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.wade.gateway.ftp.server.FtpSite;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: SecureFTPClient
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-4-18
 */
public class SecureFTPClient extends AbstractFTPClient {

	private static final Logger log = Logger.getLogger(SecureFTPClient.class);
	private FtpSite ftpSite;

	public SecureFTPClient(FtpSite ftpSite) {
		this.ftpSite = ftpSite;
	}
	
	@Override
	public boolean downloadFile(String filePath, OutputStream os) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("downloadFile...");
			log.debug(ftpSite);
			log.debug("filePath: " + filePath);
		}

		boolean rtn = false;
		ChannelSftp client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(filePath)) { // 使用相对路径时，首先变更当前工作目录
				client.cd(ftpSite.getBasePath());
			}
			
			client.get(filePath, os);
			rtn = true;
		} finally {
			if (null != client) {
				client.quit();
				client.disconnect();
				client.getSession().disconnect();
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
	public boolean uploadFile(InputStream is, String dstFilePath) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("uploadFile...");
			log.debug(ftpSite);
			log.debug("dstFilePath: " + dstFilePath);
		}

		boolean rtn = false;
		ChannelSftp client = null;

		try {
			client = beReady(ftpSite);
			
			/** 确保目录存在 */
			makeSureDirExist(client, dstFilePath);
			
			client.put(is, dstFilePath, ChannelSftp.OVERWRITE);
			rtn = true;
		} finally {
			if (null != client) {
				client.quit();
				client.disconnect();
				client.getSession().disconnect();
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
	private void makeSureDirExist(ChannelSftp client, String dstFilePath) throws Exception {
		
		String dirname = FilenameUtils.getFullPath(dstFilePath);
		try {
			client.cd(dirname);
		} catch (Exception e) {
			String basePath = ftpSite.getBasePath();
			client.cd(basePath);
			String subdir = StringUtils.removeStart(dirname, basePath);
			subdir = StringUtils.removeStart(subdir, "/");
			
			log.debug("subdir: " + subdir);
			for (String dir : StringUtils.split(subdir, "/")) {
				log.debug("mkdir " + dir);
				
				try {
					client.cd(dir);
				} catch (Exception ex) {
					client.mkdir(dir);
					client.cd(dir);
				}
				
			}
		
		}
		
	}
	
	@Override
	public boolean deleteFile(String filePath) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("deleteFile...");
			log.debug(ftpSite);
			log.debug("filePath: " + filePath);
		}

		boolean rtn = false;
		ChannelSftp client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(filePath)) { // 使用相对路径时，首先变更当前工作目录
				client.cd(ftpSite.getBasePath());
			}
			
			client.rm(filePath);
			rtn = true;
		} finally {
			if (null != client) {
				client.quit();
				client.disconnect();
				client.getSession().disconnect();
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
		ChannelSftp client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(srcFilePath)) {
				srcFilePath = ftpSite.getBasePath() + '/' + srcFilePath;
			}
			
			if (isRelativePath(dstFilePath)) {
				dstFilePath = ftpSite.getBasePath() + '/' + dstFilePath;
			}
			
			client.rename(srcFilePath, dstFilePath);
			rtn = true;
		} finally {
			if (null != client) {
				client.quit();
				client.disconnect();
				client.getSession().disconnect();
			}
		}

		return rtn;
	}

	@Override
	public boolean makeDirectory(String path) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("move...");
			log.debug(ftpSite);
			log.debug("path: " + path);
		}

		boolean rtn = false;
		ChannelSftp client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(path)) { // 使用相对路径时，首先变更当前工作目录
				client.cd(ftpSite.getBasePath());
			}
			
			client.mkdir(path);
			rtn = true;
		} finally {
			if (null != client) {
				client.quit();
				client.disconnect();
				client.getSession().disconnect();
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
		
		ChannelSftp client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(path)) { // 使用相对路径时，首先变更当前工作目录
				client.cd(ftpSite.getBasePath());
			}

			return removeDirectoryRecursion(client, path);
			
		} finally {
			if (null != client) {
				client.quit();
				client.disconnect();
				client.getSession().disconnect();
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	private boolean removeDirectoryRecursion(ChannelSftp client, String path) throws Exception {
		
		Vector<ChannelSftp.LsEntry> entrys = client.ls(path);
		
		if (entrys == null || entrys.size() == 0) {
			client.rmdir(path);	
		} else {
	    	for (ChannelSftp.LsEntry entry : entrys) {
	    		
	    		String name = entry.getFilename();
	    		SftpATTRS attrs = entry.getAttrs();
	    		
	    		String filePath = path + "/" + name;
	    		
	    		if (name.equals(".") || name.equals("..")) {
	    			continue;
	    		}
	    		
	    		if (attrs.isDir()) {
	    			removeDirectoryRecursion(client, filePath);
	    		} else {
	    			client.rm(filePath);
	    		}
	    	}
	    	client.rmdir(path);
		}
		
		return true;
	}
	
	@Override
	public List<String> listDirectorys(String path) throws Exception {
		if (StringUtils.isBlank(path)) {
			path = ".";
		}
		
		if (log.isDebugEnabled()) {
			log.debug("listDirectorys...");
			log.debug(ftpSite);
			log.debug("path: " + path);
		}

		List<String> rtn = new ArrayList<String>();
		ChannelSftp client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(path)) { // 使用相对路径时，首先变更当前工作目录
				client.cd(ftpSite.getBasePath());
			}
			
			client.ls(path, new DirectoryLsEntrySelector(rtn));
		} finally {
			if (null != client) {
				client.quit();
				client.disconnect();
				client.getSession().disconnect();
			}
		}

		return rtn;
	}
	
	@Override
	public List<String> listFiles(String path) throws Exception {

		if (StringUtils.isBlank(path)) {
			path = ".";
		}
		
		if (log.isDebugEnabled()) {
			log.debug("listFiles...");
			log.debug(ftpSite);
			log.debug("path: " + path);
		}

		List<String> rtn = new ArrayList<String>();
		ChannelSftp client = null;

		try {
			client = beReady(ftpSite);
			
			if (isRelativePath(path)) { // 使用相对路径时，首先变更当前工作目录
				client.cd(ftpSite.getBasePath());
			}
			
			client.ls(path, new FileLsEntrySelector(rtn));
		} finally {
			if (null != client) {
				client.quit();
				client.disconnect();
				client.getSession().disconnect();
			}
		}

		return rtn;
	}
	
	private static final ChannelSftp beReady(FtpSite ftpSite) throws Exception {
		String ftpHost = ftpSite.getIp();
		int ftpPort = ftpSite.getPort();
		String ftpUserName = ftpSite.getUsername();
		String ftpPassword = ftpSite.getPassword();

		// 1. Session created.
		JSch jsch = new JSch();
		Session session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
		session.setPassword(ftpPassword);

		// 2. Session connected.
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");

		session.setConfig(config);
		session.setTimeout(1000);
		session.connect();

		// 3. Opening Channel.
		Channel channel = session.openChannel("sftp");
		channel.connect();

		return (ChannelSftp) channel;
	}

	/**
	 * 目录选择器
	 */
	private static class DirectoryLsEntrySelector implements LsEntrySelector {

		private List<String> list;

		public DirectoryLsEntrySelector(List<String> list) {
			this.list = list;
		}

		@Override
		public int select(LsEntry entry) {
			SftpATTRS attrs = entry.getAttrs();
			if (attrs.isDir()) {
				list.add(entry.getFilename());
				return LsEntrySelector.CONTINUE;
			}

			return LsEntrySelector.CONTINUE;
		}

	}
	
	/**
	 * 文件选择器
	 */
	private static class FileLsEntrySelector implements LsEntrySelector {

		private List<String> list;

		public FileLsEntrySelector(List<String> list) {
			this.list = list;
		}

		@Override
		public int select(LsEntry entry) {
			SftpATTRS attrs = entry.getAttrs();
			if (attrs.isDir()) { // 过滤掉文件夹
				return LsEntrySelector.CONTINUE;
			}

			list.add(entry.getFilename());
			return LsEntrySelector.CONTINUE;
		}

	}

}
