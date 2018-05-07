/**
 * 
 */
package com.ailk.service.invoker;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.ailk.common.Constants;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.logger.AbstractLogger;
import com.ailk.common.logger.ILogger;
import com.ailk.service.invoker.impl.ServiceMethodIntercept;
import com.ailk.service.protocol.IBaseService;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.ailk.service.session.SessionManager;

/**
 * @author $Id: ServiceInvokeCallable.java 10800 2017-05-18 10:15:16Z liaos $
 * 
 */
public class ServiceInvokeCallable implements Callable<IDataOutput> {

	private static final transient Logger log = Logger.getLogger(ServiceInvokeCallable.class);
	private static final String MDC_KEY = "STAFF_ID";
	private static final String MDC_SESSION_KEY = "SESSION_ID";

	private String name;
	private String group;
	private ServiceEntity entity;
	private IDataInput input;

	private IBaseService service;
	private Method method;
	private IVisit visit;

	/**
	 * ServiceInvokeThread
	 * 
	 * @param name
	 * @param group
	 * @param entity
	 * @param input
	 * @throws Exception
	 */
	public ServiceInvokeCallable(String name, String group, ServiceEntity entity, IDataInput input) throws Exception {
		this.name = name;
		this.group = group;
		this.entity = entity;
		this.input = input;

		service = (IBaseService) entity.getEntityClass().newInstance();
		method = entity.getEntityMethod();
		method.setAccessible(true);

		IData head = input.getHead();
		service.setName(this.name);
		service.setGroup(group);
		service.setPagination(input.getPagination());
		service.setEntity(entity);

		// 创建线程上下文对象
		visit = service.createVisit(head);
		visit.set(Constants.X_TRANS_CODE, name);
		
		IData data = input.getData();
		String inModeCode = head.getString(Constants.IN_MODE_CODE);
		if (null == inModeCode || inModeCode.length() <= 0) {
			inModeCode = data.getString(Constants.IN_MODE_CODE);
		}
		if (null == inModeCode || inModeCode.length() <= 0) {
			inModeCode = "0";
		}
		visit.set(Constants.IN_MODE_CODE, inModeCode);
		head.put(Constants.IN_MODE_CODE, inModeCode);
	}

	public IDataOutput call() throws Exception {
		IDataOutput output = new DataOutput();

		long start = System.currentTimeMillis();

		SessionManager manager = SessionManager.getInstance();

		try {
			// 激活会话,并设置上下文对象
			manager.start();
			manager.setContext(this.service, this.visit);
			//manager.getSession().setTimeout(entity.getTimeout());

			String key = this.visit.get(MDC_KEY);
			MDC.put(MDC_KEY, null == key ? "" : key);
			MDC.put(MDC_SESSION_KEY, SessionManager.getInstance().getId());

			// 服务初始化
			this.service.initialize(input.getData());
			ServiceMethodIntercept intercept = (ServiceMethodIntercept) entity.getMethodIntercept();
			if (log.isDebugEnabled()) {
				log.debug(">>>服务调用开始:" + this.name);
				log.debug(">>>服务调用上下文:" + this.visit);
				log.debug(">>>服务调用输入:" + input.getHead());
				log.debug(">>>服务调用数据:" + input.getData());
				log.debug(">>>服务方法拦截器:" + (intercept == null ? "" : intercept.getClass().getName()));
			}

			if (intercept != null) {
				if (intercept.invokeBefore(this.name, input.getHead(), input.getData())) {
					// 反射
					Object result = ServiceInvoker.invoke(this.method, this.service, this.input.getData());
					output = ServiceInvoker.objectToDataOutput(result, this.service);
					if (!intercept.invokeAfter(this.name, input.getHead(), input.getData(), output.getData())) {
						output.getHead().put(Constants.X_RESULTCODE, this.service.getResultCode());
						output.getHead().put(Constants.X_RESULTINFO, this.service.getResultInfo());
					}
				} else {
					output.getHead().put(Constants.X_RESULTCODE, this.service.getResultCode());
					output.getHead().put(Constants.X_RESULTINFO, this.service.getResultInfo());
				}
			} else {
				// 反射
				Object result = ServiceInvoker.invoke(this.method, this.service, this.input.getData());
				output = ServiceInvoker.objectToDataOutput(result, this.service);
			}
			
			manager.commit();
		} catch (Throwable e) {
			manager.rollback();
			
			if (log.isDebugEnabled()) {
				log.debug(">>>服务调用异常:[name:" + name + "][error:" + e.getClass() + ":" + e.getMessage() + "]");
			}
			
			throw new RuntimeException(e);
		} finally {
			try {
				try {
					this.service.destroy(this.input, output);
				} catch (Exception e) {
					throw e;
				} finally {
					manager.destroy();
				}
			} catch (Exception e) {
				log.error("SERVICE ERROR ", e);
				throw e;
			} finally {
				//记录统计日志
				ILogger logger = AbstractLogger.getLogger(this.service.getClass());
				if (null != logger)
					logger.log(this.service, this.name + ":" + this.method.getName(), start, (System.currentTimeMillis() - start), null);
				
				this.service = null;
				MDC.remove(MDC_KEY);
				MDC.remove(MDC_SESSION_KEY);
			}
		}

		return output;
	}

	public String getServiceClass() {
		return this.service.getClass().getName();
	}

	public String getServiceMethod() {
		return this.method.getName();
	}

	public String getName() {
		return this.name;
	}

	public String getGroup() {
		return this.group;
	}

	
}
