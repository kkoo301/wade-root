package com.ailk.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.ailk.common.BaseException;

public final class FtpUtil {
	
	private static final Logger log = Logger.getLogger(FtpUtil.class);
    public static final int FILE_TYPE_BINARY = FTP.BINARY_FILE_TYPE;
    public static final int FILE_TYPE_ASCII = FTP.ASCII_FILE_TYPE;
    
	protected FTPClient client;
	
	private String ftpserver;
	private int ftpport;
	private String ftpuser;
	private String ftppasswd;
	private String ftppath;
	
	/**
	 * construct function
	 * @param server
	 * @param user
	 * @param password
	 * @throws Exception
	 */
	public FtpUtil(String server, String user, String password){
		this(server, 21, user, password, null);
	}
	
	/**
	 * construct function
	 * @param server
	 * @param user
	 * @param password
	 * @param path
	 * @throws Exception
	 */
	public FtpUtil(String server, String user, String password, String path){
		this(server, 21, user, password, path);
	}
	
	/**
	 * construct function
	 * @param server
	 * @param port
	 * @param user
	 * @param password
	 * @param path
	 * @throws Exception
	 */
	public FtpUtil(String server, int port, String user, String password, String path){
		ftpserver = server;
		ftpport = port;
		ftpuser = user;
		ftppasswd = password;
		ftppath = path;
		
		client = new FTPClient();
		connectServer(ftpserver, ftpport, ftpuser, ftppasswd, ftppath);
	}
	
	/**
	 * connect server
	 * @param server
	 * @param port
	 * @param user
	 * @param password
	 * @param path
	 * @throws Exception
	 */
	protected void connectServer(String server, int port, String user, String password, String path){
		if(log.isDebugEnabled())
			log.debug("ftp>connected to " + server + ".");
		
		try {
			client.connect(server, port);
		} catch (SocketException e) {
			throw new BaseException("FTP网络连接异常", e);
		} catch (IOException e) {
			throw new BaseException("FTP网络连接中断", e);
		}
		if(log.isDebugEnabled())
			log.debug("ftp>connection reply : " + client.getReplyCode());
        
    	boolean loginrs;
		try {
			loginrs = client.login(user, password);
		} catch (IOException e) {
			throw new BaseException("FTP登陆异常", e);
		}
        if (loginrs) {
        	client.enterLocalPassiveMode();
        	if(log.isDebugEnabled())
        		log.debug("ftp>login successful.");
        } else {
        	throw new BaseException("FTP登陆异常Passive模式");
        }
        
        if(log.isDebugEnabled())
        	log.debug("ftp>change working directory :" + path);
        
        if (path != null && !"".equals(path)) {
        	boolean changed = false;
        	try {
        		changed = client.changeWorkingDirectory(path);
        		if (!changed) {
        			log.debug("ftp>change working directory error, current is " + client.printWorkingDirectory());
        		} else {
        			if (log.isDebugEnabled()) {
        				log.debug("ftp>change working directory ok, current is " + client.printWorkingDirectory());
        			}
        		}
			} catch (IOException e) {
				throw new BaseException("FTP目录切换异常[" + path + "]", e);
			}
        }
    }
	
	/**
	 * set file type
	 * @param fileType
	 * @throws Exception
	 */
	public void setFileType(int fileType){
		if(log.isDebugEnabled())
			log.debug("ftp>set " + (fileType == FILE_TYPE_ASCII ? "assii" : "binary") + " file type.");
		try {
			client.setFileType(fileType);
		} catch (IOException e) {
			throw new BaseException("FTP传输过程中设置文件类型异常", e);
		}
	}
	
	/**
	 * close server
	 * @throws Exception
	 */
	public void closeServer(){
		if (client.isConnected()) {
			try {
				client.disconnect();
			} catch (IOException e) {
				throw new BaseException("FTP关闭连接时异常", e);
			} finally {
				if(log.isDebugEnabled())
					log.debug("ftp>close " + ftpserver + "...");
			}
		}
    }
	
	public boolean changeParentDirectory() {
		boolean result;
		try {
			result = client.changeToParentDirectory();
			
			if (result) {
				if(log.isDebugEnabled()) {
					log.debug("ftp>change parent directory ok.");
					log.debug("ftp>current directory [" + client.printWorkingDirectory() + "]");
				}
			} else {
				if(log.isDebugEnabled()) {
					log.debug("ftp>change parent directory error.");
					log.debug("ftp>current directory [" + client.printWorkingDirectory() + "]");
				}
				throw new BaseException("ftputil-10007");
			}
			return result;
		} catch (IOException e) {
			throw new BaseException("FTP切换到上一级目录异常", e);
		} finally {
			
		}
	}
	
	/**
	 * change directory
	 * @param path
	 * @return boolean
	 * @throws Exception
	 */
	public boolean changeDirectory(String path){
		boolean result;
		try {
			result = client.changeWorkingDirectory(path);
			
			if (result) {
				if(log.isDebugEnabled()) {
					log.debug("ftp>change directory [" + path + "] ok.");
					log.debug("ftp>current directory [" + client.printWorkingDirectory() + "]");
				}
			} else {
				if(log.isDebugEnabled()) {
					log.debug("ftp>change directory [" + path + "] error.");
					log.debug("ftp>current directory [" + client.printWorkingDirectory() + "]");
				}
				throw new BaseException("FTP切换目录异常[" + path + "]");
			}
			return result;
		} catch (IOException e) {
			throw new BaseException("FTP切换目录时网络异常", e);
		} finally {
			
		}
	}
    
