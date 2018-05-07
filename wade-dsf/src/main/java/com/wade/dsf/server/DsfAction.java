/**
 * $
 */
package com.wade.dsf.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wade.dsf.server.DsfAction;
import com.wade.dsf.server.DsfContext;
import com.wade.dsf.adapter.IDsfAdapter;
import com.wade.dsf.config.DsfConfigure;
import com.wade.dsf.exception.DsfErr;
import com.wade.dsf.exception.DsfException;
import com.wade.dsf.filter.DsfRemoteFilterChain;
import com.wade.dsf.registry.entity.IDsfEntity;
import com.wade.dsf.registry.entity.IDsfEntityManager;
import com.wade.dsf.request.DsfRequest;
import com.wade.dsf.request.DsfRequestHeader;
import com.wade.dsf.response.DsfResponse;
import com.wade.dsf.startup.IDsfStartup;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfAction.java
 * @description: 该对象为单例对象，操作时请注意线程安全
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public final class DsfAction implements IDsfAction {
	
	private static final Logger log = Logger.getLogger(DsfAction.class);
	
	/**
	 * 用来避免重复执行startup对象
	 */
	private Set<IDsfStartup> started = new HashSet<IDsfStartup>(10);
	
	private static IDsfAction action = new DsfAction();
	private boolean inited = false;
	private Object lock = new Object();
	
	private DsfAction() {
		
	}
	
	public static IDsfAction getInstance() {
		return action;
	}
	
	/**
	 * 服务初始化，按顺序执行所有的启动器
	 */
	@Override
	public void init() throws DsfException {
		
		if (inited) {
			return ;
		}
		
		synchronized (lock) {
			if (inited)
				return ;
			
			DsfConfigure configure = DsfConfigure.getInstance();
			
			IDsfStartup[] startups = configure.getStartups();
			
			if (null != startups) {
				
				for (IDsfStartup startup : startups) {
					
					if (!started.contains(startup)) {
						started.add(startup);
						try {
							long start = System.currentTimeMillis();
							
							startup.startup();
							
							if (log.isInfoEnabled()) {
								log.info(String.format("执行初始化操作：%s，耗时%dms", startup.getClass().getName(), (System.currentTimeMillis() - start)));
							}
							
						} catch (Exception e) {
							throw new DsfException(DsfErr.dsf10000.getCode(), DsfErr.dsf10000.getInfo(startup.getClass().getName()), e);
						}
					} else {
						throw new DsfException(DsfErr.dsf10000.getCode(), DsfErr.dsf10000.getInfo("重复配置的启动器，" + startup.getClass().getName()));
					}
				}
				
				inited = true;
				
				return;
			}
			
			throw new DsfException(DsfErr.dsf10000.getCode(), DsfErr.dsf10000.getInfo("dsf.xml@startup不能为空"));
		}
	}
	
	
	/**
	 * 反序列化请求对象
	 * 
	 * @param serviceName
	 * @param requestType
	 * @param is
	 * @return
	 * @throws DsfException
	 */
	@Override
	public Serializable read(String serviceName, Map<String, String> requestHeader, InputStream is) throws DsfException {
		DsfConfigure configure = DsfConfigure.getInstance();
		
		String charset = requestHeader.get(DsfRequestHeader.Charset.getCode());
		String contentType = requestHeader.get(DsfRequestHeader.ContextType.getCode());
		
		IDsfAdapter adapter = configure.getAdapter(contentType);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("协议适配[读],%s=%s", serviceName, adapter.getClass().getName()));
		}
		
		try {
			return adapter.streamToObject(requestHeader, is, charset);
		} catch (Exception e) {
			throw new DsfException(DsfErr.dsf10014.getCode(), DsfErr.dsf10014.getInfo(contentType, adapter.getClass().getName()), e);
		}
	}
	
	/**
	 * 将对象数据序列化后输出
	 * @param serviceName
	 * @param requestHeader
	 * @param out
	 * @param object
	 * @throws DsfException
	 */
	@Override
	public int write(String serviceName, Map<String, String> requestHeader, OutputStream out, Serializable object) throws DsfException {
		DsfConfigure configure = DsfConfigure.getInstance();
		
		String charset = requestHeader.get(DsfRequestHeader.Charset.getCode());
		String contentType = requestHeader.get(DsfRequestHeader.ContextType.getCode());
		
		IDsfAdapter adapter = configure.getAdapter(contentType);
		
		if (log.isDebugEnabled()) {
			log.debug(String.format("协议适配[写],%s=%s", serviceName, adapter.getClass().getName()));
		}
		
		try {
			if (object instanceof Exception) {
				return adapter.exceptionToStream(requestHeader, out, (Exception) object, charset);
			}
			return adapter.objectToStream(requestHeader, out, object, charset);
		} catch (Exception e) {
			throw new DsfException(DsfErr.dsf10014.getCode(), DsfErr.dsf10014.getInfo(contentType, adapter.getClass().getName()), e);
		}
	}
	
	/**
	 * 请求执行
	 * @param serviceName
	 * @param header
	 * @param body
	 * @return
	 */
	@Override
	public Serializable execute(String serviceName, Map<String, String> header, Serializable body) throws DsfException {
		try {
			DsfConfigure configure = DsfConfigure.getInstance();
			
			IDsfEntityManager manager = configure.getEntityManager();
			
			// 根据服务名创建服务注册信息
			IDsfEntity entity = manager.getEntity(serviceName);
			if (null == entity) {
				if (log.isDebugEnabled()) {
					log.debug("根据服务名获取服务注册信息失败, " + serviceName);
				}
				throw new DsfException(DsfErr.dsf10002.getCode(), DsfErr.dsf10002.getInfo(serviceName));
			}
			if (log.isDebugEnabled()) {
				log.debug(String.format("获取服务注册信息, 服务名:%s, 中心编码:%s", serviceName, entity.getCenter()));
			}
			
			// 创建请求、响应对象
			DsfRequest dreq = new DsfRequest(serviceName, header, body);
			DsfResponse dres = new DsfResponse();
			
			// 创建线程上下文对象, 线程退出时需清除
			DsfContext context = DsfContext.getContext();
			context.setServiceName(serviceName);
			context.setRequest(dreq);
			context.setResponse(dres);
			
			// 执行过滤器链
			DsfRemoteFilterChain chain = new DsfRemoteFilterChain();
			chain.doFilter(dreq, dres);
			
			// 返回业务对象
			Serializable response = context.getResponse().getResponse();
			
			return response;
		} catch (DsfException e) {
			throw e;
		} finally {
			DsfContext.destory();
		}
	}

	/**
	 * 将异常包装成DsfResponse对象返回
	 * @param serviceName
	 * @param header
	 * @param out
	 * @param e
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	@Override
	public int error(String serviceName, Map<String, String> header, OutputStream out, Exception e) {
		DsfConfigure configure = DsfConfigure.getInstance();
		
		String charset = header.get(DsfRequestHeader.Charset.getCode());
		String contentType = header.get(DsfRequestHeader.ContextType.getCode());
		
		IDsfAdapter adapter = configure.getAdapter(contentType);
		if (null == adapter) {
			
			NullPointerException ne = new NullPointerException(String.format("服务%s请求异常，根据请求类型%s找不到适配器对象", serviceName, DsfRequestHeader.Charset.getCode()));
			log.error(ne.getMessage(), ne);
			
			throw ne;
		}
		return adapter.exceptionToStream(header, out, e, charset);
	}

}
