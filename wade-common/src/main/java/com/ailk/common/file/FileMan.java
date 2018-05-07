package com.ailk.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.util.FileManHelper;

public class FileMan {
	private static transient final Logger	log	= Logger.getLogger(FileMan.class);

  	/**
	 * 根据fileId从服务器读取指定类型的数据并返回到客户端
	 * @param fileName 文件名
	 */
	public static OutputStream show(HttpServletResponse response, String fileType) throws Exception{
		String contentType = "application/octet-stream";
		if( null != fileType && !"".equals(fileType.trim()) ){
			contentType = FileManHelper.getContentType(fileType.toUpperCase());
		}
		return getOutputStreamByShow(response, contentType);
	}
	
	/**
	 * get output stream
	 * @param response
	 * @param file_name
	 * @return OutputStream
	 * @throws Exception
	 */
	public static OutputStream getOutputStreamByDown(HttpServletRequest request, HttpServletResponse response, String file_name){
		initResponse(request, response, file_name);
		try {
			return response.getOutputStream();
		} catch (IOException e) {
			throw new BaseException("fileman-10001", e);
		}
	}
	
	public static OutputStream getOutputStreamByImpExp(HttpServletRequest request, HttpServletResponse response, String file_name){
		initResponseByImpExp(request, response, file_name);
		try {
			return response.getOutputStream();
		} catch (IOException e) {
			throw new BaseException("fileman-10001", e);
		}
	}
	
	private static void initResponse(HttpServletRequest request, HttpServletResponse response, String file_name){
		//xiedx 2016/08/25 统一使用URLEncoder编码文件名
		try {
			if(file_name != null && !"".equals(file_name.trim())){
				file_name = URLEncoder.encode(file_name, GlobalCfg.getCharset());
			}
		} catch (UnsupportedEncodingException e) {
			log.error("encode response fileName failed!");
		}
		
		int index = file_name.lastIndexOf(".swf");
		if (index != -1) {
			response.setContentType("application/x-shockwave-flash");
		} else {
			response.setContentType("application/octet-stream; charset=" + GlobalCfg.getCharset());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file_name + "\"");
		}
	}
	
	
	private static void initResponseByImpExp(HttpServletRequest request, HttpServletResponse response, String file_name){
		int index = file_name.lastIndexOf(".swf");
		if (index != -1) {
			response.setContentType("application/x-shockwave-flash");
		} else {
			response.setContentType("application/octet-stream; charset=" + GlobalCfg.getCharset());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file_name + "\"");
		}
	}
	
	/**
	 * 在文件时 设置对应的头信息
	 * @param response
	 * @param contenet_type
	 * @return OutputStream
	 * @throws Exception
	 */
	public static OutputStream getOutputStreamByShow(HttpServletResponse response, String contenet_type){
		//weblogic 10 not support
		//response.reset();
		response.setContentType(contenet_type);
		/* no cache */
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		try {
			return response.getOutputStream();
		} catch (IOException e) {
			throw new BaseException("fileman-10002", e);
		}
	}

	/**
	 * 在文件时 设置对应的头信息
	 * @param response
	 * @param contenet_type
	 * @return OutputStream
	 * @throws Exception
	 */
	public static OutputStream getOutputStreamByBytes(HttpServletResponse response, String file_name, String contentLength){
		initResponse(null, response, file_name);
		if(StringUtils.isNotBlank(contentLength) && Long.valueOf(contentLength)>0){
			response.setHeader("Content-Length", contentLength);
		}
		try {
			return response.getOutputStream();
		} catch (IOException e) {
			throw new BaseException("fileman-10003", e);
		}
	}
    /**
     * download file
     * @param response
     * @param full_name
     * @throws Exception
     */
    public static void downFile(HttpServletResponse response, String full_name){
		downFile(response, full_name, null);
  	}
    
    /**
     * download file
     * @param response
     * @param full_name
     * @param real_name
     * @throws Exception
     */
    public static void downFile(HttpServletResponse response, String full_name, String real_name){
		String file_name = real_name == null ? full_name : real_name;
		
		File file = FileManHelper.getFile(full_name);
		
		OutputStream out = getOutputStreamByDown(null, response, file_name);
		try {
			FileManHelper.writeInputToOutput(new FileInputStream(file), out);
		} catch (FileNotFoundException e) {
			throw new BaseException("fileman-10007", e);
		}
  	}
        
    /**
     * upload file
     * @param item:FileItem or IUploadFile
     * @param file_path
     * @param file_name
     * @throws Exception
     */
    public static void uploadFile(FileItem item, String file_path, String file_name){
    	if (!new File(file_path).isDirectory()) throw new BaseException("fileman-10002");
    	
    	File file = new File(file_path, file_name);
    	
    	try {
			item.write(file);
		} catch (Exception e) {
			throw new BaseException("fileman-10008", e);
		}
    }
}