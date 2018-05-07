package com.ailk.database.sequence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.sequence.ISequence;
import com.ailk.service.session.SessionManager;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: AbstractSequence
 * @description: 序列抽象类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-4-18
 */
public abstract class AbstractSequence implements ISequence {

	private static final Logger log = Logger.getLogger(AbstractSequence.class);
	private static final int MIN_FETCH_SIZE = 20;
	private static final int MAX_FETCH_SIZE = 1000;
	
	/**
	 * 序列名
	 */
	private String seqName;

	/**
	 * 每批次获取数量
	 */
	private int fetchSize;

	/**
	 * 批次获取SQL
	 */
	private String sql;

	/**
	 * 序列的缓存容器，connName -> Queue(seq1,seq2,...,seqN)
	 */
	private Map<String, Queue<String>> cacheMap = new HashMap<String, Queue<String>>();

	public AbstractSequence(String seqName) {
		this(seqName, 50); // 默认每批取50个
	}

	public AbstractSequence(String seqName, int fetchSize) {

		if (StringUtils.isBlank(seqName)) {
			throw new IllegalArgumentException("序列名不能为空！");
		}
		
		if (fetchSize < MIN_FETCH_SIZE) {
			this.fetchSize = MIN_FETCH_SIZE;
			log.warn("批量获取序列，fetchSize不能为负数[fetchSize=" + fetchSize + "]，系统自动修改为" + MIN_FETCH_SIZE);
		}

		if (fetchSize > MAX_FETCH_SIZE) {
			this.fetchSize = MAX_FETCH_SIZE;
			log.warn("批量获取序列，fetchSize设置过大[fetchSize=" + fetchSize + "]，系统自动修改为" + MAX_FETCH_SIZE);
		}

		this.seqName = seqName;
		this.fetchSize = fetchSize;
		this.sql = "select " + this.seqName + ".nextval from dual connect by level <= " + this.fetchSize;

	}

	/**
	 * 获取序列的下一个值
	 * 
	 * @param connName 连接名
	 * @return
	 */
	protected final String nextval(String connName) {
		
		if (StringUtils.isBlank(connName)) {
			throw new IllegalArgumentException("connName连接名不能为空！connName=" + connName);
		}
		
		Queue<String> seqCache = cacheMap.get(connName);
		while (null == seqCache) {
			synchronized (this) {
				seqCache = cacheMap.get(connName);
				if (null != seqCache) {
					break;
				}
				
				seqCache = new ConcurrentLinkedQueue<String>();
				cacheMap.put(connName, seqCache);
			}
		}
		
		String rtn = seqCache.poll();
		if (null != rtn) {
			return rtn;
		}

		synchronized (this) {

			rtn = seqCache.poll();
			if (null != rtn) {
				return rtn;
			}

			DBConnection conn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			try {

				conn = SessionManager.getInstance().getSessionConnection(connName);
				stmt = conn.prepareStatement(sql);
				stmt.setFetchSize(MAX_FETCH_SIZE);
				rs = stmt.executeQuery();

				rs.next();
				rtn = rs.getString("NEXTVAL");

				while (rs.next()) {
					String nextval = rs.getString("NEXTVAL");
					seqCache.add(nextval);
				}

			} catch (Exception e) {
				log.error("批量获取序列时发生错误！", e);
			} finally {
				try {
					if (null != rs)   rs.close();
					if (null != stmt) stmt.close();
				} catch (SQLException e) {
					log.error("批量取序列，关闭游标和连接发生错误！", e);
				}

			}
		}
		
		return rtn;
	}

}
