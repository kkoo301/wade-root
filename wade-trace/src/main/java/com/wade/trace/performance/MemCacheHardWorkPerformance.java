package com.wade.trace.performance;

import java.util.HashMap;
import java.util.Map;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;

import com.ailk.cache.memcache.performance.IMemCachePerformance;
import com.ailk.mq.util.KafkaUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.trace.ITrace;
import com.wade.trace.TraceContext;
import com.wade.trace.logsystem.LogKeys;
import com.wade.trace.util.IOUtil;
import com.wade.trace.util.SystemUtil;

public class MemCacheHardWorkPerformance implements IMemCachePerformance {

	@Override
	public void report(String cmd, String key, long cCost, long eCost) {
		
		Map<String, Object> logInfo = new HashMap<String, Object>();
		
		ITrace trace = TraceContext.getTrace();
		if (null == trace) {
			return;
		}
		
		String traceId = trace.getTraceId();
		if (StringUtils.isBlank(traceId)) {
			return;
		}
		
		String parentId = trace.getId();
		
		/** 公共基础参数 */
		logInfo.put(LogKeys.PROBE_TYPE, "memcache");
		logInfo.put(LogKeys.ID, SystemUtil.uuid());
		logInfo.put(LogKeys.PARENT_ID, parentId);
		logInfo.put(LogKeys.TRACE_ID, traceId);
		logInfo.put(LogKeys.CMD, cmd);
		logInfo.put(LogKeys.KEY, key);
	    logInfo.put(LogKeys.CCOST, String.valueOf(cCost));
	    logInfo.put(LogKeys.ECOST, String.valueOf(eCost));
	    
    	Producer<byte[], byte[]> producer = KafkaUtil.getProducerInstance();
    	byte[] payload = IOUtil.encode(logInfo);
		producer.send(new KeyedMessage<byte[], byte[]>(LogKeys.TOPIC_TRACE, traceId.getBytes(), payload));   		

	}

}
