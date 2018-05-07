/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.invoke.so;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;
import com.ailk.service.session.app.AppSession;

import test.service.invoke.lock.TestServiceLock;
import test.service.invoke.lock.TestServiceShareObject;


/**
 * 服务对象
 * 
 * @className: TestServiceObject.java
 * @author: liaosheng
 * @date: 2014-3-27
 */
public class TestServiceObject {
	
	private static final Logger log = Logger.getLogger(TestServiceObject.class);

	public IData service(String id) throws Exception {
		String dataSourceName = "oracle";
		IData map =  test1(dataSourceName);
		
		TestServiceShareObject cache = (TestServiceShareObject) AppSession.getSession().getShareObject(TestServiceShareObject.class);
		cache.testToDo("world");
		
		boolean lock = AppSession.getSession().lock(TestServiceLock.class, new Object[] {"world"});
		log.debug(">>>> lock " + lock);
		
		for (int i = 0; i < 2; i++)
			map =  test2(dataSourceName);
		
		return map;
	}
	
	public IData query(String flag) throws Exception {
		IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
		Connection conn = null;
		
		if ("1".equals(flag)) {
			for (int i = 0; i < 10; i++) {
				conn = manager.getConnection("name1");
			}
		} else {
			try {
				conn = manager.getConnection("name1");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != conn) {
					conn.close();
				}
			}
			
			BaseException be = new BaseException("111");
			IData err = new DataMap();
			err.put("X_RESULTCODE", "10000");
			be.setData(err);
			throw be;
		}
		
