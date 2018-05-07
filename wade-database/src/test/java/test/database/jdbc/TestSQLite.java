/*
 * Copyright: Copyright (c) 2015 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.database.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.service.session.app.AppInvoker;

/**
 * 测试SQLite的连接、驱动、SQL语法
 * 
 * @className: TestSQLite.java
 * @author: liaosheng
 * @date: 2015-2-3
 */
public class TestSQLite {

	public static void main(String[] args) throws Exception {
		TestSQLite ts = new TestSQLite();
		ts.testJDBC();
		
		ts.invoke();
	}
	
	public void invoke() throws Exception {
		AppInvoker.invoke(null, new TestSQLite(), "testDAO", new String[] {"liaos"});
	}
	
	public void testDAO(String name) throws Exception {
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class, "sqlite");
		IData source = new DataMap();
		source.put("TEST_ID", "117");
		source.put("TEST_NAME", name);
		/*source.put("TEST_DATE", dao.getSysDate());
		dao.insert("TEST_DML", source);
		
		source.put("TEST_ID", "117");
		source.put("TEST_TIME", dao.getSysTime());
		dao.update("TEST_DML", source);
		
		source.put("TEST_NAME", "update+" + name);
		dao.save("TEST_DML", source);
		
		source.put("TEST_ID", "116");
		IData info = dao.queryByPK("TEST_DML", source);
		System.out.println("主键查询：" + info);
		
		dao.delete("TEST_DML", info);*/
		
		Pagination p = new Pagination();
		p.setNeedCount(true);
		p.setPageSize(2);
		IDataset ds = dao.queryList("select * from TEST_DML where TEST_NAME like '%' || :TEST_NAME || '%' ", source, p);
		System.out.println("分页查询：" + ds);
	}
	
	
	private void testJDBC() throws SQLException {
		Connection conn = getConnection();
		
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			
			try {
				/** 连接池探测语句 **/
				//ResultSet rs = stmt.executeQuery("select 1 from sqlite_master where 0 = 1");
				
				/** 获取系统时间语句 **/
				//ResultSet rs = stmt.executeQuery("select strftime('%Y-%m-%d %H:%M:%f', 'now')");
				
				/** 获取表主键 **/
				//ResultSet rs = stmt.executeQuery("pragma table_info ('TEST_TABLE')");
				
				/** 获取表字段 **/
				/**
				 * SQLite不支持Date和Time类型，建议将这类类型统一成TEXT(string)X
				 */
				//ResultSetMetaData meta = stmt.executeQuery("select * from TEST_DML t where 0 = 1").getMetaData();
				
				/** 获取序列 **/
				//ResultSet rs = stmt.executeQuery("select seq from sqlite_sequence where name='TEST_SEQ'");
				
				/** 分页查询 **/
				//ResultSet rs = stmt.executeQuery("select * from TEST_SEQ limit 3, 1");
				
				/*ResultSet rs = conn.createStatement().executeQuery("pragma table_info (TEST_DML)");
				while (rs.next()) {
					rs.getString(1);
				}
				rs = conn.createStatement().executeQuery("select * from TEST_DML where 0 = 1");
				while (rs.next()) {
					rs.getString(1);
				}*/
				PreparedStatement pstmt = conn.prepareStatement("select ROWID, t.TEST_TIME, t.TEST_DATE, t.TEST_ID, t.TEST_NAME from TEST_DML t  where TEST_ID = ?");
				pstmt.setString(1, "111");
				ResultSet rs = pstmt.executeQuery();
				
				try {
					/*int cnt = meta.getColumnCount();
					for (int i = 1; i <= cnt; i++) {
						IColumnObject column = new ColumnObject();
						String colName = meta.getColumnName(i).toUpperCase();
						column.setColumnName(colName);
						column.setColumnType(meta.getColumnType(i));
						column.setColumnDesc(meta.getColumnLabel(i));
						column.setColumnSize(meta.getColumnDisplaySize(i));
						column.setDecimalDigits(meta.getScale(i));
						column.setNullable(meta.isNullable(i) == ResultSetMetaData.columnNoNulls ? false : true);
						System.out.println(column);
					}*/
					
					while (rs.next()) {
						/*System.out.println("字段名：" + rs.getString("name"));
						System.out.println("是否为主键：" + rs.getString("pk"));*/
						//System.out.println(rs.getString("seq"));
						
						System.out.println(rs.getString("TEST_ID"));
						System.out.println(rs.getString("TEST_TIME"));
					}
				} catch (SQLException e) {
					throw e;
				} finally {
					if (null != rs) {
						rs.close();
					}
				}
				
			} catch (SQLException e) {
				throw e;
			} finally {
				if (null != stmt) {
					stmt.close();
				}
			}
			
		} catch (SQLException e) {
			throw e;
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}
	
	
	private Connection getConnection() throws SQLException {
		Connection conn = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:framework\\database\\data\\sqlite\\test.db");
		} catch (SQLException e) {
			throw e;
		}
		
		return conn;
	}
	
	static {
		try {
			Class.forName(DatabaseCfg.class.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
