package com.ailk.common.trace;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;

/**
 * 日志记录器，统计类日志不会生成文件，接触类日志将生成文件以提供分析的源数据
 * 
 * @author $Id: AbstractLogger.java 2015-05-19 15:19:49Z liaosheng $
 * 
 */

public abstract class AbstractTracer implements ITracer {
	
	private static final Logger log = Logger.getLogger(AbstractTracer.class);
	private static Map<Class<ITracer>, ITracer> clazz = new HashMap<Class<ITracer>, ITracer>(100);
	
	/**
	 * 获取注入的ILogger实例
	 * @param annotationClass
	 */
	@SuppressWarnings("unchecked")
	public static ITracer getTracer(Class<?> annotationClass) {
		try {
			if (annotationClass.isAnnotationPresent(com.ailk.common.trace.Tracer.class)) {
				
				com.ailk.common.trace.Tracer tracer = annotationClass.getAnnotation(com.ailk.common.trace.Tracer.class);
				
				Class<ITracer> tracerClass = (Class<ITracer>) tracer.tracer();
				
				ITracer instance = clazz.get(tracerClass);
				
				if (null == instance) {
					instance = tracerClass.newInstance();
					clazz.put(tracerClass, instance);
				}
				
				return instance;
			}
		} catch (Exception e) {
			log.error("日志跟踪失败，不影响系统正常运行。" + e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	public String getTraceId() {
		return "traceId";
	}
	
	@Override
	public String getCurrentProbeId() {
		return "ptraceId";
	}
	
	@Override
	public void startAppProbe(String traceId, String parentTraceId, String bizId, String operId,
			String mainServiceName, String inModeCode, IData param) {
	}
	
	@Override
	public void startDaoProbe(String dataSourceName, long connCost, String sqlName, String sql, IData param) {
	}
	
	@Override
	public void startMainServiceProbe(String traceId, String parentTraceId, String bizId, String operId, String serviceName) {
	}
	
	@Override
	public void startSubServiceProbe(String serviceName) {
	}
	
	@Override
	public void startWebProbe(String bizId, String operId, String sessionId,
			String clientIp, String url, String menuId, IData param) {
	}
	
	@Override
	public void stopAppProbe(boolean success) {
	}
	
	@Override
	public void stopDaoProbe(boolean success) {
	}
	
	@Override
	public void stopServiceProbe(boolean success) {
	}
	
	@Override
	public void stopWebProbe(boolean success) {
	}
	
	@Override
	public void startAjaxProbe(String url, IData param) {
	}
	
	@Override
	public void startCacheProbe(String key) {
	}
	
	@Override
	public void startHandlerProbe(String handler, IData param) {
	}
	
	@Override
	public void startRightProbe(String rightCode) {
	}
	
	@Override
	public void stopAjaxProbe(boolean success) {
	}
	
	@Override
	public void stopCacheProbe(boolean success) {
	}
	
	@Override
	public void stopHandlerProbe(boolean success) {
	}
	
	@Override
	public void stopRightProbe(boolean success) {
	}
}
