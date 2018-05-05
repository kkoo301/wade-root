package com.wade.trace.service;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.BaseService;
import com.ailk.service.bean.BeanManager;
import com.wade.trace.bean.LogConfBean;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: LogConfService
 * @description: 日志配置信息查询服务
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class LogConfService extends BaseService {

	private static final long serialVersionUID = -7116440825641697758L;
	
	/**
	 * 查询WEB日志配置信息
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset queryWebConfData(IData data) throws Exception {
		LogConfBean bean = BeanManager.createBean(LogConfBean.class);
		return bean.queryWebConf();
	}
	
	/**
	 * 查询APP日志配置信息
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset queryAppConfData(IData data) throws Exception {
		LogConfBean bean = BeanManager.createBean(LogConfBean.class);
		return bean.queryAppConf();
	}
	
}
