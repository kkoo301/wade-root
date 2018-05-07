package com.ailk.search;

import java.io.Serializable;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SearchResponse
 * @description: 搜索结果
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-16
 */
public class SearchResponse implements Serializable {
	
	private static final long serialVersionUID = -3807356107888957591L;
	
	/**
	 * 异常信息
	 */
	private String errInfo = "";
	
	/**
	 * 匹配总数
	 */
	private int numTotalHits = 0;
	
	/**
	 * 本次搜索结果（分页后的结果）
	 */
	private IDataset datas = new DatasetList();
	
	public IDataset getDatas() {
		return datas;
	}

	public void setDatas(IDataset datas) {
		this.datas = datas;
	}

	public int getNumTotalHits() {
		return numTotalHits;
	}

	public void setNumTotalHits(int numTotalHits) {
		this.numTotalHits = numTotalHits;
	}
	
	public String getErrInfo() {
		return errInfo;
	}

	public void setErrInfo(String errInfo) {
		this.errInfo = errInfo;
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append(" 'numTotalHits' : '" + numTotalHits + "', ");
		sb.append(" 'datas.size' : '" + datas.size() + "', ");
		sb.append(" 'errInfo' : '" + errInfo + "' ");
		sb.append("}");
		
		return sb.toString();
	}
}
