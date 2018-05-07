package com.wade.gateway.ftp.server.op.impl;

import java.io.IOException;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import com.wade.container.server.Request;

import com.ailk.org.apache.commons.io.IOUtils;
import com.wade.gateway.ftp.server.op.AbstractOperation;
import com.wade.gateway.ftp.server.FtpSite;
import com.wade.gateway.ftp.server.FtpSiteFactory;
import com.wade.gateway.ftp.util.FTPClientFactory;
import com.wade.gateway.ftp.util.IFTPClient;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: CreateDirectory
 * @description: 创建目录
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public final class MakeDirectory extends AbstractOperation {

	private static final Logger log = Logger.getLogger(MakeDirectory.class);
	
	@Override
	public void execute(String siteId, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String dirName = request.getParameter("DIR_NAME");
		ServletOutputStream sos = null;
		
		try {
		
			assertNotBlank(siteId);
			assertNotBlank(dirName);			
			
			FtpSite ftpSite = FtpSiteFactory.getInstance(siteId);
			String dirPath = super.buildAbsolutePath(ftpSite, dirName);
			IFTPClient client = FTPClientFactory.getInstance(ftpSite);
			
			if (!client.makeDirectory(dirPath)) {
				throw new Exception(MakeDirectory.class.getName() + " 执行失败!");
			}
			
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			
		} catch (Exception e) {
			
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			baseRequest.setHandled(true);
			
			String exceptionInfo = super.formatException(e);
			
			sos = response.getOutputStream();
			sos.write(exceptionInfo.getBytes());
			
			log.error(exceptionInfo);
			
		} finally {
			IOUtils.closeQuietly(sos);
		}
	}

}
