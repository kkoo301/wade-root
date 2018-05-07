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
 * 1、验证配置读取
 * 2、验证节点执行
 * 3、验证输入输出参数
 * 4、验证异常处理
 */
public class TestSVF extends BaseSVF {
	
	/**
	 * @param name
	 * @throws Exception
	 */
	public TestSVF(String name) throws Exception {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		TestSVF svf = new TestSVF("test.TestSVF");
		
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("TRADE_STAFF_ID", "TESTSX01");
		request.put("TRADE_STAFF_NAME", "TESTSX01");
		request.put("TRADE_EPARCHY_CODE", "0029");
		request.put("serialNumber", "13787135111");
		
		// 添加流程的trace过滤器
		FlowConfigure.addFilter(new TraceFlowFilter());
		
		IFlowExecutor executor = FlowConfigure.getExecutor();
		Map<String, Object> response = executor.execute(svf, request);
		System.out.println("流程返回内容：" + response);
	}

}
