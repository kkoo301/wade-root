package com.ailk.service;
/**
 * 版本信息：$Id: ServiceManager.java 8520 2016-09-25 05:01:52Z liaos $
 * 说明：
 * 将服务列表(list)、服务超时控制、服务并发控制、服务流量统计存在Redis[name=service]里
 * 将服务实体对象(ServiceEntity)存在MemCached[name=service]里
*/
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ailk.common.util.ClazzUtil;
import com.ailk.service.invoker.MethodInterceptFactory;
import com.ailk.service.loader.IServiceRegister;
import com.ailk.service.loader.impl.ServiceConfigRegister;
import com.ailk.service.protocol.config.CenterInfo;
import com.ailk.service.protocol.config.ICenterResolver;
import com.ailk.service.protocol.config.impl.DefaultCenterResolver;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.ailk.service.router.IServiceRouter;
import com.wade.relax.registry.SystemUtil;

public class ServiceManager implements Serializable {
	
	private transient static final long serialVersionUID = 1L;
	private transient static IServiceRouter router = null;
	private transient static IServiceRegister register = null;
	private transient static ICenterResolver resolver = null;
	private static Map<String, ServiceEntity> entities = null;
	private transient static String localCenterName = SystemUtil.getCenterName();
	private transient static String commonCenterName = "base";
	private static Set<String> serviceNames = null;
	
	private static ServiceManager manager = new ServiceManager();
	
	private static boolean isLoadFinish = false;
	
	private ServiceManager() {
		
	}
	
	/**
	 * get instance
	 * @return
	 */
	public static ServiceManager getInstance() throws Exception {
		return manager;
	}
	
	/**
	 * get router
	 */
	public static IServiceRouter getRouter() {
		return router;
	}
	
	
	/**
	 * create register
	 * @param clazz
	 * @return
	 */
	public static IServiceRegister createRegister(String clazz) {
		if (register != null) return register;
		
		register = (IServiceRegister)ClazzUtil.load(clazz, new ServiceConfigRegister());
		
		return register;
	}
	
	public static ICenterResolver createResolver(String clazz) {
		if (resolver != null) return resolver;
		
		resolver = (ICenterResolver)ClazzUtil.load(clazz, new DefaultCenterResolver());
		
		return resolver;
	}
	
	/**
	 * get router
	 */
	public static IServiceRegister getRegister() {
		return register;
	}
	
	/**
	 * 获取中心配置
	 * @return
	 */
	public static ICenterResolver getResolver() {
		return resolver;
	}
	
	
	/**
	 * 仅在子服务调用时触发，主服务不做判断
	 * 1. 当子服务归属当前中心或common中心时，做本地调用
	 * 2. 当子服务归属其它中心，且该中心为允许跨中心调用时，做远程调用，否则为本地调用
	 * @param entity
	 * @return
	 */
	public static boolean isLocalService(ServiceEntity entity) {
		if (null == entity || null == entity.getCenter() || entity.getCenter().length() == 0) {
			return true;
		}
		
		if ( null == localCenterName || localCenterName.length() == 0) {
			return true;
		}
		
		if (localCenterName.equals(entity.getCenter()) || "common".equals(entity.getGroup())) {
			return true;
		}
		
		try {
			if (resolver.resolve().get(entity.getCenter()).isCrossCentre()) {
				return false;
			} else {
				return true; 
			}
		} catch (Exception e) {
			return true;
		}
	}
	
	/**
	 * 判断当前服务是否为本地中心的
	 * @param entity
	 * @return
	 */
	public static boolean isLocalService(String name) {
		return isLocalService(getServiceEntity(name));
	}
	
	
	/**
	 * 返回所有服务对象
	 * @return
	 */
	public static Map<String, ServiceEntity> getEntities() {
		return entities;
	}
	
	/**
	 * 获取所有当前中心的服务名
	 * @return
	 */
	public static Map<String, Set<String>> getAllLocalService() {
		Map<String, Set<String>> services = new HashMap<String, Set<String>>(3000);
		Set<String> local = new HashSet<String>(10000);
		Set<String> common = new HashSet<String>(1000);
		
		Iterator<String> iter = entities.keySet().iterator();
		
		while (iter.hasNext()) {
			ServiceEntity entity = entities.get(iter.next());
			String svcname = entity.getName();
			
			int index = svcname.indexOf(":");
			if (index != -1) {
				svcname = svcname.substring(index+1);
			}
			
			if (localCenterName.equals(entity.getCenter())) {
				local.add(svcname);
			} else if (commonCenterName.equals(entity.getCenter())) {
				common.add(svcname);
			}
		}
		
		services.put("local", local);
		services.put("common", common);
		
		return services;
	}
	
	
	/**
	 * 服务查询
	 * @param name
	 * @return
	 */
	public static ServiceEntity find(String name) {
		ServiceEntity entity = getServiceEntity(name);
		return entity;
	}
	
	/**
	 * 注册
	 * @throws Exception
	 */
	public static void register() throws Exception {
		if (null == entities) {
			Map<String, ServiceEntity> load = getRegister().loadService();
			serviceNames = load.keySet();
			
			entities = new HashMap<String, ServiceEntity>(load.size());
			entities.putAll(load);
		}
	}
	
	
	/**
	 * 设置服务中心
	 * @throws Exception
	 */
	public static void resolve() throws Exception {
		if (null != entities) {
			Map<String, CenterInfo> centers = resolver.resolve();
			
			Iterator<String> iter = entities.keySet().iterator();
			while (iter.hasNext()) {
				ServiceEntity entity = entities.get(iter.next());
				String group = entity.getGroup();
				CenterInfo cc = centers.get(group);
				if (null != cc)
					entity.setCenter(cc.getCenterName());
			}
			
			isLoadFinish = true;
		}
	}
	
	/**
	 * 获取中心配置
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static CenterInfo getCenter(String name) throws Exception {
		Map<String, CenterInfo> centers = resolver.resolve();
		return centers.get(name);
	}
	
	
	/**
	 * 服务是否加完完成
	 * @return
	 */
	public static boolean isLoadFinish() {
		return isLoadFinish;
	}
	
	/**
	 * 服务列表
	 * @return
	 */
	public static Set<String> serviceList() throws Exception {
		return listServiceEntity();
	}
	
	/**
	 * 路由列表
	 * @return
	 */
	public static List<String> routeList(String key) {
		return null;
	}
	
	
	/**
	 * 设置服务方法拦截器
	 * @param name
	 * @param clazz
	 * @throws Exception
	 */
	public static void setServiceIntercept(String name, String clazz) throws Exception {
		find(name).setMethodIntercept(MethodInterceptFactory.getMethodIntercept(clazz));
	}
	
	/**
	 * 获取服务当前状态
	 * @param name
	 * @return
	 */
	public static int status(String name) throws Exception {
		ServiceEntity entity = getServiceEntity(name);
		if (entity != null) 
			return entity.getStatus();
		return -2;
	}
	
	/**
	 * is active
	 * @param name
	 * @return
	 */
	public static boolean isActive(String name) throws Exception {
		return status(name) >= 0;
	}
	
	
	/**
	 * 注销服务
	 * @param name
	 */
	public static void destroy(String name) {
		
	}
	
	/**
	 * list service entity
	 * @return
	 */
	private static Set<String> listServiceEntity() throws Exception {
		if (null == entities)
			return null;
		
		return serviceNames;
	}
	
	
	
	/**
	 * get cache
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static ServiceEntity getServiceEntity(String name) {
		if (null == entities)
			return null;
		
		return entities.get(name);
	}
	
}
