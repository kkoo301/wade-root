package com.wade.dfs.client.proto;

public final class SProto {
	public static final byte STORAGE_PROTO_CMD_UPLOAD_FILE = 11;
	public static final byte STORAGE_PROTO_CMD_DELETE_FILE = 12;
	public static final byte STORAGE_PROTO_CMD_SET_METADATA = 13;
	public static final byte STORAGE_PROTO_CMD_DOWNLOAD_FILE = 14;
	public static final byte STORAGE_PROTO_CMD_GET_METADATA = 15;
	public static final byte STORAGE_PROTO_CMD_UPLOAD_SLAVE_FILE = 21;
	public static final byte STORAGE_PROTO_CMD_QUERY_FILE_INFO = 22;
	public static final byte STORAGE_PROTO_CMD_UPLOAD_APPENDER_FILE = 23;
																		
																		
	public static final byte STORAGE_PROTO_CMD_APPEND_FILE = 24;
	public static final byte STORAGE_PROTO_CMD_MODIFY_FILE = 34;
	public static final byte STORAGE_PROTO_CMD_TRUNCATE_FILE = 36;

	public static final byte STORAGE_PROTO_CMD_RESP = 100;
	
	public static final int MAX_FILE_SIZE = 50 * 1024 * 1024; // 上传文件<=50MB
}