    /**
     * create directory
     * @param path
     * @return boolean
     * @throws Exception
     */
    public boolean createDirectory(String path){
    	boolean result;
		try {
			result = client.makeDirectory(path);
			if (result) {
				if(log.isDebugEnabled())
					log.debug("ftp>create directory [" + path + "].");
			} else {
				throw new BaseException("FTP创建目录时异常[" + path + "]");
			}
			return result;
		} catch (IOException e) {
			throw new BaseException("FTP创建目录时网络异常[" + path + "]", e);
		}
    }
    
    /**
     * remote directory
     * @param path
     * @return boolean
     * @throws Exception
     */
    public boolean removeDirectory(String path){
    	boolean result;
		try {
			result = client.removeDirectory(path);
			if (result) {
				if(log.isDebugEnabled())
					log.debug("ftp>remove directory [" + path + "].");
			} else {
				throw new BaseException("FTP删除目录时异常[" + path + "]");
			}
			return result;
		} catch (IOException e) {
			throw new BaseException("FTP删除目录时网络异常[" + path + "]", e);
		}
    }
    
    /**
     * remove directory
     * @param path
     * @param isall
     * @return boolean
     * @throws Exception
     */
    public boolean removeDirectory(String path, boolean isall){
    	if (!isall) return removeDirectory(path);
    	
    	FTPFile[] files;
		try {
			files = client.listFiles(path);
		} catch (IOException e) {
			throw new BaseException("FTP删除目录时异常[" + path + "][" + isall + "]", e);
		}
    	if (files == null || files.length == 0) return removeDirectory(path);
    	if(log.isDebugEnabled())
    		log.debug("ftp>remove directory [" + path + "] and sub directory.");
    	
    	for (FTPFile ftpfile : files) {
    		String name = ftpfile.getName();
    		if (ftpfile.isDirectory()) {
    			removeDirectory(path + "/" + name, true);
    		} else if (ftpfile.isFile()) {
    			deleteFile(path + "/" + name);
    		}
    	}
    	
    	try {
			return client.removeDirectory(path);
		} catch (IOException e) {
			throw new BaseException("FTP删除目录时异常[" + path + "][" + isall + "]", e);
		}
    }
    
    /**
     * get file list
     * @param path
     * @return List
     * @throws Exception
     */
    public List<String> getFileList(String path){   
        FTPFile[] files;
		try {
			files = client.listFiles(path);
		} catch (IOException e) {
			throw new BaseException("FTP获取文件列表异常[" + path + "]", e);
		}
        
        List<String> list = new ArrayList<String>();
        if (files == null || files.length == 0) {
            return list;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
            	list.add(files[i].getName());
            }
        }
        
