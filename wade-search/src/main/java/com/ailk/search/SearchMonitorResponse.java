package com.ailk.search;

import java.util.Map;

import com.ailk.search.server.SearchRecorder;

public class SearchMonitorResponse {
	private String bootTime;
	private Map<String, SearchRecorder> searchStatistics;
	
	public String getBootTime() {
		return bootTime;
	}
	
	public void setBootTime(String bootTime) {
		this.bootTime = bootTime;
	}
	
	public Map<String, SearchRecorder> getSearchStatistics() {
		return searchStatistics;
	}
	
	public void setSearchStatistics(Map<String, SearchRecorder> searchStatistics) {
		this.searchStatistics = searchStatistics;
	}
}
