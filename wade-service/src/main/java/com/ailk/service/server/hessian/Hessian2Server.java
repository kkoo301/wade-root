package com.ailk.service.server.hessian;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.ailk.common.BaseException;
import com.ailk.common.Constants;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.config.SystemCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataContext;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.trace.ITracer;
import com.ailk.common.util.ClazzUtil;
import com.ailk.common.util.Utility;
import com.ailk.service.ServiceManager;
import com.ailk.service.invoker.ServiceInvoker;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.ailk.service.serializer.HessianToIOData;
import com.ailk.service.serializer.JavaToIOData;
import com.ailk.service.serializer.Json2ToIOData;
import com.ailk.service.serializer.JsonToIOData;
import com.ailk.service.serializer.StdJsonToIOData;
import com.ailk.service.serializer.Wade3ToIOData;
import com.ailk.service.server.bcc.ServiceBCCIntercept;
import com.ailk.service.server.sec.IServiceSecurity;

public class Hessian2Server implements Filter {
	private static transient final Logger log = Logger.getLogger(Hessian2Server.class);
	
	private static IServiceSecurity sec = null;
	private static ServiceBCCIntercept bcc = null;
	private static ITracer tracer = null;
	
	private static final String TYPE_PLAIN = "text/plain";
	private static final String TYPE_HESSIAN = "binary/hessian-stream";
	private static final String TYPE_JSON = "binary/json-stream";
	private static final String TYPE_JSON2 = "binary/json2-stream";
	private static final String TYPE_JAVA = "binary/java-stream";
	private static final String TYPE_WADE3 = "application/x-www-form-urlencoded";
	
	private static final String TYPE_STANDARD_JSON = "application/json";
	
	private static final String MDC_KEY = "STAFF_ID";

	private static String secClazzName = GlobalCfg.getProperty("service.sec.clazz");
	private static String bccClazzName = GlobalCfg.getProperty("service.bcc.clazz");
	private static String tracerClazzName = GlobalCfg.getProperty("trace.app.clazz");
	private static String registerClazzName = GlobalCfg.getProperty("service.register.clazz","com.ailk.biz.service.BizServiceRegister");
	private static String centerResolveClazzName = GlobalCfg.getProperty("service.centerresolve.clazz","com.ailk.service.protocol.config.impl.DefaultCenterResolver");
	
	public static boolean APP_SERVER_PREPARED = false;
	
	private long doSucCount = 0;
	private long doErrCount = 0;
	
	/**
	 * init
	 */
	public void init(FilterConfig config) throws ServletException {
		try {
			
			log.info("开始注入服务跟踪类...");
			if (tracerClazzName != null && tracerClazzName.length() > 0) {
				tracer = (ITracer) ClazzUtil.load(tracerClazzName, null);
			}
			log.info("注入服务跟踪类" + tracer);
			
			log.info("开始注入服务安全验证类...");
			if (secClazzName != null && secClazzName.length() > 0) {
				sec = (IServiceSecurity) ClazzUtil.load(secClazzName, null);
			}
			log.info("注入服务安全验证类" + sec);

			log.info("开始注入服务BCC类...");
			if (bccClazzName != null && bccClazzName.length() > 0) {
				bcc = (ServiceBCCIntercept) ClazzUtil.load(bccClazzName, null);
			}
			log.info("注入服务BCC类" + bcc);

			// 注册服务
			log.info("开始注册服务..." + registerClazzName);
			ServiceManager.createRegister(registerClazzName);
			ServiceManager.createResolver(centerResolveClazzName);
			ServiceManager.register();
			ServiceManager.resolve();
			log.info("服务注册完成[" + ServiceManager.isLoadFinish() + "]");
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			System.setProperty("AppVisit", "true");
		}
	}

