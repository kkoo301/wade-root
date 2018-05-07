package com.wade.gateway.ftp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: Constants
 * @description: 常量集
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public interface Constants {
	
	public static final String SITE_ID = "SITE_ID";
	public static final String OP_KEY = "OP_KEY";
	
	public static final String OP_DOWNLOAD_FILE = "com.wade.gateway.ftp.server.op.impl.DownloadFile";
	public static final String OP_UPLOAD_FILE = "com.wade.gateway.ftp.server.op.impl.UploadFile";
	public static final String OP_DELETE_FILE = "com.wade.gateway.ftp.server.op.impl.DeleteFile";
	public static final String OP_MAKE_DIRECTORY = "com.wade.gateway.ftp.server.op.impl.MakeDirectory";
	public static final String OP_REMOVE_DIRECTORY = "com.wade.gateway.ftp.server.op.impl.RemoveDirectory";
	public static final String OP_LIST_DIRECTORYS = "com.wade.gateway.ftp.server.op.impl.ListDirectorys";
	public static final String OP_LIST_FILES = "com.wade.gateway.ftp.server.op.impl.ListFiles";
	public static final String OP_MOVE_FILE = "com.wade.gateway.ftp.server.op.impl.MoveFile";
	
}
