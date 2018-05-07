package com.ailk.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.util.impl.DefaultFileAction;

public class FileHelper {

	private String		opertype;
	private FtpUtil		ftputil;
	private boolean		autorelease	= true;
	private transient static final Logger log = Logger.getLogger(FileHelper.class);
	protected static IFileAction action = null;
	
	public FileHelper(String name) {
		if (name == null || "".equals(name)) {
			opertype = FileManHelper.FILE_OPERMODE_SIMPLE;
		} else {
			opertype = FileManHelper.FILE_OPERMODE_FTP;

			String server = GlobalCfg.getProperty("fileman.ftp." + name + ".server");
			String port = GlobalCfg.getProperty("fileman.ftp." + name + ".port", "21");
			String username = GlobalCfg.getProperty("fileman.ftp." + name + ".username");
			String password = GlobalCfg.getProperty("fileman.ftp." + name + ".password");
			String homepath = GlobalCfg.getProperty("fileman.ftp." + name + ".path", "");
			ftputil = new FtpUtil(server, Integer.parseInt(port), username, password, homepath);
			ftputil.setFileType(FtpUtil.FILE_TYPE_BINARY);
		}
	}
	
	public static IFileAction getFileAction(){
		if(action == null){
			synchronized(FileHelper.class){
				if(action == null){
					String actionClazz = GlobalCfg.getFileAction();
					try {
						if ("".equals(actionClazz) || actionClazz == null)
							throw new ClassNotFoundException("action is empty");

						Class<?> clazz = FileHelper.class.getClassLoader().loadClass(actionClazz);
						action = (IFileAction) clazz.newInstance();

					} catch (ClassNotFoundException e) {
						if (log.isInfoEnabled())
							log.info("init file action [" + actionClazz + "], use DefaultFileAction.");
						action = new DefaultFileAction();
					} catch (InstantiationException e) {
						Utility.getBottomException(e).printStackTrace();
					} catch (IllegalAccessException e) {
						Utility.getBottomException(e).printStackTrace();
					}
					
					if (log.isDebugEnabled())
						log.debug("file helper init action " + action.getClass().getName());
				}
			}
		}
		return action;
	}	
	
	public FileHelper(IData config) {
		if (config == null) {
			opertype = FileManHelper.FILE_OPERMODE_SIMPLE;
		} else {
			opertype = FileManHelper.FILE_OPERMODE_FTP;

			String server = config.getString("FTP_SERVER");
			String port = config.getString("FTP_PORT", "21");
			String username = config.getString("ACCT_USR");
			String password = config.getString("ACCT_PWD");
			String homepath = config.getString("ROOT_PATH");
			ftputil = new FtpUtil(server, Integer.parseInt(port), username, password, homepath);
			ftputil.setFileType(FtpUtil.FILE_TYPE_BINARY);
		}
	}

	/**
	 * construct function
	 * 
	 * @throws Exception
	 */
	public FileHelper() {
		
		this(GlobalCfg.getProperty("fileman.ftp.default"));
		/*opertype = GlobalCfg.getProperty("fileman/default", FileMan.FILE_OPERMODE_SIMPLE);
		if (FileMan.FILE_OPERMODE_FTP.equals(opertype)) {
			String server = GlobalCfg.getProperty("fileman/ftp/server");
			String port = GlobalCfg.getProperty("fileman/ftp/port", "21");
			String username = GlobalCfg.getProperty("fileman/ftp/username");
			String password = GlobalCfg.getProperty("fileman/ftp/password");
			String homepath = GlobalCfg.getProperty("fileman/ftp/homepath", "");
			ftputil = new FtpUtil(server, Integer.parseInt(port), username, password, homepath);
			ftputil.setFileType(FtpUtil.FILE_TYPE_BINARY);
		}*/
	}

	/**
	 * set auto release
	 * 
	 * @param autorelease
	 * @throws Exception
	 */
	public void setAutoRelease(boolean autorelease) {
		this.autorelease = autorelease;
	}

	/**
	 * create directory
	 * 
	 * @param file_path
	 * @return boolean
	 * @throws Exception
	 */
	public boolean createDirectory(String file_path) {
		if (ftputil != null) {
			try {
				return ftputil.createDirectory(file_path);
			} catch (Exception e) {
				throw new BaseException("filehelper-10000", e);
			} finally {
				if (autorelease)
					ftputil.closeServer();
			}
		} else {
			return FileManHelper.createDirectory(file_path);
		}
	}

	/**
	 * retrieve file
	 * 
	 * @param out
	 * @param file_path
	 * @throws Exception
	 */
	public void retrieveFile(OutputStream out, String remoteFileName) {
		if (ftputil != null) {
			try {
				ftputil.downloadFile(remoteFileName, out);
			} catch (Exception e) {
				throw new BaseException("filehelper-10001", e);
			} finally {
				if (autorelease)
					ftputil.closeServer();
			}
		} else {
			File file = FileManHelper.getFile(remoteFileName);
			try {
				FileManHelper.writeInputToOutput(new FileInputStream(file), out);
			} catch (FileNotFoundException e) {
				throw new BaseException("filehelper-10002", e);
			}finally{
				if(file != null && file.exists()){
					file.delete();
					if(file.exists()){
						file.delete();
					}
				}
			}
		}
	}
	
