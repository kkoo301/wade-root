package com.wade.trace.bean;

import com.ailk.common.data.IDataset;
import com.ailk.database.dao.impl.BaseDAO;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: LogConfDAO
 * @description: 日志配置信息查询DAO
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class LogConfDAO extends BaseDAO {

	/**
	 * 查询WEB日志配置信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public IDataset queryWebConf() throws Exception {
		String sql = "SELECT MENU_ID, PARAM_NAMES, SAMPLE_DENOM FROM WD_WEBLOG_CFG WHERE STATE = 'U'";
		return queryList(sql, new String[0]);
	}

	/**
	 * 查询APP日志配置信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public IDataset queryAppConf() throws Exception {
		String sql = "SELECT SERVICE_NAME, PARAM_NAMES, SAMPLE_DENOM FROM WD_APPLOG_CFG WHERE STATE = 'U'";
		return queryList(sql, new String[0]);
	}
	
}