	/**
	 * do filter
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest hreq = (HttpServletRequest) req;
			HttpServletResponse hres = (HttpServletResponse) res;
			
			String uri = hreq.getRequestURI();
			if (uri.indexOf("/count") == 0) {
				doCount(hreq, hres);
				return;
			}
			
			if (!APP_SERVER_PREPARED && SystemCfg.isDataPreloadOn) {
				hres.setContentType("text/plain");
				hres.setCharacterEncoding(GlobalCfg.getCharset());
				hres.setHeader("Connection", "close");
				
				String info = "app not prepared, please warm first";
				hres.setContentLength(info.getBytes().length);
				hres.getOutputStream().println(info);
				
				return ;
			}
			
			MDC.put(MDC_KEY, "STAFF_ID");
			
			String contentType = hreq.getContentType();
			if (null == contentType)
				contentType = "";
			
			if (log.isDebugEnabled()) {
				log.debug(">>>服务接入类型" + contentType);
			}

			String clientIp = hreq.getHeader("Client-IP");
			String clientMac = hreq.getHeader("Client-MAC");
			
			// 如果是JSON客户端调用
			if (contentType.startsWith(TYPE_JSON)) {
				doJson(hreq, hres, clientIp, clientMac);
				return ;
			}
			// 如果是JSON客户端调用
			if (contentType.startsWith(TYPE_JSON2)) {
				doJson2(hreq, hres, clientIp, clientMac);
				return ;
			}
			// 如果是Hessian客户端调用
			else if (contentType.startsWith(TYPE_HESSIAN)) {
				doHessian(hreq, hres, clientIp, clientMac);
				return ;
			}
			// 如果是Java客户端调用
			else if (contentType.startsWith(TYPE_JAVA)) {
				doJava(hreq, hres, clientIp, clientMac);
				return;
			}
			// 如果是WADE3.0客户端调用
			else if (contentType.startsWith(TYPE_WADE3)) {
				doWade3(hreq, hres, clientIp, clientMac);
				return;
			}
			// 如果是用来获取认证码的请求
			else if (TYPE_PLAIN.equals(contentType)) {
				doHead(hreq, hres);
				return;
			}
			// 如果是标准的JSON请求
			else if (contentType.startsWith(TYPE_STANDARD_JSON)) {
				doStandardJson(hreq, hres, clientIp, clientMac);
				return;
			}
			// 什么都不是，则查询已注册的服务列表，跳转到wsdl
			else {
				if (!APP_SERVER_PREPARED && SystemCfg.isDataPreloadOn) {
					hres.setContentType("text/plain");
					hres.setCharacterEncoding(GlobalCfg.getCharset());
					hres.setHeader("Connection", "close");
					
					String info = "app not prepared, please warm first";
					hres.setContentLength(info.getBytes().length);
					hres.getOutputStream().println(info);
					
					return ;
				} else {
					RequestDispatcher dispatcher = req.getRequestDispatcher("/wsdl");
					dispatcher.forward(req, res);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (ServletException e) {
			log.error(e.getMessage(), e);
		}
	}
	

	/**
	 * do security
	 * 
	 * @param input
	 * @return
	 */
	private boolean doSecurity(IDataInput input) {
		if (sec == null)
			return true;

		IData head = input.getHead();

		String clientKey = head.getString(Constants.X_CLIENT_KEY);
		return sec.isValidKey(clientKey, "");
	}

	/**
	 * do service
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private IDataOutput doService(IDataInput input) {
		String wadeServerName = System.getProperty("wade.server.name", "");
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		
		//创建x_node_name的关键字段
		StringBuilder xNodeName = new StringBuilder();
		xNodeName.append(wadeServerName);
		xNodeName.append(":");
		xNodeName.append(uuid);
		xNodeName.append(":");
		xNodeName.append(System.currentTimeMillis());
		String xNodeNameInfo = xNodeName.toString();
		
		IData head = input.getHead();

		String xForwardedFor = head.getString("X-Forwarded-For", "");
		
		String serviceName = head.getString(Constants.X_TRANS_CODE);
		String inModeCode = head.getString(Constants.IN_MODE_CODE);
		String channelTypeId = head.getString(Constants.CHANNEL_TYPE_ID);
		String transSerial = head.getString(Constants.X_TRANSSERIAL);
		
		//四个公共参数
		String tradeStaffId = head.getString("TRADE_STAFF_ID");
		String tradeCityCode = head.getString("TRADE_CITY_CODE");
		String tradeDepartId = head.getString("TRADE_DEPART_ID");
		String tradeEparcyCode = head.getString("TRADE_EPARCHY_CODE");
		if (null == tradeStaffId || tradeStaffId.length() <= 0) {
			tradeStaffId = head.getString("STAFF_ID");
		}
		if (null == tradeCityCode || tradeCityCode.length() <= 0) {
			tradeCityCode = head.getString("CITY_CODE");
		}
		if (null == tradeDepartId || tradeDepartId.length() <= 0) {
			tradeDepartId = head.getString("DEPART_ID");
		}
		if (null == tradeEparcyCode || tradeEparcyCode.length() <= 0) {
			tradeEparcyCode = head.getString("STAFF_EPARCHY_CODE");
		}
		
		head.put("STAFF_ID", tradeStaffId);
		head.put("CITY_CODE", tradeCityCode);
		head.put("DEPART_ID", tradeDepartId);
		head.put("STAFF_EPARCHY_CODE", tradeEparcyCode);
		head.put(Constants.CHANNEL_TYPE_ID, channelTypeId);
		
		input.getData().put("TRADE_EPARCHY_CODE", tradeEparcyCode);
		
		IDataOutput output = null;
		
		//添加日志跟踪
		if (null != tracer) {
			String traceId = head.getString(Constants.X_TRACE_ID);
			String bizId = head.getString(Constants.X_BIZ_ID);
			String pTraceId = head.getString("X_PTRACE_ID");
			tracer.startAppProbe(traceId, pTraceId, bizId, tradeStaffId, serviceName, inModeCode, input.getData());
		}
		
		//判断服务名是否合法
		ServiceEntity entity = ServiceManager.find(serviceName);
		if (null == entity) {
			IData outHead = new DataMap();
			outHead.put(Constants.IN_MODE_CODE, inModeCode);
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_NO_SERVICE);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.INFO_NO_SERVICE + "[" + serviceName + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			outHead.put(Constants.X_RSPTYPE, "2");
			outHead.put(Constants.X_RSPDESC, BaseException.INFO_NO_SERVICE + "[" + serviceName + "]");
			outHead.put(Constants.X_RSPCODE, BaseException.CODE_NO_SERVICE);
			outHead.put(Constants.X_NODENAME, xNodeNameInfo);
			outHead.put(Constants.X_RECORDNUM, "0");
			output = new DataOutput(outHead, new DatasetList());
			
			//添加日志跟踪
			if (null != tracer) {
				tracer.stopAppProbe(false);
			}
			
			return output;
		}
		
		if (entity.getStatus() == ServiceEntity.STATUS_DISABLED) {
			IData outHead = new DataMap();
			outHead.put(Constants.IN_MODE_CODE, inModeCode);
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_OFFLINE);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.INFO_SVC_OFFLINE + "[" + serviceName + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			outHead.put(Constants.X_RSPTYPE, "2");
			outHead.put(Constants.X_RSPDESC, BaseException.INFO_SVC_OFFLINE + "[" + serviceName + "]");
			outHead.put(Constants.X_RSPCODE, BaseException.INFO_SVC_OFFLINE + "[" + serviceName + "]");
			outHead.put(Constants.X_NODENAME, xNodeNameInfo);
			outHead.put(Constants.X_RECORDNUM, "0");
			output = new DataOutput(outHead, new DatasetList());
			return output;
		}
		
		boolean compatibleWade3 = false;
		boolean success = false;
		try {
			if (bcc != null) {
				if (bcc.invokeBefore(serviceName, inModeCode, transSerial, xForwardedFor)) {
					output = mainServiceInvoke(serviceName, input);
					bcc.invokeAfter(serviceName, inModeCode, transSerial, xForwardedFor);
				} else {
					String xResultInfo = BaseException.INFO_SVC_BCC + "[" + serviceName + "]";
					IData outHead = new DataMap();
					outHead.put(Constants.IN_MODE_CODE, inModeCode);
					outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_BCC);
					outHead.put(Constants.X_RESULTCOUNT, "0");
					outHead.put(Constants.X_RESULTINFO, xResultInfo);
					outHead.put(Constants.X_RESULTSIZE, "0");
					outHead.put(Constants.X_RSPTYPE, "2");
					outHead.put(Constants.X_RSPDESC, xResultInfo);
					outHead.put(Constants.X_RSPCODE, BaseException.CODE_SVC_BCC);
					outHead.put(Constants.X_NODENAME, xNodeNameInfo);
					outHead.put(Constants.X_RECORDNUM, "0");
					output = new DataOutput(outHead, new DatasetList());
				}
			} else {
				output = mainServiceInvoke(serviceName, input);
			}
			
			IData outHead = output.getHead();
			outHead.put(Constants.X_RECORDNUM, String.valueOf(output.getData().size()));
			
			doSucCount ++;
			success = true;
		} catch (BaseException e) {
			IData data = e.getData();
			if (null != data) {
				
				compatibleWade3 = true;
				
				IDataset err = new DatasetList();
				err.add(data);
				output = new DataOutput(data, err);
				
				doErrCount ++;
				Utility.print(e);
			} else {
				output = doError(tradeStaffId, serviceName, e, wadeServerName, inModeCode);
			}
		} catch (Exception e) {
			output = doError(tradeStaffId, serviceName, e, wadeServerName, inModeCode);
		}
		
		IData outHead = output.getHead();
		if (!compatibleWade3) {
			String xResultCode = outHead.getString(Constants.X_RESULTCODE);
			String xResultInfo = outHead.getString(Constants.X_RESULTINFO);
			
			outHead.put(Constants.X_RSPTYPE, "0".equals(xResultCode) ? "0" : "2");
			
			/*if ("6".equals(inModeCode) || "N".equals(inModeCode) || "X".equals(inModeCode)) {
				outHead.put(Constants.X_RESULTCODE, "00");
				outHead.put(Constants.X_RSPCODE, "0000");
			}*/
			
