package com.wade.trace.sample;

import com.wade.trace.sample.ISample;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: AbstractSample
 * @description: 采样抽样率
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public abstract class AbstractSample implements ISample {

	/**
	 * 调用次数
	 */
	private long count;
	
	/**
	 * 采样分母
	 */
	private long sampleDenom;
	
	public AbstractSample(long sampleDenom) {
		this.sampleDenom = sampleDenom;
	}
	
	public long getSampleDenom() {
		return this.sampleDenom;
	}
	
	@Override
	public boolean isSample() {
		
		if (0 >= sampleDenom) { // 采样分母必须配置成大于等于0，否则一律不采样。
			return false;
		}
		
		if (this.count++ % this.sampleDenom == 0) {
			return true;
		}
		
		return false;
		
	}
	
}
