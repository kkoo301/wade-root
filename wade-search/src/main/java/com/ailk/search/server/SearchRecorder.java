package com.ailk.search.server;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SearchRecorder
 * @description: 记录搜索统计信息
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-9-18
 */
public class SearchRecorder {
	
	/**
	 * 总调用次数
	 */
	private AtomicLong cnt = new AtomicLong(0L);
	
	/**
	 * 总耗时(毫秒)
	 */
	private AtomicLong ttc = new AtomicLong(0L);
	
	public AtomicLong getCnt() {
		return cnt;
	}

	public void setCnt(AtomicLong cnt) {
		this.cnt = cnt;
	}

	public AtomicLong getTtc() {
		return ttc;
	}

	public void setTtc(AtomicLong ttc) {
		this.ttc = ttc;
	}
	
}
