/**
 * $
 */
package com.wade.dsf.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.wade.dsf.config.DsfConfigure;
import com.wade.dsf.executor.DsfTransactionManager;
import com.wade.dsf.executor.IDsfExecutor;
import com.wade.dsf.executor.IDsfTransaction;
import com.wade.dsf.executor.IDsfTranscationManager;
import com.wade.dsf.executor.invoker.DsfInvoker;
import com.wade.dsf.executor.invoker.IDsfInvoker;
import com.wade.dsf.filter.DsfExecutorFilter;
import com.wade.dsf.filter.DsfServerFilter;
import com.wade.dsf.filter.IDsfFilter;
import com.wade.dsf.adapter.IDsfAdapter;
import com.wade.dsf.registry.IDsfRegistry;
import com.wade.dsf.registry.entity.DsfEntityManagerImpl;
import com.wade.dsf.registry.entity.IDsfEntityManager;
import com.wade.dsf.startup.IDsfStartup;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfConfigure.java
 * @description: Dsf配置信息
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-2
 */
public final class DsfConfigure {
	
	private static final Logger log = Logger.getLogger(DsfConfigure.class);
	
	/**
	 * dsf.xml的ROOT对象
	 */
	private static Element root = null;
	
	private static final String DSF_CONFIGURE_FILE = "dsf.xml";
	
	
	/**
	 * 服务启动器配置
	 */
	private static final String DSF_CFG_STARTUP_PATH = "//startup/class";
	/**
	 * 远程服务过滤器配置
	 */
	private static final String DSF_CFG_REMOTE_FILTER_PATH = "//filter/remote/class";
	
	/**
	 * 本地服务过滤器配置
	 */
	private static final String DSF_CFG_LOCAL_FILTER_PATH = "//filter/local/class";
	
	/**
	 * 服务过滤器配置
	 */
	private static final String DSF_CFG_REGISTRY_PATH = "//registry/class";
	/**
	 * 数据转换适配器
	 */
	private static final String DSF_CFG_ADAPTER_PATH = "//adapter/*";
	/**
	 * 服务执行器
	 */
	private static final String DSF_CFG_EXECUTOR_PATH = "//executor/class";
	/**
	 * 方法反射调用类
	 */
	private static final String DSF_CFG_INVOKER_PATH = "//executor/invoker";
	/**
	 * 事务控制类
	 */
	private static final String DSF_CFG_TRANSCATION_PATH = "//executor/transaction";
	
	
	/**
	 * 启动器数组，将按顺序执行
	 */
	private static IDsfStartup[] startups = null;
	
	
	/**
	 * 启动器数组，将按顺序执行
	 */
	private static IDsfRegistry[] registrys = null;
	

	/**
	 * 过滤器数组, 将按顺序执行
	 */
	private static IDsfFilter[] remoteFilters = null;
	
	/**
	 * 过滤器数组, 将按顺序执行
	 */
	private static IDsfFilter[] localFilters = null;
	
	/**
	 * 框架控制, 主过滤器
	 */
	private static IDsfFilter serverFilter = new DsfServerFilter();
	/**
	 * 框架控制, 服务调用
	 */
	private static IDsfFilter executorFilter = new DsfExecutorFilter();
	
	/**
	 * 服务实体对象管理器
	 */
	private static IDsfEntityManager manager = new DsfEntityManagerImpl();
	
	/**
	 * 默认的服务调用器
	 */
	private static IDsfInvoker invoker = null;
	
	/**
	 * 数据适配器集合
	 */
	private static Map<String, IDsfAdapter> adapters = new HashMap<String, IDsfAdapter>(10);
	
	/**
	 * 服务执行器
	 */
	private static IDsfExecutor executor = null;
	
	/**
	 * 事务控制实现类
	 */
	private static IDsfTranscationManager transcation = new DsfTransactionManager();
	
	/**
	 * 单例对象
	 */
	private static DsfConfigure configure = new DsfConfigure();
	
	
	private DsfConfigure() {
		
	}
	
	public static DsfConfigure getInstance() {
		return configure;
	}
	
	
	public IDsfStartup[] getStartups() {
		return startups;
	}
	
	public IDsfRegistry[] getRegistrys() {
		return registrys;
	}
	
	
	public IDsfFilter[] getRemoteFilters() {
		return remoteFilters;
	}
	
	public IDsfFilter[] getLocalFilters() {
		return localFilters;
	}
	
	public IDsfAdapter getAdapter(String contentType) {
		return adapters.get(contentType);
	}
	
