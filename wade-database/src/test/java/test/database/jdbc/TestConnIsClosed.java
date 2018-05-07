/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

/**
 * 验证连接池获取的连接close状态在多线程下是否存在不同步的问题
 * 场景说明：多线程共享同一个Connection对象，A线程close()后，B线程获取的状态仍是非close状态
 * 
 * @className: TestDBCP.java
 * @author: liaosheng
 * @date: 2014-9-5
 */
public class TestConnIsClosed {
	
	static final Logger log = Logger.getLogger(TestConnIsClosed.class);
	
	public static void main(String[] args) throws Exception {
		
		TestDBCP dbcp = new TestDBCP();
		
		Connection conn = null;
		try {
			BasicDataSource ds = dbcp.getDataSource();
			conn = ds.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new ThreadA(conn).start();
		new ThreadB(conn).start();
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


class ThreadA extends Thread {
	private Connection conn = null;
	
	public ThreadA (Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void run() {
		try {
			PreparedStatement stmt = conn.prepareStatement("select * from tf_f_customer where cust_id=1112092189174340 and partition_id=40");
			
			stmt.executeQuery();
			
			System.out.println(">>>>第一次查询完成，close状态为：" + conn.isClosed());
			
			stmt = conn.prepareStatement("select * from tf_f_customer where cust_name like '%' || :a || '%'");
			stmt.setString(1, "ad");
			
			System.out.println(">>>>第二次查询开始，close状态为：" + conn.isClosed());
			stmt.executeQuery();
			
			System.out.println(">>>>第二次查询完成，close状态为：" + conn.isClosed());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


class ThreadB extends Thread {
	private Connection conn = null;
	
	public ThreadB (Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(10);
			System.out.println(">>>>准备close，close状态为：" + conn.isClosed());
			conn.close();
			System.out.println(">>>>完成close，close状态为：" + conn.isClosed());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}