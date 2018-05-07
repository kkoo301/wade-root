package com.ailk.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import com.ailk.common.BaseException;
import com.ailk.common.config.GlobalCfg;

public class FileManHelper {


	public static final String FILE_OPERMODE_SIMPLE = "simple";
	public static final String FILE_OPERMODE_FTP = "ftp";
	
	public static final String UPLOAD_TYPE_EXPORT_FAIL = "6";
	
	public static final String UPLOAD_KIND_USER = "1";
	public static final String UPLOAD_KIND_SYSTEM = "2";
	
	public static final String FILE_TYPE_JPEG = "JPEG";
	public static final String FILE_TYPE_JPG = "JPG";
	public static final String FILE_TYPE_GIF = "GIF";
	public static final String FILE_TYPE_PNG = "PNG";
	public static final String FILE_TYPE_DOC = "DOC";
	public static final String FILE_TYPE_XLS = "XLS";
	public static final String FILE_TYPE_PPT = "PPT";
	public static final String FILE_TYPE_PDF = "PDF";
		            
	public static final String CONTENT_TYPE_IMAGE_JPEG = "image/jpeg";
	public static final String CONTENT_TYPE_IMAGE_GIF = "image/gif";
	public static final String CONTENT_TYPE_IMAGE_PNG = "image/png";
	public static final String CONTENT_TYPE_WORD = "application/vnd.msword";
	public static final String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
	public static final String CONTENT_TYPE_POWERPOINT = "application/vnd.ms-powerpoint";
	public static final String CONTENT_TYPE_PDF = "application/pdf";
	
	/** 
	 * get oper mode
	 * @return String
	 * @throws Exception
	 */
	public static String getOperMode(){
		return GlobalCfg.getProperty("fileman/default", FILE_OPERMODE_SIMPLE);
	}
	
	/**
  	 * get upload type
  	 * @param upload_type
  	 * @return String
  	 */
  	public static String getUploadPath(String upload_type) {
		if (IFileAction.UPLOAD_TYPE_ATTACH.equals(upload_type)) return "attach";
		if (IFileAction.UPLOAD_TYPE_IMAGE.equals(upload_type)) return "image";
		if (IFileAction.UPLOAD_TYPE_EXPORT.equals(upload_type)) return "export";
		if (IFileAction.UPLOAD_TYPE_IMPORT.equals(upload_type)) return "import";
		if (IFileAction.UPLOAD_TYPE_TEMP.equals(upload_type)) return "temp";
		return null;
  	}
	
	/**
	 * flush output
	 * @param out
	 * @throws Exception
	 */
    private static void flush(OutputStream out){
        try {
        	out.flush();
        } catch (IOException e) {
        	throw new BaseException("filemanhelper-10000", e);
        }
    }

  	/**
  	 * get content type
  	 * @param file_type
  	 * @return String
  	 */
  	public static String getContentType(String file_type) {
		String content_type = null;
		
		if (FILE_TYPE_JPG.equals(file_type) || FILE_TYPE_JPEG.equals(file_type)) content_type = CONTENT_TYPE_IMAGE_JPEG;
		if (FILE_TYPE_GIF.equals(file_type)) content_type = CONTENT_TYPE_IMAGE_GIF;
		if (FILE_TYPE_PNG.equals(file_type)) content_type = CONTENT_TYPE_IMAGE_PNG;
		
		if (FILE_TYPE_DOC.equals(file_type)) content_type = CONTENT_TYPE_WORD;
		if (FILE_TYPE_XLS.equals(file_type)) content_type = CONTENT_TYPE_EXCEL;
		if (FILE_TYPE_PPT.equals(file_type)) content_type = CONTENT_TYPE_POWERPOINT;
		
		if (FILE_TYPE_PDF.equals(file_type)) content_type = CONTENT_TYPE_PDF;
		
		return content_type;
  	}

  	/**
  	 * get main file name
  	 * @param file_name
  	 * @return String
  	 */
  	public static String getMainFileName(String file_name) {
  		if (file_name.lastIndexOf(".") == -1) return file_name;
  		return file_name.substring(0, file_name.lastIndexOf("."));
  	}

  	/**
  	 * get expand file name
  	 * @param file_name
  	 * @return String
  	 */
  	public static String getExpandFileName(String file_name) {
  		if (file_name.lastIndexOf(".") == -1) return null;
  		return file_name.substring(file_name.lastIndexOf(".") + 1, file_name.length());
  	}
  	
