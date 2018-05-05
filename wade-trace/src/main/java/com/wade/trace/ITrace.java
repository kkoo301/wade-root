package com.wade.trace;

import com.ailk.common.data.IData;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: ITrace
 * @description: 跟踪接口
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public interface ITrace {
	
	/**
	 * 获取上下文中的追踪ID
	 * 
	 * @return
	 */
	public String getTraceId();
	
	/**
	 * 获取上下文中最顶端Probe的ID
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * 获取浏览器Probe的ID
	 * 
	 * @return
	 */
	public String getBrowserId();
	
	/**
	 * 用于收集异常日志信息
	 * 
	 * @param staffId
	 * @param serviceName
	 * @param errinfo
	 */
	public void collectException(String staffId, String serviceName, String errinfo);
	
	/**
	 * 开启browser探针
	 * 
	 * @param id
	 * @param traceid
	 * @param statuscode
	 * @param starttime
	 * @param endtime
	 * @param ieVer
	 */
	public void logBrowserProbe(String id, String traceid, String statuscode, String starttime, String endtime, String ieVer);
		
	/**
	 * 开启Web探针
	 * 
	 * @param bizId
	 * @param operId
	 * @param sessionId
	 * @param clientIp
	 * @param url
	 * @param menuId
	 * @param param
	 * @throws Exception
	 */
	public void startWebProbe(String bizId, String operId, String sessionId, String clientIp, String url, String menuId, IData param);
	
	/**
	 * 关闭Web探针
	 * 
	 * @param success
	 */
	public void stopWebProbe(boolean success);
		
	/**
	 * 开启一个App探针
	 * 
	 * @param traceId
	 * @param parentId
	 * @param bizId
	 * @param operId
	 * @param mainServiceName
	 * @param param
	 * @throws Exception
	 */
	public void startAppProbe(String traceId, String parentId, String bizId, String operId, String mainServiceName, IData param);
	
	/**
	 * 关闭App探针
	 * 
	 * @param success
	 */
	public void stopAppProbe(boolean success);
	
	/**
	 * 开启(主)服务探针
	 * 
	 * @param traceId
	 * @param serviceName
	 */
	public void startMainServiceProbe(String traceId, String parentId, String bizId, String operId, String serviceName);
	
	/**
	 * 开启(子)服务探针
	 * 
	 * @param serviceName
	 */
	public void startSubServiceProbe(String serviceName);
	
	/**
	 * 关闭服务探针
	 * 
	 * @param success
	 */
	public void stopServiceProbe(boolean success);
	
	/**
	 * 开启Dao探针
	 * 
	 * @param dataSource
	 * @param cCost
	 * @param sqlName
	 * @param sql
	 * @param param
	 */
	public void startDaoProbe(String dataSource, long cCost, String sqlName, String sql, IData param);
	
	/**
	 * 关闭Dao探针
	 * 
	 * @param success
	 */
	public void stopDaoProbe(boolean success);
		
	/**
	 * 开启ECS(电渠)探针
	 * 
	 * @param serviceName
	 * @param operId
	 */
	public void startEcsProbe(String serviceName, String operId);
	
	/**
	 * 关闭ECS探针
	 * 
	 * @param success
	 */
	public void stopEcsProbe(boolean success);

	/**
	 * 开启IBS(一级BOSS)探针
	 * 
	 * @param serviceName
	 * @param operId
	 * @throws Exception
	 */
	public void startIbsProbe(String serviceName, String operId);
	
	/**
	 * 关闭IBS探针
	 * 
	 * @param success
	 */
	public void stopIbsProbe(boolean success);
	
	/**
	 * 开启服开探针
	 * 
	 * @param serviceName
	 * @param traceId
	 * @param parentId
	 * @param operId
	 * @throws Exception
	 */
	public void startPfProbe(String serviceName, String traceId, String parentId, String operId);
	
	/**
	 * 关闭服开探针
	 * 
	 * @param success
	 */
	public void stopPfProbe(boolean success);
	
	/**
	 * 开启UIP探针
	 * 
	 * @param serviceName
	 * @param traceId
	 * @param parentId
	 * @param operId
	 */
	public void startUipProbe(String serviceName, String traceId, String parentId, String operId);
	
	/**
	 * 关闭UIP探针
	 * 
	 * @param success
	 */
	public void stopUipProbe(boolean success);
	
	/**
	 * 菜单点击
	 * 
	 * @param paramString1
	 * @param paramString2
	 * @param paramString3
	 */
	public abstract void menuClick(String paramString1, String paramString2, String paramString3);
	
}
