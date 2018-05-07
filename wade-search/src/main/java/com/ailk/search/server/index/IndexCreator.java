package com.ailk.search.server.index;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import com.ailk.org.apache.commons.lang3.SerializationUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;
import com.ailk.search.StaticFactory;
import com.ailk.search.server.index.SearchConfig;
import com.ailk.search.server.index.SearchConfig.ColumnField;
import com.ailk.search.util.ConfigUtil;
import com.ailk.search.util.PYUtil;
import com.ailk.search.util.SplitUtil;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: IndexCreator
 * @description: 索引创建者
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-16
 */
public class IndexCreator {

	private static final transient Logger log = Logger.getLogger(IndexCreator.class);

	/**
	 * 创建索引
	 * 
	 * @param searchCode
	 * @throws Exception
	 */
	private static void createIndex(String searchCode) throws Exception {

		if (StringUtils.isBlank(searchCode)) {
			throw new IllegalArgumentException("搜索编码不能为空！");
		}

		SearchConfig config = ConfigUtil.getConfigure(searchCode);

		long start = System.currentTimeMillis();
		System.out.println("\n\n");
		log.info("------------------------------------------------------");
		log.info("SEARCH_CODE:" + searchCode + " 开始创建索引...");
		log.info("------------------------------------------------------");
		log.info(searchCode + ",开始时间:" + DateFormatUtils.format(start, "yyyy-MM-dd HH:mm:ss"));
		log.info(searchCode + ",搜索名称:" + config.getSearchName());
		log.info(searchCode + ",查询语句:\n" + config.getQuerySql());
		log.info(searchCode + ",索引临时目录:" + config.getIndexTempPath());
		log.info(searchCode + ",索引数据目录:" + config.getIndexDataPath());
		log.info(searchCode + ",索引备份目录:" + config.getIndexBackupPath());
		for (ColumnField field : config.getFields()) {
			log.info(searchCode + ",索引字段:" + field.getFieldName());
			log.info(searchCode + "         支持拼音搜索？" + (field.isNeedPinyinIndex() ? "YES" : "NO"));
			log.info(searchCode + "         支持全词匹配？" + (field.isNeedFullMatch() ? "YES" : "NO"));
			log.info(searchCode + "         字段需要存储？" + (field.isNeedStore() ? "YES" : "NO"));
		}
		
		String indexDataPath = config.getIndexDataPath();
		String indexTempPath = config.getIndexTempPath();
		String indexBackupPath = config.getIndexBackupPath();

		FileUtils.forceMkdir(new File(indexDataPath));
		FileUtils.forceMkdir(new File(indexTempPath));
		FileUtils.forceMkdir(new File(indexBackupPath));
		
		File tempFile = new File(indexTempPath);
		if (!tempFile.exists()) {
			FileUtils.forceMkdir(tempFile);
			log.info(searchCode + "临时目录不存在,创建临时目录:" + indexTempPath);
		}

		if (!tempFile.isDirectory()) {
			throw new IllegalArgumentException("索引临时目录地址" + indexTempPath + "是文件！");
		}

		FileUtils.forceDelete(tempFile);
		log.info(searchCode + ",清空临时目录:" + indexTempPath);

		Directory dir = FSDirectory.open(tempFile);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36, new WhitespaceAnalyzer(Version.LUCENE_36));
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		iwc.setMaxBufferedDocs(1000);
		IndexWriter iw = new IndexWriter(dir, iwc);

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String dataSource = config.getDataSource();
			
			IConnectionManager manager = ConnectionManagerFactory.getConnectionManager();
			if (null != manager) {
				conn = manager.getConnection(dataSource);
			}
			if (null == conn) 
				throw new NullPointerException("找不到数据库连接:cen1");
			
			String sql = config.getQuerySql();
			log.info("执行数据获取的SQL：" + sql);
			
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			rs.setFetchSize(2000);
			
			int cnt = storeDocument(iw, rs, config);
			long end = System.currentTimeMillis();
			log.info(searchCode + ",索引建立完毕,共处理" + cnt + "条记录,耗时:" + (end - start) + "毫秒,TPS:" + (int)(cnt * 1.0 / (end - start) * 1000));

			start = System.currentTimeMillis();
			File backupFile = new File(indexBackupPath + "/" + DateFormatUtils.format(start, "yyyyMMddHHmmss"));
			FileUtils.forceMkdir(backupFile);
			FileUtils.copyDirectory(new File(indexDataPath), backupFile);
			log.info(searchCode + ",备份老索引数据，耗时:" + (System.currentTimeMillis() - start) + "毫秒");
			
			start = System.currentTimeMillis();
		    FileUtils.forceDelete(new File(indexDataPath));
		    FileUtils.forceMkdir(new File(indexDataPath));
		    FileUtils.copyDirectory(new File(indexTempPath), new File(indexDataPath));
		    FileUtils.forceDelete(new File(indexTempPath));
		    log.info(searchCode + ",拷贝新索引数据到生产目录，耗时:" + (System.currentTimeMillis() - start) + "毫秒");
			
