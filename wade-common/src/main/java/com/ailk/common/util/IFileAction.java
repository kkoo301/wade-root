package com.ailk.common.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IVisit;

public interface IFileAction {
	
	public static final int FILE_MAX_SIZE = GlobalCfg.getFileMaxSize() * 1024 * 1024;
	public static final int FILE_THRESHOLD_SIZE = 300 * 1024;
	
	public static final String UPLOAD_TYPE_ATTACH = "1";
	public static final String UPLOAD_TYPE_IMAGE = "2";
	public static final String UPLOAD_TYPE_EXPORT = "3";
	public static final String UPLOAD_TYPE_IMPORT = "4";
	public static final String UPLOAD_TYPE_TEMP = "5";
	public static final String UPLOAD_TYPE_EXPORT_FAIL = "6";
	public static final String UPLOAD_TYPE_IMPORT_FAIL = "7";
	
	public static final String UPLOAD_PATH = "";//"upload";
	public static final String UPLOAD_ATTACH = "";//UPLOAD_PATH+"/attach";
	public static final String UPLOAD_IMAGE = "";//UPLOAD_PATH+"/image";
	public static final String UPLOAD_EXPORT = "";//UPLOAD_PATH+"/export";
	public static final String UPLOAD_IMPORT = "";//UPLOAD_PATH+"/import";
	public static final String UPLOAD_TEMP = "";//UPLOAD_PATH+"/temp";
	
	public static final String UPLOAD_KIND_USER = "1";
	public static final String UPLOAD_KIND_SYSTEM = "2";
	
	/**
	 * 创建文件ID
	 * 
	 * @return
	 * @throws Exception
	 */
	public String createFileId() throws Exception;
	/**
	 * 设置IVisit对象
	 * 
	 * @param visit
	 */
	public void setVisit(IVisit visit);
	
	/**
	 *	获取IVisit对象
	 *
	 * @return
	 */
	public IVisit getVisit();
	
	/**
	 * 删除的文件信息
	 * 
	 * @param fileId 
	 */
	public void removeFileInfo(String fileId);
	
	/**
	 * 添加上传文件信息
	 * 
	 * @param params 记录文件上传的信息，如：fileId,ftpSite,filePath,fileName
	 */
	public void addFileInfos(IData params);
	
	/**
	 * 根据文件ID查找文件上传时的参数信息
	 * 返回的数据结构必须是{fileId:{fileId:xx,fileName:xx,fileSize:xx,fileType:xx}},否则解析失败
	 * @param fileId 文件id
	 * @return
	 */
	public Map<String, Object> query(String fileId) throws Exception;
	
	/**
	 * 查询显示已上传的文件列表，多个fileId以“,”隔开
	 * 
	 * 返回的数据结构必须是<String,IData>,否则解析失败
	 * @param fileIds 文件列表 以,分隔
	 * @return
	 */
	public Map<String, Object> querys(String fileIds) throws Exception;
	
	/**
	 * 删除指定的文件，返回未删除成功的fileid集合
	 * @param fileIds 文件列表 以,分隔
	 * @throws Exception
	 */
	public List<String> deletes(String fileIds) throws Exception;
	public List<String> deletes(String fileIds, boolean isNeedSuffix) throws Exception;
	
	/**
	 * 根据文件ID删除,成功返回true
	 * @param fileIds 文件标识
	 * @throws Exception
	 */
	public boolean delete(String fileId) throws Exception;
	
	/**
	 * 根据参数配置将数据流生成文件并上传到服务器，成功返回fileId
	 * @param input  待上传文件流
	 * @param ftpSite FTP站点
	 * @param filePath 自定义上传路径
	 * @param fileName 上传文件的文件名
	 * @throws Exception
	 */
	public String upload(InputStream input, String ftpSite, String filePath, String fileName) throws Exception;
	
	public String upload(InputStream input, String ftpSite, String filePath, String fileName, boolean isNeedSuffix) throws Exception;
	
	/**
	 * 根据参数配置将数据流生成文件并上传到服务器，成功返回fileId
	 * @param input  待上传文件流
	 * @param fileId 自定义fileId
	 * @param ftpSite FTP站点
	 * @param filePath 自定义上传路径
	 * @param fileName 上传文件的文件名
	 * @throws Exception
	 */
	public String upload(InputStream input, String fileId, String ftpSite, String filePath, String fileName, boolean isNeedSuffix) throws Exception;
	
	public String upload(InputStream input, String fileId, String ftpSite, String filePath, String fileName) throws Exception;
	
	/**
	 * 指定fileId，复制文件到其它站点中
	 * @param fileId
	 * @param isNeedSuffix
	 * @param dstFtpSites
	 * @param dstFilePath
	 * @return
	 * @throws Exception
	 */
	public boolean remoteCopyFile(String fileId, boolean isNeedSuffix, String[] dstFtpSites, String[] dstFilePath) throws Exception;

	/**
	 * 复制inputStream中的文件流到其它站点中
	 * @param input
	 * @param fileId
	 * @param fileName
	 * @param isNeedSuffix
	 * @param dstFtpSites
	 * @param dstFilePath
	 * @return
	 * @throws Exception
	 */
	public boolean copyFile(InputStream input, String fileId, String fileName, boolean isNeedSuffix, String[] dstFtpSites, String[] dstFilePath) throws Exception;
	
	/**
	 * 根据文件ID从服务器获取对应的文件
	 * 
	 * @param fileId 文件id
	 * @throws Exception
	 */
	public File download(String fileId) throws Exception;
	
	public File download(String fileId, boolean isNeedSuffix) throws Exception;
	
	/**
	 * 从服务器获取文件流片段
	 * 
	 * @param out
	 * @param file_path 自定义fileId
	 * @param startPos 文件下载开始位置
	 * @param endPos 文件下载结束位置
	 * @throws Exception
	 */
	public void downloadBinary(OutputStream out, String fileId, long startPos, long endPos) throws Exception;
		
	/**
	 * 限制上传文件的最大值，默认是FILE_MAX_SIZE(30M)
	 * @return
	 */
	public int getMaxSize();
	
	/**
	 * 获取单个文件存在内存中的阀值大小，默认是FILE_THRESHOLD_SIZE(300kb)
	 * @return
	 */
	public int getThresholdSize();

	/**
	 * 获取文件临时存放的磁盘目录，默认是System.getProperty("java.io.tmpdir");
	 * @return
	 */
	public String getTempRepository();
}