  	/**
  	 * get file type
  	 * @param file_name
  	 * @return String
  	 */
  	public static String getFileType(String file_name) {
  		if (file_name.lastIndexOf(".") == -1) return null;
  		String file_type = file_name.substring(file_name.lastIndexOf(".") + 1, file_name.length());
  		return file_type.toUpperCase();
  	}
  	
  	/**
  	 * get content type by file name
  	 * @param file_name
  	 * @return String
  	 */
  	public static String getContentTypeByFileName(String file_name) {
  		return getContentType(getFileType(file_name));
  	}

	/**
	 * get file name
	 * @param file_name
	 * @return String
	 */
	public static String getFileName(String file_name) {
		file_name = file_name.replaceAll("\\\\", "/");
		int index = file_name.lastIndexOf("/");
		return index == -1 ? file_name : file_name.substring(index + 1);	
	}
	
	/**
	 * get file path
	 * @param file_name
	 * @return String
	 */
	public static String getFilePath(String file_name) {
		file_name = file_name.replaceAll("\\\\", "/");
		int index = file_name.lastIndexOf("/");
		return index == -1 ? null : file_name.substring(0, index);
	}
  	
    /**
     * get file list
     * @param path
     * @return File[]
     * @throws Exception
     */
    public static File[] getFileList(String path){
		File file = new File(path);
		return file.exists() ? file.listFiles() : null;
    }

    /**
     * delete file
     * @param file
     * @throws Exception
     */
    public static void deleteFiles(File file){
        if (file.exists()) {
	    	if (file.isDirectory()) {
            	File[] fileList = file.listFiles();
          		for (int i=0; i<fileList.length; i++) {
            		deleteFiles(fileList[i]);
          		}
			} else {
				file.delete();
	    	}
	    	file.delete();
	  	}
    }
    
	/**
	 * write the input stream to the output stream
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	public static void writeInputToOutput(InputStream in, OutputStream out){
		writeInputToOutput(in, out, false);
	}
	
	/**
	 * write the input stream to the output stream
	 * @param in
	 * @param out
	 * @param persist
	 * @throws Exception
	 */
	public static void writeInputToOutput(InputStream in, OutputStream out, boolean ispersist){
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		int len = -1;
		/* if no arrive the end(len is -1) in the data stream then write */
		try {
			while((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
				flush(out);
			}
		} catch (IOException e) {
			throw new BaseException("filemanhelper-10003", e);
		} finally {
			if (!ispersist) {
				try {
					in.close();
				} catch (IOException e) {
					throw new BaseException("filemanhelper-10004", e);
				}
				try {
					out.close();
				} catch (IOException e) {
					throw new BaseException("filemanhelper-10005", e);
				}
			}
			
		}
	}
  	
    /**
     * get file
     * @param full_name
     * @return File
     * @throws Exception
     */
	public static File getFile(String full_name){
    	File file = new File(full_name);
		if (!file.exists()) throw new BaseException("filemanhelper-10006");
		return file;
    }
	
	public static File getBinaryFile(String file_path, long startPos, long endPos){
		OutputStream os = null;
		File tempFile = null;
		RandomAccessFile file = null;
		try {
			tempFile = new File(file_path + "_" + System.currentTimeMillis() + "_" + Math.random());
			os = new FileOutputStream(tempFile);
			file = new RandomAccessFile(new File(file_path), "rw");
			file.seek(startPos);
			byte [] a = new byte[1024];
			long downloaded = 0;
			long byteReaded = 0;
			long blockSize = endPos-startPos;
			while((byteReaded = file.read(a))!=-1){
				downloaded += byteReaded;
				if(downloaded > blockSize && blockSize >= 0){
					byteReaded = byteReaded - (downloaded-blockSize);
				}
				os.write(a, 0, (int)byteReaded);
				if(downloaded>blockSize&&blockSize>=0){
					break;
				}
			}
		} catch (FileNotFoundException e) {
			throw new BaseException("filemanhelper-10016");
		} catch (IOException e) {
			throw new BaseException("filemanhelper-10019");
		}finally{
			if(file!=null){
				try {
					file.close();
				} catch (IOException e) {
					throw new BaseException("filemanhelper-10021");
				}
			}
			if(os!=null){
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					throw new BaseException("filemanhelper-10020");
				}
			}
		}
		return tempFile ;
	}
	
