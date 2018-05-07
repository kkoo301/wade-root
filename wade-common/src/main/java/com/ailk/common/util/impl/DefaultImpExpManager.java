package com.ailk.common.util.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.util.AbstractImpExpManager;

/**
 * 管理导入或导出的公共信息
 * 
 * @author lvchao
 *
 */
public class DefaultImpExpManager extends AbstractImpExpManager{

	private static final transient Logger log = Logger.getLogger(DefaultImpExpManager.class);
	
	public static Map STATUS_MAP = new ConcurrentHashMap<String,ConcurrentHashMap<String, String>>();
	
	static{
		String activeTimeStr = GlobalCfg.getProperty("impExp.activetime");
		long activeTime = 600;// 以秒为单位
		if(activeTimeStr != null && !"".equals(activeTimeStr)){
			activeTime = Long.valueOf(activeTimeStr);
		}
		new Thread(new DefaultImpExpManager().new ClearStatus(activeTime)).start();
	}
	
	public Map getStatus(){
		return STATUS_MAP;
	}
	
	public Map getStatus(String fileSerializeId){
		Map fileMap = (Map)STATUS_MAP.get(fileSerializeId);
		if(fileMap == null){
			return null;
		}
		updateStatus(fileSerializeId,AbstractImpExpManager.LAST_UPDATE_TIME, System.currentTimeMillis() + "");
		return (Map)STATUS_MAP.get(fileSerializeId);
	}
	
	public void updateStatus(String fileSerializeId,String key,String value){
		Map<String,String> map = (Map)STATUS_MAP.get(fileSerializeId);
		if(map == null){
			addStatus(fileSerializeId,new ConcurrentHashMap<String, String>());
		}
		((Map)STATUS_MAP.get(fileSerializeId)).put(key, value);
	}
	
	public void addStatus(String fileSerializeId, Map status){
		STATUS_MAP.put(fileSerializeId, status);
	}
	
	public void removeStatus(String fileSerializeId){
		if(STATUS_MAP.containsKey(fileSerializeId)){
			STATUS_MAP.remove(fileSerializeId);
		}
	}
	
	public void removeAllStatus(){
		if(STATUS_MAP != null){
			STATUS_MAP.clear();
		}
	}

}



