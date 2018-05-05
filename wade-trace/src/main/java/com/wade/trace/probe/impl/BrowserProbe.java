package com.wade.trace.probe.impl;

import java.util.HashMap;
import java.util.Map;

import com.wade.trace.logsystem.LogKeys;
import com.wade.trace.logsystem.LogSystemUtil;
import com.wade.trace.probe.AbstractProbe;

public class BrowserProbe extends AbstractProbe {

	private String statuscode;
	private String starttime;
	private String endtime;
	private String ieVer;
	
	public BrowserProbe() {
		setProbeType(BROWSER);
	}
	
	public String getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	
	public String getIeVer() {
		return this.ieVer;
	}
	  
	public void setIeVer(String ieVer) {
		this.ieVer = ieVer;
	}
	
	@Override
	public void logging() {
		
		// 计算耗时
		long costtime = Long.parseLong(getEndtime()) - Long.parseLong(getStarttime());
		
		Map<String, Object> logInfo = new HashMap<String, Object>();
		
		/** 公共基础参数 */
		logInfo.put(LogKeys.PROBE_TYPE, getProbeType());
		logInfo.put(LogKeys.ID, getId());
		logInfo.put(LogKeys.PARENT_ID, "root");
		logInfo.put(LogKeys.TRACE_ID, getTraceId());
	    logInfo.put(LogKeys.START_TIME, getStarttime());
	    logInfo.put(LogKeys.END_TIME, getEndtime());
	    logInfo.put(LogKeys.COST_TIME, String.valueOf(costtime));
	    
	    /** 特有参数 */
	    logInfo.put(LogKeys.STATUS_CCODE, getStatuscode());

		LogSystemUtil.send(logInfo);
		
	}

}