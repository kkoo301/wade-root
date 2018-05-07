package com.wade.dfs.client.track;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.wade.dfs.client.proto.Proto;
import com.wade.dfs.client.proto.TProto;
import com.wade.dfs.client.store.StoreSite;
import com.wade.dfs.client.util.ProtoUtil;
import com.wade.dfs.client.util.ResponseBody;
import com.wade.dfs.client.util.StringUtils;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TrackClient
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-2-5
 */
public final class TrackClient {

	private static final Logger log = Logger.getLogger(TrackClient.class);
	
	private String name;
	
	private LinkedBlockingQueue<InetSocketAddress> liveAddrs = new LinkedBlockingQueue<InetSocketAddress>();
	private LinkedBlockingQueue<InetSocketAddress> deadAddrs = new LinkedBlockingQueue<InetSocketAddress>();

	public TrackClient(String name, List<InetSocketAddress> trackServers) {
		this.name = name;
		
		for (InetSocketAddress address : trackServers) {
			liveAddrs.offer(address);
		}
		
		MaintTask task = new MaintTask();
		task.setDaemon(true);
		task.start();
		
		log.info("track:" + this.name + ", TrackerClient.MaintTask Thread Start!");
		
	}

	/**
	 * 获取Track的Socket对象
	 * 
	 * @return
	 * @throws Exception
	 */
	private Socket getTrackSocket() throws Exception {
		Socket sock = new Socket();
		sock.setSoTimeout(2000);

		InetSocketAddress address = liveAddrs.poll(1, TimeUnit.SECONDS);
		if (null != address) {
			liveAddrs.offer(address);
			sock.connect(address, 2000);
		}

		return sock;
	}

	/**
	 * 获取Store站点地址
	 * 
	 * @return
	 * @throws Exception
	 */
	public StoreSite getStoreSite() throws Exception {
		
		StoreSite site = null;
		Socket sock = null;
		
		try {
			
			sock = getTrackSocket();
			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();
			
			byte[] headData = ProtoUtil.packHeader(TProto.GET_STORESITE_WITHOUT_GNAME, 0, (byte) 0);
			
			out.write(headData);
			out.flush();
			
			ResponseBody resp = ProtoUtil.readResponse(in, TProto.RESP, TProto.QUERY_STORAGE_STORE_BODY_LEN);
			if (0 != resp.errno) {
				throw new IllegalStateException("Response.errno=" + resp.errno);
			}
			
			String ip = new String(resp.body, Proto.GNAME_MAX_LEN, Proto.IPADDR_SIZE - 1).trim();
			int port = (int) ProtoUtil.buff2long(resp.body, Proto.GNAME_MAX_LEN + Proto.IPADDR_SIZE - 1);
			byte store_path = resp.body[TProto.QUERY_STORAGE_STORE_BODY_LEN - 1];
			
			site = new StoreSite();
			site.setAddress(new InetSocketAddress(ip, port));
			site.setStorePathIndex(store_path);
			
		} finally {
			if (null != sock) {
				sock.close();
			}
		}
		
		return site;
	}
	
	/**
	 * 根据group获取Store站点地址
	 * 
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public StoreSite getStoreSite(String group) throws Exception {

		if (StringUtils.isBlank(group)) {
			throw new IllegalArgumentException("group could not be blank!");
		}

		if (group.length() > Proto.GNAME_MAX_LEN) {
			throw new IllegalArgumentException("group is larger than " + Proto.GNAME_MAX_LEN);
		}

		StoreSite site = null;
		Socket sock = null;
		
		try {
			
			sock = getTrackSocket();
			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();
			
			byte[] headData = ProtoUtil.packHeader(TProto.GET_STORESITE_WITH_GNAME, Proto.GNAME_MAX_LEN, (byte) 0);
			byte[] bodyData = ProtoUtil.packGroupName(group);
			
			out.write(headData);
			out.write(bodyData);
			out.flush();
			
			ResponseBody resp = ProtoUtil.readResponse(in, TProto.RESP, TProto.QUERY_STORAGE_STORE_BODY_LEN);
			if (0 != resp.errno) {
				throw new IllegalStateException("Response.errno: " + resp.errno + ", group: " + group);
			}
			
			String ip = new String(resp.body, Proto.GNAME_MAX_LEN, Proto.IPADDR_SIZE - 1).trim();
			int port = (int) ProtoUtil.buff2long(resp.body, Proto.GNAME_MAX_LEN + Proto.IPADDR_SIZE - 1);
			byte store_path = resp.body[TProto.QUERY_STORAGE_STORE_BODY_LEN - 1];
			
			site = new StoreSite();
			site.setAddress(new InetSocketAddress(ip, port));
			site.setStorePathIndex(store_path);
			
		} finally {
			if (null != sock) {
				sock.close();
			}
		}
		
		return site;
	}

	private class MaintTask extends Thread {

		private boolean ruok(InetSocketAddress address) {

			boolean rtn = false;
			Socket sock = null;

			try {
				sock = new Socket();
				sock.connect(address, 2000);
				rtn = true;
			} catch (IOException e) {
				log.error("�����쳣! " + address, e);

			} finally {
				if (null != sock) {
					try {
						sock.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return rtn;
		}

		@Override
		public void run() {
			while (true) {
				
				for (int i = 0, size = liveAddrs.size(); i < size; i++) {
					InetSocketAddress address = liveAddrs.poll();
					if (null == address) {
						break;
					}
					
					if (ruok(address)) {
						liveAddrs.offer(address);
					} else {
						deadAddrs.offer(address);
					}
				}

				for (int i = 0, size = deadAddrs.size(); i < size; i++) {
					InetSocketAddress address = deadAddrs.poll();
					if (null == address) {
						break;
					}

					if (ruok(address)) {
						liveAddrs.offer(address);
					} else {
						deadAddrs.offer(address);
					}
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

}
