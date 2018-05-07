/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.transaction.service;

import java.sql.SQLException;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.service.BaseService;
import com.ailk.service.session.SessionManager;
import com.ailk.service.session.app.AppSession;

/**
 * TODO
 * 
 * @className: TestQueryService.java
 * @author: liaosheng
 * @date: 2014-9-10
 */
public class TestTranscationService extends BaseService {

	private static final long serialVersionUID = -6727434353576342471L;
	
	
	public IDataset test(IData param)  throws Exception {
		insert(param);
		query(param);
		
		String action = param.getString("ACTION", "0");
		if ("1".equals(action)) {
			for (int i = 0; i < 6; i++) {
				Thread.sleep(1000);
			}
		}
		
		if ("2".equals(action)) {
			throw new SQLException("12121212");
		}
		
		if ("3".equals(action)) {
			AppSession.getSession().setConnectionStmtTimeout(120);
		}
		save(param);
		
		if ("4".equals(action)) {
			asyncInsert(param);
		}
		
		if ("5".equals(action)) {
			newDBConnSave(param);
		}
		
		if ("6".equals(action)) {
			queryTimeout(param);
		}
		return new DatasetList();
	}
	
	public void queryTimeout(IData param) throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
	}
	
	public void delete() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		dao.delete("WD_STATIC", new String[] {"TYPE_ID", "DATA_ID"}, new String[] {"1403203", "1403203"});
		Thread.sleep(6000);
	}
	
	public void add() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		IData param = new DataMap();
		param.put("TYPE_ID", "1403203");
		param.put("DATA_ID", "1403203");
		param.put("DATA_NAME", "1403203");
		
		dao.insert("WD_STATIC", param);
	}
	
	public String getId() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		return dao.getSequence("SEQ_WD_STATIC");
	}
	
	public void insert(IData param) throws Exception {
		String id = getId();
		param.put("TYPE_ID", id);
		param.put("DATA_ID", id);
		param.put("DATA_NAME", id);
		
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("declare");
		sql.append("  iv_idx number := 0;");
		sql.append("  iv_start  number := 0;");
		sql.append("  iv_end  number := 0;");
		sql.append("begin");
		sql.append("  for iv_idx in 0..9 loop");
		sql.append("    iv_start := iv_idx * 10;");
		sql.append("    iv_end := iv_start + 9;");
		sql.append("    SELECT serial_number");
		sql.append("    FROM   UOP_RES.TF_R_MPHONECODE_IDLE");
		sql.append("    WHERE  EPARCHY_CODE = '0898'");
		sql.append("    AND    RES_STATE <> '7'");
		sql.append("    AND    LENGTH(SERIAL_NUMBER) = LENGTH('18800000000')");
		sql.append("    AND    SERIAL_NUMBER >= TO_CHAR('18800000000')");
		sql.append("    AND    SERIAL_NUMBER <= TO_CHAR('18899999999')");
		sql.append("    AND    SERIAL_NUMBER LIKE '188________'");
		sql.append("    and    partition_id between 0 and 9");
		sql.append("    and    rownum < 2;");
		sql.append("  end loop;");
		sql.append("end;");
		
		dao.queryList(sql.toString(), param);
		
		dao.insert("WD_STATIC", param);
	}
	

	public IDataset query(IData param) throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		dao.queryByPK("WD_STATIC", new String[] {"TYPE_ID", "DATA_ID"}, new String[] {"1", "2"});
		return new DatasetList();
	}
	
	public IDataset save(IData param) throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		//param.put("TYPE_ID", "2");
		//param.put("DATA_ID", "2");
		param.put("DATA_NAME", "DATA_NAME");
		
		dao.save("WD_STATIC", param, new String[]{"TYPE_ID", "DATA_ID"});
		
		return new DatasetList();
	}
	
	public IDataset asyncInsert(IData param) throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		
		DBConnection conn = SessionManager.getInstance().getAsyncConnection("name1");
		dao.insert(conn, "WD_STATIC_ASYNC", param);
		
		param.put("DATA_NAME", "async");
		dao.save(conn, "WD_STATIC_ASYNC", param);
		
		return new DatasetList();
	}
	
	public IDataset newDBConnSave(IData param) throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		
		DBConnection conn = null;
		try {
			conn = new DBConnection("name1", true, false);
			param.put("DATA_NAME", "4");
			dao.insert(conn, "WD_STATIC_SINGLE", param);
			conn.commit();
			return new DatasetList();
		} catch (Exception e) {
			if (null != conn) 
				conn.rollback();
			throw e;
		} finally {
			if (null != conn) 
				conn.close();
		}
	}
}
