package com.ailk.common.util.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.FileHelper;
import com.ailk.common.util.FileManHelper;
import com.ailk.common.util.IFileAction;
import com.ailk.common.util.Utility;

public class DefaultFileAction implements IFileAction {
	
	private static transient final Logger log = Logger.getLogger(DefaultFileAction.class);
	
	private ThreadLocal<IVisit> visitLocal = new ThreadLocal<IVisit>();
	
	//上传时是否自动创建不存在路径
	private static boolean isAutoCreatePath = true;
	
	//最多创建目录层次
	private static int maxDirs = 5;
	
	private static IData fileInfos = new DataMap();
	
	public IData query(String fileId) throws Exception{
		IData data = fileInfos.getData(fileId);
		return data;
	}

	public IData querys(String fileIds) throws Exception{
		IData filesMap = new DataMap();
		String[] files = fileIds.split(",");
		for(String fileId : files){
			IData file = query(fileId);
			if(file != null){
				filesMap.put(fileId, file);
			}
		}
		return filesMap;
	}
	
	public List deletes(String fileIds) throws Exception {
		return deletes(fileIds, false);
	}
	
	public List deletes(String fileIds, boolean isNeedSuffix) throws Exception {
		List failFileIds = new ArrayList();
		String[] files = fileIds.split(",");
		for(String fileId :files){
			boolean deleteFlag = delete(fileId, isNeedSuffix);
			if(!deleteFlag){
				failFileIds.add(fileId);
			}
		}
		return failFileIds;
	}
	
	public boolean delete(String fileId) throws Exception {
		return delete(fileId, false);
	}
	
	public boolean delete(String fileId, boolean isNeedSuffix) throws Exception{
		if(!GlobalCfg.getProperty("fileman.delete", "false").equals("true")){
			return true;
		}
		boolean re = false ;
		IData params = query(fileId);
		String filePath = params.getString("filePath");
		if(GlobalCfg.getProperty("fileman.mode", "local").equals("local")){
			String path = buildFilePath(filePath);
			File file = FileManHelper.getFile(path+"/"+fileId);
			if(file.exists()){
				if(file.delete()){
					re = true;
				}
			}	
		}else{
			FileHelper fileHelper = null;
			String ftpSite = params.getString("ftpSite");
			if (ftpSite == null || "".equals(ftpSite)) {
				fileHelper=new FileHelper();
			}else{
				fileHelper=new FileHelper(ftpSite);
			}
			if(changeDirectory(fileHelper,filePath)){
				if(fileHelper.deleteFile(fileId)){
					re = true;
				}
			}
		}
		
		removeFileInfo(fileId);
		if(log.isInfoEnabled())
			log.info("delete file " + fileId);
		return re;
	}
	
	public void removeFileInfo(String fileId){
		fileInfos.remove(fileId);
	}
	
	public void downloadBinary(OutputStream out, String fileId, long startPos, long endPos) throws Exception{
		IData params = query(fileId);
		String filePath = params.getString("filePath");
		String ftpSite = params.getString("ftpSite");
		if(GlobalCfg.getProperty("fileman.mode", "local").equals("local")){
			String path = buildFilePath(filePath);
			File tempFile = null;
			try {
				tempFile = FileManHelper.getBinaryFile(path + "/" + fileId, startPos, endPos);
				FileManHelper.writeInputToOutput(new FileInputStream(tempFile), out);
			} catch (FileNotFoundException e) {
				Utility.error(e);
			}finally{
				if(tempFile != null){
					if(tempFile.exists()){
						tempFile.delete();
						if(tempFile.exists()){
							tempFile.delete();
						}
					}
				}
			}
		}else{
			FileHelper fileHelper=null;
			if (ftpSite == null || "".equals(ftpSite)) {
				fileHelper = new FileHelper();
			}else{
				fileHelper = new FileHelper(ftpSite);
			}
			fileHelper.setAutoRelease(false);
			if(changeDirectory(fileHelper,filePath)){
				fileHelper.retrieveBinaryFile(out, fileId, startPos, endPos);
			}
			fileHelper.releaseResourse();
		}
	}
	
