package com.ailk.mq.task;

import java.util.Map;
import com.ailk.mq.client.consume.AsyncTaskExecutor;

public class AsyncMessageReceiver0 extends AsyncTaskExecutor {

	@Override
	public void doAsyncTask(String taskId, Map<String, Object> param) {
		
		System.out.println("我收到一个异步任务，taskId=" + taskId + " 参数：" + param);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
