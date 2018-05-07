package com.ailk.common.util.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.util.AbstractImpExpAction;
import com.ailk.common.util.Utility;

public class DefaultImpExpAction extends AbstractImpExpAction {
	
	private static transient final Logger log = Logger.getLogger(DefaultImpExpAction.class);
	
	public void initConfig(Map<String, Object> config) {
		
	}
	
	public void callService(Map<String, Object> param){
		String fileSerializeId = (String)param.get("fileSerializeId");
		String serviceName = (String)param.get("serviceName");
		try {
			String[] serviceArray = serviceName.split("@");
			String className = serviceArray[0];
			String methodName = serviceArray[1];
			Class serviceCls = Class.forName(className);
			serviceCls.getMethod(methodName,new Class[]{String.class, Map.class}).invoke(serviceCls.newInstance(), fileSerializeId, param);
		} catch (Exception e) {
			Utility.getBottomException(e).printStackTrace();
			getImpExpManager().setSimpleStatusWithHint(fileSerializeId, "100", "error", Utility.parseExceptionMessage(e));
		}
	}

	public void cancelProgress(Map<String, Object> params) throws Exception{
		String fileSerializeId = (String)params.get("fileSerializeId");
		
		// 如果service有cancel方法的话,则调用service的cancel方法
		try {
			doCancelProgress(params, (String)params.get("serviceName"), fileSerializeId);
			super.getImpExpManager().clearFileSerial(fileSerializeId);
		}catch (Exception e) {
			Utility.getBottomException(e).printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public String getProgressData(String serializeId) throws IOException{
		String progress = buildProgressData(serializeId);
		if("100".equals(getImpExpManager().getProgress(serializeId))){
			getImpExpManager().clearFileSerial(serializeId);
		}
		return progress;
	}
	
	public String initTimer(Map<String, Object> params) throws Exception{
		String totalSize = "totalSize";
		String startTime = "startTime";
		String useTime = "useTime";
		//@TODO 暂未实现能获取导入或导出用时，数据量的内容
		Map<String, Object> map = new HashMap<String, Object>();//impExpAction.initTimer(request, response);
		String out = buildInitTimerData(map, totalSize, startTime, useTime);
		return out;
	}
}
