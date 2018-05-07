package com.ailk.common.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SystemCfg {

	private static SystemCfg systemcfg = new SystemCfg();
	private static Map<String, String> data = Collections.synchronizedMap(new HashMap<String, String>(50));
	private static Map<String, String> subsys = Collections.synchronizedMap(new HashMap<String, String>(50));
	
	static {
		try {
			PropertiesConfig cfg = new PropertiesConfig("system.properties");
			data = cfg.getProperties();
			if(data !=null && data.size() > 0){
				for(Entry<String,String> entry:data.entrySet()){
					String key = entry.getKey();
					if(key.indexOf("subsys.") > -1){
						subsys.put(key.replace("subsys.", ""), entry.getValue());
					}
				}
			}
		} catch (Exception e) {
			
		}
	}
	
	
	public static final String subsysCode    = data.get("system.subsys.code");
	public static final String provinceCode  = data.get("system.province.code");

	public static final boolean isPagePreloadOn       = "true".equals(data.get("system.page.preload"));
	public static final boolean isPageNameLocation    = data.get("system.page.name.location") == null ? false : "true".equals(data.get("system.page.name.location"));
	public static final boolean isPageModPrivLoad     = data.get("system.page.modpriv.load") == null ? true : "true".equals(data.get("system.page.modpriv.load"));
	public static final boolean isPagePrivCheck       = data.get("system.page.priv.check") == null ? true : "true".equals(data.get("system.page.priv.check"));
	public static final boolean isServicePreloadOn    = "true".equals(data.get("system.service.preload"));
	public static final boolean isDataPreloadOn       = "true".equals(data.get("system.data.preload"));
	public static final boolean isLogDebugOn          = "true".equals(data.get("system.log.debug"));
	public static final boolean isLocalCacheOn        = "true".equals(data.get("system.local.cache"));
	public static final boolean isCodeCodeCacheOn     = "true".equals(data.get("system.codecode.cache"));
	public static final boolean isReleaseDBConn       = "true".equals(data.get("system.release.dbconn"));
	
	//system.errorstack.level OFF|LOG|DEBUG
	public static final String  errorStackLevel       = data.get("system.errorstack.level") == null ? "DEBUG" : data.get("system.errorstack.level");
	
	public static final String  clientConnMode        = data.get("client.conn.mode");
	public static final int     clientConnTimeout     = data.get("client.conn.timeout") == null ? 10000 : Integer.parseInt(data.get("client.conn.timeout"));
	public static final boolean clientConnTcpnodelay  = "true".equals(data.get("client.conn.tcpnodelay"));

	public static final String systemProcessName      = data.get("system.process.name");

	public static final boolean isServiceInputCheckOn     = "true".equals(data.get("service.input.check"));
	public static final boolean isServiceOutputCheckOn    = "true".equals(data.get("service.output.check"));
	public static final boolean isServiceInputVerifyOn    = "true".equals(data.get("service.input.verify"));
	public static final boolean isServiceOutputVerifyOn   = "true".equals(data.get("service.output.verify"));
	public static final boolean isServiceInputTransOn     = "true".equals(data.get("service.input.trans"));
	public static final boolean isServiceOutputTransOn    = "true".equals(data.get("service.output.trans"));
	public static final boolean isServiceInputObfusOn     = "true".equals(data.get("service.input.obfus"));
	public static final boolean isServiceOutputObfusOn    = "true".equals(data.get("service.output.obfus"));
	public static final boolean isServiceCtrlByDB         = "true".equals(data.get("service.contrl.db"));
	public static final boolean isDaoUseMemCache          = "true".equals(data.get("dao.use.memcache"));

	public static final int     serviceInvokeMinsize   = data.get("service.invoke.minsize") == null ? 2 : Integer.parseInt(data.get("service.invoke.minsize"));	
	public static final int     serviceInvokeMaxsize   = data.get("service.invoke.maxsize") == null ? 1000 : Integer.parseInt(data.get("service.invoke.maxsize"));	
	public static final long    serviceInvokeKeepalive = data.get("service.invoke.keepalive") == null ? 0 : Long.parseLong(data.get("service.invoke.keepalive"));
	public static final int     serviceInvokeQueuesize = data.get("service.invoke.queuesize") == null ? 10000 : Integer.parseInt(data.get("service.invoke.queuesize"));
	public static final String  serviceInvokeTimeout   = data.get("service.invoke.timeout") == null ? "600000" : data.get("service.invoke.timeout");
	public static final int     maxFetchSize           = data.get("resultset.max.fetchsize") == null ? 1000 : Integer.parseInt(data.get("resultset.max.fetchsize"));	
	public static final int     maxSqlExecuteTime      = data.get("max.sql.execute.time") == null ? 3000 : Integer.parseInt(data.get("max.sql.execute.time"));

	public static final String  wadeWebResourcePath     = data.get("wade.web.resource.path");
	public static final String  wadeWebResourceVersion  = data.get("wade.web.resource.version") != null && !"".equals(data.get("wade.web.resource.version")) ? data.get("wade.web.resource.version") : ReleaseCfg.getWadeReleaseNumber(); // ? data.get("wade.web.resource.version")
			
	public static final boolean isDisabledDataBaseConfig  = "true".equals(data.get("system.database.disabled"));	
	public static final boolean isThrowSQLError           = "true".equals(data.get("system.database.throwerror"));	
	public static final String  ccdDataSource             = data.get("system.ccd.datasource");
	
	/*
	 * 数据库密码同步时间间隔
	 */
	public static final int    dataBaseSyncPwdIntvl         = data.get("database.syncpwd.intvl") == null ? 0: Integer.parseInt(data.get("database.syncpwd.intvl"));
	
	public static Map<String,String> getSubsys(){
		return subsys;
	}
	
	private SystemCfg() {

	}

	/**
	 * get instance
	 * 
	 * @return
	 */
	public static synchronized SystemCfg getInstance() {
		return systemcfg;
	}

}