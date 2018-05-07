package com.ailk.service.client;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.Constants;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.Utility;
import com.ailk.service.ServiceManager;
import com.ailk.service.client.hessian.HessianClient;
import com.ailk.service.invoker.ServiceInvoker;
import com.ailk.service.protocol.IService;
import com.ailk.service.protocol.IServiceRule;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.ailk.service.session.SessionManager;
import com.wade.relax.tm.context.XContext;

public class ServiceFactory {

	private transient static final Logger log = Logger.getLogger(ServiceFactory.class);
	private static IProtocalClient client = ProtocalClientFactory.getClient();
	private static String origin = GlobalCfg.getProperty("service.access.origin", null);
	private static String router = GlobalCfg.getProperty("service.router.addr", null);
	private static String trace = System.getProperty("trace.enable", "true");
	private static boolean useRegistry = "registry".equals(router);
	public static final int SOCKET_SOTIMEOUT = Integer.parseInt(GlobalCfg.getProperty("client.socket.sotimeout", "" + HessianClient.SO_TIMEOUT));
	public static final int SOCKET_CONNECTTIMEOUT = Integer.parseInt(GlobalCfg.getProperty("client.socket.connecttimeout", "" + HessianClient.CONNECT_TIMEOUT));
	
	static boolean useRemoteAddr = false;
	
	public static void setUseRemoteAddr(boolean use) {
		useRemoteAddr = use;
	}
	
	/**
	 * 创建客户端服务对象
	 * 
	 * @param name
	 * @param rule
	 * @return
	 */
	public static IService create(String name, IServiceRule rule) {
		IService service = new ClientServiceEntity();
		service.setName(name);
		service.setRule(rule);
		return service;
	}

