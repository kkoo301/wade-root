/**
 * 
 */
package com.ailk.service.client;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;

/**
 * @author yifur
 *
 */
public interface IProtocalClient {
	
	
	/**
	 * 发送请求
	 * @param url
	 * @param svcname
	 * @param input
	 * @param soTimeout
	 * @param connectTimeout
	 * @return
	 * @throws Exception
	 */
	public IDataOutput request(String url, String svcname, IDataInput input, int soTimeout, int connectTimeout) throws Exception ;
}
