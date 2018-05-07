/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.common.thread;

import com.ailk.common.data.IVisit;

/**
 * 以线程为单位的会话对象
 * 
 * @className: ThreadSession.java
 * @author: liaosheng
 * @date: 2014-3-28
 */
public interface ThreadSession {
	
	public String getSessionId();
	
	public Object getValue(String key);
	
	public IVisit getContext();
	
}
