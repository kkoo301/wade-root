package com.ailk.service.protocol.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IDataInput;
import com.ailk.service.client.ServiceFactory;
import com.ailk.service.protocol.IService;
import com.ailk.service.protocol.IServiceBus;
import com.ailk.service.protocol.IServiceRule;

public class ServiceBus implements IServiceBus {
	
	private static final long serialVersionUID = 1L;
	private Map<String, String> global = new HashMap<String, String>();
	private Map<String, Integer> names = new HashMap<String, Integer>();
	private List<IService> services = new ArrayList<IService>();
	private Map<String, IDataInput> data = new HashMap<String, IDataInput>();
	
	/**
	 * get attribute
	 * @param name
	 * @return
	 */
	public String getAttribute(String name) {
		if (global.containsKey(name)) {
			return global.get(name);
		}
		return null;
	}
	
	
	/**
	 * get service by name
	 * @param name
	 * @return
	 */
	public IService getService(String name) {
		if (names.containsKey(name)) {
			int index = names.get(name).intValue();
			if (index >=0 && index < services.size()) {
				return services.get(index);
			}
		}
		return null;
	}

	/**
	 * get data
	 * @param name
	 * @return
	 */
	public IDataInput getData(String name) {
		if (names.containsKey(name)) {
			int index = names.get(name).intValue();
			if (index >=0 && index < services.size()) {
				return data.get(name);
			}
		}
		return null;
	}
	
	/**
	 * remote service
	 * @param name
	 * @return
	 */
	public IService remove(String name) {
		if (names.containsKey(name)) {
			int index = names.get(name).intValue();
			if (index >=0 && index < services.size()) {
				IService service = services.remove(index);
				if (service != null) {
					this.data.remove(name);
				}
				return service;
			}
		}
		return null;
	}
	
	/**
	 * add service
	 * @param serviceName
	 * @param data
	 * @param rule
	 * @return
	 */
	public boolean add(String name, IDataInput data, IServiceRule rule) {
		if (names.containsKey(name)) {
			return true;
		} else {
			IService service = ServiceFactory.create(name, rule);
			if (services.add(service)) {
				this.data.put(name, data);
				return true;
			}
			return false;
		}
	}
}
