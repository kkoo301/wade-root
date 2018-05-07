package com.wade.gateway.ftp.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.ailk.common.config.GlobalCfg;
import com.ailk.org.apache.commons.io.FileUtils;
import com.ailk.org.apache.commons.io.IOUtils;
import com.wade.gateway.ftp.Constants;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: FTPClient
 * @description: FTP客户端
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-12-10
 */
public final class FTPClient implements Constants {
	
	private static final Logger log = Logger.getLogger(FTPClient.class);
	
	/**
	 * FTP网关地址
	 */
	public static final String FTP_GATEWAY_ADDR;
	
	/**
	 * FTP网关临时目录
	 */
	public static final String FTP_GATEWAY_TEMPDIR;

	/**
	 * 最大上传文件体积 60MB
	 */
	public static final long MAX_UPLOAD_SIZE = 1024 * 1024 * 60;
	
	static {
		FTP_GATEWAY_ADDR = GlobalCfg.getProperty("ftp.gateway.addr");
		FTP_GATEWAY_TEMPDIR = GlobalCfg.getProperty("ftp.gateway.tempdir", System.getProperty("java.io.tmpdir"));
		
		log.info("FTP gateway: " + FTP_GATEWAY_ADDR);
		log.info("FTP gateway temporary directory: " + FTP_GATEWAY_TEMPDIR);
	}
	
	/**
	 * 把一台FTP主机上的某个文件，拷贝到另一台FTP主机上
	 * 
	 * @param srcSiteId
	 * @param srcFileName
	 * @param dstSiteId
	 * @param dstFileName
	 * @return
	 * @throws Exception
	 */
	public static final boolean remoteCopyFile(String srcSiteId, String srcFileName, String dstSiteId, String dstFileName) throws Exception {

		log.debug("srcSiteId: " + srcSiteId + ", srcFileName: " + srcFileName + ", dstSiteId:" + dstSiteId + ", dstFileName: " + dstFileName);
		
		boolean rtn = false;
		
		String tmpFileName = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString();
		
		try {
			
			if (!downloadFile(srcSiteId, srcFileName, tmpFileName)) {
				log.error("srcSiteId -> tmpLocalFile error!");
				return false;
			}
			
			if (!uploadFile(dstSiteId, dstFileName, tmpFileName)) {
				log.error("tmpLocalFile -> dstSiteId error!");
				return false;
			}
			
			rtn = true;
			
		} finally {
			FileUtils.deleteQuietly(new File(tmpFileName));
		}
		
		return rtn;
	}
	