	public IDsfExecutor getExecutor() {
		return executor;
	}
	
	public IDsfTranscationManager getTranscationManager() {
		return transcation;
	}
	
	/**
	 * 获取服务调用器
	 * @param 
	 * @return the invoker
	 */
	public IDsfInvoker getInvoker() {
		return invoker;
	}
	
	
	public IDsfEntityManager getEntityManager() {
		return manager;
	}
	
	/**
	 * 获取扩展配置属性
	 * @param path
	 * @return
	 */
	public String getProperty(String path) {
		Node node = root.selectSingleNode(path);
		if (null != node) {
			return node.getText().trim();
		}
		return null;
	}
	
	
	/**
	 * 采用Dom4j解析dsf-configure.xml文件
	 * @return
	 * @throws Exception
	 */
	private static Element getRoot() throws Exception {
		InputStream in = null;
		try {
			in = DsfConfigure.class.getClassLoader().getResourceAsStream(DSF_CONFIGURE_FILE);
			if (in == null) {
				throw new FileNotFoundException();
			}
			SAXReader reader = new SAXReader();
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			return root;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (DocumentException e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}
	
	/**
	 * 解析//filter/local/class
	 * @param root
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static void parseLocalFilters(Element root) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<Element> nodes = root.selectNodes(DSF_CFG_LOCAL_FILTER_PATH);
		
		int size = nodes.size();
		
		List<IDsfFilter> list = new ArrayList<IDsfFilter>(size);
		
		/*去掉默认的过滤器*/
		//list.add(serverFilter);
		
		for (Node node : nodes) {
			String className = node.getText().trim();
			
			IDsfFilter filter = null;
			
			try {
				filter = (IDsfFilter)Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new ClassNotFoundException("实例化Filter时异常：[" + className + "]", e);
			}
			
			list.add(filter);
		}
		
		/*去掉默认的过滤器*/
		//list.add(executorFilter);
		
		localFilters = new IDsfFilter[list.size()];
		
		int index = 0; 
		for (IDsfFilter filter : list) {
			localFilters[index] = filter;
			index ++;
		}
	}
	
	/**
	 * 解析//filter/remote/class
	 * @param nodes
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	private static void parseRemoteFilters(Element root) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<Element> nodes = root.selectNodes(DSF_CFG_REMOTE_FILTER_PATH);
		
		int size = nodes.size();
		
		List<IDsfFilter> list = new ArrayList<IDsfFilter>(size);
		
		/*去掉默认的过滤器*/
		//list.add(serverFilter);
		
		for (Node node : nodes) {
			String className = node.getText().trim();
			
			IDsfFilter filter = null;
			
			try {
				filter = (IDsfFilter)Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new ClassNotFoundException("实例化Filter时异常：[" + className + "]", e);
			}
			
			list.add(filter);
		}
		
		/*去掉默认的过滤器*/
		//list.add(executorFilter);
		
		remoteFilters = new IDsfFilter[list.size()];
		
		int index = 0; 
		for (IDsfFilter filter : list) {
			remoteFilters[index] = filter;
			index ++;
		}
	}
	
	
	/**
	 * 解析服务启动器配置//startup/class
	 * @param root
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	private static void parseStartups(Node root) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<Element> nodes = root.selectNodes(DSF_CFG_STARTUP_PATH);
		
		int size = nodes.size();
		
		List<IDsfStartup> list = new ArrayList<IDsfStartup>(size);
		
		for (Node node : nodes) {
			String className = node.getText().trim();
			
			IDsfStartup startup = null;
			
			try {
				startup = (IDsfStartup)Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new ClassNotFoundException("实例化Startup时异常：[" + className + "]", e);
			}
			
			list.add(startup);
		}
		
		startups = new IDsfStartup[list.size()];
		
		int index = 0; 
		for (IDsfStartup startup : list) {
			startups[index] = startup;
			index ++;
		}
	}
	
	
	/**
	 * 解析//registry/class
	 * @param root
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	private static void parseRegistrys(Node root) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<Element> nodes = root.selectNodes(DSF_CFG_REGISTRY_PATH);
		
		int size = nodes.size();
		
		List<IDsfRegistry> list = new ArrayList<IDsfRegistry>(size);
		
		for (Node node : nodes) {
			String className = node.getText().trim();
			
			IDsfRegistry registry = null;
			
			try {
				registry = (IDsfRegistry)Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new ClassNotFoundException("实例化Registry时异常：[" + className + "]", e);
			}
			
			list.add(registry);
		}
		
		registrys = new IDsfRegistry[list.size()];
		
		int index = 0; 
		for (IDsfRegistry registry : list) {
			registrys[index] = registry;
			index ++;
		}
	}
	
	/**
	 * 解析//adapter/*
	 * @param root
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private static void parseAdapters(Node root) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<Element> nodes = root.selectNodes(DSF_CFG_ADAPTER_PATH);
		for (Node node : nodes) {
			Node typeNode = node.selectSingleNode("type");
			Node classNode = node.selectSingleNode("class");
			if (null != typeNode && null != classNode) {
				String type = typeNode.getText().trim();
				String className = classNode.getText().trim();
				
				IDsfAdapter adapter = null;
				try {
					adapter = (IDsfAdapter)Class.forName(className).newInstance();
				} catch (Exception e) {
					throw new ClassNotFoundException("实例化Adapter时异常：[" + className + "]", e);
				}
				
				adapters.put(type, adapter);
			} else {
				throw new ClassNotFoundException("Adapter配置异常，type和class属性不能为空，" + node.getText());
			}
		}
	}
	
	/**
	 * 解析//executor/class
	 * @param root
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private static void parseExecutor(Node root) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Node node = root.selectSingleNode(DSF_CFG_EXECUTOR_PATH);
		if (null == node)
			return;
		
		String className = node.getText().trim();
		
		try {
			executor = (IDsfExecutor)Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new ClassNotFoundException("实例化Executor时异常：" + className, e);
		}
	}
	
	
	/**
	 * 解析服务调用类//executor/invoker配置
	 * @param root
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private static void parseInvoker(Node root) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Node node = root.selectSingleNode(DSF_CFG_INVOKER_PATH);
		if (null == node) {
			invoker = new DsfInvoker();
			return;
		}
		
		String className = node.getText();
		try {
			invoker = (IDsfInvoker)Class.forName(className.trim()).newInstance();
		} catch (Exception e) {
			throw new ClassNotFoundException("实例化Executor-Invoker时异常：" + className, e);
		}
	}
	
	
	/**
	 * 解析服务调用类//executor/transcation配置
	 * @param root
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private static void parseTranscation(Node root) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Node node = root.selectSingleNode(DSF_CFG_TRANSCATION_PATH);
		if (null == node) {
			return;
		}
		
		String className = node.getText();
		try {
			@SuppressWarnings("unchecked")
			Class<IDsfTransaction> transactionImplClass = (Class<IDsfTransaction>) Class.forName(className.trim());
			transcation.setTranscationImplClass(transactionImplClass);
		} catch (Exception e) {
			throw new ClassNotFoundException("实例化Executor-Transcation时异常：" + className, e);
		}
	}
	
	static {
		try {
			root = getRoot();
			// 解析服务启动器
			parseStartups(root);
			
			// 解析服务注册器
			parseRegistrys(root);
			
			// 解析远程过滤器
			parseRemoteFilters(root);
			
			// 解析本地过滤器
			parseLocalFilters(root);
			
			// 解析适配器
			parseAdapters(root);
			
			// 解析执行器
			parseExecutor(root);
			
			// 解析服务调用类
			parseInvoker(root);
			
			// 解决事务控制类
			parseTranscation(root);
		} catch (Exception e) {
			log.error("加载dsf-configure.xml失败", e);
		}
	}
	
	public static void main(String[] args) {
		DsfConfigure configure = DsfConfigure.getInstance();
		
		for (IDsfStartup startup : configure.getStartups()) {
			System.out.println("Startup : " + startup.getClass().getName());
		}
		
		for (IDsfRegistry registry : configure.getRegistrys()) {
			System.out.println("Registry : " + registry.getClass().getName());
		}
		
		for (IDsfFilter filter : configure.getRemoteFilters()) {
			System.out.println("Remote Filter : " + filter.getClass().getName());
		}
		
		for (IDsfFilter filter : configure.getLocalFilters()) {
			System.out.println("Local Filter : " + filter.getClass().getName());
		}
		
		System.out.println("Executor : " + executor.getClass().getName());
		
		Iterator<String> iter = adapters.keySet().iterator();
		while (iter.hasNext()) {
			String type = iter.next();
			System.out.println("Adapter : type=" + type + ", class=" + adapters.get(type).getClass().getName());
		}
		
		System.out.println("Invoker : " + invoker.getClass().getName());
		
		System.out.println("Transcation : " + transcation.getClass().getName());
	}
}
