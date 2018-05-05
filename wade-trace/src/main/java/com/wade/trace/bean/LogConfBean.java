package com.wade.trace.bean;

import com.ailk.common.data.IDataset;
import com.ailk.database.dao.DAOManager;
import com.ailk.service.bean.BaseBean;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: LogConfBean
 * @description: 日志配置信息查询Bean
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class LogConfBean extends BaseBean {

	/**
	 * 查询WEB日志配置信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public IDataset queryWebConf() throws Exception {
		LogConfDAO dao = (LogConfDAO) DAOManager.createDAO(LogConfDAO.class, "cen1");
		return dao.queryWebConf();
	}

	/**
	 * 查询APP日志配置信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public IDataset queryAppConf() throws Exception {
		LogConfDAO dao = (LogConfDAO) DAOManager.createDAO(LogConfDAO.class, "cen1");
		return dao.queryAppConf();
	}
	
}
