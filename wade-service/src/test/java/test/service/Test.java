package test.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Test {
	
	protected static ThreadLocal<SecretKey> secretKeyThreadLocal = new ThreadLocal<SecretKey>();
	
	public String requestDecrypt(String desKey, String data) throws Exception {
		getKey(desKey);
		return data;
	}
	
	public static SecretKey getKey(String key) throws Exception {
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey sk = keyFactory.generateSecret(desKeySpec);
		return sk;
	}
	
	private static ExecutorService executor = Executors.newFixedThreadPool(100);
	
	public static void main(String[] args) throws Exception {
		Thread.sleep(10000);
		while (true) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					Test t = new Test();
					try {
						String key = "325m@#$rt4vt";
						t.requestDecrypt(key, key);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
	}

}
