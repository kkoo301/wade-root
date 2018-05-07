package com.wade.gateway.ftp.server.op.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import com.wade.container.server.Request;

import com.ailk.org.apache.commons.io.IOUtils;
import com.wade.gateway.ftp.client.FTPClient;
import com.wade.gateway.ftp.server.op.AbstractOperation;
import com.wade.gateway.ftp.server.FtpSite;
import com.wade.gateway.ftp.server.FtpSiteFactory;
import com.wade.gateway.ftp.util.FTPClientFactory;
import com.wade.gateway.ftp.util.IFTPClient;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: UploadFile
 * @description: 文件上传
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public final class UploadFile extends AbstractOperation {

	private static final Logger log = Logger.getLogger(UploadFile.class);
	
	/**
	 * 模拟上传请求: curl -F upload=@/root/biz.log http://192.168.245.1:8080
	 */
	@Override
	public void execute(String siteId, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if (!ServletFileUpload.isMultipartContent(request)) {			
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().print("is not multipart content!");
			baseRequest.setHandled(true);
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		
		// 构造一个文件上传处理对象
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		File uploaderFile = null;
		InputStream is = null;
		ServletOutputStream sos = null;
		
		try {
			// 解析表单中提交的所有文件内容
			Iterator<FileItem> iter = upload.parseRequest(request).iterator();
			while (iter.hasNext()) {
				FileItem item = iter.next();
				if (item.isFormField()) {
					String paramName = item.getFieldName();
                    String paramValue = item.getString();                    
                    params.put(paramName, paramValue);
				} else {
					String name = item.getName();
					String fileName = name.substring(name.lastIndexOf('\\') + 1, name.length());
					String path = FTPClient.FTP_GATEWAY_TEMPDIR + File.separatorChar + fileName;

					uploaderFile = new File(path);
					item.write(uploaderFile);

				}
			}
			
			String fileName = params.get("FILE_NAME");
			is = new FileInputStream(uploaderFile);
			FtpSite ftpSite = FtpSiteFactory.getInstance(siteId);
			String filePath = super.buildAbsolutePath(ftpSite, fileName);
			
			IFTPClient client = FTPClientFactory.getInstance(ftpSite);
			
			if (!client.uploadFile(is, filePath)) {
				throw new Exception(UploadFile.class.getName() + " 执行失败!");
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
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(sos);
			
			if (null != uploaderFile) {
				uploaderFile.delete();
			}
		}
		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
	}

}
