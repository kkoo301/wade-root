/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.so;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dao.impl.TableDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.object.TableMetaObject;
import com.ailk.database.util.SQLParser;
import com.ailk.service.BaseService;
import com.ailk.service.ServiceManager;
import com.ailk.service.invoker.ServiceInvoker;
import com.ailk.service.loader.impl.ServiceConfigRegister;
import com.ailk.service.session.SessionManager;


/**
 * 通过服务调用，测试数据库的DML操作及连接控制、事务控制
 * 
 * @className: TestDmlService.java
 * @author: liaosheng
 * @date: 2014-5-16
 */
public class TestDmlService extends BaseService {
	
	public static final long serialVersionUID = 7287033785576052711L;

	public IDataset testDML(IData input) throws Exception {
		/*testExecuteQuery(input);
		testExecuteUpdate(input);
		
		delete();
		deleteAsync();
		
		insert();
		insertAsync();
		
		update();
		updateAsync();
		
		query();
		queryAsync();
		
		executeInsertBatch();
		
		executeUpdateBatch();
		
		executeDeleteBatch();
		
		executeBatchSql();
		
		executeBatch();
		
		count();*/
		
		queryList();
		
		return new DatasetList();
	}
	
	public void query() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		IData input = new DataMap();
		input.put("STATUS", "U");
		input.put("ACCT_ID", "1000");
		System.out.println(dao.queryByPK("SYS_ADM_ACCT", input));
		
