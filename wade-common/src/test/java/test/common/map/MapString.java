/**
 * 
 */
package test.common.map;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.logger.AbstractLogger;
import com.ailk.common.logger.ILogger;

/**
 * @author yifur
 *
 */
public class MapString {

	private static Map<String, ILogger> logger1 = new HashMap<String, ILogger>();
	private static Map<Class<ILogger>, ILogger> logger2 = new HashMap<Class<ILogger>, ILogger>();
	
	public static void main(String[] args) throws Exception {
		while(true) {
			new MapString().test();
		}
	}
	
	public ILogger getLogger1(Class<ILogger> clazz) {
		String name = clazz.getName();
		return logger1.get(name);
	}
	
	public ILogger getLogger2(Class<ILogger> clazz) {
		return logger2.get(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public void test() throws Exception {
		for (int i=0; i<10; i++) {
			Class<ILogger> log = (Class<ILogger>) Class.forName("test.common.map.Logger" + i);
			
			ILogger instance1 = getLogger1(log);
			if (null == instance1) {
				instance1 = log.newInstance();
				logger1.put(instance1.getClass().getName(), instance1);
			}
			
			ILogger instance2 = getLogger2(log);
			if (null == instance2) {
				instance2 = log.newInstance();
				logger2.put(log, instance2);
			}
			
		}
	}
	
	
	public class Logger0 extends AbstractLogger {
		@Override
		public void log(Object object, String subkey, long start, long cost, String content) {
			
		}
	}
	
	public class Logger1 extends AbstractLogger {
		@Override
		public void log(Object object, String subkey, long start, long cost, String content) {
			
		}
	}
	
	public class Logger2 extends AbstractLogger {
		@Override
		public void log(Object object, String subkey, long start, long cost, String content) {
			
		}
	}
	
	
	public class Logger3 extends AbstractLogger {
		@Override
		public void log(Object object, String subkey, long start, long cost, String content) {
			
		}
	}
	
	public class Logger4 extends AbstractLogger {
		@Override
		public void log(Object object, String subkey, long start, long cost, String content) {
			
		}
	}
	
	public class Logger5 extends AbstractLogger {
		@Override
		public void log(Object object, String subkey, long start, long cost, String content) {
			
		}
	}
	
	public class Logger6 extends AbstractLogger {
		@Override
		public void log(Object object, String subkey, long start, long cost, String content) {
			
		}
	}
	
	
	public class Logger7 extends AbstractLogger {
		@Override
		public void log(Object object, String subkey, long start, long cost, String content) {
			
		}
	}
	
	public class Logger8 extends AbstractLogger {
		@Override
		public void log(Object object, String subkey, long start, long cost, String content) {
			
		}
	}
	
	public class Logger9 extends AbstractLogger {
		@Override
		public void log(Object object, String subkey, long start, long cost, String content) {
			
		}
	}
	
}
