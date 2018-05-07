/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.transaction;

import java.util.concurrent.atomic.AtomicInteger;

import test.service.transaction.service.TestTranscationService;

import com.ailk.common.data.IData;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Visit;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.service.BaseService;
import com.ailk.service.session.app.AppInvoker;

/**
 * 模拟事务控制的多种场景，服务超时、服务异常、服务全局异步事务、服务独立异步事务
 * 
 * 初始化数据库脚本
 * -- Create table create table WD_STATIC ( TYPE_ID VARCHAR2(30) not null,
 * DATA_ID VARCHAR2(50) not null, DATA_NAME VARCHAR2(100) not null ); alter
 * table WD_STATIC add constraint PK_WDSTATIC primary key (DATA_ID, TYPE_ID)
 * using index ;
 * 
 * -- Create table create table WD_STATIC_ASYNC ( TYPE_ID VARCHAR2(30) not null,
 * DATA_ID VARCHAR2(50) not null, DATA_NAME VARCHAR2(100) not null ); alter
 * table WD_STATIC_ASYNC add constraint PK_WDSTATICASYNC primary key (DATA_ID,
 * TYPE_ID) using index;
 * 
 * -- Create table create table WD_STATIC_SINGLE ( TYPE_ID VARCHAR2(30) not
 * null, DATA_ID VARCHAR2(50) not null, DATA_NAME VARCHAR2(100) not null ); --
 * Create/Recreate primary, unique and foreign key constraints alter table
 * WD_STATIC_SINGLE add constraint PK_WDSTATICSYNCLE primary key (DATA_ID,
 * TYPE_ID) using index ;
 * 
 * @className: TestTransactionDBConn.java
 * @author: liaosheng
 * @date: 2014-9-10
 */
public class TestTransactionDBConn {

	static volatile AtomicInteger index = new AtomicInteger(0);
	static volatile int count = 10;

	static volatile AtomicInteger errorAll = new AtomicInteger(0);
	static volatile AtomicInteger timeout = new AtomicInteger(0);
	static volatile AtomicInteger throwExce = new AtomicInteger(0);

	static volatile AtomicInteger asynGlobalConn = new AtomicInteger(0);
	static volatile AtomicInteger asynConn = new AtomicInteger(0);
	
	static volatile AtomicInteger sqlTimeout = new AtomicInteger(0);

	public static void main(String[] args) {

		for (int i = 0; i < 2; i++) {
			new Thread() {
				public void run() {
					int idx = index.incrementAndGet();

					while (idx <= count) {
						IData param = new DataMap();

						System.out.println("run:" + idx);

						// 模拟一次服务超时
						if (idx % 106 == 0) {
							param.put("ACTION", "1");
							timeout.incrementAndGet();
							System.out.println("服务超时: " + timeout.get() + "=" + idx);
							invoke(param);

							idx = index.incrementAndGet();

							continue;
						}

						// 模拟一次服务抛错
						if (idx % 127 == 0) {
							param.put("ACTION", "2");
							throwExce.incrementAndGet();
							System.out.println("服务抛错:" + throwExce.get() + "=" + idx);
							invoke(param);

							idx = index.incrementAndGet();

							continue;
						}

						// 模拟一次语句超时设置
						if (idx % 138 == 0) {
							param.put("ACTION", "3");
							invoke(param);

							idx = index.incrementAndGet();
							continue;
						}

						// 模拟一次全局异步事务
						if (idx % 155 == 0) {
							param.put("ACTION", "4");
							asynGlobalConn.incrementAndGet();
							System.out.println("全局异步事务:" + asynGlobalConn.get() + "=" + idx);
							invoke(param);

							idx = index.incrementAndGet();
							continue;
						}

						// 模拟一次独立事务
						if (idx % 171 == 0) {
							param.put("ACTION", "5");
							asynConn.incrementAndGet();
							System.out.println("独立事务:" + asynConn.get() + "=" + idx);
							invoke(param);

							idx = index.incrementAndGet();
							continue;
						}
						
						invoke(param);
						idx = index.incrementAndGet();
					}
				};

				private void invoke(IData param) {
					IVisit context = new Visit();
					BaseService service = new TestTranscationService();
					try {
						AppInvoker.invoke(context, service, "test", new Object[] { param }, 3);
					} catch (Exception e) {
						errorAll.incrementAndGet();
					}
				}
			}.start();
		}

		// 每30秒监控一次当前连接池的连接情况，若连接池有线程栈未释放，说明逻辑存在问题
		while (true) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}
			System.out.println(ConnectionManagerFactory.getConnectionManager().traceInfo("name1"));

			StringBuilder sb = new StringBuilder();
			sb.append("超时数：");
			sb.append(timeout.get());
			sb.append("; 异常数：");
			sb.append(throwExce.get());
			sb.append("; 全局事务数：");
			sb.append(asynGlobalConn.get());
			sb.append("; 独立事务数：");
			sb.append(asynConn.get());
			sb.append("; 所有异常数：");
			sb.append(errorAll.get());
			sb.append(">>>\n");
			sb.append(errorAll.get() == timeout.get() + throwExce.get() ? "正常" : "异常");
			System.out.println(sb.toString());
		}
	}

}
