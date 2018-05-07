/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月18日
 * 
 * Just Do IT.
 */
package test.com.wade.svf;

import java.util.HashMap;
import java.util.Map;

import com.wade.svf.biz.BaseSVF;
import com.wade.svf.biz.filter.TraceFlowFilter;
import com.wade.svf.flow.FlowConfigure;
import com.wade.svf.flow.executor.IFlowExecutor;

/**
 * @description
 * 1、验证流程执行性能，需要关闭log4j日志，改成INFO级别
 */
public class PerfTestSVF extends BaseSVF {
	
	/**
	 * @param name
	 * @throws Exception
	 */
	public PerfTestSVF(String name) throws Exception {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		PerfTestSVF svf = new PerfTestSVF("test.TestSVF");
		
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("TRADE_STAFF_ID", "TESTSX01");
		request.put("TRADE_STAFF_NAME", "TESTSX01");
		request.put("TRADE_EPARCHY_CODE", "0029");
		request.put("serialNumber", "13787135111");
		
		// 添加流程的trace过滤器
		FlowConfigure.addFilter(new TraceFlowFilter());
		
		while (true) {
			IFlowExecutor executor = FlowConfigure.getExecutor();
			executor.execute(svf, request);
			Thread.sleep(100);
		}
	}

}
