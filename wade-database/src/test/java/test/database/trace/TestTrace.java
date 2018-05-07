package test.database.trace;

import java.sql.Connection;

import org.apache.log4j.Logger;


import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;

public class TestTrace {
	
	private static final Logger log = Logger.getLogger(TestTrace.class);
	
	private static IConnectionManager manager = null;
	
	public static void main(String[] args) {
		
		System.setProperty("wade.server.name", "app-node01-srv01");
		
		manager = ConnectionManagerFactory.getConnectionManager();
		
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			TestTrace tt = new TestTrace();
			String dataSourceName = null;
			int code = i % 3;
		
			switch (code) {
				case 0:
					dataSourceName = "cen1";
					break;
				case 1:
					dataSourceName = "sys";
					break;
				case 2:
					dataSourceName = "console";
					break;
				default:
					dataSourceName = "cen1";
					break;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			tt.newConnection(dataSourceName);
			
			if (i % 1000 == 0) {
				if (log.isInfoEnabled()) {
					log.info(manager.traceInfo("cen1"));
					log.info(manager.traceInfo("sys"));
					log.info(manager.traceInfo("console"));
				}
			}
		}
	}
	
	
	/**
	 * 根据数据源名称创建线程
	 * @param dataSourceName
	 */
	private void newConnection(String dataSourceName) {
		new Thread(dataSourceName) {
			
			public void run() {
				Connection conn = null;
				try {
					conn = manager.getConnection(getName());
					if (null != conn) {
						conn.setAutoCommit(false);
					}
					
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (null != conn) {
						try {
							conn.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			};
			
		}.start();
	}

}
