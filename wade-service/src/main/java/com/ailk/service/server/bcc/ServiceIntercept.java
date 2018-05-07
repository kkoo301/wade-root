/**
 * 
 */
package com.ailk.service.server.bcc;

import org.apache.log4j.Logger;

import com.ailk.cache.memcache.MemCacheFactory;
import com.ailk.cache.memcache.interfaces.IMemCache;
import com.ailk.service.ServiceManager;
import com.ailk.service.protocol.impl.ServiceEntity;

/**
 * 服务并发控制,以65536=(2^16)毫秒(约一分钟)为区间段控制服务并发量
 * 1.根据接入渠道及服务名控制单位时间内服务调用次数
 * 2.服务调用时长,以UDP的方式发送给统计进程
 * 
 * @author yifur
 * 
 */
public class ServiceIntercept {
	private static final transient Logger log = Logger.getLogger(ServiceIntercept.class);
	private static IMemCache cache = MemCacheFactory.getCache(MemCacheFactory.BCC_CACHE);
	private static ThreadLocal<Long> startTime = new ThreadLocal<Long>();
	
	
	/**
	 * 服务接入开始
	 * @param serviceName
	 * @param inModeCode
	 * @param transSerial
	 * @return
	 */
	public static boolean invokeBefore(String serviceName, String inModeCode, String transSerial) {
		try {
			ServiceEntity entity = ServiceManager.find(serviceName);
			long time = System.currentTimeMillis();
			startTime.set(Long.valueOf(time));
			
			if (entity.getThreshold() == 0) {
				return false;
			} else if (entity.getThreshold() > 0) {
				long currCnt = cache.incr(inModeCode + (time >>= 16));
				
				if (log.isDebugEnabled()) {
					log.debug("当前服务并发数量:[" + currCnt + "]");
				}
				return (currCnt <= entity.getThreshold());
			} else {
				return true;
			}
		} catch (Exception e) {
			if (log.isDebugEnabled())
				e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 服务接入完成
	 * @param serviceName
	 * @param inModeCode
	 * @param transSerial
	 * @return
	 */
	public static boolean invokeAfter(String serviceName, String inModeCode, String transSerial) {
		long time = System.currentTimeMillis();
		if (log.isDebugEnabled()) {
			if (startTime.get() != null)
				log.debug("服务接入耗时:" + (time - startTime.get()));
		}
		return true;
	}
	
}
