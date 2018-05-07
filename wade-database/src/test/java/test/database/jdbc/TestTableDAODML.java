/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.dao.impl.TableDAO;
import com.ailk.database.jdbc.DataSourceManager;
import com.ailk.database.object.TableMetaObject;

/**
 * 测试单表增删改查功能
 * 
 * @className: Testjava
 * @author: liaosheng
 * @date: 2014-3-25
 */
public class TestTableDAODML {
	
	private static final transient Logger log = Logger.getLogger(TestTableDAODML.class);
	
	public static void main(String[] args) throws Exception {
		TestTableDAODML dml = new TestTableDAODML();
		
		dml.testSave("name1");
		
		dml.testTableDAODML("oracle");
		//dml.testTableDAODML("mysql");
		//dml.testTableDAODML("timesten");
	}
	
	private void testSave(String dataSourceName) throws SQLException {
		DataSourceManager datasource = new DataSourceManager();
		
		String tableName = "TD_S_BIZENV";
		TableMetaObject table = datasource.getDataSource(dataSourceName).getTableMetaData().getTableMetaObject(tableName);
		
		Connection conn = null;
		try {
			conn = datasource.getConnection(dataSourceName);
			conn.setAutoCommit(false);
			
			TableDAO dao = new TableDAO();
			IData source = new DataMap();
			source.put("PARAM_VALUE", "5");
			source.put("PARAM_NAME", "acct.print.fontSize");
			String[] keys = {"PARAM_NAME"};
			String[] values = {"acct.print.fontSize"};
			dao.saveByPK(conn, table, source, keys, values);
			
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != conn)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void testTableDAODML(String dataSourceName) throws SQLException {
		DataSourceManager datasource = new DataSourceManager();
		
		String tableName = "SYS_ADM_ACCT";
		TableMetaObject table = datasource.getDataSource(dataSourceName).getTableMetaData().getTableMetaObject(tableName);
		
		Connection conn = null;
		try {
			conn = datasource.getConnection(dataSourceName);
			conn.setAutoCommit(false);
			
			executeQuery(conn, table);
			
			testRowId(datasource, dataSourceName, table);
			
			insert(conn, table);
			conn.commit();
			
			update(conn, table);
			conn.commit();
			
			save(conn, table);
			conn.commit();
			
			/*executeUpdate(conn, table);
			executeUpdateColonSql(conn, table);
			
			query(conn, table);
			conn = datasource.getConnection(dataSourceName);
			conn.setAutoCommit(false);
			
			delete(conn, table);
			conn.commit();
			
			executeQuery(conn, table);
			
			conn = datasource.getConnection(dataSourceName);
			conn.setAutoCommit(false);
			executeQueryColonSql(conn, table);
			
			conn = datasource.getConnection(dataSourceName);
			conn.setAutoCommit(false);
			executeInsertBatch(conn, table);
			conn.commit();
			
			executeUpdateBatch(conn, table);
			conn.commit();
			
			executeBatchSql(conn, table);
			conn.commit();
			
			executeBatchSql(conn);
			conn.commit();
			
			executeDeleteBatch(conn, table);
			conn.commit();
			
			getSequence(conn, table);
			conn = datasource.getConnection(dataSourceName);
			
			count(conn, table);*/
			
		} catch (SQLException e) {
			try {
				if (null != conn)
					conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} finally {
				e.printStackTrace();
			}
		} finally {
			try {
				if (null != conn)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getSequence(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		String nextval = dao.getSequence(conn, table, "SEQ_ACCT_ID", 1);
		log.debug("nextval:" + nextval);
	}
	
	private int insert(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		
		IData source = new DataMap();
		source.put("ACCT_ID", "99");
		source.put("ACCT_NAME", "");
		source.put("STATUS", "U");
		source.put("ACCT_SN", "137111111");
		source.put("CREATE_TIME", "2013-04-06 12:11:11");
		
		dao.insert(conn, table, source);
		
		return dao.insert(conn, table, 
				new String[] {"ACCT_ID", "ACCT_NAME", "STATUS", "ACCT_SN", "CREATE_TIME"}, 
				new String[] {"98", "98", "U", "137", "2013-04-06 12:11:11"});
	}
	
	
	private int update(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		
		IData source = new DataMap();
		source.put("ACCT_ID", "99");
		source.put("ACCT_NAME", "99");
		source.put("STATUS", "U");
		source.put("ACCT_SN", "13722222");
		source.put("CREATE_TIME", "2013-04-09 12:11:11");
		
		dao.updateByPK(conn, table, null, source, null, null);
		
		return dao.updateByPK(conn, table, new String[] {"ACCT_NAME"}, new String[]{"9912"}, new String[] {"ACCT_ID"}, new String[] {"98"});
	}
	
	private int save(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		
		IData source = new DataMap();
		source.put("ACCT_ID", "99");
		source.put("ACCT_NAME", "99");
		source.put("STATUS", "U");
		source.put("ACCT_SN", "137");
		source.put("CREATE_TIME", "2013-06-09 12:11:11");
		dao.saveByPK(conn, table, source, null, null);
		
		return dao.saveByPK(conn, table, 
				new String[] {"ACCT_NAME"}, 
				new String[] {"91123"}, 
				new String[] {"ACCT_ID"}, 
				new String[] {"98"});
	}
	
	private void query(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		
		IData source = new DataMap();
		source.put("ACCT_ID", "99");
		source.put("ACCT_NAME", "99");
		source.put("STATUS", "U");
		source.put("ACCT_SN", "137");
		
		/*IData map1 = dao.queryByPK(conn, table, null, source);
		log.debug(map1);*/
		
		IData map2 = dao.queryByPK(conn, table, new String[] {"ACCT_ID"}, new String[] {"98"});
		log.debug(map2);
	}
	
	
	private int delete(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		
		IData source = new DataMap();
		source.put("ACCT_ID", "99");
		source.put("STATUS", "U");
		
		dao.deleteByPK(conn, table, null, source);
		return dao.deleteByPK(conn, table, new String[] {"ACCT_ID"}, new String[] {"98"});
	}
	
	
	private void executeQuery(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select a.acct_id, b.right_id");
		sql.append(" from sys_adm_acct a, sys_adm_acct_right b, sys_adm_right c");
		sql.append(" where a.acct_id = b.acct_id");
		sql.append(" and b.right_id = c.right_id");
		sql.append(" and a.acct_id = ? ");
		
		//sql.append("select PACKAGE_NAME, PACKAGE_ID from TD_B_PACKAGE where PACKAGE_ID = ?");
		
		IDataset data = dao.executeQuery(conn, table, sql.toString(), new String[] {"1000"}, 0, 2, 20);
		log.debug(data);
	}
	
	
	private void executeQueryColonSql(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		
		StringBuilder colonSql = new StringBuilder();
		colonSql.append("select a.acct_id, b.right_id");
		colonSql.append(" from sys_adm_acct a, sys_adm_acct_right b, sys_adm_right c");
		colonSql.append(" where a.acct_id = b.acct_id");
		colonSql.append(" and b.right_id = c.right_id");
		colonSql.append(" and a.acct_id = :ACCT_ID ");
		IData source = new DataMap();
		source.put("ACCT_ID", "1000");
		
		IDataset data = dao.executeQuery(conn, table, colonSql.toString(), source, 0, 3, 20);
		log.debug(data);
	}
	
	
	private void executeUpdate(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		StringBuilder sql = new StringBuilder();
		sql.append("update sys_adm_acct a set a.acct_name = a.acct_name where a.acct_id = ?");
		dao.executeUpdate(conn, sql.toString(), new String[] {"1000"});
	}
	
	private void executeUpdateColonSql(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		StringBuilder sql = new StringBuilder();
		sql.append("update sys_adm_acct a set a.acct_name = a.acct_name where a.acct_id = :ACCT_ID");
		
		IData source = new DataMap();
		source.put("ACCT_ID", "1000");
		dao.executeUpdate(conn, sql.toString(), source);
	}

	private void executeInsertBatch(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		IDataset source = new DatasetList(100);
		for (int i = 0, cnt = 23; i < cnt ; i++) {
			IData map =new DataMap(4);
			map.put("ACCT_ID", "999" + i);
			map.put("ACCT_NAME", "1111");
			map.put("ACCT_SN", "1238711");
			map.put("STATUS", "U");
			map.put("CREATE_TIME", "2013-04-06 12:11:11");
			source.add(map);
		}
		dao.insert(conn, table, source, 4);
	}
	
	
	private void executeDeleteBatch(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		IDataset source = new DatasetList(100);
		for (int i = 0, cnt = 23; i < cnt ; i++) {
			IData map =new DataMap(4);
			map.put("ACCT_ID", "999" + i);
			map.put("ACCT_NAME", "1111");
			map.put("ACCT_SN", "1238711");
			map.put("STATUS", "U");
			source.add(map);
		}
		dao.delete(conn, table, source, new String[] {"ACCT_ID"}, 4);
	}
	
	private void executeUpdateBatch(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		IDataset source = new DatasetList(100);
		for (int i = 0, cnt = 23; i < cnt ; i++) {
			IData map =new DataMap(4);
			map.put("ACCT_ID", "999" + i);
			map.put("ACCT_NAME", "2222");
			map.put("ACCT_SN", "33333");
			map.put("STATUS", "U");
			map.put("CREATE_TIME", "2015-01-04");
			source.add(map);
		}
		dao.update(conn, table, source, null, new String[] {"ACCT_ID"}, 4);
	}
	
	private void executeBatchSql(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		String[] sqls = new String[23];
		for (int i = 0, cnt = 23; i < cnt ; i++) {
			sqls[i] = "update sys_adm_acct set acct_sn = 'xxxxx' where acct_id = 999" + i;
		}
		dao.executeBatch(conn, sqls, 4);
	}
	
	private void executeBatchSql(Connection conn) throws SQLException {
		TableDAO dao = new TableDAO();
		
		String colonSql = "update sys_adm_acct set ACCT_SN=:ACCT_SN where ACCT_ID=:ACCT_ID";
		
		IDataset source = new DatasetList(100);
		for (int i = 0, cnt = 23; i < cnt ; i++) {
			IData map =new DataMap(4);
			map.put("ACCT_ID", "999" + i);
			map.put("ACCT_NAME", "2222");
			map.put("ACCT_SN", "66666");
			map.put("STATUS", "U");
			source.add(map);
		}
		dao.executeBatch(conn, colonSql, source, 4);
	}
	
	
	private void count(Connection conn, TableMetaObject table) throws SQLException {
		TableDAO dao = new TableDAO();
		
		String colonSql = "select count(1) as CNT from sys_adm_acct where status = :STATUS group by status";
		
		IData source = new DataMap(4);
		source.put("STATUS", "U");
			
		log.debug(dao.count(conn, table, colonSql, source));
	}
	
	private void testRowId(DataSourceManager datasource, String dataSourceName, TableMetaObject table) throws SQLException {
		Connection conn = datasource.getConnection(dataSourceName);
		conn.setAutoCommit(false);
		
		TableDAO dao = new TableDAO();
		IData data = dao.queryByPK(conn, table, new String[] {"ACCT_ID"}, new String[]{"199"});
		if (null != data)
			System.out.println(">>>>" + data);
		else {
			return ;
		}
		
		conn = datasource.getConnection("oracle");
		String rowid = data.getString("ROWID");
		dao.updateByPK(conn, table, new String[] {"ACCT_NAME"}, new String[]{"9912"}, new String[] {"ROWID"}, new String[] {rowid});
		
		dao.deleteByPK(conn, table, new String[]{"ROWID"}, new String[]{rowid});
		
		dao.insert(conn, table, data);
	}
}
