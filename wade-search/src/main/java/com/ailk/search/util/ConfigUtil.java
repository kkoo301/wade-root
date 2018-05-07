package com.ailk.search.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;
import com.ailk.org.apache.commons.lang3.StringUtils;

import com.ailk.search.server.index.SearchConfig;
import com.ailk.search.server.index.SearchConfig.ColumnField;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ConfigUtil
 * @description: 配置工具类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-16
 */
public class ConfigUtil {
		
	private ConfigUtil() {}
	
	/**
	 * 根据集群名子获取可用的搜索编码
	 * 
	 * @param clusterName
	 * @return
	 */
	public static List<String> getSearchCodeByClusterName(String clusterName) {
		
		List<String> searchCodes = new ArrayList<String>();
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
			if (null != manager) {
				conn = manager.getConnection("cen1");
			}
			
			if (null == conn) 
				throw new NullPointerException("找不到数据库连接:cen1");
			
			String sql = "SELECT DISTINCT SEARCH_CODE FROM WD_SEARCH_CFG WHERE CLUSTER_NAME = ? AND STATE = 'U'";
			
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, clusterName);
			rs = stmt.executeQuery();

			while (rs.next()) {
				String searchCode = rs.getString("SEARCH_CODE");
				searchCodes.add(searchCode);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != rs)
					rs.close();
				if (null != stmt)
					stmt.close();
				if (null != conn)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		
		return searchCodes;
		
	}
	
	/**
	 * 根据搜索编码获取搜索配置数据
	 * 
	 * @param searchCode
	 * @return
	 */
	public static SearchConfig getConfigure(String searchCode) {

		SearchConfig rtn = new SearchConfig();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
			if (null != manager) {
				conn = manager.getConnection("cen1");
			}
			
			if (null == conn) 
				throw new NullPointerException("找不到数据库连接:cen1");
			
			String sql = "SELECT * FROM WD_SEARCH_CFG WHERE STATE = 'U' AND SEARCH_CODE = ?";			
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, searchCode);
			rs = stmt.executeQuery();

			while (rs.next()) {

				String searchName = rs.getString("SEARCH_NAME");
				String dataSource = rs.getString("DATA_SOURCE");
				String querySql = rs.getString("QUERY_SQL");
				String indexDataPath = rs.getString("INDEX_DATA_PATH");
				String indexTempPath = rs.getString("INDEX_TEMP_PATH");
				String indexBackupPath = rs.getString("INDEX_BACKUP_PATH");
				String limitHits = rs.getString("LIMIT_HITS");
				String maxBackup = rs.getString("MAX_BACKUP");
				if (StringUtils.isBlank(maxBackup)) {
					maxBackup = "2";
				}
				
				rtn.setSearchCode(searchCode);
				rtn.setSearchName(searchName);
				rtn.setDataSource(dataSource);
				rtn.setQuerySql(querySql);
				rtn.setIndexDataPath(indexDataPath);
				rtn.setIndexTempPath(indexTempPath);
				rtn.setIndexBackupPath(indexBackupPath);
				rtn.setLimitHits(Integer.parseInt(limitHits));
				rtn.setMaxBackup(Integer.parseInt(maxBackup));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != rs)
					rs.close();
				if (null != stmt)
					stmt.close();
				if (null != conn)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		try {
			IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
			if (null != manager) {
				conn = manager.getConnection("cen1");
			}
			if (null == conn) 
				throw new NullPointerException("找不到数据库连接:cen1");
			
			stmt = conn.prepareStatement("SELECT * FROM WD_SEARCH_FIELDS WHERE STATE = 'U' AND SEARCH_CODE = ?");
			stmt.setString(1, searchCode);
			rs = stmt.executeQuery();

			List<ColumnField> fields = new ArrayList<ColumnField>();
			while (rs.next()) {

				String fieldName = rs.getString("FIELD_NAME");
				String needPinyinIndex = rs.getString("NEED_PINYIN_INDEX");
				String needFullMatch = rs.getString("NEED_FULL_MATCH");
				String needStore = rs.getString("NEED_STORE");
				
				ColumnField field = new ColumnField();
				field.setFieldName(fieldName);
				field.setNeedPinyinIndex("Y".equals(needPinyinIndex));
				field.setNeedFullMatch("Y".equals(needFullMatch));
				field.setNeedStore("Y".equals(needStore));
				fields.add(field);
			}

			rtn.setFields(fields);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != rs)
					rs.close();
				if (null != stmt)
					stmt.close();
				if (null != conn)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return rtn;
	}
	
}
