package com.wade.relax.tm.context;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.database.config.DatabaseCfg;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.database.dbconn.IConnectionManager;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.relax.RelaxXml;
import com.wade.relax.tm.LocalXGuarder;
import com.wade.relax.tm.context.impl.DevelopXContext;
import com.wade.relax.tm.context.impl.ProductXContext;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: XContext
 * @description: 事务上下文
 *                 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-12-15
 */
public abstract class XContext {
	
	private static final Logger LOG = LoggerFactory.getLogger(XContext.class);
	
	/**
	 * 放置事务上下文
	 */
	protected static final ThreadLocal<XContext> CTX = new ThreadLocal<XContext>();
	
	/**
	 * 销毁线程上下文
	 */
	public void destroyCTX() {
		CTX.remove();
	}
	
	/**
	 * 数据源管理器
	 */
	protected static final IConnectionManager CONN_MANAGER = ConnectionManagerFactory.getConnectionManager();

	/**
	 * DTM运行模式
	 */
	private static boolean PRODUCT_MODE;
	
	/**
	 * DTM事务超时时间
	 */
	private static int X_TIMEOUT_MSEC;
	
	public static final int getTimeoutMSec() {
		return X_TIMEOUT_MSEC;
	}
	
	static {
		
		PRODUCT_MODE = DatabaseCfg.useDTM();
		X_TIMEOUT_MSEC = RelaxXml.getxTimeoutMsec();
		
		LOG.info("DTM模式: {}", (PRODUCT_MODE ? "生产" : "开发"));
		LOG.info("DTM超时时间(毫秒): {}", X_TIMEOUT_MSEC);
		
		new LocalXGuarder().start();
	}
	
	/**
	 * 入口APP，主服务开始前调用。构建事务上下文。
	 * 
	 * @param timeout
	 */
	public static final void build(int timeout) {
		
		XContext xContext = CTX.get();
		if (null != xContext) {
			LOG.debug("当前线程上下文中，已存在事务对象，无需重复构造。");
			return;
		}
		
		if (PRODUCT_MODE) {
			xContext = new ProductXContext(timeout);
		} else {
			xContext = new DevelopXContext(timeout);
		}
		
		CTX.set(xContext);
			
	}
	
	/**
	 * 从属APP，主服务开始前调用。构建事务上下文。
	 * 
	 * @param tid
	 */
	public static final void build(String tid) {
		
		XContext xContext = CTX.get();
		if (null != xContext) {
			LOG.debug("当前线程上下文中，已存在事务对象，无需重复构造。");
			return;
		}
		
		if (PRODUCT_MODE) {
			xContext = new ProductXContext(tid);
		} else {
			xContext = new DevelopXContext(tid);
		}
		
		CTX.set(xContext);
		
	}
		
	/**
	 * 从线程上下文获取事务上下文对象
	 * 
	 * @return
	 */
	public static final XContext getInstance() {

		XContext xContext = CTX.get();

		if (null == xContext) {
			throw new IllegalArgumentException("事务上下文为空!");
		}

		return xContext;

	}

	/**
	 * 根据数据源名获取一个数据库连接
	 * 
	 * @param connName
	 * @return
	 */
	public abstract Connection getConnection(String connName) throws Exception;
	
	/**
	 * 事务提交
	 * 
	 * @throws SQLException
	 */
	public abstract void commit() throws SQLException;
	
	/**
	 * 事务回滚
	 * 
	 * @throws SQLException
	 */
	public abstract void rollback() throws SQLException;
	
	/**
	 * 获取事务ID（TID）
	 * 
	 * @return
	 */
	public abstract String getTID();

	/**
	 * 根据中心名获取活跃实例
	 * 
	 * @param name
	 * @return
	 */
	public abstract String getActiveInstance(String centerName);
	
	/**
	 * 创建事务ID <br/>
	 * 
	 * tx-${uuid}-${时间戳}-${超时时间}, 示例: tx-567776880e034a4ab821049d6c163d65-1452063586-600
	 * 
	 * @return
	 */
	protected static final String createTID(int timeout) {
		String uuid = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
		return "tx-" + uuid + "-" + (System.currentTimeMillis() / 1000) + "-" + timeout;
	}

}