	public static InputStream getInputStream(File file){
    	InputStream in = null;
    	try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Utility.getBottomException(e).printStackTrace();
			throw new BaseException("filemanhelper-10018");
		}
    	return in;
    }
	/**
     * get file outputstream by filePath
     * @param filePath
     * @return
     */
    public static OutputStream getOutputStream(File file){
    	OutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			Utility.getBottomException(e).printStackTrace();
			throw new BaseException("filemanhelper-10017");
		}	
    	return out;
    }
    
    /**
     * delete file
     * @param full_name
     * @return boolean
     * @throws Exception
     */
    public static boolean deleteFile(String full_name){
    	File file = new File(full_name);
    	if (file.exists()) return file.delete();
    	return false;
    }
    
    /**
     * delete file
     * @param file_path
     * @param file_name
     * @return boolean
     * @throws Exception
     */
    public static boolean deleteFile(String file_path, String file_name){
    	File file = new File(file_path, file_name);
    	if (file.exists()) return file.delete();
    	return false;
    }
    
	/** 
	 * write object
	 * @param file
	 * @param out
	 * @throws Exception
	 */
	public static void writeObject(OutputStream out, Object obj){
		ObjectOutputStream oout = null;
		try {
			oout = new ObjectOutputStream(out);
			oout.writeObject(obj);
		} catch (IOException e) {
			throw new BaseException("filemanhelper-10009", e);
		} finally {
			try {
				if (out != null)
					out.close();
				if (oout != null)
					oout.close();
			} catch (IOException e) {
				throw new BaseException("filemanhelper-10010", e);
			}
		}
	}
    
	/** 
	 * write object
	 * @param file
	 * @param obj
	 * @throws Exception
	 */
	public static void writeObject(File file, Object obj){
		try {
			writeObject(new FileOutputStream(file), obj);
		} catch (FileNotFoundException e) {
			throw new BaseException("filemanhelper-10011", e);
		}
	}
	
	/** 
	 * write object
	 * @param file_name
	 * @param obj
	 * @throws Exception
	 */
	public static void writeObject(String file_name, Object obj){
		writeObject(new File(file_name), obj);
	}

	/** 
	 * write object
	 * @param in
	 * @return Object
	 * @throws Exception
	 */
	public static Object readObject(InputStream in){
		ObjectInputStream oin = null;
		try {
			oin = new ObjectInputStream(in);
			Object obj = oin.readObject();
			return obj;
		} catch (IOException e) {
			throw new BaseException("filemanhelper-10012", e);
		} catch (ClassNotFoundException e){
			throw new BaseException("filemanhelper-10013", e);
		} finally {
			try {
				if (in != null)
					in.close();
				if (oin != null) 
					oin.close();
			} catch (IOException e) {
				throw new BaseException("filemanhelper-10014", e);
			}
		}
	}
	
	/** 
	 * write object
	 * @param file
	 * @return Object
	 * @throws Exception
	 */
	public static Object readObject(File file){
		try {
			return readObject(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new BaseException("filemanhelper-10015", e);
		}
	}
	
	/** 
	 * write object
	 * @param file_name
	 * @return Object
	 * @throws Exception
	 */
	public static Object readObject(String file_name){
		File file = getFile(file_name);
		return readObject(file);
	}
	
	/**
	 * create directory
	 * @param file_path
	 * @return boolean
	 * @throws Exception
	 */
    public static boolean createDirectory(String file_path){	    	
    	File dir = new File(file_path);
		if (!dir.exists()) return dir.mkdir();
		return true;
  	}
	
    /**
     * remove directory
     * @param file_path
     * @param isall
     * @return boolean
     * @throws Exception
     */
    public static boolean removeDirectory(String file_path, boolean isall){
    	File file = new File(file_path);
    	if (!file.exists()) return false;
    	
    	if (isall) {
	    	File[] fileList = file.listFiles();
	    	for (int i = 0; i < fileList.length; i++) {
	    		File fileItem = fileList[i];
	    		if (fileItem.isDirectory()) {
	    			removeDirectory(fileItem.getPath(), isall);
	    		} else {
	    			fileItem.delete();
	    		}
	    	}
    	}
	    
	    return file.delete();
    }
  	
}