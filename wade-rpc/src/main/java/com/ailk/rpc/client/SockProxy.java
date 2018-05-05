package com.ailk.rpc.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.ailk.rpc.codec.Transporter;
import com.ailk.rpc.org.jboss.netty.channel.Channel;
import com.ailk.rpc.org.jboss.netty.handler.timeout.TimeoutException;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SockProxy
 * @description: 
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-4
 */
public class SockProxy {

	public static final Map<Long, SockProxy> REQUEST_QUEUE = new ConcurrentHashMap<Long, SockProxy>();
	private static final AtomicLong SEQ = new AtomicLong(0);
	
	private long seq = 0;
	private Channel channel = null;
	private Object rsp = null;
	
	public SockProxy(Channel channel) {
		if (null == channel) {
			throw new IllegalArgumentException("socket通道channel不可为空！");
		}
		this.channel = channel;
	}

	public void write(Transporter transporter) {
		
		this.seq = getNextSeq();
		SockProxy proxy = REQUEST_QUEUE.put(this.seq, this);
		if (null != proxy) {
			throw new RuntimeException("存在重复数据");
		}
	    transporter.setSeq(this.seq);
		this.channel.write(transporter);
		
	}

	public Transporter read(int secTTL) {
		
		try {
			
			long timeout = secTTL * 1000L;
			long start = System.currentTimeMillis();

			synchronized (this) {
				while (null == this.rsp) {
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

			return (Transporter)this.rsp;
			
		} finally {
			REQUEST_QUEUE.remove(this.seq);
		}
	}

	public Transporter read() throws Exception {
		return read(0);
	}

	public boolean setResponse(Object rsp) {
		this.rsp = rsp;
		synchronized (this) {
			notify(); // 通知接受数据
		}
		return true;
	}
	
	private static long getNextSeq() {
		return SEQ.getAndIncrement();
	}
	
}
