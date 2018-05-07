package com.wade.gateway.ftp.server.op.impl;

import java.io.IOException;

import java.util.UUID;

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
 * @className: DownloadFile
 * @description: 实现文件下载
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public final class DownloadFile extends AbstractOperation {

	private static final Logger log = Logger.getLogger(DownloadFile.class);
	
	@Override
	public void execute(String siteId, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String fileName = request.getParameter("FILE_NAME");
		ServletOutputStream sos = null;
		
		try {
		
			assertNotBlank(siteId);
			assertNotBlank(fileName);
			
			// HTTP响应
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);

			response.reset();
			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition", "attachment; filename=\"" + UUID.randomUUID() + "\"");
			
			sos = response.getOutputStream();
			FtpSite ftpSite = FtpSiteFactory.getInstance(siteId);
			
			String filePath = super.buildAbsolutePath(ftpSite, fileName);
			IFTPClient client = FTPClientFactory.getInstance(ftpSite);
			
			if (!client.downloadFile(filePath, sos)) {
				throw new Exception(DownloadFile.class.getName() + " 执行失败!");
			}
			
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