	/**
	 * 从FTP服务器上下载文件
	 * 
	 * @param siteId FTP站点
	 * @param fileName 文件名
	 * @param localFilePath 本地文件路径
	 * @throws Exception
	 */
	public static final boolean downloadFile(String siteId, String fileName, String localFilePath) throws Exception {
		
		log.debug("siteId: " + siteId + ", fileName: " + fileName + ", localFilePath: " + localFilePath);
		
		/** 默认取第一个站点 */
		String singleSiteId = StringUtils.split(siteId, ",")[0];
		
		boolean rtn = false;
		HttpClient httpClient = new DefaultHttpClient();
		
		try {		
			HttpPost httpPost = new HttpPost(FTP_GATEWAY_ADDR);
			setHeaders(httpPost, OP_DOWNLOAD_FILE, singleSiteId);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("FILE_NAME", fileName));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				FileOutputStream fos = new FileOutputStream(localFilePath);
				InputStream is = httpResponse.getEntity().getContent();
				
				int copiedByte = IOUtils.copy(is, fos);
				log.debug("文件大小: " + copiedByte + " Byte");
				
				fos.flush();
				IOUtils.closeQuietly(fos);
				IOUtils.closeQuietly(is);
				rtn = true;
			} else {
				String result = EntityUtils.toString(httpResponse.getEntity());
				log.error(result);
				rtn = false;
			}
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		
		return rtn;
	}
		
	/**
	 * 文件删除
	 * 
	 * @param siteId
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static final boolean deleteFile(String siteId, String fileName) throws Exception {
		
		log.debug("siteId=" + siteId + ", fileName=" + fileName);
		
		boolean rtn = false;
		HttpClient httpClient = new DefaultHttpClient();
		
		for (String singleSiteId : StringUtils.split(siteId, ",")) {
		
			try {
				HttpPost httpPost = new HttpPost(FTP_GATEWAY_ADDR);
				setHeaders(httpPost, OP_DELETE_FILE, singleSiteId);
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("FILE_NAME", fileName));
				httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						
				HttpResponse httpResponse = httpClient.execute(httpPost);		
				if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
					rtn = true;
				} else {
					String result = EntityUtils.toString(httpResponse.getEntity());
					log.error(result);
					rtn = false;
				}
			} finally {
				httpClient.getConnectionManager().shutdown();			
			}
			
		}
		return rtn;
	}
	
	/**
	 * 创建目录
	 * 
	 * @param siteId
	 * @param dirName
	 * @return
	 * @throws Exception
	 */
	public static final boolean makeDirectory(String siteId, String dirName) throws Exception {
		
		log.debug("siteId:" + siteId + ", dirName:" + dirName);
		
		boolean rtn = false;
		HttpClient httpClient = new DefaultHttpClient();
		
		try {
			HttpPost httpPost = new HttpPost(FTP_GATEWAY_ADDR);	
			setHeaders(httpPost, OP_MAKE_DIRECTORY, siteId);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("DIR_NAME", dirName));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					
			HttpResponse httpResponse = httpClient.execute(httpPost);		
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				rtn = true;
			} else {
				String result = EntityUtils.toString(httpResponse.getEntity());
				log.error(result);
				rtn = false;
			}
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		
		return rtn;
	}
	
	/**
	 * 递归删除目录
	 * 
	 * @param siteId
	 * @param dirName
	 * @return
	 * @throws Exception
	 */
	public static final boolean removeDirectory(String siteId, String dirName) throws Exception {
		log.debug("siteId:" + siteId + ", dirName:" + dirName);
		
		boolean rtn = false;
		HttpClient httpClient = new DefaultHttpClient();
		
		try {
			HttpPost httpPost = new HttpPost(FTP_GATEWAY_ADDR);	
			setHeaders(httpPost, OP_REMOVE_DIRECTORY, siteId);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("DIR_NAME", dirName));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					
			HttpResponse httpResponse = httpClient.execute(httpPost);		
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				rtn = true;
			} else {
				String result = EntityUtils.toString(httpResponse.getEntity());
				log.error(result);
				rtn = false;
			}
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return rtn;
	}
	
	/**
	 * 文件移动
	 * 
	 * @param siteId
	 * @param srcFileName
	 * @param dstFileName
	 * @return
	 * @throws Exception
	 */
	public static final boolean move(String siteId, String srcFileName, String dstFileName) throws Exception {
		log.debug("siteId:"+ siteId + ", srcFileName:" + srcFileName + ", dstFileName:" + dstFileName);
		
		boolean rtn = false;
		HttpClient httpClient = new DefaultHttpClient();
		
		try {
			HttpPost httpPost = new HttpPost(FTP_GATEWAY_ADDR);	
			setHeaders(httpPost, OP_MOVE_FILE, siteId);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("SRC_FILE_NAME", srcFileName));
			params.add(new BasicNameValuePair("DST_FILE_NAME", dstFileName));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				rtn = true;
			} else {
				String result = EntityUtils.toString(httpResponse.getEntity());
				log.error(result);
				rtn = false;
			}
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		
		return rtn;
	}
	
	/**
	 * 获取目录清单
	 * 
	 * @param siteId
	 * @param dirName
	 * @return
	 * @throws Exception
	 */
	public static final List<String> listDirectorys(String siteId, String dirName) throws Exception {
		
		log.debug("siteId:" + siteId + ", dirName:" + dirName);
		
		List<String> rtn = new ArrayList<String>();
		HttpClient httpClient = new DefaultHttpClient();
		
		try {
			HttpPost httpPost = new HttpPost(FTP_GATEWAY_ADDR);	
			setHeaders(httpPost, OP_LIST_DIRECTORYS, siteId);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("DIR_NAME", dirName));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				String[] files = StringUtils.split(result, '\n');
				rtn = Arrays.asList(files);
			} else {
				String result = EntityUtils.toString(httpResponse.getEntity());
				log.error(result);
			}
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		
		return rtn;
	}
	
	/**
	 * 获取文件清单
	 * 
	 * @param siteId
	 * @param dirName
	 * @return
	 * @throws Exception
	 */
	public static final List<String> listFiles(String siteId, String dirName) throws Exception {
		
		log.debug("siteId:" + siteId + ", dirName:" + dirName);
		
		List<String> rtn = new ArrayList<String>();
		HttpClient httpClient = new DefaultHttpClient();
		
		try {
			HttpPost httpPost = new HttpPost(FTP_GATEWAY_ADDR);	
			setHeaders(httpPost, OP_LIST_FILES, siteId);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("DIR_NAME", dirName));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				String[] files = StringUtils.split(result, '\n');
				rtn = Arrays.asList(files);
			} else {
				String result = EntityUtils.toString(httpResponse.getEntity());
				log.error(result);
			}
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		
		return rtn;
	}
		
	/**
	 * 上传文件
	 * 
	 * @param siteId
	 * @param fileName
	 * @param fileData
	 * @return
	 * @throws Exception
	 */
	public static final boolean uploadFile(String siteId, String fileName, byte[] fileData) throws Exception {
		
		if (fileData.length > MAX_UPLOAD_SIZE) {
			throw new Exception("上传文件大小超过阀值! MAX_UPLOAD_SIZE=" + MAX_UPLOAD_SIZE + ", 实际文件大小=" + fileData.length);
		}
		
		log.debug("siteId:" + siteId + ", fileName:" + fileName);
		
		File file = null;
		boolean rtn = false;
		
		try {
			String tmpFileName = System.getProperty("java.io.tmpdir") + File.separator + "GW-" + UUID.randomUUID().toString();
			file = new File(tmpFileName);
			FileUtils.writeByteArrayToFile(file, fileData);
			rtn = uploadFile(siteId, fileName, tmpFileName);
		} finally {
			if (null != file) {
				FileUtils.deleteQuietly(file);
			}
		}	
				
		return rtn;
	}
	
	/**
	 * 上传文件
	 * 
	 * @param siteId
	 * @param fileName
	 * @param localFilePath
	 * @return
	 * @throws Exception
	 */
	public static final boolean uploadFile(String siteId, String fileName, String localFilePath) throws Exception {
		
		log.debug("siteId:" + siteId + ", fileName:" + fileName + ", localFilePath:" + localFilePath);
		
		boolean rtn = false;
		HttpClient httpClient = new DefaultHttpClient();
		
		for (String singleSiteId : StringUtils.split(siteId, ',')) {
			try {
				HttpPost httpPost = new HttpPost(FTP_GATEWAY_ADDR);
				setHeaders(httpPost, OP_UPLOAD_FILE, singleSiteId);
				
				File file = new File(localFilePath);
				if (file.length() > MAX_UPLOAD_SIZE) {
					throw new Exception("上传文件大小超过阀值! MAX_UPLOAD_SIZE=" + MAX_UPLOAD_SIZE + ", 实际文件大小=" + file.length());
				}
				
				MultipartEntity reqEntity = new MultipartEntity();
				reqEntity.addPart("FILE_NAME", new StringBody(fileName));
				reqEntity.addPart("FILE_BODY", new FileBody(new File(localFilePath)));
				
			    httpPost.setEntity(reqEntity);
				
				HttpResponse httpResponse = httpClient.execute(httpPost);		
				if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
					rtn = true;
				} else {
					String result = EntityUtils.toString(httpResponse.getEntity());
					log.error(result);
					rtn = false;
				}
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
		}
		
		return rtn;
	}

	/**
	 * 设置HTTP头部参数
	 * 
	 * @param httpPost
	 * @param handler 处理类
	 * @param siteId FTP站点编码
	 */
	private static final void setHeaders(HttpPost httpPost, String handler, String siteId) {
		httpPost.setHeader(OP_KEY, handler);
		httpPost.setHeader(SITE_ID, siteId);
	}
}
