package com.ailk.mq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ailk.mq.client.produce.AsyncTaskClusterCast;

public class MQTester {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void 组播任务() throws IOException {
		Map param = new HashMap();
		param.put("任务名", "这是一个组播任务！");
		param.put("任务编号", "1002");
		//String logId = AsyncTaskMultiCast.getInstance().sendAsyncTask("9006", param);
		//System.out.println("组播任务ID:" + logId);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void 集群任务() throws IOException {
		Map param = new HashMap();
		param.put("任务名", "这是一个集群任务！");
		param.put("任务编号", "1002");
		String logId = AsyncTaskClusterCast.getInstance().sendAsyncTask("1002", param);
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < 2000; i++) {
			logId = AsyncTaskClusterCast.getInstance().sendAsyncTask("1002", param);
		}
		System.out.println("耗时" + (System.currentTimeMillis() - start) + "毫秒");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void 单播任务() throws IOException {
		Map param = new HashMap();
		param.put("任务名", "这是一个单播任务！");
		param.put("任务编号", "1000");
		
		System.out.println("---->");
		//String logId = AsyncTaskUniCast.getInstance().sendAsyncTask("1000", param);
		System.out.println("---->>>>");
		//System.out.println("单播任务ID:" + logId);
	}
	
	public static void main(String[] args) throws IOException {
		
		//MQClientBoot.startup();
		
		//单播任务();
		集群任务();
		//组播任务();
	}
	
}
