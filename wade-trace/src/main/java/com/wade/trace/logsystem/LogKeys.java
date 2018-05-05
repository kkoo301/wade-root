package com.wade.trace.logsystem;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: LogKeys
 * @description: 日志KEY静态类
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public final class LogKeys {
	
	/**	追踪日志Topic */
	public static final String TOPIC_TRACE = "topic-trace";
	
	/** 错误日志Topic */
	public static final String TOPIC_ERROR = "topic-error";
	
	/** 公共参数 */
	public static final String PROBE_TYPE = "probetype";
	public static final String ID = "id";
	public static final String PARENT_ID = "parentid";
	public static final String TRACE_ID = "traceid";
	public static final String BIZ_ID = "bizid";
	public static final String OPER_ID = "operid";
	public static final String START_TIME = "starttime";
	public static final String END_TIME = "endtime";
	public static final String COST_TIME = "costtime";
	public static final String SUCCESS = "success";
	public static final String IP = "ip";
	
	/** 公共特殊参数 */
	public static final String SERVER_NAME = "servername";
	public static final String EXT = "ext";
	
	/** Browser特有参数 */
	public static final String STATUS_CCODE = "statuscode";
	
	/** WebProbe特有参数 */
	public static final String SESSION_ID = "sessionid";
	public static final String CLIENT_IP = "clientip";
	public static final String URL = "url";
	public static final String MENU_ID = "menuid";
	
	
	/** AppProbe特有参数 */
	
	
	/** ServiceProbe特有参数 */
	public static final String SERVICE_NAME = "servicename";
	public static final String CENTER_NAME = "centername";
	
	
	/** DaoProbe特有参数 */
	public static final String DATASOURCE_NAME = "datasource";
	public static final String SQL_NAME = "sqlname";
	public static final String DCCOST = "dccost";
	public static final String SQL = "sql";
	public static final String SQL_PARAM = "sqlparam";
	
	/** MemCache特有参数 */
	public static final String CCOST = "cCost";
	public static final String ECOST = "cCost";
	public static final String CMD = "cmd";
	public static final String KEY = "key";
	
	/** ECH特有参数 */
	
	
	/** IBOSS特有参数 */
	
	
	/** PF特有参数 */
	
	
	/** UIP特有参数 */
	
	/** 异常日志特有参数 */
	public static final String ERR_TIME = "errtime";
	public static final String ERR_INFO = "errinfo";
}
