package com.wade.trace.cache;

import java.util.HashMap;
import java.util.Map;

import com.ailk.cache.localcache.AbstractReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.wade.trace.conf.WebConf;
import com.wade.trace.sample.impl.WebSample;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: WebConfCache
 * @description: Web配置缓存
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class WebConfCache extends AbstractReadOnlyCache {

	@Override
	public Map<String, Object> loadData() throws Exception {
		
		Map<String, Object> rtn = new HashMap<String, Object>();

		IDataInput input = new DataInput();
		IDataOutput output = ServiceFactory.call("SYS.LOG.WEBCONF", input);
		IDataset datas = output.getData();

		for (int i = 0, size = datas.size(); i < size; i++) {
			
			IData data = datas.getData(i);

			String menuId = data.getString("MENU_ID");
			String strNames = data.getString("PARAM_NAMES", "");
			String strSampleDenom = data.getString("SAMPLE_DENOM", "0");

			int sampleDenom = Integer.parseInt(strSampleDenom);
			WebSample sample = new WebSample(menuId, sampleDenom);
			String[] keys = StringUtils.split(strNames, ',');			
			
			WebConf conf = new WebConf();
			conf.setMenuId(menuId);
			conf.setSample(sample);
			conf.setKeys(keys);

			rtn.put(menuId, conf);

		}

		return rtn;
		
	}

}
