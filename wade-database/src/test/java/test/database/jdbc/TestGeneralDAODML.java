/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc;

import java.sql.SQLException;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.dao.impl.GeneralDAO;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.service.session.app.AppInvoker;

/**
 * 测试单表增删改查功能
 * 
 * @className: TestDML.java
 * @author: liaosheng
 * @date: 2014-3-25
 */
public class TestGeneralDAODML {
	
	public static void main(String[] args) throws Exception {
		System.setProperty("wade.server.name", "database-test");
		
		String tableName = "SYS_ADM_ACCT";
		
		TestGeneralDAODML dml = new TestGeneralDAODML();
		
		ConnectionManagerFactory.getConnectionManager();
		
		try {
			AppInvoker.invoke(null, dml, "testQueryDML", new String[] {"oracle", "mysql", tableName});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//AppInvoker.shutdown();
		}
	}
	
	public void testAllDML(String dataSourceName, String tableName) throws SQLException {
		getSequence(dataSourceName);
		insert(dataSourceName, tableName);
		update(dataSourceName, tableName);
		save(dataSourceName, tableName);
		query(dataSourceName, tableName);
		delete(dataSourceName, tableName);
		
		executeQuery(dataSourceName);
		executeQueryColonSql(dataSourceName);
		
		executeUpdate(dataSourceName);
		executeUpdateColonSql(dataSourceName);
	}
	
	public void testQueryDML(String dataSourceName1, String dataSourceName2, String tableName) throws SQLException {
		query(dataSourceName1, tableName);
		
		testAllDML(dataSourceName1, tableName);
	}
	
	private void getSequence(String dataSourceName) throws SQLException {
		GeneralDAO dao = new GeneralDAO();
		String nextval = dao.getSequence(dataSourceName, "SEQ_ACCT_ID");
		System.out.println("nextval:" + nextval);
	}
	
	private int insert(String dataSourceName, String tableName) throws SQLException {
		GeneralDAO dao = new GeneralDAO();
		
		IData source = new DataMap();
		source.put("ACCT_ID", "999999");
		source.put("ACCT_NAME", "");
		source.put("STATUS", "U");
		source.put("ACCT_SN", "137");
		
		dao.insert(dataSourceName, tableName, source);
		
		return dao.insert(dataSourceName, tableName, 
				new String[] {"ACCT_ID", "ACCT_NAME", "STATUS", "ACCT_SN"}, 
				new String[] {"100000", "100000", "U", "137"});
	}
	
	
	private int update(String dataSourceName, String tableName) throws SQLException {
		GeneralDAO dao = new GeneralDAO();
		
		IData source = new DataMap();
		source.put("ACCT_ID", "999999");
		source.put("ACCT_NAME", "999999");
		source.put("STATUS", "U");
		source.put("ACCT_SN", "137");
		
		dao.updateByPK(dataSourceName, tableName, null, source, null, null);
		
		return dao.updateByPK(dataSourceName, tableName, new String[] {"ACCT_NAME"}, new String[]{"99999912"}, new String[] {"ACCT_ID"}, new String[] {"100000"});
	}
	
	private int save(String dataSourceName, String tableName) throws SQLException {
		GeneralDAO dao = new GeneralDAO();
		
		IData source = new DataMap();
		source.put("ACCT_ID", "999999");
		source.put("ACCT_NAME", "999999");
		source.put("STATUS", "U");
		source.put("ACCT_SN", "137");
		dao.saveByPK(dataSourceName, tableName, source, null, null);
		
		return dao.saveByPK(dataSourceName, tableName, 
				new String[] {"ACCT_NAME"}, 
				new String[] {"91123"}, 
				new String[] {"ACCT_ID"}, 
				new String[] {"100000"});
	}
	
	private void query(String dataSourceName, String tableName) throws SQLException {
		GeneralDAO dao = new GeneralDAO();
		
		IData source = new DataMap();
		source.put("ACCT_ID", "999999");
		source.put("ACCT_NAME", "999999");
		source.put("STATUS", "U");
		source.put("ACCT_SN", "137");
		
		IData map1 = dao.queryByPK(dataSourceName, tableName, null, source);
		System.out.println(map1);
		
		IData map2 = dao.queryByPK(dataSourceName, tableName, new String[] {"ACCT_ID"}, new String[] {"100000"});
		System.out.println(map2);
	}
	
	
	private int delete(String dataSourceName, String tableName) throws SQLException {
		GeneralDAO dao = new GeneralDAO();
		
		IData source = new DataMap();
		source.put("ACCT_ID", "999999");
		source.put("STATUS", "U");
		
		dao.deleteByPK(dataSourceName, tableName, null, source);
		return dao.deleteByPK(dataSourceName, tableName, new String[] {"ACCT_ID"}, new String[] {"100000"});
	}
	
	private void executeQuery(String dataSourceName) throws SQLException {
		GeneralDAO dao = new GeneralDAO();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select a.acct_id, b.right_id");
		sql.append(" from sys_adm_acct a, sys_adm_acct_right b, sys_adm_right c");
		sql.append(" where a.acct_id = b.acct_id");
		sql.append(" and b.right_id = c.right_id");
		sql.append(" and a.acct_id = ? ");
		
		IDataset data = dao.executeQuery(dataSourceName, sql.toString(), new String[] {"1000"}, 0, 2, 20);
		System.out.println(data);
	}
	
	
	private void executeQueryColonSql(String dataSourceName) throws SQLException {
		GeneralDAO dao = new GeneralDAO();
		
		StringBuilder colonSql = new StringBuilder();
		colonSql.append("select a.acct_id, b.right_id");
		colonSql.append(" from sys_adm_acct a, sys_adm_acct_right b, sys_adm_right c");
		colonSql.append(" where a.acct_id = b.acct_id");
		colonSql.append(" and b.right_id = c.right_id");
		colonSql.append(" and a.acct_id = :ACCT_ID ");
		IData source = new DataMap();
		source.put("ACCT_ID", "1000");
		
		IDataset data = dao.executeQuery(dataSourceName, colonSql.toString(), source, 0, 3, 20);
		System.out.println(data);
	}
	
	
	private void executeUpdate(String dataSourceName) throws SQLException {
		GeneralDAO dao = new GeneralDAO();
		StringBuilder sql = new StringBuilder();
		sql.append("update sys_adm_acct a set a.acct_name = a.acct_name where a.acct_id = ?");
		dao.executeUpdate(dataSourceName, sql.toString(), new String[] {"1000"});
	}
	
	private void executeUpdateColonSql(String dataSourceName) throws SQLException {
		GeneralDAO dao = new GeneralDAO();
		StringBuilder sql = new StringBuilder();
		sql.append("update sys_adm_acct a set a.acct_name = a.acct_name where a.acct_id = :ACCT_ID");
		
		IData source = new DataMap();
		source.put("ACCT_ID", "1000");
		dao.executeUpdate(dataSourceName, sql.toString(), source);
	}

}
