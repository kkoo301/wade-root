package com.ailk.service.protocol.impl;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IDataInput;
import com.ailk.service.invoker.IMethodIntercept;
import com.ailk.service.protocol.IService;
import com.ailk.service.protocol.IServiceBus;
import com.ailk.service.protocol.IServiceProtocol;
import com.ailk.service.protocol.IServiceRule;

public class ServiceEntity implements IService {
	private static final long serialVersionUID = 1L;
	
	public static final int STATUS_DISABLED = -1;
	public static final int STATUS_REGISTERED = 0;
	public static final int STATUS_ENABLED = 1;
	
	private IServiceBus bus = new ServiceBus();
	private String name;
	private String desc;
	private IServiceRule rule;
	private String fileName;
	private IServiceProtocol protocol;
	private Class<?> entityClass;
	private Method entityMethod;
	private IMethodIntercept methodIntercept;
	private Map<String, String> attributes = new HashMap<String, String>(10);
	private String group;
	private int timeout;
	private String version = "wade";
	
	/**
	 * 默认不限制并发数
	 */
	private long threshold = 0;
	private String requestType;
	
	
	/**
	 * 中心编码
	 */
	private String center;
	
	/*-1：失效；0:注册；1：激活*/
	private int status=0;
	
	public ServiceEntity() {
		
	}

	public IServiceProtocol getProtocol() {
		return protocol;
	}
	
	
	public void setProtocol(IServiceProtocol protocol) {
		this.protocol = protocol;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}
	
	
	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	
	/**
	 * @return the requestType
	 */
	public String getRequestType() {
		return requestType;
	}
	
	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	
	public IServiceRule getRule() {
		return rule;
	}

	public void setRule(IServiceRule rule) {
		this.rule = rule;
	}
	
	public boolean add(String name, IDataInput in, IServiceRule rule) {
		return bus.add(name, in, rule);
	}
	
	public IService remove(String name) {
		return bus.remove(name);
	}
	
	public IServiceBus getServiceBus() {
		return this.bus;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	public Class<?> getEntityClass() {
		return this.entityClass;
	}
	
	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
	
	public Method getEntityMethod() {
		return this.entityMethod;
	}
	
	public void setEntityMethod(Method entityMethod) {
		this.entityMethod = entityMethod;
	}

	public IMethodIntercept getMethodIntercept() {
		return methodIntercept;
	}

	public void setMethodIntercept(IMethodIntercept methodIntercept) {
		this.methodIntercept = methodIntercept;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}
	
	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * @return the threshold
	 */
	public long getThreshold() {
		return threshold;
	}
	
	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}
	
	public String getCenter() {
		return center;
	}
	
	public void setCenter(String center) {
		this.center = center;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
}
