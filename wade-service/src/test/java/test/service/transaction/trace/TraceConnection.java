package test.service.transaction.trace;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Visit;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;
import com.ailk.service.session.app.AppInvoker;


/**
 * 模拟并发调服务，验证数据源的跟踪是否存在close后不能清除的日志
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: TraceConnection.java
 * @author: liaosheng
 * @date: 2015-4-1
 */
public class TraceConnection {
	
	private static final Logger log = Logger.getLogger(TraceConnection.class);
	
	public static void main(String[] args) {
		System.setProperty("wade.server.name", "app-node01-srv01");
		
		IConnectionManager mgr = ConnectionManagerFactory.getConnectionManager();
		
		for (int i = 0 ; i < 1; i++) {
			test(i);
		}
	}
	
	public static void test(int index) {
		new Thread(String.valueOf(index)) {
			
			public void run() {
				IConnectionManager mgr = ConnectionManagerFactory.getConnectionManager();
				
				int i = 0;
				while (true) {
					TraceConnection service = new TraceConnection();
					try {
						AppInvoker.invoke(new Visit(), service, "traceConection", new String[] {"name"});
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					i++;
					
					if (i % 1000 == 0) {
						if (log.isInfoEnabled()) {
							log.info("======================漂亮的分界线======================");
							log.info(mgr.traceInfo("cen"));
							log.info(mgr.traceInfo("sys"));
							log.info(mgr.traceInfo("crm1"));
							log.info("======================漂亮的分界线======================");
						}
					}
				}
			};
			
		}.start();
	}
	
	
	
	/**
	 * 服务方法，操作多个库
	 */
	public void traceConection(String name) throws Exception {
		BaseDAO cen = DAOManager.createDAO(BaseDAO.class, "cen");
		IData data1 = cen.queryByPK("sys_adm_acct", new DataMap());
		
		BaseDAO sys = DAOManager.createDAO(BaseDAO.class, "sys");
		IData data2 = cen.queryByPK("sys_adm_acct", new DataMap());
		
		cen.delete("sys_adm_acct", new String[] {"ACCT_ID"}, new String[] {"1921"});
		
		BaseDAO console = DAOManager.createDAO(BaseDAO.class, "crm1");
		IData data3 = cen.queryByPK("sys_adm_acct", new DataMap());
	}

}
