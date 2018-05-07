/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc.timesten;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dao.impl.TableDAO;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.service.session.app.AppInvoker;
import com.ailk.service.session.app.AppSession;

/**
 * 模拟多个Insert后抛异常，看是否存在事务不回滚的情况
 * 
 * @className: TestTimesTenTransation.java
 * @author: liaosheng
 * @date: 2014-11-17
 */
public class TestTimesTenTransaction {
	
	public static void main(String[] args) {
		try {
			AppInvoker.invoke(null, new TestTimesTenTransaction(), "test", new String[]{});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void test2() throws Exception {
		String dataSourceName = "mdb1";
		String sql = "insert into TS_B_IMME_ADJUST (ACCT_ID, USER_ID) values (:ACCT_ID, :USER_ID)";
		Connection conn = null;
		
		try {
			conn = AppSession.getSession().getConnection(dataSourceName);
			
			TableDAO dao = new TableDAO();
			IData source = new DataMap();
			source.put("ACCT_ID", "1");
			source.put("USER_ID", "1");
			dao.executeUpdate(conn, sql, source);
			
		} catch (Exception e) {
			throw e;
		}
		
		try {
			conn = AppSession.getSession().getConnection(dataSourceName);
			
			TableDAO dao = new TableDAO();
			IData source = new DataMap();
			source.put("ACCT_ID", "2");
			source.put("USER_ID", "2");
			dao.executeUpdate(conn, sql, source);
			
		} catch (Exception e) {
			throw e;
		}
		
		try {
			conn = AppSession.getSession().getConnection(dataSourceName);
			
			TableDAO dao = new TableDAO();
			IData source = new DataMap();
			source.put("ACCT_ID", "3");
			source.put("USER_ID", "3");
			dao.executeUpdate(conn, sql, source);
			
			throw new Exception("Error >>>>>>>>>>>>>>>");
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public void test1() throws Exception {
		String dataSourceName = "mdb1";
		Connection conn = null;
		
		try {
			conn = AppSession.getSession().getConnection(dataSourceName);
			String sql = "insert into TS_B_IMME_ADJUST (ACCT_ID, USER_ID) values (?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, 1);
			stmt.setInt(2, 1);
			stmt.executeUpdate();
			
		} catch (Exception e) {
			throw e;
		}
		
		try {
			String sql = "insert into TS_B_IMME_ADJUST (ACCT_ID, USER_ID) values (?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, 2);
			stmt.setInt(2, 2);
			stmt.executeUpdate();
			
		} catch (Exception e) {
			throw e;
		}
		
		try {
			String sql = "insert into TS_B_IMME_ADJUST (ACCT_ID, USER_ID) values (?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, 3);
			stmt.setInt(2, 3);
			stmt.executeUpdate();
			
			throw new Exception("Error >>>>>>>>>>>>>>>");
		} catch (Exception e) {
			if (null != conn) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}
	
	public void test() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "mdb1");
		String sql = "insert into TS_B_IMME_ADJUST (ACCT_ID, USER_ID) values (:ACCT_ID, :USER_ID)";
		
		IData source = new DataMap();
		source.put("ACCT_ID", "1");
		source.put("USER_ID", "1");
		dao.executeUpdate(sql, source);
		
		source = new DataMap();
		source.put("ACCT_ID", "2");
		source.put("USER_ID", "2");
		dao.executeUpdate(sql, source);
		
		source = new DataMap();
		source.put("ACCT_ID", "3");
		source.put("USER_ID", "3");
		dao.executeUpdate(sql, source);
		
		throw new Exception("这里抛异常了，去表里查下看数据回滚了没");
	}
	
	static {
		DatabaseCfg.getAllDBConfig();
		ConnectionManagerFactory.getInstance();
	}
	
}
