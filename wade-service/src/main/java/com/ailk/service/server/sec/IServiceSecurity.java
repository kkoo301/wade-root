/**
 * 
 */
package com.ailk.service.server.sec;

import com.ailk.common.data.IData;

/**
 * 服务安全验证，单例
 * 1.管理验证码及其生命周期
 * 2.校验验证码
 * @author yifur
 *
 */
public interface IServiceSecurity {
	
	/**
	 * 创建Key
	 * @param encry 加密因子
	 * @return
	 */
	public String createKey(String encry, IData head);
	
	/**
	 * 效验Key
	 * @param key 明文Key
	 * @param encry 加密因子
	 * @return
	 */
	public boolean isValidKey(String key, String encry);
	
	
}
