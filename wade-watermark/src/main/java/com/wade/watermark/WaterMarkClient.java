package com.wade.watermark;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import com.ailk.common.config.GlobalCfg;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: WaterMarkClient
 * @description: 
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-06-16
 */
public final class WaterMarkClient {

	private static DatagramSocket client = null;
	private static List<WaterMarkAddress> WM_ADDR_LIST = new ArrayList<WaterMarkAddress>();
	
	static {
		
		try {
			
			client = new DatagramSocket();
			
			String address = GlobalCfg.getProperty("watermark.server.address");
			String[] addrs = StringUtils.split(address, ',');
			
			for (String addr : addrs) {
				WaterMarkAddress waterMarkAddress = new WaterMarkAddress(addr);
				WM_ADDR_LIST.add(waterMarkAddress);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 发送水印数据
	 * 
	 * @param operId
	 * @param clientIp
	 * @throws Exception
	 */
	public static final void send(String operId, String clientIp) throws Exception {

		String content = operId + "," + clientIp;
		byte[] data = content.getBytes();
		
		for (WaterMarkAddress addr : WM_ADDR_LIST) { 
			DatagramPacket packet = new DatagramPacket(data, data.length, addr.getInetAddress(), addr.getPort());
			packet.setData(data);
			client.send(packet);
		}

	}
	
}
