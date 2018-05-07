package com.ailk.service.invoker;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.Constants;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IVisit;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.logger.AbstractLogger;
import com.ailk.common.logger.ILogger;
import com.ailk.common.trace.AbstractTracer;
import com.ailk.common.trace.ITracer;
import com.ailk.common.util.Utility;
import com.ailk.service.ServiceManager;
import com.ailk.service.invoker.impl.ServiceMethodIntercept;
import com.ailk.service.invoker.priv.AnnotationServicePriv;
import com.ailk.service.invoker.priv.IServicePriv;
import com.ailk.service.protocol.IBaseService;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.ailk.service.server.filter.MainServiceAction;
import com.ailk.service.session.SessionManager;
import com.ailk.service.session.app.AppInvoker;
import com.ailk.service.session.app.AppSession;
import com.ailk.service.session.app.LocalServiceAction;

public class ServiceInvoker {

	private transient static final Logger log = Logger.getLogger(ServiceInvoker.class);
	
	private static boolean DSF_REMOTE_SERVICE_ENABLE = "true".equals(GlobalCfg.getProperty("dsf.remoteservice.enable", "false"));
	private static boolean DSF_LOCALE_SERVICE_ENABLE = "true".equals(GlobalCfg.getProperty("dsf.localservice.enable", "false"));

	private ServiceInvoker() {

	}
	
	
	
