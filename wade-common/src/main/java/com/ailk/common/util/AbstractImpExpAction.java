package com.ailk.common.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IVisit;
import com.ailk.common.util.impl.DefaultImpExpAction;
import com.ailk.common.util.impl.DefaultImpExpManager;

/**
 * 导入导出Servlet调用的处理方法，用于自定义ImpExpServlet的处理方式
 * 
 * @author lvchao
 *
 */
public abstract class AbstractImpExpAction {

	private static transient final Logger log = Logger.getLogger(AbstractImpExpAction.class);
	private static AbstractImpExpManager impExpManager = null;
	private ThreadLocal<IVisit> visitLocal = new ThreadLocal<IVisit>();
	
	/**
	 * 传入servlet的配置数据
	 * 
	 * @param config
	 */
	public abstract void initConfig(Map<String, Object> config);
	
	/**
	 * 获取导入或导出的唯一编号，要求全局唯一
	 * 
	 * @return
	 */
	public String getSerializeId() throws Exception {
		return UUID.randomUUID().toString();
	}
	
	/**
	 *	自定义导入导出中断对应的操作
	 * 
	 * @param request
	 * @param response
	 */
	public abstract void cancelProgress(Map<String, Object> params) throws Exception;
	
	/**
	 * 当存在定时调度任务时，可通过可方法初始化定时调度数据，返回的数据中应包含totalSize，startTime，useTime
	 * 
	 * @param request
	 * @param response
	 */
	public abstract String initTimer(Map<String, Object> params) throws Exception;
	
	/**
	 * 调用组件中配置的ServiceName参数指定的方法
	 * @param serviceName serviceName对应的处理方法
	 * @param fileSerializeId
	 * @param paramMap
	 * @throws Exception
	 */
	public abstract void callService(Map<String, Object> param);
	
	/**
	 * 获取当前状态
	 * 
	 * @param request
	 * @param response
	 * @param fileSerializeId
	 * @throws IOException
	 */
	public abstract String getProgressData(String serializeId) throws Exception;

	public void setVisit(IVisit visit) {
		visitLocal.set(visit);
	}

	public IVisit getVisit() {
		return visitLocal.get();
	}
	
	public void doService(Map<String, Object> params, String serviceName, String serializeId) throws Exception {
		String[] serviceArray = serviceName.split("@");
		String className = serviceArray[0];
		String methodName = serviceArray[1];
		Class<?> serviceCls = Class.forName(className);
		serviceCls.getMethod(methodName, new Class[]{String.class, Map.class}).invoke(serviceCls.newInstance(), serializeId, params);
	}

	public AbstractImpExpManager getImpExpManager(){
		if(impExpManager == null){
			synchronized(DefaultImpExpAction.class){
				if(impExpManager == null){
					String managerClazz = GlobalCfg.getImpExpManager();
					try {
						if ("".equals(managerClazz) || managerClazz == null)
							throw new ClassNotFoundException("action is empty");

						Class<?> clazz = getClass().getClassLoader().loadClass(managerClazz);
						impExpManager = (AbstractImpExpManager) clazz.newInstance();

					} catch (Exception e) {
						if (log.isInfoEnabled())
							log.info("init impExp manager [" + managerClazz + "], use DefaultImpExpManager.");
						impExpManager = new DefaultImpExpManager();
					}
				}
			}
		}
		return impExpManager; 
	}
	
	public void doCancelProgress(Map<String, Object> params, String serviceName, String serializeId) throws Exception{
		//IData param = new BaseContext(request).getData();
		String fileSerializeId = (String)params.get("fileSerializeId");
		String[] serviceArray = serviceName.split("@");
		String clazz = serviceArray[0];
		Class<?> serviceCls;
		try {
			serviceCls = Class.forName(clazz);
			Method[] methods = serviceCls.getMethods();
			if(methods != null && methods.length > 0){
				for(Method method : methods){
					if("cancel".equals(method.getName())){
						serviceCls.getMethod("cancel",new Class[]{String.class, Map.class}).invoke(serviceCls.newInstance(), fileSerializeId, params);
						break ;
					}
				}
			}
		} catch(NoSuchMethodException e){
			Utility.getBottomException(e).printStackTrace();
		}catch (Exception e) {
			Utility.getBottomException(e).printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public String buildProgressData(String serializeId){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"progress\":\"" + getImpExpManager().getProgress(serializeId) + "\"");
		sb.append(",");
		sb.append("\"status\":\"" + getImpExpManager().getStatusStep(serializeId) + "\"");
		sb.append(",");
		sb.append("\"fileSerializeId\":\"" + serializeId + "\"");
		sb.append(",");
		sb.append("\"hint\":\"" + getImpExpManager().getHint(serializeId) + "\"");
		sb.append(",");
		sb.append("\"downloadUrl\":\"" + getImpExpManager().getDownLoadUrl(serializeId) + "\"");
		sb.append(",");
		sb.append("\"needTime\":\"" + getImpExpManager().getRemainTime(serializeId) + "\"");
		sb.append("}");
		return sb.toString();
	}
	
	public String buildInitTimerData(Map<String, Object> map, String totalSize, String startTime, String useTime){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"totalSize\":\"" + map.get(totalSize) + "\"");
		sb.append(",");
		sb.append("\"startTime\":\"" + map.get(startTime) + "\"");
		sb.append(",");
		sb.append("\"useTime\":\"" + map.get(useTime) + "\"");
		sb.append("}");
		return sb.toString();
	}
}
