package com.wade.trace.sample.impl;

import com.wade.trace.sample.AbstractSample;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: UipSample
 * @description: UIP采样
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class UipSample extends AbstractSample {

	public UipSample(long sampleDenom) {
		super(sampleDenom);
	}

	@Override
	public String toString() {
		return "{ sample_denom : " + this.getSampleDenom() + " }";
	}
	
}