		    start = System.currentTimeMillis();
		    File backupDir = new File(indexBackupPath);
		    Map<String, File> m = new TreeMap<String, File>();
		    for (File file : backupDir.listFiles()) {
		    	if (file.isDirectory()) {
		    		m.put(file.getName(), file);
		    	}
		    }
		    
		    int maxBackup = config.getMaxBackup();
		    if (m.size() > maxBackup) {
		    	int deleteCount = m.size() - maxBackup;
		    	int i = 0;
		    	for (String name : m.keySet()) {	    		
		    		if (i == deleteCount) {
		    			break;
		    		}
		    		String dirname = indexBackupPath + "/" + name;
		    		FileUtils.forceDelete(new File(dirname));
		    		log.info(searchCode +",删除备份目录:" + dirname);
		    		i++;
		    	}
		    }

		    log.info(searchCode + ",完成时间:" + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
		    
		} finally {
			if (null != rs) {
				rs.close();
			}
			
			if (null != stmt) {
				stmt.close();
			}
			
			if (null != conn) {
				conn.close();
			}
		}
	}

	/**
	 * 文档索引
	 * 
	 * @param iw
	 * @param rs
	 * @param config
	 * @return
	 */
	private static int storeDocument(IndexWriter iw, ResultSet rs, SearchConfig config) {

		int cnt = 0;
		String searchCode = config.getSearchCode();
		Set<String> columnNames = getColumnNames(rs);

		try {

			while (rs.next()) {

				// 准备数据
				IData data = new DataMap();
				for (String columnName : columnNames) {
					data.put(columnName, rs.getString(columnName));
				}

				// 准备索引
				StringBuilder splitBuff = new StringBuilder();
				StringBuilder fullMatchBuff = new StringBuilder();
				for (ColumnField field : config.getFields()) {
					String indexColunmName = field.getFieldName();
					String colunmValue = data.getString(indexColunmName);
					
					if (field.isNeedFullMatch()) { // 需全词匹配,字段名+字段值
						fullMatchBuff.append(indexColunmName);
						fullMatchBuff.append(colunmValue);
						fullMatchBuff.append(StaticFactory.KEYWORD_SEPARATOR);
					} else { // 分词匹配
						splitBuff.append(colunmValue);
						splitBuff.append(StaticFactory.KEYWORD_SEPARATOR);
					}
					
					if (field.isNeedPinyinIndex()) { // 加入拼音首字母支持
						splitBuff.append(PYUtil.parseToPinYinHeadChar2(colunmValue));
						splitBuff.append(StaticFactory.KEYWORD_SEPARATOR);
					}
					
					if (!field.isNeedStore()) { // 去掉无需存储的字段
						data.remove(indexColunmName);
					}
				}

				String index = SplitUtil.indexKeywordSplit(splitBuff.toString());
				index = index + StaticFactory.KEYWORD_SEPARATOR + fullMatchBuff.toString();
				byte[] bytes = SerializationUtils.serialize(data);
				
				Document doc = new Document();
				doc.add(new Field(StaticFactory.INDEX_FIELD_NAME, index, Field.Store.NO, Field.Index.ANALYZED));								
				doc.add(new Field("DATA", bytes, 0, bytes.length));
				iw.addDocument(doc);

				cnt++;
				if (0 == cnt % 1000) {
					log.info(searchCode + ",已处理:" + cnt + "条数据");
					iw.commit();
				}
			}
			
			iw.close();

		} catch (Exception e) {
			log.error("创建索引发生错误！", e);
		}
		return cnt;
	}

	/**
	 * 从结果集元数据中获取列名集合
	 * 
	 * @param rs
	 * @return
	 */
	private static Set<String> getColumnNames(ResultSet rs) {
		Set<String> rtn = new HashSet<String>();

		try {
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				String colunmName = metaData.getColumnName(i);
				rtn.add(colunmName);
			}
		} catch (SQLException e) {
			log.error(e);
		}
		return rtn;
	}

	public static void main(String[] args) throws Exception {
		
		if (args.length < 2 || (!args[0].equals("--searchcodes") && !args[0].equals("--clustername"))) {
			System.err.println("Usage: java com.ailk.search.server.index.IndexCreator [--searchcodes SEARCH_CODE1 SEARCH_CODE2 ...] ");
			System.err.println("Usage: java com.ailk.search.server.index.IndexCreator [--clustername CLUSTER_NAME] ");
			System.exit(255);
		}
		
		Set<String> rebuildCodes = new HashSet<String>();
		List<String> searchCodes = null;
		
		if (args[0].equals("--searchcodes")) {
			for (int i = 1; i < args.length; i++) {
				rebuildCodes.add(args[i]);
			}
		} else if (args[0].equals("--clustername")) {
			searchCodes = ConfigUtil.getSearchCodeByClusterName(args[1]);
			rebuildCodes.addAll(searchCodes);
		} else {
			log.error("错误的命令行参数: " + StringUtils.join(args));
			System.exit(255);
		}
		
		/**
		 * 索引重建
		 */
		for (String code : rebuildCodes) {
			createIndex(code);
		}
	
	}

}