			outHead.put(Constants.X_RSPDESC, xResultInfo);
		}
		outHead.put(Constants.X_NODENAME, xNodeNameInfo);
		
		//添加日志跟踪
		if (null != tracer) {
			tracer.stopAppProbe(success);
		}
		
		return output;
	}
	

	/**
	 * write data
	 * 
	 * @param hreq
	 * @param hres
	 * @param output
	 * @throws IOException
	 */
	private void writeData(HttpServletRequest hreq, HttpServletResponse hres, byte[] bytes) throws IOException {
		BufferedOutputStream bos = null;
		try {
			hres.setContentType("binary/octet-stream");
			hres.setContentLength(bytes.length);
			hres.setCharacterEncoding(GlobalCfg.getCharset());
			hres.setHeader("Connection", "close");

			bos = new BufferedOutputStream(hres.getOutputStream());
			bos.write(bytes);
			bos.flush();
			bos.close();
			
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != bos) {
				bos.close();
			}
		}
	}
	
	
	/**
	 * writeWade3Data
	 * @param hreq
	 * @param hres
	 * @param bytes
	 * @throws IOException
	 */
	private void writeWade3Data(HttpServletRequest hreq, HttpServletResponse hres, byte[] bytes) throws IOException {
		BufferedOutputStream bos = null;
		try {
			hres.setContentType("binary/octet-stream");
			hres.setContentLength(bytes.length);
			hres.setCharacterEncoding("GBK");
			hres.setHeader("Connection", "close");

			bos = new BufferedOutputStream(hres.getOutputStream());
			bos.write(bytes);
			bos.flush();
			bos.close();
			
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != bos) {
				bos.close();
			}
		}
	}
	
	/**
	 * 标准的JSON
	 * @param hreq
	 * @param hres
	 * @param clientIp
	 * @param clientMac
	 * @throws IOException
	 */
	public void doStandardJson(HttpServletRequest hreq, HttpServletResponse hres, String clientIp, String clientMac) throws IOException {
		StdJsonToIOData jsonio = new StdJsonToIOData();
		IDataInput input = jsonio.read(hreq.getInputStream());
		IDataOutput output = new DataOutput();
		
		if (!APP_SERVER_PREPARED && SystemCfg.isDataPreloadOn) {
			output.getHead().put(Constants.X_RESULTCODE, BaseException.CODE_SVC_NO_INIT);
			output.getHead().put(Constants.X_RESULTINFO, BaseException.INFO_SVC_NO_INIT);
			writeData(hreq, hres, jsonio.write(output));
			return ;
		}
		
		String serviceName = getServiceName(hreq.getRequestURI());
		if (null == serviceName || serviceName.trim().length() == 0) {
			output.getHead().put(Constants.X_RESULTCODE, "-1");
			output.getHead().put(Constants.X_RESULTINFO, "服务名不存在");
			writeData(hreq, hres, jsonio.write(output));
			return ;
		}
		
		String xForwardedFor = hreq.getHeader("X-Forwarded-For");
		try {
			IData head = input.getHead();

			serviceName = serviceName.trim();
			
			head.put(Constants.X_TRANS_CODE, serviceName);
			String inModeCode = head.getString(Constants.IN_MODE_CODE);
			
			if (null == clientIp || clientIp.length() <= 0) {
				clientIp = head.getString(Constants.X_CLIENT_IP, hreq.getRemoteAddr());
			}
			if (null == clientMac || clientMac.length() <= 0) {
				clientMac = head.getString(Constants.X_CLIENT_MAC);
			}
			
			MDC.put(MDC_KEY, head.getString("STAFF_ID", ""));
			
			// String transSerial = head.getString(Constants.X_TRANSSERIAL);

			if (doSecurity(input)) {
				input.getHead().put("X-Forwarded-For", xForwardedFor);
				output = doService(input);
				writeData(hreq, hres, jsonio.write(output));
				return;
			} else {
				IData outHead = new DataMap();
				outHead.put(Constants.IN_MODE_CODE, inModeCode);
				outHead.put(Constants.X_RESULTCODE, BaseException.CODE_NO_SVC_PRIV);
				outHead.put(Constants.X_RESULTCOUNT, "0");
				outHead.put(Constants.X_RESULTINFO, BaseException.INFO_NO_SVC_PRIV + "[" + serviceName + "]");
				outHead.put(Constants.X_RESULTSIZE, "0");
				output = new DataOutput(outHead, new DatasetList());
				writeData(hreq, hres, jsonio.write(output));
				return;
			}
		} catch (EOFException e) {
			log.error(e.getMessage(), e);
			
			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_TESTATTACK);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.CODE_SVC_TESTATTACK + "[" + new Date(System.currentTimeMillis()).toString() + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(hreq, hres, jsonio.write(output));
			return;
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_INVOKEERROR);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.INFO_SVC_INVOKEERROR + "[" + serviceName + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(hreq, hres, jsonio.write(output));
			return;
		} finally {
			MDC.remove(MDC_KEY);
		}
	}
	
	/**
	 * do json
	 * @param hreq
	 * @param hres
	 * @param serviceName
	 * @param clientIp
	 * @param clientMac
	 * @throws IOException
	 */
	public void doJson(HttpServletRequest hreq, HttpServletResponse hres, String clientIp, String clientMac) throws IOException {
		JsonToIOData jsonio = new JsonToIOData();
		IDataInput input = jsonio.read(hreq.getInputStream());
		IDataOutput output = new DataOutput();
		
		if (!APP_SERVER_PREPARED && SystemCfg.isDataPreloadOn) {
			output.getHead().put(Constants.X_RESULTCODE, BaseException.CODE_SVC_NO_INIT);
			output.getHead().put(Constants.X_RESULTINFO, BaseException.INFO_SVC_NO_INIT);
			output.getHead().put(Constants.X_RSPTYPE, "2");
			output.getHead().put(Constants.X_RSPDESC, BaseException.INFO_SVC_NO_INIT);
			output.getHead().put(Constants.X_RSPCODE, BaseException.CODE_SVC_NO_INIT);
			writeData(hreq, hres, jsonio.write(output));
			return ;
		}
		
		String serviceName = null;
		String xForwardedFor = hreq.getHeader("X-Forwarded-For");
		try {
			IData head = input.getHead();

			serviceName = head.getString(Constants.X_TRANS_CODE);
			String inModeCode = head.getString(Constants.IN_MODE_CODE);
			
			if (null == clientIp || clientIp.length() <= 0) {
				clientIp = head.getString(Constants.X_CLIENT_IP, hreq.getRemoteAddr());
			}
			if (null == clientMac || clientMac.length() <= 0) {
				clientMac = head.getString(Constants.X_CLIENT_MAC);
			}
			
			MDC.put(MDC_KEY, head.getString("STAFF_ID", ""));
			
			// String transSerial = head.getString(Constants.X_TRANSSERIAL);

			if (doSecurity(input)) {
				input.getHead().put("X-Forwarded-For", xForwardedFor);
				output = doService(input);
				writeData(hreq, hres, jsonio.write(output));
				return;
			} else {
				IData outHead = new DataMap();
				outHead.put(Constants.IN_MODE_CODE, inModeCode);
				outHead.put(Constants.X_RESULTCODE, BaseException.CODE_NO_SVC_PRIV);
				outHead.put(Constants.X_RESULTCOUNT, "0");
				outHead.put(Constants.X_RESULTINFO, BaseException.INFO_NO_SVC_PRIV + "[" + serviceName + "]");
				outHead.put(Constants.X_RESULTSIZE, "0");
				output = new DataOutput(outHead, new DatasetList());
				writeData(hreq, hres, jsonio.write(output));
				return;
			}
		} catch (EOFException e) {
			log.error(e.getMessage(), e);
			
			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_TESTATTACK);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.CODE_SVC_TESTATTACK + "[" + new Date(System.currentTimeMillis()).toString() + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(hreq, hres, jsonio.write(output));
			return;
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_INVOKEERROR);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.INFO_SVC_INVOKEERROR + "[" + serviceName + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(hreq, hres, jsonio.write(output));
			return;
		} finally {
			MDC.remove(MDC_KEY);
		}
	}
	
	
	/**
	 * 带Head,Data结构的JSON对象
	 * @param hreq
	 * @param hres
	 * @param serviceName
	 * @param clientIp
	 * @param clientMac
	 * @throws IOException
	 */
	public void doJson2(HttpServletRequest hreq, HttpServletResponse hres, String clientIp, String clientMac) throws IOException {
		Json2ToIOData jsonio = new Json2ToIOData();
		
		String charset = hreq.getCharacterEncoding();
		
		if (log.isDebugEnabled()) {
			log.debug(">>>JSON2请求字符集:" + charset);
		}
		if (null != charset && charset.length() > 0)
			jsonio.setCharset(charset);
		
		IDataInput input = jsonio.read(hreq.getInputStream());
		IDataOutput output = new DataOutput();
		
		if (!APP_SERVER_PREPARED && SystemCfg.isDataPreloadOn) {
			output.getHead().put(Constants.X_RESULTCODE, BaseException.CODE_SVC_NO_INIT);
			output.getHead().put(Constants.X_RESULTINFO, BaseException.INFO_SVC_NO_INIT);
			output.getHead().put(Constants.X_RSPTYPE, "2");
			output.getHead().put(Constants.X_RSPDESC, BaseException.INFO_SVC_NO_INIT);
			output.getHead().put(Constants.X_RSPCODE, BaseException.CODE_SVC_NO_INIT);
			writeData(hreq, hres, jsonio.write(output));
			return ;
		}
		
		String serviceName = null;
		String xForwardedFor = hreq.getHeader("X-Forwarded-For");
		try {
			IData head = input.getHead();

			serviceName = head.getString(Constants.X_TRANS_CODE);
			String inModeCode = head.getString(Constants.IN_MODE_CODE);
			
			if (null == clientIp || clientIp.length() <= 0) {
				clientIp = head.getString(Constants.X_CLIENT_IP, hreq.getRemoteAddr());
			}
			if (null == clientMac || clientMac.length() <= 0) {
				clientMac = head.getString(Constants.X_CLIENT_MAC);
			}
			
			MDC.put(MDC_KEY, head.getString("STAFF_ID", ""));
			
			// String transSerial = head.getString(Constants.X_TRANSSERIAL);

			if (doSecurity(input)) {
				input.getHead().put("X-Forwarded-For", xForwardedFor);
				output = doService(input);
				writeData(hreq, hres, jsonio.write(output));
				return;
			} else {
				IData outHead = new DataMap();
				outHead.put(Constants.IN_MODE_CODE, inModeCode);
				outHead.put(Constants.X_RESULTCODE, BaseException.CODE_NO_SVC_PRIV);
				outHead.put(Constants.X_RESULTCOUNT, "0");
				outHead.put(Constants.X_RESULTINFO, BaseException.INFO_NO_SVC_PRIV + "[" + serviceName + "]");
				outHead.put(Constants.X_RESULTSIZE, "0");
				output = new DataOutput(outHead, new DatasetList());
				writeData(hreq, hres, jsonio.write(output));
				return;
			}
		} catch (EOFException e) {
			log.error(e.getMessage(), e);
			
			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_TESTATTACK);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.CODE_SVC_TESTATTACK + "[" + new Date(System.currentTimeMillis()).toString() + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(hreq, hres, jsonio.write(output));
			return;
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_INVOKEERROR);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.INFO_SVC_INVOKEERROR + "[" + serviceName + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(hreq, hres, jsonio.write(output));
			return;
		} finally {
			MDC.remove(MDC_KEY);
		}
	}
	
	/**
	 * do hessian
	 * @param hreq
	 * @param hres
	 * @param serviceName
	 * @param clientIp
	 * @param clientMac
	 * @throws IOException
	 */
	public void doHessian(HttpServletRequest hreq, HttpServletResponse hres, String clientIp, String clientMac) throws IOException {
		HessianToIOData hessianio = new HessianToIOData();
		IDataOutput output = new DataOutput();
		
		if (!APP_SERVER_PREPARED && SystemCfg.isDataPreloadOn) {
			output.getHead().put(Constants.X_RESULTCODE, BaseException.CODE_SVC_NO_INIT);
			output.getHead().put(Constants.X_RESULTINFO, BaseException.INFO_SVC_NO_INIT);
			output.getHead().put(Constants.X_RSPTYPE, "2");
			output.getHead().put(Constants.X_RSPDESC, BaseException.INFO_SVC_NO_INIT);
			output.getHead().put(Constants.X_RSPCODE, BaseException.CODE_SVC_NO_INIT);
			writeData(hreq, hres, hessianio.write(output));
			return ;
		}
		
		String serviceName = null;
		String xForwardedFor = hreq.getHeader("X-Forwarded-For");

		try {
			IDataInput input = hessianio.read(hreq.getInputStream());
			IData head = input.getHead();

			serviceName = head.getString(Constants.X_TRANS_CODE);
			String inModeCode = head.getString(Constants.IN_MODE_CODE);
			
			if (null == clientIp || clientIp.length() <= 0) {
				clientIp = head.getString(Constants.X_CLIENT_IP, hreq.getRemoteAddr());
			}
			if (null == clientMac || clientMac.length() <= 0) {
				clientMac = head.getString(Constants.X_CLIENT_MAC);
			}
			
			MDC.put(MDC_KEY, head.getString("STAFF_ID", ""));
			
			// String transSerial = head.getString(Constants.X_TRANSSERIAL);

			if (doSecurity(input)) {
				input.getHead().put("X-Forwarded-For", xForwardedFor);
				output = doService(input);
				writeData(hreq, hres, hessianio.write(output));
				return;
			} else {
				IData outHead = new DataMap();
				outHead.put(Constants.IN_MODE_CODE, inModeCode);
				outHead.put(Constants.X_RESULTCODE, BaseException.CODE_NO_SVC_PRIV);
				outHead.put(Constants.X_RESULTCOUNT, "0");
				outHead.put(Constants.X_RESULTINFO, BaseException.INFO_NO_SVC_PRIV + "[" + serviceName + "]");
				outHead.put(Constants.X_RESULTSIZE, "0");
				output = new DataOutput(outHead, new DatasetList());
				writeData(hreq, hres, hessianio.write(output));
				return;
			}
		} catch (EOFException e) {
			log.error(e.getMessage(), e);
			
			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_TESTATTACK);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.CODE_SVC_TESTATTACK + "[" + new Date(System.currentTimeMillis()).toString() + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(hreq, hres, hessianio.write(output));
			return;
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_INVOKEERROR);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.INFO_SVC_INVOKEERROR + "[" + serviceName + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(hreq, hres, hessianio.write(output));
			return;
		} finally {
			MDC.remove(MDC_KEY);
		}
	}
	
	
	/**
	 * do java
	 * @param hreq
	 * @param hres
	 * @param serviceName
	 * @param clientIp
	 * @param clientMac
	 * @throws IOException
	 */
	public void doJava(HttpServletRequest hreq, HttpServletResponse hres, String clientIp, String clientMac) throws IOException {
		JavaToIOData javaio = new JavaToIOData();
		IDataOutput output = new DataOutput();
		
		if (!APP_SERVER_PREPARED && SystemCfg.isDataPreloadOn) {
			output.getHead().put(Constants.X_RESULTCODE, BaseException.CODE_SVC_NO_INIT);
			output.getHead().put(Constants.X_RESULTINFO, BaseException.INFO_SVC_NO_INIT);
			output.getHead().put(Constants.X_RSPTYPE, "2");
			output.getHead().put(Constants.X_RSPDESC, BaseException.INFO_SVC_NO_INIT);
			output.getHead().put(Constants.X_RSPCODE, BaseException.CODE_SVC_NO_INIT);
			writeData(hreq, hres, javaio.write(output));
			return ;
		}
		
		String serviceName = null;
		String xForwardedFor = hreq.getHeader("X-Forwarded-For");

		try {
			IDataInput input = javaio.read(hreq.getInputStream());
			IData head = input.getHead();

			serviceName = head.getString(Constants.X_TRANS_CODE);
			String inModeCode = head.getString(Constants.IN_MODE_CODE);
			
			if (null == clientIp || clientIp.length() <= 0) {
				clientIp = head.getString(Constants.X_CLIENT_IP, hreq.getRemoteAddr());
			}
			if (null == clientMac || clientMac.length() <= 0) {
				clientMac = head.getString(Constants.X_CLIENT_MAC);
			}
			
			MDC.put(MDC_KEY, head.getString("STAFF_ID", ""));
			
			// String transSerial = head.getString(Constants.X_TRANSSERIAL);

			if (doSecurity(input)) {
				input.getHead().put("X-Forwarded-For", xForwardedFor);
				output = doService(input);
				writeData(hreq, hres, javaio.write(output));
				return;
			} else {
				IData outHead = new DataMap();
				outHead.put(Constants.IN_MODE_CODE, inModeCode);
				outHead.put(Constants.X_RESULTCODE, BaseException.CODE_NO_SVC_PRIV);
				outHead.put(Constants.X_RESULTCOUNT, "0");
				outHead.put(Constants.X_RESULTINFO, BaseException.INFO_NO_SVC_PRIV + "[" + serviceName + "]");
				outHead.put(Constants.X_RESULTSIZE, "0");
				output = new DataOutput(outHead, new DatasetList());
				writeData(hreq, hres, javaio.write(output));
				return;
			}
		} catch (EOFException e) {
			log.error(e.getMessage(), e);
			
			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_TESTATTACK);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.CODE_SVC_TESTATTACK + "[" + new Date(System.currentTimeMillis()).toString() + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(hreq, hres, javaio.write(output));
			return;
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_INVOKEERROR);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, BaseException.INFO_SVC_INVOKEERROR + "[" + serviceName + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(hreq, hres, javaio.write(output));
			return;
		} finally {
			MDC.remove(MDC_KEY);
		}
	}
	
	public void doWade3(HttpServletRequest hreq, HttpServletResponse hres, String clientIp, String clientMac) throws IOException {
		Wade3ToIOData wade3io = new Wade3ToIOData();
		
		String charset = hreq.getCharacterEncoding();
		
		if (log.isDebugEnabled()) {
			log.debug(">>>WADE3请求字符集:" + charset);
		}
		if (null != charset && charset.length() > 0)
			wade3io.setCharset(charset);
		
		IDataOutput output = new DataOutput();
		
		if (!APP_SERVER_PREPARED && SystemCfg.isDataPreloadOn) {
			output.getHead().put(Constants.X_RESULTCODE, BaseException.CODE_SVC_NO_INIT);
			output.getHead().put(Constants.X_RESULTINFO, BaseException.INFO_SVC_NO_INIT);
			output.getHead().put(Constants.X_RSPTYPE, "2");
			output.getHead().put(Constants.X_RSPDESC, BaseException.INFO_SVC_NO_INIT);
			output.getHead().put(Constants.X_RSPCODE, BaseException.CODE_SVC_NO_INIT);
			writeWade3Data(hreq, hres, wade3io.write(output));
			return ;
		}
		
		String serviceName = null;
		String xForwardedFor = hreq.getHeader("X-Forwarded-For");
		try {
			IDataInput input = wade3io.read(hreq.getInputStream());
			IData head = input.getHead();

			serviceName = head.getString(Constants.X_TRANS_CODE);
			String inModeCode = head.getString(Constants.IN_MODE_CODE);
			String ibossMode = head.getString("X_IBOSSMODE", "0");
			String xTransMode = head.getString("X_TRANSMODE", "0");
			
			if (log.isDebugEnabled()) {
				log.debug(">>>IBOSS多行请求模式:" + "1".equals(ibossMode));
			}
			
			MDC.put(MDC_KEY, head.getString("STAFF_ID", ""));
			
			if (null == clientIp || clientIp.length() <= 0) {
				clientIp = head.getString(Constants.X_CLIENT_IP, hreq.getRemoteAddr());
			}
			if (null == clientMac || clientMac.length() <= 0) {
				clientMac = head.getString(Constants.X_CLIENT_MAC);
			}
			
			// String transSerial = head.getString(Constants.X_TRANSSERIAL);

			if (doSecurity(input)) {
				input.getHead().put("X-Forwarded-For", xForwardedFor);
				output = doService(input);
				
				IData outHead = output.getHead();
				outHead.put("X_IBOSSMODE", ibossMode);
				outHead.put("X_TRANSMODE", xTransMode);
				
				String code = outHead.getString(Constants.X_RESULTCODE);
				if ("0".equals(code)) {
					if ("SHXI".equals(SystemCfg.provinceCode)) {
						if ("6".equals(inModeCode)) {
							outHead.put(Constants.X_RESULTCODE, "00");
						} else {
							outHead.put(Constants.X_RESULTCODE, "0");
						}
					} else {
						if ("6".equals(inModeCode) || "N".equals(inModeCode)) {
							outHead.put(Constants.X_RESULTCODE, "00");
						} else {
							outHead.put(Constants.X_RESULTCODE, "0");
						}
					}
					outHead.put(Constants.X_RSPCODE, "0000");
				}
				if ("1".equals(ibossMode)) {
					writeWade3Data(hreq, hres, wade3io.write(output, true));
				} else {
					writeWade3Data(hreq, hres, wade3io.write(output));
				}
				return;
			} else {
				IData outHead = new DataMap();
				outHead.put(Constants.IN_MODE_CODE, inModeCode);
				outHead.put(Constants.X_RESULTCODE, BaseException.CODE_NO_SVC_PRIV);
				outHead.put(Constants.X_RESULTCOUNT, "0");
				outHead.put(Constants.X_RESULTINFO, BaseException.INFO_NO_SVC_PRIV + "[" + serviceName + "]");
				outHead.put(Constants.X_RESULTSIZE, "0");
				output = new DataOutput(outHead, new DatasetList());
				writeWade3Data(hreq, hres, wade3io.write(output));
				return;
			}
		} catch (EOFException e) {
			StringBuilder err = new StringBuilder();
			err.append(BaseException.CODE_SVC_TESTATTACK);
			err.append("[");
			err.append(new Date(System.currentTimeMillis()).toString());
			err.append("]");
			
			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_TESTATTACK);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, err.toString());
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeWade3Data(hreq, hres, wade3io.write(output));
			
			log.error(err.toString(), e);
			
			return;
		} catch (BaseException e) {
			StringBuilder err = new StringBuilder();
			err.append(BaseException.INFO_SVC_INVOKEERROR);
			err.append(":");
			err.append(e.getMessage());
			err.append("[");
			err.append(System.getProperty("wade.server.name"));
			err.append(",");
			err.append(serviceName);
			err.append(",");
			err.append(System.currentTimeMillis());
			err.append("]");
			
			log.error(err.toString(), e);
			
			IDataset data = new DatasetList();
			data.add(e.getData());
			
			output = new DataOutput(e.getData(), data);
			writeWade3Data(hreq, hres, wade3io.write(output));
			return;
		} catch (Exception e) {
			StringBuilder err = new StringBuilder();
			err.append(BaseException.INFO_SVC_INVOKEERROR);
			err.append(":");
			err.append(e.getMessage());
			err.append("[");
			err.append(System.getProperty("wade.server.name"));
			err.append(",");
			err.append(serviceName);
			err.append(",");
			err.append(System.currentTimeMillis());
			err.append("]");
			
			log.error(err.toString(), e);
			
			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, BaseException.CODE_SVC_INVOKEERROR);
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, err.toString());
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeWade3Data(hreq, hres, wade3io.write(output));
			return;
		} finally {
			MDC.remove(MDC_KEY);
		}
	}
	
	
	/**
	 * do head
	 * @param hreq
	 * @param hres
	 * @throws IOException
	 */
	private void doHead(HttpServletRequest hreq, HttpServletResponse hres) throws IOException {
		String clientKey = hreq.getHeader("client.key");
		
		hres.setContentType("text/html");
		hres.setContentLength(0);
		hres.setCharacterEncoding(GlobalCfg.getCharset());
		hres.setHeader("Connection", "close");
		
		IData data = new DataMap();
		
		if (sec != null)
			hres.setHeader("server.key", sec.createKey(clientKey, data));
		else
			hres.setHeader("server.key", String.valueOf(System.currentTimeMillis()));
		
		hres.flushBuffer();
		return;
	}
	
	/**
	 * do count
	 * @param hreq
	 * @param hres
	 * @throws IOException
	 */
	private void doCount(HttpServletRequest hreq, HttpServletResponse hres) throws IOException {
		hres.setContentType("text/html");
		hres.setCharacterEncoding(GlobalCfg.getCharset());
		hres.setHeader("Connection", "close");
		
		String info = String.valueOf(doSucCount) + "," + String.valueOf(doErrCount);
		hres.setContentLength(info.getBytes().length);
		hres.getOutputStream().println(info);
	}
	
	/**
	 * destroy
	 */
	public void destroy() {
		doSucCount = 0;
		doErrCount = 0;
	}
	
	
	/**
	 * 服务调用
	 * @param serviceName
	 * @param input
	 * @return
	 * @throws Exception
	 */
	protected IDataOutput mainServiceInvoke(String serviceName, IDataInput input) throws Exception {
		return ServiceInvoker.mainServiceInvoke(serviceName, input);
	}
	
	
	private IDataOutput doError(String staffId, String serviceName, Exception e, String wadeServerName, String inModeCode) {
		StringBuilder error = new StringBuilder();
		error.append(wadeServerName);
		error.append(" at ");
		error.append(System.currentTimeMillis());
		
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stack = sw.toString();
				
		if (IDataContext.ERROR_LEVEL_DEBUG.equals(SystemCfg.errorStackLevel)
				|| IDataContext.ERROR_LEVEL_LOG.equals(SystemCfg.errorStackLevel)) {
			error.append(stack);
		}
		
		IData outHead = new DataMap();
		
		String info = Utility.parseExceptionMessage(e);
		
		int index = info.indexOf(BaseException.INFO_SPLITE_CHAR);
		String msg = null, code = null, xResultCode = null, xRspType = null, xRspCode = null;
		
		if (index != -1) {
			code = info.substring(0, index);
			msg = info.substring(index + 1);
			xResultCode = code;
			xRspType = "2";
			xRspCode = code;
			
			//一级BOSS调用的特殊处理
			if (code.indexOf(":") != -1) {
				String[] codes = code.split(":");
				xRspCode = codes[0];
				xResultCode = codes[1];
				if (codes.length > 2){
					xRspType = codes[2];
			    }
			}
		} else {
			code = "-1";
			msg = info;
		}
		
		outHead.put(Constants.IN_MODE_CODE, inModeCode);
		outHead.put(Constants.X_RESULTCODE, xResultCode);
		outHead.put(Constants.X_RESULTCOUNT, "0");
		outHead.put(Constants.X_RESULTINFO, msg);
		outHead.put(Constants.X_RESULTSIZE, "0");
		outHead.put(Constants.X_RSPTYPE, xRspType);
		outHead.put(Constants.X_RSPDESC, msg);
		outHead.put(Constants.X_RSPCODE, xRspCode);
		outHead.put(Constants.X_EXCEPTION, error.toString());
		outHead.put(Constants.X_RECORDNUM, "0");
		
		DataOutput output = new DataOutput(outHead, new DatasetList());
		
		Utility.print(e);
		
		doErrCount ++;
		
		return output;
	}
	
	
	/**
	 * 通过URI获取服务名http://ip:port/xx/service/ServceName
	 * @param uri
	 * @return
	 */
	private static final String getServiceName(String uri) {
		if (log.isDebugEnabled()) {
			log.debug("解析服务请求URI:" + uri);
		}
		
		int index = uri.indexOf("service/");
		if (index != -1) {
			return uri.substring(index + 8);
		}
		
		return null;
	}

}
