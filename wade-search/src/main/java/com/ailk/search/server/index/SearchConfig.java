package com.ailk.search.server.index;

import java.util.List;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SearchConfig
 * @description: 搜索配置信息类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-16
 */
public class SearchConfig {
	
	private String searchCode;
	private String searchName;
	private String dataSource;
	private String querySql;
	private String indexDataPath;
	private String indexTempPath;
	private String indexBackupPath;
	private int limitHits;
	private int maxBackup;
	
	private List<ColumnField> fields;
	
	public String getSearchCode() {
		return searchCode;
	}

	public void setSearchCode(String searchCode) {
		this.searchCode = searchCode;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	
	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getQuerySql() {
		return querySql;
	}

	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}

	public String getIndexDataPath() {
		return indexDataPath;
	}

	public void setIndexDataPath(String indexDataPath) {
		this.indexDataPath = indexDataPath;
	}

	public String getIndexTempPath() {
		return indexTempPath;
	}

	public void setIndexTempPath(String indexTempPath) {
		this.indexTempPath = indexTempPath;
	}

	public String getIndexBackupPath() {
		return indexBackupPath;
	}

	public void setIndexBackupPath(String indexBackupPath) {
		this.indexBackupPath = indexBackupPath;
	}

	public int getLimitHits() {
		return limitHits;
	}

	public void setLimitHits(int limitHits) {
		this.limitHits = limitHits;
	}

	public int getMaxBackup() {
		return maxBackup;
	}

	public void setMaxBackup(int maxBackup) {
		this.maxBackup = maxBackup;
	}
	
	public List<ColumnField> getFields() {
		return fields;
	}

	public void setFields(List<ColumnField> fields) {
		this.fields = fields;
	}

	public static class ColumnField {
		
		/**
		 * 索引字段名
		 */
		private String fieldName;
		
		/**
		 * 是否需要按拼音首字母见索引
		 */
		private boolean needPinyinIndex;
		
		/**
		 * 是否需要全词匹配
		 */
		private boolean needFullMatch;
		
		/**
		 * 是否需要存储该字段
		 */
		private boolean needStore;
		
		public String getFieldName() {
			return fieldName;
		}
		
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		
		public boolean isNeedPinyinIndex() {
			return needPinyinIndex;
		}
		
		public void setNeedPinyinIndex(boolean needPinyinIndex) {
			this.needPinyinIndex = needPinyinIndex;
		}
		
		public boolean isNeedFullMatch() {
			return needFullMatch;
		}

		public void setNeedFullMatch(boolean needFullMatch) {
			this.needFullMatch = needFullMatch;
		}

		public boolean isNeedStore() {
			return needStore;
		}

		public void setNeedStore(boolean needStore) {
			this.needStore = needStore;
		}
	}
}
