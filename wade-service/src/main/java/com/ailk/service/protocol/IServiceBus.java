package com.ailk.service.protocol;

import java.io.Serializable;

import com.ailk.common.data.IDataInput;

public interface IServiceBus extends Serializable {
	
	/**
	 * get attribute
	 * @param name
	 * @return
	 */
	public String getAttribute(String name);
	
	
	/**
	 * get service by name
	 * @param name
	 * @return
	 */
	public IService getService(String name);

	
	/**
	 * get data
	 * @param name
	 * @return
	 */
	public IDataInput getData(String name);
	
	/**
	 * remote service
	 * @param name
	 * @return
	 */
	public IService remove(String name);
	
	/**
	 * add service
	 * @param serviceName
	 * @param input
	 * @param rule
	 * @return
	 */
	public boolean add(String name, IDataInput input, IServiceRule rule) ;

}
