package com.wade.dfs.client;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wade.dfs.client.track.TrackClient;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DFSClientFactory
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-2-5
 */
public final class DFSClientFactory {
	
	private static final Map<String, DFSClient> TRACKS = new HashMap<String, DFSClient>();
	
	public static final DFSClient getDFSClient(String groupName) {
		return TRACKS.get(groupName);
	}
	
	static {
		DFSXml xml = new DFSXml();
		xml.load();
		
		Map<String, List<InetSocketAddress>> mapping = xml.getMapping();
		for (String name : mapping.keySet()) {
			List<InetSocketAddress> isaList = mapping.get(name);
			TrackClient tClient = new TrackClient(name, isaList);
			DFSClient client = new DFSClient(tClient);
			TRACKS.put(name, client);
		}
		
	}
	
}