	public File download(String fileId) throws Exception {
		return download(fileId, false);
	}
	
	public File download(String fileId, boolean isNeedSuffix) throws Exception{
		File file = null;
		IData params = query(fileId);
		String filePath = params.getString("filePath");
		String ftpSite = params.getString("ftpSite");
		if(GlobalCfg.getProperty("fileman.mode", "local").equals("local")){
			//String path=GlobalCfg.getProperty("fileman.local.dir", getTempRepository()+"/upload");
			String path = buildFilePath(filePath);
			file = FileManHelper.getFile(path+"/"+fileId);
			/*if(response!=null){
				String file_name = real_name == null ? fileId : real_name;
				OutputStream out = FileMan.getOutputStreamByDown(response, file_name);
				try {
					FileMan.writeInputToOutput(new FileInputStream(file), out);
				} catch (FileNotFoundException e) {
					throw new BaseException("fileutil-10002", e);
				}
			}	*/		
		}else{
			FileHelper fileHelper = null;
			if (ftpSite == null || "".equals(ftpSite)) {
				fileHelper = new FileHelper();
			}else{
				fileHelper = new FileHelper(ftpSite);
			}
			//临时文件以从ftp获取相应的文件
			file = new File(fileId);
			fileHelper.setAutoRelease(false);
			if(changeDirectory(fileHelper, filePath)){
				OutputStream out = null;
				/*if(response!=null){
					String file_name = real_name == null ? fileId : real_name;
					out = FileMan.getOutputStreamByDown(response, file_name);
				}else{*/
				try{
					out = FileManHelper.getOutputStream(file);
					fileHelper.retrieveFile(out, fileId);
				}catch(Exception e){
					Utility.error(Utility.getBottomException(e));
					if(file != null&&file.exists()){
						file.delete();
						if(file.exists()){
							file.delete();
						}
					}
				}finally{
					if(out != null){
						out.flush();
						out.close();
					}
				}
			}
			fileHelper.releaseResourse();
		}
		/*if(response!=null){
			return "0";
		}else{*/
		return file;
		//}
	}
	
	/*
	public OutputStream show(HttpServletResponse response, String fileType) throws Exception{
		return FileMan.getOutputStreamByShow(response, FileMan.getContentType(fileType.toUpperCase()));
	}
	*/
	
	private String buildFilePath(String filePath){
		String path = GlobalCfg.getProperty("fileman.local.dir");
		if(StringUtils.isBlank(path)){
			path = getTempRepository();
		}
		if(StringUtils.isNotBlank(filePath)){
			if(filePath.startsWith("/")||filePath.indexOf("./")>0){ 
				throw new BaseException("Param filePath includes illegal characters");
			}
			if(path.endsWith("/")){
				path += filePath;
			}else{
				path += "/"+filePath;
			}
		}
		if(path.endsWith("/")){
			path = path.substring(0, path.length()-1);
		}
		return path;
	}
	
	public String upload(InputStream input, String fileId, String ftpSite, String filePath, String fileName) throws Exception{
		return upload(input, fileId, ftpSite, filePath, fileName, false);
	}
	
