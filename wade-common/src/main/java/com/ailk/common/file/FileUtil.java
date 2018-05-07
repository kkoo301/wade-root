package com.ailk.common.file;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.util.FileHelper;
import com.ailk.common.util.FileManHelper;
import com.ailk.common.util.IFileAction;

public class FileUtil extends FileHelper{

	protected static IFileAction action = null;
	
	public FileUtil(String name) {
		super(name);
	}
	
	public static IFileAction getFileAction(){
		action = FileHelper.getFileAction();
		return action;
	}
	
	public FileUtil(IData config) {
		super(config);
	}

	/**
	 * construct function
	 * 
	 * @throws Exception
	 */
	public FileUtil() {
		super();
	}

	/**
	 * download file
	 * 
	 * @param response
	 * @param full_name
	 * @param real_name
	 * @throws Exception
	 */
	public void downloadFile(HttpServletResponse response, String full_name, String real_name) {
		String file_name = real_name == null ? full_name : real_name;
		OutputStream out = FileMan.getOutputStreamByDown(null, response, file_name);
		retrieveFile(out, full_name);
	}
	
	/**
	 * download file for multi point resume
	 *  
	 * @param response
	 * @param full_name
	 * @param real_name
	 * @throws Exception
	 */
	public void downloadBinaryFile(HttpServletRequest request, HttpServletResponse response, String full_name, String real_name){
		String file_name = real_name == null ? full_name : real_name;
		String bytesStr = request.getHeader("RANGE");
		long startPos = 0;
		long endPos = 0;
		if(StringUtils.isNotBlank(bytesStr)){
			String[] bytesArr = bytesStr.split("=");
			if(bytesArr!=null&&bytesArr.length>1){
				String bytesRange = bytesArr[1];
				if(StringUtils.isNotBlank(bytesRange)){
					String[] bytesRangeArr = bytesRange.split("-");
					if(bytesRangeArr!=null){
						startPos = Long.valueOf(bytesRangeArr[0]);
						if(bytesRangeArr.length>1){
							endPos = Long.valueOf(bytesRangeArr[1]);
						}else{
							endPos = -1;
						}
					}
				}
			}
		}
		
		OutputStream out = FileMan.getOutputStreamByBytes(response, file_name ,(endPos-startPos)+"");
		retrieveBinaryFile(out, full_name, startPos, endPos);
	}

	/**
	 * show file
	 * 
	 * @param response
	 * @param full_name
	 * @param real_name
	 * @throws Exception
	 */
	public void showFile(HttpServletResponse response, String full_name, String real_name) {
		String file_name = real_name == null ? full_name : real_name;
		OutputStream out = FileMan.getOutputStreamByShow(response, FileManHelper.getContentTypeByFileName(file_name));
		retrieveFile(out, full_name);
	}

}