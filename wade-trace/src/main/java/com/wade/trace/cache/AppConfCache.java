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
import com.wade.trace.conf.AppConf;
import com.wade.trace.sample.impl.AppSample;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: AppConfCache
 * @description: App配置缓存
 * 
 * @version: v1.0.0
 * @author: steven.zhou
 * @date: 2015-5-12
 */
public class AppConfCache extends AbstractReadOnlyCache {

	@Override
	public Map<String, Object> loadData() throws Exception {
		
		Map<String, Object> rtn = new HashMap<String, Object>();
		
		IDataInput input = new DataInput();
		IDataOutput output = ServiceFactory.call("SYS.LOG.APPCONF", input);
		IDataset datas = output.getData();

		for (int i = 0, size = datas.size(); i < size; i++) {
			
			IData data = datas.getData(i);

			String serviceName = data.getString("SERVICE_NAME");
			String strNames = data.getString("PARAM_NAMES", "");
			String strSampleDenom = data.getString("SAMPLE_DENOM", "0");

			int sampleDenom = Integer.parseInt(strSampleDenom);
			AppSample sample = new AppSample(serviceName, sampleDenom);
			String[] keys = StringUtils.split(strNames, ',');			
			
			AppConf conf = new AppConf();
			conf.setServiceName(serviceName);
			conf.setSample(sample);
			conf.setKeys(keys);

			rtn.put(serviceName, conf);

		}

		return rtn;
		
	}

}