	/**
	 * 主服务调用
	 * @param serviceName
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static final IDataOutput mainServiceInvoke(String serviceName, IDataInput input) throws Exception {
		if (DSF_REMOTE_SERVICE_ENABLE) {
			Map<String, String> header = new HashMap<String, String>();
			IData head = input.getHead();
			for (Map.Entry<String, Object> item : head.entrySet()) {
				if (null != item.getValue()) {
					header.put(item.getKey(), item.getValue().toString());
				}
			}
			MainServiceAction action = new MainServiceAction();
			IDataOutput output = (IDataOutput) action.execute(serviceName, header, input);
			return output;
		} else {
			return remoteServiceInvoke(serviceName, input);
		}
	}

	/**
	 * 主服务调用
	 * 
	 * @param name
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static final IDataOutput remoteServiceInvoke(String name, IDataInput input) throws Exception {
		ServiceEntity entity = ServiceManager.find(name);
		if (entity == null) {
			throw new NullPointerException("服务名不存在[" + name + "]");
		}

		String group = "";
		String svcname = entity.getName();
		int index = svcname.indexOf(":");
		if (index != -1) {
			group = svcname.substring(0, index);
		} else {
			group = "";
		}
		
		IBaseService service = (IBaseService) entity.getEntityClass().newInstance();
		service.setGroup(group);
		service.setPagination(input.getPagination());
		service.setEntity(entity);
		service.setName(name);
		
		//日志跟踪
		ITracer tracer = AbstractTracer.getTracer(service.getClass());
		if (null != tracer) {
			input.getHead().put("X_PTRADE_ID", tracer.getCurrentProbeId());
		}
		
		Method method = ServiceInvoker.class.getMethod("executeServiceMethod", new Class<?>[] {IBaseService.class, Method.class, IDataInput.class});
		method.setAccessible(true);
		
		IVisit visit = service.createVisit(input.getHead());
		visit.set(Constants.X_TRANS_CODE, name);
		
		// 支持客户端指定服务超时阈值，客户端传过来的值是毫秒，但限制在600秒内
		int clientTimeout = input.getHead().getInt(Constants.X_CLIENT_TIMEOUT, 0);
		
		//　不知道哪里来的默认值6000000，强制改成30秒
		if (clientTimeout == 6000000) {
			clientTimeout = 30;
		}
		if (clientTimeout > 600000) {
			throw new BaseException("SERVICE_TIMEOUT_ERR", null, "客户端设置的超时阈值不合法[" + name + "][" + clientTimeout + "]");
		} else {
			clientTimeout = clientTimeout / 1000;
		}
		
		int timeout = clientTimeout <= 0 ? entity.getTimeout() : clientTimeout;
		return AppInvoker.invoke(visit, service, method, new Object[] {service, entity.getEntityMethod(), input}, timeout);
	}
	

	/**
	 * 本地服务调用
	 * @param name
	 * @param group
	 * @param entity
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static final IDataOutput subServiceinvoke(String name, String group, ServiceEntity entity, IDataInput input) throws Exception {
		if (DSF_LOCALE_SERVICE_ENABLE) {
			Map<String, String> header = new HashMap<String, String>();
			header.put("name", name);
			header.put("group", group);
			
			LocalServiceAction action = new LocalServiceAction();
			IDataOutput output = (IDataOutput) action.execute(name, header, input);
			return output;
		} else {
			return localServiceinvoke(name, group, entity, input);
		}
	}
	
	/**
	 * 子服务调用
	 * 
	 * @param clazzName
	 * @param paramTypes
	 * @param objs
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static final IDataOutput localServiceinvoke(String name, String group, ServiceEntity entity, IDataInput input) throws Exception {
		Class<IBaseService> clazz = (Class<IBaseService>) entity.getEntityClass();
		Method callMethod = entity.getEntityMethod();

		IBaseService svc = clazz.newInstance();
		IDataOutput output = new DataOutput();

		SessionManager manager = SessionManager.getInstance();
		// 获取线程上下文对象
		IVisit visit = manager.getVisit();

		String xTransCode = null == visit ? name : visit.get(Constants.X_TRANS_CODE);
		String subsys = null == visit ? "common" : visit.get("SUBSYS_CODE");

		long start = System.currentTimeMillis();
		
		
		//日志跟踪
		ITracer tracer = AbstractTracer.getTracer(clazz);
		boolean success = true;
		
		try {
			if (log.isDebugEnabled())
				log.debug(">>>子服务执行开始:[name:" + name + "][subsys:" + group + "][class:" + clazz.getName() + "][method:"
						+ callMethod.getName() + "]");
			
			if (null != tracer) {
				tracer.startSubServiceProbe(name);
			}
			
			AppSession.getSession().pushService(svc);

			IData head = input.getHead();
			svc.setName(name);
			svc.setGroup(group);
			svc.setPagination(input.getPagination());
			svc.setEntity(entity);

			if (visit == null) {
				visit = svc.createVisit(head);
			}
			visit.set(Constants.X_TRANS_CODE, head.getString(Constants.X_TRANS_CODE));
			visit.set("SUBSYS_CODE", group);

			// 设置上下文Visit
			manager.setContext(svc, visit);

			// 服务初始化
			svc.initialize(input.getData());
			IMethodIntercept intercept = entity.getMethodIntercept();
			if (log.isDebugEnabled())
				log.debug(">>>子服务方法拦截器:[" + (intercept == null ? null : intercept.getClass().getName()) + "]");
			
			Serializable request = null;
			Class<?> type = callMethod.getParameterTypes()[0];
			if (IData.class.isAssignableFrom(type)) {
				request = input.getData();
			} else if (IDataInput.class.isAssignableFrom(type)) {
				request = input;
			}
			
			if (intercept != null) {
				if (intercept.invokeBefore(name, input.getHead(), input.getData())) {
					// 反射
					Object result = ServiceInvoker.invoke(callMethod, svc, request);
					output = ServiceInvoker.objectToDataOutput(result, svc);
					if (!intercept.invokeAfter(name, head, input.getData(), output.getData())) {
						output.getHead().put(Constants.X_RESULTCODE, svc.getResultCode());
						output.getHead().put(Constants.X_RESULTINFO, svc.getResultInfo());
					}
				} else {
					output.getHead().put(Constants.X_RESULTCODE, svc.getResultCode());
					output.getHead().put(Constants.X_RESULTINFO, svc.getResultInfo());
				}
			} else {
				// 反射
				Object result = ServiceInvoker.invoke(callMethod, svc, request);
				output = ServiceInvoker.objectToDataOutput(result, svc);
			}

		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug(">>>子服务调用异常:[name:" + name + "][error:" + e.getClass() + ":" + e.getMessage() + "]");
			}
			success = false;
			throw e;
		} finally {
			try {
				try {
					svc.destroy(input, output);
				} catch (Exception e) {
					success = false;
					throw e;
				} finally {
					manager.destroy();
				}
			} catch (Exception e) {
				log.error("SUB SERVICE ERROR", e);
				success = false;
				throw e;
			} finally {
				AppSession.getSession().popService();
				
				svc = null;

				visit.set(Constants.X_TRANS_CODE, xTransCode);
				visit.set("SUBSYS_CODE", subsys);
				
				if (null != tracer) {
					tracer.stopServiceProbe(success);
				}
				
				long cost = (System.currentTimeMillis() - start);
				if (log.isDebugEnabled())
					log.debug(">>>子服务调用结束:[" + group + "][" + clazz.getName() + "][" + callMethod.getName() + "][costtime:"
							+ cost + " ms],回到上级服务:" + visit.get(Constants.X_TRANS_CODE));
				
				if (cost > 10000) {
					if (log.isInfoEnabled()) {
						log.info("子服务调用超过10秒" + group + "][" + clazz.getName() + "][" + callMethod.getName() + "]" + xTransCode);
					}
				}
			}
		}

		return output;
	}
	

	/**
	 * 服务调用后IDataOutput处理
	 * 
	 * @param result
	 * @return
	 */
	public static final IDataOutput objectToDataOutput(Object result, IBaseService service) {
		IDataOutput output = null;

		String xResultCode = service.getResultCode();
		String xResultInfo = service.getResultInfo();

		// 处理方法调用的返回对象
		if (null != result) {
			if (result instanceof String) {
				String value = (String) result;
				IDataset ds = new DatasetList();
				ds.add(value);
				output = new DataOutput(new DataMap(), ds);
			} else if (result instanceof Boolean) {
				Boolean value = (Boolean) result;
				IDataset ds = new DatasetList();
				ds.add(value);
				output = new DataOutput(new DataMap(), ds);
			} else if (result instanceof IData) {
				IData value = (IData) result;
				IDataset ds = new DatasetList();
				ds.add(value);
				output = new DataOutput(new DataMap(), ds);
			} else if (result instanceof IDataset) {
				IDataset value = (IDataset) result;
				output = new DataOutput(new DataMap(), value);
			} else if (result instanceof IDataOutput) {
				output = (IDataOutput) result;
			} else {
				output = new DataOutput();
			}
			
			IData head = output.getHead();

			// 分页参数
			long pagesize = 0;
			long count = 0;
			long current = 0;
			Pagination p = service.getPagination();
			if (p != null) {
				pagesize = p.getPageSize();
				count = p.getCount();
				current = p.getCurrent();
			}
			
			IVisit visit = SessionManager.getInstance().getVisit();
			if (null != visit) {
				head.putAll(visit.getAll());
			}
			
			// 填充上下文数据
			head.put(Constants.X_RESULTCODE, xResultCode);
			head.put(Constants.X_RESULTINFO, xResultInfo);
			head.put(Constants.X_RESULTCOUNT, String.valueOf(count));
			head.put(Constants.X_RESULTSIZE, String.valueOf(output.getData().size()));
			// 填充分页数据
			head.put(Constants.X_PAGINCOUNT, String.valueOf(count));
			head.put(Constants.X_PAGINCURRENT, String.valueOf(current));
			head.put(Constants.X_PAGINSIZE, String.valueOf(pagesize));

		} else {
			if (null == output)
				output = new DataOutput();
			
			IData head = output.getHead();
			
			// 填充上下文数据
			head.put(Constants.X_RESULTCODE, xResultCode);
			head.put(Constants.X_RESULTINFO, xResultInfo);
			head.put(Constants.X_RESULTCOUNT, "0");
			head.put(Constants.X_RESULTSIZE, "0");
		}
		
		return output;
	}
	
