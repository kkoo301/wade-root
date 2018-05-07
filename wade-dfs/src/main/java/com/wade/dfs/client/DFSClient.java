package com.wade.dfs.client;

import com.wade.dfs.client.proto.DFSFile;
import com.wade.dfs.client.store.StoreClient;
import com.wade.dfs.client.store.StoreSite;
import com.wade.dfs.client.track.TrackClient;
import com.wade.dfs.client.util.StringUtils;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DFSClient
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-2-5
 */
public final class DFSClient {
	
	private TrackClient trackClient;
	
	public DFSClient(TrackClient trackClient) {
		this.trackClient = trackClient;
	}
	
	/**
	 * 下载
	 * 
	 * @param dfsFileName
	 * @return
	 * @throws Exception
	 */
	public final byte[] download(String dfsFileName) throws Exception {
		
		DFSFile file = StringUtils.parse(dfsFileName);
		String group = file.getGroup();
		String localtion = file.getLocaltion();
		
		StoreSite site = trackClient.getStoreSite(group);
		
		StoreClient storeClient = new StoreClient(site);
		byte[] data = storeClient.download(group, localtion, 0, 0);
		
		return data;
	}
	
	/**
	 * 删除
	 * 
	 * @param dfsFileName
	 * @return
	 * @throws Exception
	 */
	public final boolean delete(String dfsFileName) throws Exception {
		
		DFSFile file = StringUtils.parse(dfsFileName);
		String group = file.getGroup();
		String localtion = file.getLocaltion();
		
		StoreSite site = trackClient.getStoreSite(group);
		StoreClient storeClient = new StoreClient(site);
		
		return storeClient.delete(group, localtion);
	}
	
	/**
	 * 上传
	 * 
	 * @param locFileName 本地文件名
	 * @return
	 * @throws Exception
	 */
	public String upload(String locFileName) throws Exception {
		
		StoreSite site = trackClient.getStoreSite();
		
		StoreClient storeClient = new StoreClient(site);
		String dfsFileName = storeClient.upload(locFileName);
		
		return dfsFileName;
	}
	
	public String upload(byte[] data) throws Exception {
		String dfsFileName = upload(data, null);
		return dfsFileName;
	}
	
	public String upload(byte[] data, String extName) throws Exception {
		
		StoreSite site = trackClient.getStoreSite();
		
		return null;
	}
	
	public static void main(String[] args) throws Exception {

		DFSClient client = DFSClientFactory.getDFSClient("crm");
		
		//byte[] data = client.downloadFile("CRM01/M01/00/00/wKj1gFUg5Q-ACGT6AAAHGjkscnM75.java");
		//FileUtils.writeByteArrayToFile(new File("C:/Users/Administrator/Desktop/DFS-DIR/xxx.java"), data);
		
		//boolean rtn = client.deleteFile("CRM01/M00/00/00/wKj1gFUg5JiAainGAAAHGjkscnM61.java");
		//System.out.println("delete ret = " + rtn);
		
		for (int i = 0; i < 1; i++) {
			String rtn = client.upload("C:/Users/Administrator/Desktop/DFS-DIR/MonitorSessionAttributeListener");
			System.out.println(i + " " + rtn);
			Thread.sleep(500);
		}
		
		//Thread.sleep(1000 * 1000);
	
	
	}
}