		dao = DAOManager.createDAO(BaseDAO.class, "name2");
		System.out.println(dao.queryByPK("SYS_ADM_ACCT", input));
	}
	
	public void queryAsync() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		DBConnection conn = SessionManager.getInstance().getAsyncConnection("name1");
		
		IData input = new DataMap();
		input.put("STATUS", "U");
		input.put("ACCT_ID", "1000");
		
		System.out.println(dao.queryByPK(conn, "SYS_ADM_ACCT", input));
		
		dao = DAOManager.createDAO(BaseDAO.class);
		conn = SessionManager.getInstance().getAsyncConnection("name2");
		System.out.println(dao.queryByPK(conn, "SYS_ADM_ACCT", input));
	}
	
	public void delete() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		IData input = new DataMap();
		input.put("STATUS", "U");
		input.put("ACCT_ID", "1000");
		input.put("ACCT_SN", "1000");
		input.put("ACCT_NAME", "1000");
		dao.delete("SYS_ADM_ACCT", input);
	}
	
	public void deleteAsync() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		DBConnection conn = SessionManager.getInstance().getAsyncConnection("name1");
		
		IData input = new DataMap();
		input.put("STATUS", "U");
		input.put("ACCT_ID", "1002");
		input.put("ACCT_SN", "1002");
		input.put("ACCT_NAME", "1002");
		
		dao.delete(conn, "SYS_ADM_ACCT", input);
	}
	
	
	public void insert() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		IData input = new DataMap();
		input.put("STATUS", "U");
		input.put("ACCT_ID", "1000");
		input.put("ACCT_SN", "1000");
		input.put("ACCT_NAME", "1000");
		dao.insert("SYS_ADM_ACCT", input);
		
		// 模拟两个连接同时插一条数据导致行锁挂死的情况
		/*DBConnection conn = SessionManager.getInstance().getAsyncConnection("name1");
		dao.insert(conn, "SYS_ADM_ACCT", input);*/
	}
	
	public void insertAsync() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		DBConnection conn = SessionManager.getInstance().getAsyncConnection("name1");
		
		IData input = new DataMap();
		input.put("STATUS", "U");
		input.put("ACCT_ID", "1002");
		input.put("ACCT_SN", "1002");
		input.put("ACCT_NAME", "1002");
		
		dao.insert(conn, "SYS_ADM_ACCT", input);
	}
	
	public void update() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		IData input = new DataMap();
		input.put("STATUS", "U");
		input.put("ACCT_ID", "1000");
		input.put("ACCT_SN", "1000");
		input.put("ACCT_NAME", "1000");
		
		dao.update("SYS_ADM_ACCT", input);
	}
	
	public void updateAsync() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		DBConnection conn = SessionManager.getInstance().getAsyncConnection("name1");
		
		IData input = new DataMap();
		input.put("STATUS", "U");
		input.put("ACCT_ID", "1002");
		input.put("ACCT_SN", "1002");
		input.put("ACCT_NAME", "1002");
		
		dao.update(conn, "SYS_ADM_ACCT", input);
	}
	
	public void testExecuteQuery(IData input) throws Exception {
		BaseDAO acctdao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		SQLParser parser = new SQLParser(input);
		parser.addSQL("select a.acct_id, b.right_id");
		parser.addSQL(" from sys_adm_acct a, sys_adm_acct_right b, sys_adm_right c");
		parser.addSQL(" where a.acct_id = b.acct_id");
		parser.addSQL(" and b.right_id = c.right_id");
		parser.addSQL(" and a.acct_id = :ACCT_ID ");
		IDataset ds = acctdao.queryList(parser, getPagination());
		System.out.println(ds);
	}
	
	public void testExecuteUpdate(IData input) throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		input.put("ACCT_NAME", "1122");
		SQLParser parser = new SQLParser(input);
		parser.addSQL("update sys_adm_acct a set a.acct_name = :ACCT_NAME");
		parser.addSQL(" where 1 = 1");
		parser.addSQL(" and a.status = :STATUS ");
		parser.addSQL(" and a.acct_id = :ACCT_ID ");
		
		dao.executeUpdate(parser);
	}
	
	public void executeInsertBatch() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		IDataset source = new DatasetList();
		for (int i = 0, cnt = 23; i < cnt ; i++) {
			IData map = new DataMap();
			map.put("ACCT_ID", "999" + i);
			map.put("ACCT_NAME", "1111");
			map.put("ACCT_SN", "1238711");
			map.put("STATUS", "U");
			source.add(map);
		}
		
		dao.insert("SYS_ADM_ACCT", source);
	}
	
	
	public void executeDeleteBatch() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		IDataset source = new DatasetList();
		for (int i = 0, cnt = 23; i < cnt ; i++) {
			IData map = new DataMap();
			map.put("ACCT_ID", "999" + i);
			map.put("ACCT_NAME", "1111");
			map.put("ACCT_SN", "1238711");
			map.put("STATUS", "U");
			source.add(map);
		}
		
		dao.delete("SYS_ADM_ACCT", source, 4);
	}
	
	public void executeUpdateBatch() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		IDataset source = new DatasetList();
		for (int i = 0, cnt = 23; i < cnt ; i++) {
			IData map = new DataMap();
			map.put("ACCT_ID", "999" + i);
			map.put("ACCT_NAME", "1111");
			map.put("ACCT_SN", "1238711");
			map.put("STATUS", "U");
			source.add(map);
		}
		
		dao.update("SYS_ADM_ACCT", source, new String[]{"ACCT_ID", "STATUS"}, 4);
	}
	
	public void executeBatchSql() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		String[] sqls = new String[23];
		for (int i = 0, cnt = 23; i < cnt ; i++) {
			sqls[i] = "update sys_adm_acct set acct_sn = 'xxxxx' where acct_id = 999" + i;
		}
		
		dao.executeBatch(sqls);
	}
	
	public void executeBatch() throws Exception {
		String colonSql = "update sys_adm_acct set ACCT_SN=:ACCT_SN where ACCT_ID=:ACCT_ID";
		
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		IDataset source = new DatasetList();
		for (int i = 0, cnt = 23; i < cnt ; i++) {
			IData map = new DataMap();
			map.put("ACCT_ID", "999" + i);
			map.put("ACCT_NAME", "1111");
			map.put("ACCT_SN", "1238711");
			map.put("STATUS", "U");
			source.add(map);
		}
		
		dao.executeBatch(colonSql, source, 4);
	}
	
	
	public void count() throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "name1");
		
		String colonSql = "select count(1) from sys_adm_acct where status = :STATUS group by status";
		
		IData source = new DataMap();
		source.put("STATUS", "U");
			
		System.out.println(dao.getCount(colonSql, source));
	}
	
	
	public void queryList() throws Exception {
		IData source = new DataMap();
		source.put("STATUS", "U");
		
		BaseDAO dao1 = DAOManager.createDAO(BaseDAO.class, "name1");
		String sql1 = "select * from sys_adm_acct where STATUS = :STATUS";
		
		Pagination pagin1 = new Pagination(12);
		pagin1.setNeedCount(true);
		pagin1.setOnlyCount(true);
		
		dao1.queryList(sql1, source, pagin1);
		System.out.println("Count值：" + pagin1.getCount());
		
		pagin1.setNeedCount(false);
		pagin1.setOnlyCount(false);
		System.out.println(dao1.queryList(sql1, source, pagin1));
		System.out.println("测试第二次查询是否仍执行CountSQL,Count值：" + pagin1.getCount());
		
		
		BaseDAO dao2 = DAOManager.createDAO(BaseDAO.class, "name2");
		String sql2 = "select * from sys_adm_acct where STATUS = :STATUS";
		
		Pagination pagin2 = new Pagination(12);
		pagin2.setNeedCount(true);
		pagin2.setOnlyCount(true);
		
		dao2.queryList(sql2, source, pagin2);
		System.out.println("Count值：" + pagin2.getCount());
		
		pagin2.setNeedCount(false);
		pagin2.setOnlyCount(false);
		dao2.queryList(sql1, source, pagin2);
		System.out.println("测试第二次查询是否仍执行CountSQL,Count值：" + pagin2.getCount());
		
	}
	
	public static void main(String[] args) throws Exception {
		System.setProperty("wade.server.name", "database-test");
		
		ServiceManager.createRegister(ServiceConfigRegister.class.getName());
		ServiceManager.register();
		
		String name = "testDML";
		IData param = new DataMap();
		param.put("ACCT_ID", "1000002");
		
		DataInput input = new DataInput(param, param);
		
		IDataOutput output = ServiceInvoker.mainServiceInvoke(name, input);
		System.out.println(output);
	}

}