        return list;
    } 
    
    /**
     * delete file
     * @param remotePathName
     * @return boolean
     * @throws Exception
     */
    public boolean deleteFile(String remotePathName){
    	boolean result;
		try {
			result = client.deleteFile(remotePathName);
			if (result) {
				if(log.isDebugEnabled())
					log.debug("ftp>delete file [" + remotePathName + "].");
			} else {
				throw new BaseException("FTP删除文件时异常[" + remotePathName + "]");
			}
			return result;
		} catch (IOException e) {
			throw new BaseException("FTP删除文件时网络异常[" + remotePathName + "]", e);
		}
    }
    
    /**
     * upload file
     * @param localFilePath
     * @return boolean
     * @throws Exception
     */
	public boolean uploadFile(String localFilePath){
		String fileName = FileManHelper.getFileName(localFilePath);
		if(log.isDebugEnabled())
			log.debug("ftp>ready upload file [" + localFilePath + "] to [" + fileName + "]...");
		
		FileInputStream in;
		try {
			in = new FileInputStream(localFilePath);
		} catch (FileNotFoundException e) {
			throw new BaseException("FTP上传文件时找不到源文件[" + localFilePath + "]", e);
		}
		return uploadFile(in, fileName);
	}
    
    /**
     * upload file
     * @param localFilePath
     * @param remoteFileName
     * @return boolean
     * @throws Exception
     */
	public boolean uploadFile(String localFilePath, String remoteFileName){
		
		if(log.isDebugEnabled())
			log.debug("ftp>ready upload file [" + localFilePath + "] to [" + remoteFileName + "]...");
		FileInputStream in;
		try {
			in = new FileInputStream(localFilePath);
		} catch (FileNotFoundException e) {
			throw new BaseException("FTP上传文件时找不到源文件[" + localFilePath + "][" + remoteFileName + "]", e);
		}
		return uploadFile(in, remoteFileName);
	}
    
	/**
	 * upload file
	 * @param in
	 * @param remoteFileName
	 * @return boolean
	 * @throws Exception
	 */
	public boolean uploadFile(InputStream in, String remoteFileName){   
		boolean result = false;
		try {
			result = client.storeFile(remoteFileName, in);
		} catch (Exception e) {
			throw new BaseException("FTP上传文件时异常[" + remoteFileName + "]", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				throw new BaseException("FTP上传文件后关闭文件流异常[" + remoteFileName + "]", e);
			}
	    	if (result) {
	    		if(log.isDebugEnabled())
	    			log.debug("ftp>upload file to [" + remoteFileName + "].");
	    	}
		}
		return result;
	}
	
	/**
	 * download file
	 * @param remoteFileName
	 * @param localFilePath
	 * @return boolean
	 * @throws Exception
	 */
	public boolean downloadFile(String remoteFileName, String localFilePath){
		if(log.isDebugEnabled())
			log.debug("ftp>ready download file [" + remoteFileName + "] to [" + localFilePath + "]...");
		File file = new File(localFilePath);
		try {
			return downloadFile(remoteFileName, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new BaseException("FTP下载文件时找不到文件[" + localFilePath + "]", e);
		}
	}
	
	/**
	 *  get file bytes from remote 
	 *  
	 * @param remoteFileName
	 * @param startPos
	 * @param endPos
	 * @return
	 */
	public void getBinaryFileStream(String remoteFileName, OutputStream os,long startPos,long endPos){
		InputStream in = null;
		try {
			setFileType(FILE_TYPE_BINARY);
			FTPFile[] ftpFiles = client.listFiles(remoteFileName);
			if(ftpFiles != null && ftpFiles.length > 0) {
				FTPFile ftpFile = ftpFiles[0];
				long fileSize = ftpFile.getSize();
				if (startPos <= fileSize) {
					if (endPos > fileSize){
						endPos = fileSize;
					}
					if (endPos < 0) {
						endPos = fileSize;
					}
					client.setRestartOffset(startPos);
					in = client.retrieveFileStream(remoteFileName);
					byte [] a = new byte[1024];
					long downloaded = 0;
					long byteReaded = 0;
					long blockSize = endPos-startPos;
					while ((byteReaded = in.read(a)) != -1) {
						downloaded += byteReaded;
						if (downloaded > blockSize && blockSize >= 0) {
							byteReaded = byteReaded - (downloaded - blockSize);
						}
						os.write(a, 0, (int)byteReaded);
						if (downloaded > blockSize && blockSize >= 0) {
							break;
						}
					}
				}else{
					if(log.isDebugEnabled())
		    			log.debug("ftp>startPosition is greater than the remote file size  [" + remoteFileName + "].");
				}
			}else{
				// 远程文件不存在
				throw new BaseException("FTP获取文件二进制流异常");
			}
		} catch (IOException e) {
			throw new BaseException("FTP获取文件二进制流网络异常", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				throw new BaseException("FTP获取文件二进制流关闭异常", e);
			}
		}
	}
	
	/**
	 * download file
	 * @param remoteFileName
	 * @param out
	 * @return boolean
	 * @throws Exception
	 */
	public boolean downloadFile(String remoteFileName, OutputStream out){
		boolean result = false;
		try {
			result = client.retrieveFile(remoteFileName, out);
		} catch (Exception e) {
			throw new BaseException("FTP文件下载异常[" + remoteFileName + "]", e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				throw new BaseException("FTP文件下载关闭输出流异常[" + remoteFileName + "]", e);
			}
	    	if (result) {
	    		if (log.isDebugEnabled())
	    			log.debug("ftp>download file[" + remoteFileName + "].");
	    	}
		}
		return result;
	}
	
	/**
	 * get file stream
	 * @param remoteFileName
	 * @return InputStream
	 * @throws Exception
	 */
	public InputStream getFileStream(String remoteFileName){
		InputStream in = null;
		try {
			in = client.retrieveFileStream(remoteFileName);
		} catch (Exception e) {
			throw new BaseException("FTP获取文件流异常[" + remoteFileName + "]", e);
		} finally {
	    	if (in != null) {
	    		if(log.isDebugEnabled())
	    			log.debug("ftp>download file stream from [" + remoteFileName + "].");
	    	} else {
	    		throw new BaseException("FTP获取文件流异常[" + remoteFileName + "]");
	    	}
		}
		return in;
	}
	
	/**
	 * store file stream
	 * @param remoteFileName
	 * @return OutputStream
	 * @throws Exception
	 */
	public OutputStream storeFileStream(String remoteFileName){
		if(log.isDebugEnabled())
			log.debug("ftp>store file stream from [" + remoteFileName + "].");
		
		try {
			return client.storeFileStream(remoteFileName);
		} catch (IOException e) {
			throw new BaseException("FTP存入文件异常[" + remoteFileName + "]", e);
		}
	}
	
	/**
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean rename(String from, String to) {
		if(log.isDebugEnabled())
			log.debug("ftp>rename file from [" + from + "] to [" + to + "].");
		
		try {
			return client.rename(from, to);
		} catch (IOException e) {
			throw new BaseException("FTP重命名异常["+ from + "]", e);
		}
	}
	
}
