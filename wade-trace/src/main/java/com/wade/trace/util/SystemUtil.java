package com.wade.trace.util;

import java.util.Map;
import java.util.UUID;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.trace.TraceContext;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: 
 * @description: 
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public final class SystemUtil {
	
	public static final String WEB_PREFIX = "web-";
	public static final String APP_PREFIX = "app-";
	public static final String ECS_PREFIX = "ecs-";
	public static final String IBS_PREFIX = "ibs-";
	public static final String UIP_PREFIX = "uip-";
	public static final String PF_PREFIX  = "pf-";
	public static final String AUTO_PREFIX = "auto-";
	
	private static final Map<String, String> mapping = TraceContext.getMapping(); 
	
	/**
	 * 判断是否为有效的traceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isValidTraceId(String traceId) {
		
		if (StringUtils.isBlank(traceId)) {
			return false;
		}
		
		if (traceId.startsWith(WEB_PREFIX)) {
			return true;
		} else if (traceId.startsWith(ECS_PREFIX)) {
			return true;
		} else if (traceId.startsWith(IBS_PREFIX)) {
			return true;
		} else if (traceId.startsWith(PF_PREFIX)) {
			return true;
		} else if (traceId.startsWith(UIP_PREFIX)) {
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * 生成UUID
	 * 
	 * @return
	 */
	public static final String uuid() {
		String uuid = UUID.randomUUID().toString();
		return StringUtils.replace(uuid, "-", "");
	}
	
	/**
	 * 生成一个带WEB标识的TRACEID
	 * 
	 * @return
	 */
	public static final String createWebTraceId() {
		return WEB_PREFIX + uuid();
	}
	
	/**
	 * 生成一个带APP标识的TRACEID
	 * 
	 * @return
	 */
	public static final String createAppTraceId() {
		return APP_PREFIX + uuid();
	}
	
	/**
	 * 生成一个带ECS标识的TRACEID
	 * 
	 * @return
	 */
	public static final String createEcsTraceId() {
		return ECS_PREFIX + uuid();
	}
	
	/**
	 * 生成一个带IBS标识的TRACEID
	 * 
	 * @return
	 */
	public static final String createIbsTraceId() {
		return IBS_PREFIX + uuid();
	}
	
	/**
	 * 生成一个带PF标识的TRACEID
	 * 
	 * @return
	 */
	public static final String createPfTraceId() {
		return PF_PREFIX + uuid();
	}
	
	/**
	 * 生成一个带UIP标识的TRACEID
	 * 
	 * @return
	 */
	public static final String createUipTraceId() {
		return UIP_PREFIX + uuid();
	}

	/**
	 * 生成一个AUTO标识的TRACEID
	 *
	 * @return
	 */
	public static final String createAutoTraceId() {
		return "auto-" + uuid();
	}

	/**
	 * 判断是否为WEB生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isWebTraceId(String traceId) {
		
		if (null == traceId) {
			return false;
		}
		
		return traceId.startsWith(WEB_PREFIX);
		
	}
	
	/**
	 * 判断是否不是WEB生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isNotWebTraceId(String traceId) {
		
		if (null == traceId) {
			return true;
		}
		
		return !traceId.startsWith(WEB_PREFIX);
		
	}
	
	/**
	 * 判断是否为APP生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isAppTraceId(String traceId) {
		
		if (null == traceId) { 
			return false;
		}
		
		return traceId.startsWith(APP_PREFIX);
		
	}
	
	/**
	 * 判断是否不是APP生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isNotAppTraceId(String traceId) {
		
		if (null == traceId) {
			return true;
		}
		
		return !traceId.startsWith(APP_PREFIX);
		
	}
	
	/**
	 * 判断是否为ECS生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isEcsTraceId(String traceId) {
		
		if (null == traceId) {
			return false;
		}
		
		return traceId.startsWith(ECS_PREFIX);
		
	}
	
	/**
	 * 判断是否不是ECS生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isNotEcsTraceId(String traceId) {
		
		if (null == traceId) {
			return true;
		}
		
		return !traceId.startsWith(ECS_PREFIX);
		
	}
	
	/**
	 * 判断是否为IBS生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isIbsTraceId(String traceId) {
		
		if (null == traceId) {
			return false;
		}
		
		return traceId.startsWith(IBS_PREFIX);
		
	}
	
	/**
	 * 判断是否不是IBS生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isNotIbsTraceId(String traceId) {
		
		if (null == traceId) {
			return true;
		}
		
		return !traceId.startsWith(IBS_PREFIX);
		
	}
	
	/**
	 * 判断是否为PF生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isPfTraceId(String traceId) {
		
		if (null == traceId) {
			return false;
		}
		
		return traceId.startsWith(PF_PREFIX);
		
	}

	/**
	 * 判断是否不是PF生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isNotPfTraceId(String traceId) {
		
		if (null == traceId) {
			return true;
		}
		
		return !traceId.startsWith(PF_PREFIX);
		
	}
	
	/**
	 * 判断是否为UIP生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isUipTraceId(String traceId) {
		
		if (null == traceId) {
			return false;
		}
		
		return traceId.startsWith(UIP_PREFIX);
		
	}
	
	/**
	 * 判断是否不是UIP生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isNotUipTraceId(String traceId) {
		
		if (null == traceId) {
			return true;
		}
		
		return !traceId.startsWith(UIP_PREFIX);
		
	}
	
	/**
	 * 判断是否为AUTO生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isAutoTraceId(String traceId) {
		
		if (null == traceId) {
			return false;
		}
		
		return traceId.startsWith(AUTO_PREFIX);
		
	}
	
	/**
	 * 判断是否不是AUTO生成的TraceId
	 * 
	 * @param traceId
	 * @return
	 */
	public static final boolean isNotAutoTraceId(String traceId) {
		
		if (null == traceId) {
			return true;
		}
		
		return !traceId.startsWith(AUTO_PREFIX);
		
	}
	
	/**
	 * 获取进程名
	 * 
	 * @return
	 */
	public static final String getServerName() {
		return System.getProperty("wade.server.name", "");
	}
	
	/**
	 * 根据服务名获取主机IP地址
	 * 
	 * @param serverName
	 * @return
	 */
	public static final String getIp(String serverName) {
		
		int index = serverName.lastIndexOf("-");
		String key = serverName.substring(0, index + 1);
		String ip = mapping.get(key);
		if (null == ip) {
			return "127.0.0.1";
		}
		
		return ip;
	}
	
	public static void main(String[] args) {
		
		System.out.println(getIp("web-node01-ngboss01"));
		System.out.println(getIp("web-node01-personserv01"));
		System.out.println(getIp("web-node01-ngboss01"));
		System.out.println(getIp("web-node01-ngboss01"));
		
		System.out.println(getIp("app-node01-srv01"));
		System.out.println(getIp("app-node01-srv02"));
		System.out.println(getIp("app-node01-srv03"));
		System.out.println(getIp("app-node01-srv04"));
		System.out.println(getIp("app-node01-srv05"));
		System.out.println(getIp("app-node01-srv06"));
		System.out.println(uuid());
		
	}
}