	/**
	 * 服务调用
	 * 
	 * @param svcname
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String svcname, IDataInput input) throws Exception {
		return call(svcname, input, null, false);
	}

	/**
	 * 服务调用
	 * 
	 * @param svcname
	 * @param input
	 * @param iscatch
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String svcname, IDataInput input, boolean iscatch) throws Exception {
		return call(svcname, input, null, iscatch);
	}

	/**
	 * 服务调用
	 * 
	 * @param svcname
	 * @param input
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String svcname, IDataInput input, Pagination pagination) throws Exception {
		return call(svcname, input, pagination, false);
	}

	/**
	 * 服务调用
	 * 
	 * @param svcname
	 * @param input
	 * @param pagination
	 * @param iscatch
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String svcname, IDataInput input, Pagination pagination, boolean iscatch)
			throws Exception {
		return call(router, svcname, input, pagination, iscatch);
	}

	/**
	 * 服务调用
	 * 
	 * @param url
	 * @param svcname
	 * @param input
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagination)
			throws Exception {
		return call(url, svcname, input, pagination, false);
	}

	/**
	 * 服务调用
	 * 
	 * @param url
	 *            服务地址
	 * @param svcname
	 *            服务名称
	 * @param input
	 *            输入参数
	 * @param pagination
	 *            分页参数
	 * @param iscatch
	 *            是否捕获异常，默认为true
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagin, boolean iscatch)
			throws Exception {
		return call(url, svcname, input, pagin, iscatch, false);

	}
	
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagin, boolean iscatch,
			boolean isremote) throws Exception {
		return call(url, svcname, input, pagin, iscatch, isremote, SOCKET_SOTIMEOUT);
	}
	
	
	/**
	 * 服务调用
	 * @param url	远程调用地址
	 * @param svcname	服务名
	 * @param input	服务输入
	 * @param pagin	分页对象
	 * @param iscatch	是否抛出异常
	 * @param isremote 是否强制远程调用
	 * @param soTimeout	Socket读取数据超时时长
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagin, boolean iscatch,
			boolean isremote, int soTimeout) throws Exception {
		return call(url, svcname, input, pagin, iscatch, isremote, SOCKET_SOTIMEOUT, SOCKET_CONNECTTIMEOUT);
	}

	/**
	 * 服务调用
	 * 1.如果服务在当前JVM上则不跨网络调用
	 * 2.如果指定参数isremote为true则走远程调用
	 * 
	 * @param url	远程调用地址
	 * @param svcname	服务名
	 * @param input	服务输入
	 * @param pagin	分页对象
	 * @param iscatch	是否抛出异常
	 * @param isremote 是否强制远程调用
	 * @param soTimeout	Socket读取数据超时时长（毫秒）
	 * @param connectTimeout	创建网络Socket等待时长（毫秒）
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagin, boolean iscatch,
			boolean isremote, int soTimeout, int connectTimeout) throws Exception {
		IDataOutput output = new DataOutput();

		if (svcname == null || "".equals(svcname))
			throw new Exception("服务名为空");

		if (input == null)
			throw new Exception("输入参数为空");

		// 设置分页参数
		if (pagin != null) {
			input.setPagination(pagin);
		}

		StringBuilder msg = null;
		long start = System.currentTimeMillis();
		
		SessionManager manager = SessionManager.getInstance();
		
		try {
			IData head = input.getHead();
			String inModeCode = head.getString(Constants.IN_MODE_CODE);
			if (null == inModeCode || inModeCode.length() <= 0) {
				head.put(Constants.IN_MODE_CODE, origin);
			}
			head.put(Constants.X_TRANS_CODE, svcname);
			
			//四个公共参数
			head.put("TRADE_STAFF_ID", head.getString("TRADE_STAFF_ID", head.getString("STAFF_ID")));
			head.put("TRADE_CITY_CODE", head.getString("TRADE_CITY_CODE", head.getString("CITY_CODE")));
			head.put("TRADE_DEPART_ID", head.getString("TRADE_DEPART_ID", head.getString("DEPART_ID")));
			head.put("TRADE_EPARCHY_CODE", head.getString("TRADE_EPARCHY_CODE", head.getString("STAFF_EPARCHY_CODE")));
			
			// 日志跟踪
			if ("true".equals(trace)) {
				head.put("X_TRACE_ID", com.wade.trace.TraceContext.getTraceId());
				head.put("X_PTRACE_ID", com.wade.trace.TraceContext.getCurrentProbeId());
			} else {
				head.put("X_TRACE_ID", "");
				head.put("X_PTRACE_ID", "");
			}
			
			if (log.isDebugEnabled()) {
				msg = new StringBuilder();
				msg.append("\n");
				if (useRegistry && !isremote) {
					msg.append("通过注册中心获取地址:").append(useRegistry).append("\n");
				} else {
					msg.append("服务地址:").append(url).append("\n");
				}
				msg.append("服务名称:").append(svcname).append("\n");
				msg.append("请求上下文:").append(head.toString()).append("\n");
				msg.append("服务入参:").append(input.getData().toString()).append("\n");
			}
			
			if (isremote || useRemoteAddr) {
				output = remoteCall(msg, url, svcname, input, soTimeout, connectTimeout);
			} else {
				//1.如果服务在当前JVM, 且归属当前中心, 则不跨网调用
				ServiceEntity entity = ServiceManager.find(svcname);
				
				if (manager.isActive() && entity != null) {
					boolean isLocalService = ServiceManager.isLocalService(entity);
					
					String group = "";
					String name = entity.getName();
					if (name.indexOf(":") != -1) {
						group = name.substring(0, name.indexOf(":"));
					} else {
						group = "";
					}
					
					if (isLocalService) {
						if (log.isDebugEnabled()) {
							msg.append("开始本地调用...");
							log.debug(msg.toString());
						}
						
						output = ServiceInvoker.subServiceinvoke(svcname, group, entity, input);
					} else {
						if (null == XContext.getInstance().getTID()) {
							if (log.isDebugEnabled()) {
								msg.append("当前TID为空，强制本地调用...");
								log.debug(msg.toString());
							}
							output = ServiceInvoker.mainServiceInvoke(svcname, input);
						} else {
							if (log.isDebugEnabled()) {
								msg.append("跨中心远程调用...\n");
								log.debug(msg.toString());
							}
							output = remoteCall(msg, url, svcname, input, soTimeout, connectTimeout);
						}
					}
				} else if (!manager.isActive() && entity != null) {
					boolean isLocalService = ServiceManager.isLocalService(entity);
					
					if (isLocalService) {
						if (log.isDebugEnabled()) {
							msg.append("开始本地调用..." + (entity != null ? "强制切换" : ""));
							log.debug(msg.toString());
						}
						
						output = ServiceInvoker.mainServiceInvoke(svcname, input);
					} else {
						if (log.isDebugEnabled()) {
							msg.append("跨中心远程调用...");
							log.debug(msg.toString());
						}
						
						output = remoteCall(msg, url, svcname, input, soTimeout, connectTimeout);
					}
				} else {
					output = remoteCall(msg, url, svcname, input, soTimeout, connectTimeout);
				}
			}

			String xResultCode = output.getHead().getString(Constants.X_RESULTCODE);
			String xResultInfo = output.getHead().getString(Constants.X_RESULTINFO);
			if (null == xResultCode)
				xResultCode = "-1";
			
			if (!"0".equals(xResultCode)) {
				throw new BaseException(xResultCode, null, xResultInfo);
			}
		} catch (Exception e) {
			Utility.print(e);

			if (!iscatch) {
				throw e;
			}
			
		} finally {
			if (log.isDebugEnabled()) {
				msg = new StringBuilder();
				msg.append("\n");
				if (output != null) {
					msg.append("响应上下文:");
					
					IData head = output.getHead();
					Iterator<?> iter = head.keySet().iterator();
					while (iter.hasNext()) {
						String s = (String) iter.next();
						msg.append(s);
						msg.append("=");
						msg.append(head.getString(s));
						msg.append(", ");
					}
					
					msg.append("\n");
				} else {
					msg.append("调用失败，请查看相关日志").append("\n");
				}
				msg.append("服务耗时:").append((System.currentTimeMillis() - start)).append("ms\n");

				if (!SessionManager.getInstance().isActive())
					msg.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<").append("\n");

				log.debug(msg.toString());
			}
		}

		return output;
	}
	
	private static final IDataOutput remoteCall(StringBuilder msg, String url, String svcname, IDataInput input, int soTimeout, int connectTimeout) throws Exception {
		if (log.isDebugEnabled()) {
			msg.append("开始远程调用...");
			log.debug(msg.toString());
		}
		
		if (null == url || url.length() == 0) {
			IDataOutput output = new DataOutput();
			
			output.getHead().put(Constants.X_RESULTCODE, "-1");
			output.getHead().put(Constants.X_RESULTINFO, "服务远程调用地址不能为空");
			
			return output;
		}
		
		return client.request(url, svcname, input, soTimeout, connectTimeout);
	}
	
	static {
		String serverName = System.getProperty("wade.server.name", "");
		if (serverName.length() == 0) {
			useRemoteAddr = true;
		}
		if (!serverName.startsWith("app")) {
			useRemoteAddr = true;
		}
	}

}
