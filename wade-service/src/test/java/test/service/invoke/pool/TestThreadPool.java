package test.service.invoke.pool;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class TestThreadPool {
	
	private static final int maxThread = 10;
	private static ExecutorService executor = Executors.newFixedThreadPool(maxThread);
	public static final long timeout = 10;
	public static AtomicLong count = new AtomicLong(0);
	
	public static void main(String[] args) throws Exception {
		
		FileInputStream fis = new FileInputStream("d:\\1399194-fp.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String s = br.readLine();
		byte[] b = s.getBytes();
		int a = (int) b[0];
		System.out.println(a);
		
		
		/*long start = System.currentTimeMillis();
		
		
		long idx = 0;
		while (true) {
			if (idx == 0) {
				new Thread(String.valueOf(0)) {
					public void run() {
						while (true) {
							try {
								TestThreadPool.invoke(getName(), false);
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
				add(idx);
				count.addAndGet(1);
				
				start = now;
			}
		}*/
	}
	
	private static void add(long idx) {
		new Thread(String.valueOf(idx)) {
			public void run() {
				System.out.println("添加线程发起一个超时调用，当前线程数：" + count.get() + ", 最大线程数：" + maxThread);
				int cnt = 0;
				while (true) {
					try {
						if (cnt == 0) {
							TestThreadPool.invoke(getName(), true);
							cnt ++;
						} else {
							TestThreadPool.invoke(getName(), false);
						}
					} catch (Exception e) {
						System.out.println("...");
					}
				}
			};
		}.start();
	}
	
	
	public static String invoke(String name, boolean tm) {
		Future<String> future = null;
		
		TestCallable<String> callable = new TestCallable<String>(name, tm);
		try {
			future = executor.submit(callable);
			return future.get(TestThreadPool.timeout, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			System.out.println("ERR:timeout >>" + name + ", 当前启动线程数:" + count.get() + ", 最大线程数：" + maxThread);
		} catch (InterruptedException e) {
			System.out.println("ERR:interrupted >>" + name);
		} catch (ExecutionException e) {
			System.out.println("ERR:execution >>" + name);
		}
		
		return null;
	}

}

class TestCallable<T> implements Callable<T> {
	
	private String name = null;
	boolean timeout = false;
	public TestCallable(String name, boolean timeout) {
		this.name = name;
		this.timeout = timeout;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public T call() throws Exception {
		if (timeout) {
			Thread.sleep(TestThreadPool.timeout * 1000 * 2);
		}
		return null;
	}

}
