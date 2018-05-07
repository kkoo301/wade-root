/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

/**
 * TODO
 * 
 * @className: TestDBCP.java
 * @author: liaosheng
 * @date: 2014-9-5
 */
public class TestDBCP {
	
	static final Logger log = Logger.getLogger(TestDBCP.class);
	
	static {
		//ConnectionManagerFactory.getConnectionManager();
	}
	
	public static void main(String[] args) throws Exception {
		
		/*TestDBCP dbcp = new TestDBCP();
		try {
			BasicDataSource ds = dbcp.getDataSource();
			Connection conn = ds.getConnection();
			
			conn.prepareStatement("select 1 from dual").executeQuery();
			
			Thread.sleep(10);
			
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		new Thread() {
			
			public void run() {
				
				TestDBCP dbcp = new TestDBCP();
				try {
					BasicDataSource ds = dbcp.getDataSource();
					int i = 0;
					while (true) {
						//System.out.println(">>>>当前活动:" + ds.getNumActive() + ",当前空闲:" + ds.getNumIdle());
						
						if (i < 3) {
							Connection conn = ds.getConnection();
							
							conn.setAutoCommit(false);
							
							conn.prepareStatement("select 1 from dual").executeQuery();
							
							conn.close();
							i++;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
			
		}.start();
		
		Thread.sleep(1000000);
	}
	
	
	public BasicDataSource getDataSource() throws SQLException {
		
		long start = System.currentTimeMillis();
		
		BasicDataSource datasource = new BasicDataSource();

		String initialSize = "1";
		String maxActive = "5";
		String maxIdle = "5";
		String maxWait = "10000";;	
		String driver = "oracle.jdbc.driver.OracleDriver";
		String user = "UOP_CRM1";
		String passwd = "UOP_CRM1";
		String url = "jdbc:oracle:thin:@10.200.130.62:1521:ngact";
		
		datasource.setDriverClassName(driver);
		datasource.setUsername(user);
		datasource.setPassword(passwd);
		datasource.setUrl(url);
		datasource.setInitialSize(Integer.parseInt(initialSize));
		datasource.setMaxActive(Integer.parseInt(maxActive));
		datasource.setMaxIdle(Integer.parseInt(maxIdle));
		datasource.setMinIdle(Integer.parseInt(initialSize));
		datasource.setMaxWait(Integer.parseInt(maxWait));
		
		datasource.setTestWhileIdle(true);
		datasource.setTestOnBorrow(false);
		datasource.setTestOnReturn(false);
		datasource.setValidationQuery("select 1 from dual");
		datasource.setTimeBetweenEvictionRunsMillis(5000);
		datasource.setNumTestsPerEvictionRun(2);
		datasource.setMinEvictableIdleTimeMillis(10000);
		
		// 初始化
		//Connection conn = datasource.getConnection();
		//conn.close();

		log.info("");
		log.info("---------- 创建" + user + "数据库连接池成功!---------- ");
		log.info("数据库驱动:" + driver);
		log.info("数据库URL:" + url);
		log.info("初始连接数:" + initialSize);
		log.info("最大连接数:" + maxActive);
		log.info("最大闲置数:" + maxIdle);
		log.info("最大等待时间:" + maxWait + "毫秒");
		log.info("创建" + user + "数据库连接耗时" + (System.currentTimeMillis() - start ) + "毫秒");
		
		return datasource;
	}

}
