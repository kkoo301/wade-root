/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年5月18日
 * 
 * Just Do IT.
 */
package com.ailk.common.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * @description
 * 数据迭代器
 */
public abstract class DataEacher<RETURN, ITEM> {
	
	private Collection<?> source = null;
	private RETURN rtn = null;
	
	public DataEacher(Collection<?> source, RETURN rtn) {
		this.source = source;
		this.rtn = rtn;
	}
	
	/**
	 * @return the source
	 */
	public Collection<?> getSource() {
		return source;
	}
	
	@SuppressWarnings("unchecked")
	public RETURN each() {
		Iterator<?> iter = this.source.iterator();
		while (iter.hasNext()) {
			ITEM item = (ITEM) iter.next();
			run(rtn, item);
		}
		return rtn;
	}
	
	
	/**
	 * 处理每个遍历的元素
	 * @param item
	 */
	public abstract void run(RETURN rtn, ITEM item);
	
}
