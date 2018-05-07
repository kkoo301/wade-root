package com.ailk.service.client.socket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.handler.timeout.TimeoutException;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SockProxy
 * @description: 
 * 
 * @version: v1.0.0
 * @author: xiedx
 * @date: 2013-7-4
 */
public class SocketProxy {

	public static final Map<Long, SocketProxy> REQUEST_QUEUE = new ConcurrentHashMap<Long, SocketProxy>();
	private static final AtomicLong SEQ = new AtomicLong(0);
	
	private long seq = 0;
	private Channel channel = null;
	private IDataOutput output = null;
	
	public SocketProxy(Channel channel) {
		if (null == channel) {
			throw new IllegalArgumentException("socket通道channel不可为空！");
		}
		this.channel = channel;
	}

	public void write(IDataInput input) {
		this.seq = getNextSeq();
		SocketProxy proxy = REQUEST_QUEUE.put(this.seq, this);
		if (null != proxy) {
			throw new RuntimeException("存在重复数据");
		}
	    input.getHead().put("_SOCKET_PROXY_SEQ", this.seq);
		this.channel.write(input);
	}

	public IDataOutput read(int secTTL) {
		
		try {
			
			long timeout = secTTL * 1000L;
			long start = System.currentTimeMillis();

			synchronized (this) {
				while (null == this.output) {
					try {
						if (timeout <= 0L) {
							wait();
						} else {
							long elapsed = System.currentTimeMillis() - start;
							long waitTime = timeout - elapsed;
							if (waitTime > 0L) {
								wait(waitTime);
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if ((timeout > 0L) && (System.currentTimeMillis() - start >= timeout)) {
						throw new TimeoutException("Timeout read data");
					}
				}
			}

			return this.output;
			
		} finally {
			REQUEST_QUEUE.remove(this.seq);
		}
	}

	public IDataOutput read() throws Exception {
		return read(30);
	}

	public boolean setOutput(IDataOutput out) {
		this.output = out;
		synchronized (this) {
			notify(); // 通知接受数据
		}
		return true;
	}
	
	private static long getNextSeq() {
		return SEQ.getAndIncrement();
	}
	
}
