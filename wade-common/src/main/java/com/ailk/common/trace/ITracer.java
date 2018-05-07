package com.ailk.common.trace;

import com.ailk.common.data.IData;


/**
 * 
 * @author $Id: ITracer.java 1 2015-05-19 08:34:02Z liaosheng $
 *
 */

public interface ITracer {
	
	public String getTraceId();
	
	public String getCurrentProbeId();
	
	public String getBrowserId();

	public void startWebProbe(String bizId, String operId, String sessionId, String clientIp, String url, String menuId, IData param);
	
	public void stopWebProbe(boolean success);
	
	public void startAjaxProbe(String url, IData param);
	
	public void stopAjaxProbe(boolean success);
	
	public void startHandlerProbe(String handler, IData param);
	
	public void stopHandlerProbe(boolean success);
	
	public void startRightProbe(String rightCode);
	
	public void stopRightProbe(boolean success);
	
	public void startCacheProbe(String key);
	
	public void stopCacheProbe(boolean success);
	
	public void startAppProbe(String traceId, String parentTraceId, String bizId, String operId, String mainServiceName, String inModeCode, IData param);

	public void stopAppProbe(boolean success);

	public void startMainServiceProbe(String traceId, String parentTraceId, String bizId, String operId, String serviceName);

	public void startSubServiceProbe(String serviceName);

	public void stopServiceProbe(boolean success);

	public void startDaoProbe(String dataSourceName, long connCost, String sqlName, String sql, IData param);

	public void stopDaoProbe(boolean success);
}