	/**
	 * retrieve file for multi point resume
	 * 
	 * @param out
	 * @param file_path
	 * @param startPos
	 * @param endPos
	 */
	public void retrieveBinaryFile(OutputStream out, String file_path, long startPos, long endPos){
		if (ftputil != null) {
			try {
				ftputil.getBinaryFileStream(file_path, out, startPos, endPos);
			} catch (Exception e) {
				throw new BaseException("filehelper-10015", e);
			} finally {
				if (autorelease)
					ftputil.closeServer();
			}
		} else {
			File tempFile = null;
			try {
				tempFile = FileManHelper.getBinaryFile(file_path, startPos, endPos);
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
		}
	}

	/**
	 * upload file
	 * 
	 * @param in
	 * @param file_path
	 * @throws Exception
	 */
	public void uploadFile(InputStream in, String file_path) {
		if (ftputil != null) {
			try {
				ftputil.uploadFile(in, file_path);
			} catch (Exception e) {
				throw new BaseException("filehelper-10003", e);
			} finally {
				if (autorelease)
					ftputil.closeServer();
			}
		} else {
			File file = new File(file_path);
			try {
				FileManHelper.writeInputToOutput(in, new FileOutputStream(file));
			} catch (FileNotFoundException e) {
				throw new BaseException("filehelper-10004", e);
			}
		}
	}

	/**
	 * delete file
	 * 
	 * @param file_path
	 * @return boolean
	 * @throws Exception
	 */
	public boolean deleteFile(String file_path) {
		if (ftputil != null) {
			try {
				return ftputil.deleteFile(file_path);
			} catch (Exception e) {
				throw new BaseException("filehelper-10006", e);
			} finally {
				if (autorelease)
					ftputil.closeServer();
			}
		} else {
			return FileManHelper.deleteFile(file_path);
		}
	}

	/**
	 * remove Directory
	 * 
	 * @param file_path
	 * @return boolean
	 * @throws Exception
	 */
	public boolean removeDirectory(String file_path) {
		return removeDirectory(file_path, false);
	}

	/**
	 * remove Directory
	 * 
	 * @param file_path
	 * @param isall
	 * @return boolean
	 * @throws Exception
	 */
	public boolean removeDirectory(String file_path, boolean isall) {
		if (ftputil != null) {
			try {
				return ftputil.removeDirectory(file_path, isall);
			} catch (Exception e) {
				throw new BaseException("filehelper-10007", e);
			} finally {
				if (autorelease)
					ftputil.closeServer();
			}
		} else {
			return FileManHelper.removeDirectory(file_path, isall);
		}
	}

	/**
	 * get file stream
	 * 
	 * @param filePath
	 * @return InputStream
	 * @throws Exception
	 */
	public InputStream getFileStream(String filePath) {
		if (ftputil != null) {
			try {
				return ftputil.getFileStream(filePath);
			} catch (Exception e) {
				throw new BaseException("filehelper-10008", e);
			} finally {
				if (autorelease)
					ftputil.closeServer();
			}
		} else {
			try {
				return new FileInputStream(FileManHelper.getFile(filePath));
			} catch (FileNotFoundException e) {
				throw new BaseException("filehelper-10009", e);
			}
		}
	}


	/**
	 * release resourse
	 * 
	 * @throws Exception
	 */
	public void releaseResourse() {
		if (ftputil != null) {
			ftputil.closeServer();
		}
	}

	/**
	 * write object
	 * 
	 * @param file_path
	 * @param obj
	 * @throws Exception
	 */
	public void writeObject(String file_path, Object obj) {
		if (ftputil != null) {
			try {
				OutputStream out = ftputil.storeFileStream(file_path);
				if (out != null)
					FileManHelper.writeObject(out, obj);
			} catch (Exception e) {
				throw new BaseException("filehelper-10013", e);
			} finally {
				if (autorelease)
					ftputil.closeServer();
			}
		} else {
			FileManHelper.writeObject(file_path, obj);
		}
	}

	/**
	 * read object
	 * 
	 * @param file_path
	 * @return Object
	 * @throws Exception
	 */
	public Object readObject(String file_path) {
		if (ftputil != null) {
			try {
				InputStream in = ftputil.getFileStream(file_path);
				if (in != null)
					return FileManHelper.readObject(in);
			} catch (Exception e) {
				throw new BaseException("filehelper-10014", e);
			} finally {
				if (autorelease)
					ftputil.closeServer();
			}
		} else {
			return FileManHelper.readObject(file_path);
		}
		return null;
	}
	
	/**
	 * change directory
	 * @param path
	 * @return boolean
	 */
	public boolean changeDirectory(String path) {
		if (ftputil != null) {
			return ftputil.changeDirectory(path);
		} 
		return false;
	}
	
}