		return null;
	}
	
	
	public IData callProc() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "crm1");
		String procName = "";
		String[] paramNames = { "IN_TRADE_ID", "IN_ACCEPT_MONTH", "IN_CANCEL_TAG", "OUT_SYNC_SEQUENCE", "OUT_RESULT_CODE", "OUT_RESULT_INFO" };
		IData params = new DataMap();
		params.put("IN_TRADE_ID", "1114061802023211");
		params.put("IN_ACCEPT_MONTH", "6");
		params.put("IN_CANCEL_TAG", "0");
		params.put("OUT_SYNC_SEQUENCE", null);
		params.put("OUT_RESULT_CODE", null);
		params.put("OUT_RESULT_INFO", null);
		dao.callProc(procName, paramNames, params);
		
		System.out.println(params);
		
		return params;
	}
	
	/**
	 * 测试字符串方式的DAO API
	 * @return
	 * @throws Exception
	 */
	public IData test1(String dataSourceName) throws Exception {
		AcctDAO dao = new AcctDAO();
		
		dao.getSequence(dataSourceName, "SEQ_ACCT_ID", 10);
		
		IData data = dao.queryByPK(dataSourceName, "SYS_ADM_ACCT", new String[] {"ACCT_ID"},  new String[] {"1000"});
		
		AppSession.getSession().lock(TestServiceLock.class, new String[] {"1000"});
		
		dao.deleteByPK(dataSourceName, "SYS_ADM_ACCT", new String[] {"ACCT_ID"}, new String[] {"1004"});
		
		dao.queryTable(dataSourceName, "SYS_ADM_ACCT", 
				new String[] {}, 
				new String[] {}, 
				0, 
				100);
		
		AppSession.getSession().lock(TestServiceLock.class, new String[] {"1001"});
		
		dao.countTable(dataSourceName, "SYS_ADM_ACCT",
				new String[] {"ACCT_SN"}, 
				new String[] {"13787135"});
		
		/*dao.insert(dataSourceName, "SYS_ADM_ACCT", 
				new String[] {"ACCT_SN", "ACCT_ID", "ACCT_NAME", "STATUS"}, 
				new String[] {"13787135", "1003", "老吉", "U"});*/
		
		boolean unlock = AppSession.getSession().unlock(TestServiceLock.class, new String[] {"1000"});
		log.debug(">>>> unlock " + unlock);
		
		dao.updateByPK(dataSourceName, "SYS_ADM_ACCT",
				new String[] {"ACCT_NAME", "ACCT_SN"}, 
				new String[] {"12231", "121212"}, 
				new String[] {"ACCT_ID"}, 
				new String[] {"1004"});
		
		AppSession.getSession().lock(TestServiceLock.class, new String[] {"1003"});
		
		dao.saveByPK(dataSourceName, "SYS_ADM_ACCT",
				new String[] {"ACCT_SN", "ACCT_NAME"}, 
				new String[] {"1378713", "老唐"}, 
				new String[] {"ACCT_ID"}, 
				new String[] {"1000"});
		
		StringBuilder sql = new StringBuilder();
		sql.append("select m.menu_id, m.menu_text, m.menu_title, m.menu_index, r.right_id").append("\n");
		sql.append("from sys_adm_menu m, sys_adm_right r, sys_adm_menu_right mr, SYS_ADM_ACCT_right a").append("\n");
		sql.append("where m.menu_id = mr.menu_id").append("\n");
		sql.append("and r.right_id = mr.right_id").append("\n");
		sql.append("and r.right_id = a.right_id").append("\n");
		sql.append("and m.parent_menu_id = ?").append("\n");
		sql.append("and m.menu_level = ?").append("\n");
		sql.append("and a.acct_id = ?").append("\n");
		sql.append("and a.status = 'U'").append("\n");
		sql.append("order by m.menu_index");
		
		dao.executeQuery(dataSourceName, 
				sql.toString(), 
				new String[] {"1000", "2", "1000"},
				1,
				100, 100);
		
		
		sql = new StringBuilder();
		sql.append("update SYS_ADM_ACCT set status = ? where acct_id = ?");
		dao.executeUpdate(dataSourceName, sql.toString(), new String[] {"U", "1007"});
		
		return data;
	}
	
	
	/**
	 * 测试Map方式的DAO API
	 * @return
	 * @throws Exception
	 */
	public IData test2(String dataSourceName) throws Exception {
		AcctDAO dao = new AcctDAO();
		
		TestServiceShareObject cache = AppSession.getSession().getShareObject(TestServiceShareObject.class);
		cache.testToDo("fox");
		
		boolean lock = AppSession.getSession().lock(TestServiceLock.class, new Object[] {"fox"});
		log.debug(">>>> lock " + lock);
		
		IData source = new DataMap(10);
		source.put("ACCT_ID", "1011");
		source.put("ACCT_SN", "13787135");
		source.put("ACCT_NAME", "12231");
		source.put("PARENT_MENU_ID", "1000");
		source.put("MENU_LEVEL", "2");
		source.put("BEGIN", "1");
		source.put("END", "100");
		source.put("STATUS", "U");
		
		dao.getSequence(dataSourceName, "SEQ_ACCT_ID", 10);
		
		IData data = dao.queryByPK(dataSourceName, "SYS_ADM_ACCT", new String[] {"ACCT_ID"}, source);
		
		dao.deleteByPK(dataSourceName, "SYS_ADM_ACCT", new String[] {"ACCT_ID"}, source);
		
		dao.queryTable(dataSourceName, "SYS_ADM_ACCT", 
				new String[] {}, 
				source, 
				0, 
				100);
		
		dao.countTable(dataSourceName, "SYS_ADM_ACCT", 
				new String[] {"ACCT_SN"}, 
				source);
		
		/*source.put("ACCT_ID", "1011");
		dao.insert(dataSourceName, source);*/
		
		source.put("ACCT_ID", "1000");
		dao.updateByPK(dataSourceName, "SYS_ADM_ACCT", 
				new String[] {"ACCT_NAME", "ACCT_SN"}, 
				source,
				new String[] {"ACCT_ID"}, 
				new String[] {"1004"});
		
		dao.updateByPK(dataSourceName, "SYS_ADM_ACCT", 
				new String[] {"ACCT_NAME", "ACCT_SN"}, 
				source,
				null, 
				null);
		
		dao.updateByPK(dataSourceName, "SYS_ADM_ACCT", 
				new String[] {"ACCT_NAME", "ACCT_SN"}, 
				source,
				new String[] {"ACCT_ID"}, 
				null);
		
		dao.saveByPK(dataSourceName, "SYS_ADM_ACCT", 
				source,
				new String[] {"ACCT_ID"}, 
				new String[] {"1011"});
		
		StringBuilder sql = new StringBuilder();
		sql.append("select m.menu_id, m.menu_text, m.menu_title, m.menu_index, r.right_id").append("\n");
		sql.append("from sys_adm_menu m, sys_adm_right r, sys_adm_menu_right mr, SYS_ADM_ACCT_right a").append("\n");
		sql.append("where m.menu_id = mr.menu_id").append("\n");
		sql.append("and r.right_id = mr.right_id").append("\n");
		sql.append("and r.right_id = a.right_id").append("\n");
		sql.append("and m.parent_menu_id = :parent_menu_id").append("\n");
		sql.append("and m.menu_level = :menu_level").append("\n");
		sql.append("and a.acct_id = :acct_id").append("\n");
		sql.append("and a.status = 'U'").append("\n");
		sql.append("order by m.menu_index");
		
		dao.executeQuery(dataSourceName, 
				sql.toString(), 
				source,
				1,
				100, 100);
		
		source.put("ACCT_ID", "1000");
		sql = new StringBuilder();
		sql.append("update SYS_ADM_ACCT set status = :status where acct_id = :acct_id");
		dao.executeUpdate(dataSourceName, sql.toString(), source);
		
		return data;
	}

}
