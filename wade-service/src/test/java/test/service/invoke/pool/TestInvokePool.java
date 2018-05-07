package test.service.invoke.pool;

import java.util.concurrent.atomic.AtomicLong;

import com.ailk.common.config.SystemCfg;
import com.ailk.service.session.app.AppInvoker;

public class TestInvokePool {
	
	private static final int maxThread = 10;
	public static final long timeout = 10;
	public static AtomicLong count = new AtomicLong(0);
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		long idx = 0;
		while (true) {
			if (idx == 0) {
				new Thread(String.valueOf(0)) {
					public void run() {
						while (true) {
							try {
								AppInvoker.invoke(null, new TestInvokePool(), "call", new String[] {"false"});
							} catch (Exception e) {
								System.out.println("...");
							}
						}
					};
				}.start();
				count.addAndGet(1);
			}
			
			idx ++;
			
			long now = System.currentTimeMillis();
			if (now - start > 5000) {
				add(idx, count.get() % 3 != 0);
				count.addAndGet(1);
				
				start = now;
			}
		}
	}
	
	private static void add(long idx, final boolean stop) {
		new Thread(String.valueOf(idx)) {
			public void run() {
				if (stop) {
					System.out.println("添加线程，当前线程数：" + count.get() + ", 最大线程数：" + maxThread);
				} else {
					System.out.println("添加线程并发起 3 个超时调用，当前线程数：" + count.get() + ", 最大线程数：" + maxThread);
				}
				int t = 0;
				while (true) {
					try {
						if (!stop && t == 0) {
							t = 1;
							AppInvoker.invoke(null, new TestInvokePool(), "call", new String[] {"true"});
							AppInvoker.invoke(null, new TestInvokePool(), "call", new String[] {"true"});
							AppInvoker.invoke(null, new TestInvokePool(), "call", new String[] {"true"});
						} else {
							AppInvoker.invoke(null, new TestInvokePool(), "call", new String[] {"false"});
						}
					} catch (Exception e) {
						System.out.println(e.getMessage() + ", 当前线程数=" + count.get() + ", 最大线程数=" + SystemCfg.serviceInvokeMaxsize);
					}
				}
			};
		}.start();
	}
	
	public void call(String flag) {
		try {
			if ("true".equals(flag)) {
				Thread.sleep(5*1000 + 2000);
			}
		} catch (Exception e) {
		}
		
	}

}
