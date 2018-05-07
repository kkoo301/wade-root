package com.wade.dfs.client.store;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.wade.dfs.client.proto.Proto;
import com.wade.dfs.client.proto.SProto;
import com.wade.dfs.client.util.ProtoUtil;
import com.wade.dfs.client.util.ResponseBody;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: StoreClient
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-2-5
 */
public class StoreClient {
	
	private InetSocketAddress isa;
	private int storePathIndex;
	private int SO_TIMEOUT = 2000;
	private int CONNECT_TIMEOUT = 2000;
	private static final String EXT_BLANK = "";
		
	public StoreClient(StoreSite site) {
		this.isa = site.getAddress();
		this.storePathIndex = site.getStorePathIndex();
	}
	
	/**
	 * 创建一个到Storage服务器的连接
	 * 
	 * @return
	 * @throws Exception
	 */
	private Socket getStoreSocket() throws Exception {
		
		Socket sock = new Socket();
		sock.setSoTimeout(SO_TIMEOUT);
		sock.connect(this.isa, CONNECT_TIMEOUT);
		
		return sock;
		
	}
	
	/**
	 * 下载
	 * 
	 * @param group
	 * @param localtion
	 * @param offset
	 * @param downloadBytes
	 * @return
	 * @throws Exception
	 */
	public byte[] download(String group, String localtion, long offset, long downloadBytes) throws Exception {
		
		Socket sock = null;
		
		try {
			
			sock = this.getStoreSocket();
			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();
			
			byte[] bsGroup = ProtoUtil.packGroupName(group);
			byte[] bsLocaltion = localtion.getBytes();
			byte[] bsOffset = ProtoUtil.getBytes(offset);
			byte[] bsDownload = ProtoUtil.getBytes(downloadBytes);
	
			int packLen = bsOffset.length + bsDownload.length + bsGroup.length + bsLocaltion.length;
			byte[] headData = ProtoUtil.packHeader(SProto.STORAGE_PROTO_CMD_DOWNLOAD_FILE, packLen, (byte) 0);
			
			out.write(headData);
			out.write(bsOffset);
			out.write(bsDownload);
			out.write(bsGroup);
			out.write(bsLocaltion);
			
			ResponseBody resp = ProtoUtil.readResponse(in, SProto.STORAGE_PROTO_CMD_RESP, -1);
			return resp.body;
		
		} finally {
			if (null != sock) {
				sock.close();
			}
		}
	}
	
	/**
	 * 删除
	 * 
	 * @param group
	 * @param localtion
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String group, String localtion) throws Exception {
		
		Socket sock = null;

		try {
			
			sock = getStoreSocket();
			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();
			
			byte[] bsGroup = ProtoUtil.packGroupName(group);
			byte[] bsLocaltion = localtion.getBytes();

			byte[] headData = ProtoUtil.packHeader(SProto.STORAGE_PROTO_CMD_DELETE_FILE, bsGroup.length + bsLocaltion.length, (byte) 0);
			
			out.write(headData);
			out.write(bsGroup);
			out.write(bsLocaltion);
			out.flush();
			
			ResponseBody resp = ProtoUtil.readResponse(in, SProto.STORAGE_PROTO_CMD_RESP, 0);
			return 0 == resp.errno;
		
		} finally {
			if (null != sock) {
				sock.close();
			}
		}
				
	}

	/**
	 * 上传
	 * 
	 * @param locFilePath
	 * @return
	 * @throws Exception
	 */
	public String upload(String locFilePath) throws Exception {

		File file = new File(locFilePath);
		if (!file.exists()) {
			throw new IllegalArgumentException("locFilePath is not exist! locFilePath: " + locFilePath);
		}
		
		if (file.length() > SProto.MAX_FILE_SIZE) {
			throw new IllegalArgumentException("upload file size is over than limited! localFilePath = " + file.length() + " > " + SProto.MAX_FILE_SIZE );
		}
		
		byte[] fileData = FileUtils.readFileToByteArray(file);
		String extName = FilenameUtils.getExtension(locFilePath);
		
		if (extName.length() > Proto.FDFS_FILE_EXT_NAME_MAX_LEN) {
			extName = extName.substring(0, Proto.FDFS_FILE_EXT_NAME_MAX_LEN);
		}
		
		return upload(fileData, extName);
	}
	
	/**
	 * 上传
	 * 
	 * @param fileData
	 * @return
	 * @throws Exception
	 */
	public String upload(byte[] fileData) throws Exception {
		
		if (fileData.length > SProto.MAX_FILE_SIZE) {
			throw new IllegalArgumentException("upload file size is over than limited! fileData = " + fileData.length + " > " + SProto.MAX_FILE_SIZE );
		}
		
		return upload(fileData, EXT_BLANK);
		
	}
	
	private String upload(byte[] fileData, String extName) throws Exception {
		
		long fileSize = fileData.length;	
		Socket sock = null;
		
		try {
			
			sock = this.getStoreSocket();

			byte[] infoData = new byte[1 + Proto.FDFS_PROTO_PKG_LEN_SIZE + Proto.FDFS_FILE_EXT_NAME_MAX_LEN]; // storePathIndex、文件大小、文件后缀
			infoData[0] = (byte) this.storePathIndex;
			
			byte[] bsFileSize = ProtoUtil.getBytes(fileSize);
			System.arraycopy(bsFileSize, 0, infoData, 1, bsFileSize.length);
			
			byte[] bsExtName = ProtoUtil.packExtName(extName);
			System.arraycopy(bsExtName, 0, infoData, 1 + Proto.FDFS_PROTO_PKG_LEN_SIZE, bsExtName.length);
			
			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();
			byte[] headData = ProtoUtil.packHeader(SProto.STORAGE_PROTO_CMD_UPLOAD_FILE, infoData.length + fileSize, (byte) 0);

			out.write(headData);
			out.write(infoData); 
			out.write(fileData);

			ResponseBody resp = ProtoUtil.readResponse(in, SProto.STORAGE_PROTO_CMD_RESP, -1);
			if (0 != resp.errno) {
				throw new IllegalStateException("0 != resp.errno, resp.errno=" + resp.errno);
			}

			if (resp.body.length <= Proto.GNAME_MAX_LEN) {
				throw new IllegalStateException("body length: " + resp.body.length + " <= " + Proto.GNAME_MAX_LEN);
			}

			String groupName = new String(resp.body, 0, Proto.GNAME_MAX_LEN).trim();
			String remoteFileName = new String(resp.body, Proto.GNAME_MAX_LEN, resp.body.length - Proto.GNAME_MAX_LEN);
						
			return groupName + "/" + remoteFileName;
			
		} finally {
			if (null != sock) {
				sock.close();
			}
		}
	}
}