	public String upload(InputStream input, String fileId, String ftpSite, String filePath, String fileName, boolean isNeedSuffix) throws Exception{
		String fileSize="0";
		if(StringUtils.isBlank(fileId)){
			return upload(input, ftpSite, filePath, fileName);
		}else{
			fileSize = String.valueOf(input.available());
			if(GlobalCfg.getProperty("fileman.mode", "local").equals("local")){
				//	String path=GlobalCfg.getProperty("fileman.local.dir", getTempRepository()+"/upload");
					String path = buildFilePath(filePath);
					File localF = new File(path+"/"+fileId);
					//localF.createNewFile();
					OutputStream output;
					try {
						output = new FileOutputStream(localF);
						byte[] b = new byte[1024];
						int c = 0;
						while((c = input.read(b)) != -1){
							output.write(b, 0, c);
						}
						output.flush();
						output.close();
						input.close();
					} catch (Exception e) {
						Utility.getBottomException(e).printStackTrace();
						Utility.error(e.getMessage());
					}
					
				}else{
					FileHelper fileHelper=null;
					if (ftpSite == null || "".equals(ftpSite)) {
						fileHelper = new FileHelper();
					}else{
						fileHelper = new FileHelper(ftpSite);
					}
					fileHelper.setAutoRelease(false);
					if(!changeDirectory(fileHelper, filePath)){
						createDirectory(fileHelper, filePath);
					}
					fileHelper.uploadFile(input, fileId);
					fileHelper.releaseResourse();
				}
			IData params = new DataMap();
			
			params.put("fileSize", fileSize);
			params.put("fileId", fileId);
			params.put("ftpSite", ftpSite);
			params.put("filePath", filePath);
			params.put("fileName", fileName);
			params.put("time", Utility.getCurrentTime());//上传时间
			if(getVisit() != null){
				params.put("userId", getVisit().get("login_userId"));//登录用户ID
			}
			addFileInfos(params);
		}
		return fileId;
	}
	
	/**
	 * 根据fileId复制文件到其它站点
	 */
	public boolean remoteCopyFile(String fileId, boolean isNeedSuffix, String[] dstFtpSites, String[] dstFilePath) throws Exception{
		
		return false;
	}
	
	/**
	 * 复制inputStream中的文件流到其它站点
	 */
	public boolean copyFile(InputStream input, String fileId, String fileName, boolean isNeedSuffix, String[] dstFtpSites, String[] dstFilePath) throws Exception{
		return false;
	}
	
	public void addFileInfos(IData params){
		fileInfos.put(params.getString("fileId"), params);
	}
	
	public String upload(InputStream input, String ftpSite, String filePath, String fileName) throws Exception{
		return upload(input, ftpSite, filePath, fileName, false);
	}
	
	public String upload(InputStream input, String ftpSite, String filePath, String fileName, boolean isNeedSuffix) throws Exception{
		String fileId = createFileId();
		//上传到本机目录或ftpSite
		upload(input, fileId, ftpSite, filePath, fileName, isNeedSuffix);
		return fileId;
	}
	
	public boolean isValidate(IData config, IData params, String fileId, String ftpSite) {
		return true;
	}
	
	public int getMaxSize() {
		return FILE_MAX_SIZE;
	}
	
	public int getThresholdSize() {
		return FILE_THRESHOLD_SIZE;
	}
	
	public String getTempRepository() {
		return System.getProperty("java.io.tmpdir");
	}
	
	public String createFileId() throws Exception {
		long fileId = System.currentTimeMillis();
		return String.valueOf(fileId);
	}

	public static boolean changeDirectory(FileHelper fileHelper, String path){
		//String path=params.getString("filePath");
		if(StringUtils.isBlank(path)){
			return true;
		}
		if(path.startsWith("/") || path.indexOf("./") > 0) 
			throw new BaseException("Param filePath includes illegal characters");
		
		try {
			fileHelper.changeDirectory(path);
		} catch (BaseException e) {
			return false;
		}
		return true;
	}
	
	public static boolean createDirectory(FileHelper fileHelper, String path){
		//目录创建 开关
		if(!isAutoCreatePath) return false;
		
		if(StringUtils.isBlank(path)){
			return true;
		}

		if(path.startsWith("/") || path.indexOf("./") > 0) 
			throw new BaseException("Param filePath includes illegal characters");
		String[] dirs = path.split("/");
		boolean result = true;
		int count = 1;
		for(String dir : dirs){
			if( !dir.equals("") ){
				boolean mark = false;
				try {
					mark = fileHelper.changeDirectory(dir);
				} catch (BaseException e) {
					mark = false;
				}
				if(count > maxDirs) break;
				if(!mark){
					result = fileHelper.createDirectory(dir);
					result = fileHelper.changeDirectory(dir);	
				}
				count++;
			}
		}
		return result;
	}

	public void setVisit(IVisit visit) {
		visitLocal.set(visit);
	}

	public IVisit getVisit() {
		return visitLocal.get();
	}
	
}