	/**
	 * 执行服务方法
	 * @param service
	 * @param method
	 * @param dataInput
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput executeServiceMethod(IBaseService service, Method method, IDataInput dataInput) throws Exception {
		IDataOutput output = new DataOutput();
		
		String name = service.getName();
		IData head = dataInput.getHead();
		IData body = dataInput.getData();
		
		Serializable input = null;
		Class<?> type = method.getParameterTypes()[0];
		if (IData.class.isAssignableFrom(type)) {
			input = body;
		} else if (IDataInput.class.isAssignableFrom(type)) {
			input = dataInput;
		}

		String inModeCode = head.getString(Constants.IN_MODE_CODE);
		if (null == inModeCode || inModeCode.length() <= 0) {
			inModeCode = body.getString(Constants.IN_MODE_CODE);
		}
		if (null == inModeCode || inModeCode.length() <= 0) {
			inModeCode = "0";
		}

		long start = System.currentTimeMillis();
		String success = "OK";
		
		//日志跟踪
		ITracer tracer = AbstractTracer.getTracer(service.getClass());
		
		try {
			ServiceEntity entity = service.getEntity();
			
			IVisit visit = service.createVisit(head);
			visit.set(Constants.X_TRANS_CODE, name);
			
			visit.set(Constants.IN_MODE_CODE, inModeCode);
			head.put(Constants.IN_MODE_CODE, inModeCode);
			
			if (null != tracer) {
				
				String traceId = head.getString("X_TRACE_ID");
				String pTraceId = head.getString("X_PTRADE_ID");
				String xBizId = head.getString("X_BIZ_ID");
				String operId = head.getString("TRADE_STAFF_ID");
				
				tracer.startMainServiceProbe(traceId, pTraceId, xBizId, operId, name);
			}
			
			output.getHead().putAll(head);
			
			// 服务初始化
			service.initialize(body);
			ServiceMethodIntercept intercept = (ServiceMethodIntercept) entity.getMethodIntercept();
			if (log.isDebugEnabled()) {
				log.debug(">>>服务调用开始:" + name);
				log.debug(">>>服务调用上下文:" + visit);
				log.debug(">>>服务调用输入:" + head);
				log.debug(">>>服务调用数据:" + body);
				log.debug(">>>服务方法拦截器:" + (intercept == null ? "" : intercept.getClass().getName()));
			}

			if (intercept != null) {
				if (intercept.invokeBefore(name, head, body)) {
					// 反射
					Object result = ServiceInvoker.invoke(method, service, input);
					output = ServiceInvoker.objectToDataOutput(result, service);
					if (!intercept.invokeAfter(name, head, body, output.getData())) {
						output.getHead().put(Constants.X_RESULTCODE, service.getResultCode());
						output.getHead().put(Constants.X_RESULTINFO, service.getResultInfo());
					}
				} else {
					output.getHead().put(Constants.X_RESULTCODE, service.getResultCode());
					output.getHead().put(Constants.X_RESULTINFO, service.getResultInfo());
				}
			} else {
				// 反射
				Object result = ServiceInvoker.invoke(method, service, input);
				output = ServiceInvoker.objectToDataOutput(result, service);
			}
		} catch (Exception e) {
			success = "ERR";
			throw e;
		} finally {
			try {
				service.destroy(dataInput, output);
			} catch (Exception e) {
				throw e;
			} finally {
				ILogger logger = AbstractLogger.getLogger(service.getClass());
				
				if (null != logger) {
					StringBuilder log = new StringBuilder();
					
					log.append(name);
					log.append(":");
					log.append(method.getName());
					log.append(":");
					log.append(inModeCode);
					log.append(":");
					log.append(success);
					
					logger.log(service, log.toString(), start, (System.currentTimeMillis() - start), null);
				}
				
				if (null != tracer) {
					tracer.stopServiceProbe("OK".equals(success));
				}
			}
		}
		
		return output;
	}
	
	/**
	 * 服务方法反射权限验证,被已废弃的ServiceInvokeCallable调用
	 * 
	 * @return
	 */
	public static final Object invoke(Method method, IBaseService service, Object input) throws Exception {
		Object object = null;

		try {
			Class<?> targetClass = service.getClass();
        	if (targetClass.isAnnotationPresent(AnnotationServicePriv.class)) {
        		AnnotationServicePriv servicepriv = targetClass.getAnnotation(AnnotationServicePriv.class);
        		IServicePriv priv = (IServicePriv) servicepriv.servicePriv().newInstance();
        		
        		boolean check = priv.check(service.getName());
        		
        		if (check) {
        			object = method.invoke(service, new Object[] { input });
        		} else {
        			throw new BaseException(BaseException.CODE_NO_SVC_PRIV, null, BaseException.INFO_NO_SVC_PRIV);
        		}
        	} else {
				object = method.invoke(service, new Object[] { input });
			}
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			Throwable t = Utility.getBottomException(e);
			if (t instanceof BaseException) {
				throw (BaseException)t;
			} else {
				throw new BaseException(BaseException.CODE_UNDEFINED, new Exception(BaseException.INFO_UNDEFINED, t));
			}
		}

		return object;
	}

}
