package com.ailk.search;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SearchRequest
 * @description: 搜索请求
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-16
 */
public class SearchRequest implements Serializable {
	
	private static final long serialVersionUID = -501465607818671474L;
	
	private String searchCode;
	private String keyword;
	private int start;
	private int end;
	
	public String getSearchCode() {
		return searchCode;
	}
	
	public void setSearchCode(String searchCode) {
		this.searchCode = searchCode;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append(" 'searchCode' : '" + searchCode + "', ");
		sb.append(" 'keyword' : '" + keyword + "', ");
		sb.append(" 'start' : '" + start + "', ");
		sb.append(" 'end' : '" + end + "' ");
		sb.append("}");
		
		return sb.toString();
	}
}
