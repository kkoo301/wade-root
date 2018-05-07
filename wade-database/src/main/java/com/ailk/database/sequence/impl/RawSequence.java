package com.ailk.database.sequence.impl;

import com.ailk.database.sequence.AbstractSequence;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: RawSequence
 * @description: 默认序列类，直接返回序列值，没有自定义拼接方式的
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-4-18
 */
public class RawSequence extends AbstractSequence {
	
	public RawSequence(String seqName) {
		super(seqName);
	}
	
	@Override
	public String getNextval(String connName) throws Exception {
		return super.nextval(connName);
	}

	@Override
	public String getNextval(String connName, String eparchyCode) throws Exception {
		return super.nextval(connName);
	}
	
}
