/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月26日
 * 
 * Just Do IT.
 */
package com.wade.svf.server.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.BaseException;
import com.ailk.common.config.GlobalCfg;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.filter.IFlowFilter;
import com.wade.svf.server.FlowService;
import com.wade.svf.server.serializer.DataSerializerFactory;

/**
 * @description
 * 流程Http服务，仅支持JSON格式的请求和响应
 */
public class FlowHttpServer extends HttpServlet {

	private static final long serialVersionUID = -1862685134279911090L;
	
	private static final String NotPreparedInfo = "{\"head\":{\"X_RESULTCODE\":\"-10000\",\"X_RESULTINFO\":\"Server Is Not Prepared!\"},\"data\":{\"X_RESULTCODE\":\"-10000\",\"X_RESULTINFO\":\"Server Is Not Prepared!\"}}";
	
	private static final Logger log = LoggerFactory.getLogger(FlowHttpServer.class);
	
	private static final String URL_PARTTEN = "service/";
	
	private static final String isPreparedEnabled = GlobalCfg.getProperty("flow.prepare.enabled", "false");
	
	/**
	 * 初始化流程过滤器
	 */
	private static final List<IFlowFilter> filters = new ArrayList<IFlowFilter>(5);
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		String[] filterClasses = config.getInitParameter("flow-filter").split(";");
		for (String filter : filterClasses) {
			try {
				IFlowFilter flowFilter = (IFlowFilter) Class.forName(filter).newInstance();
				filters.add(flowFilter);
				
				if (log.isInfoEnabled()) {
					log.info("添加流程过滤器：" + flowFilter.getClass().getName());
				}
			} catch (Exception e) {
				log.error("添加流程过滤器异常:" + filter, e);
				throw new ServletException(e);
			}
		}
	}
	
	/**
	 * 验证是否预热，未预热直接返回异常
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (isPrepared()) {
			doService(request, response);
		} else {
			printNotPrepared(request, response);
		}
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (isPrepared()) {
			doService(request, response);
		} else {
			printNotPrepared(request, response);
		}
	}
	
	/**
	 * 输出未预热提示信息
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void printNotPrepared(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 输出数据
		byte[] bytes = NotPreparedInfo.getBytes();
		response.setContentType("text/plain");
		response.setContentLength(bytes.length);
		response.setCharacterEncoding("utf-8");
		response.setHeader("Connection", "close");
		
		OutputStream os = response.getOutputStream();
		os.write(bytes);
		os.flush();
		os.close();
	}
	
	/**
	 * 判断是否预热
	 * @return
	 */
	private boolean isPrepared() {
		if (!"true".equals(isPreparedEnabled)) {
			return true;
		}
		return System.getProperty("isPrepared", "").startsWith("StartTime");
	}
	
	
	/**
	 * 通过URI获取服务名http://ip:port/xx/service/FlowName
	 * @param uri
	 * @return
	 */
	private static final String getFlowName(String uri) {
		int index = uri.indexOf(URL_PARTTEN);
		if (index != -1) {
			return uri.substring(index + URL_PARTTEN.length());
		}
		
		return null;
	}
	
	
	/**
	 * 处理服务请求
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, String> requestData = new HashMap<String, String>(10);
		
		// 处理请求头信息，Content-Type, Client-IP, Client-Mac, charset
		String contentType = request.getHeader(RequestHead.ContentType.getCode());
		if (null == contentType || contentType.trim().length() == 0) {
			contentType = request.getContentType();
		}
		if (null == contentType || contentType.trim().length() == 0) {
			contentType = DataSerializerFactory.DATA_APPLICATION_JSON;
		}
		
		String charset = request.getCharacterEncoding();
		if (null == charset || charset.length() == 0) {
			charset = "utf-8";
		}
		
		String queryString = request.getQueryString();
		if (null == queryString || queryString.trim().length() == 0) {
			queryString = "";
		}
		
		String clientIP = getClientIP(request);
		
		String flow = getFlowName(request.getRequestURI());
		String method = request.getMethod();
		
		requestData.put(RequestHead.Charset.getCode(), charset);
		requestData.put(RequestHead.ContentType.getCode(), contentType);
		requestData.put(RequestHead.ClientIp.getCode(), clientIP);
		requestData.put(RequestHead.ClientMac.getCode(), request.getHeader(RequestHead.ClientMac.getCode()));
		requestData.put(RequestHead.FlowName.getCode(), flow);
		
		if (log.isDebugEnabled()) {
			StringBuilder requestInfo = new StringBuilder(100);
			requestInfo.append("服务请求：");
			requestInfo.append(flow);
			requestInfo.append(",");
			requestInfo.append(RequestHead.ContentType.getCode());
			requestInfo.append("=");
			requestInfo.append(contentType);
			requestInfo.append(",");
			requestInfo.append(RequestHead.ClientIp.getCode());
			requestInfo.append("=");
			requestInfo.append(clientIP);
			requestInfo.append(",");
			requestInfo.append(RequestHead.Charset.getCode());
			requestInfo.append("=");
			requestInfo.append(charset);
			requestInfo.append(",");
			requestInfo.append(RequestHead.QueryString.getCode());
			requestInfo.append("=");
			requestInfo.append(queryString);
			requestInfo.append(",");
			requestInfo.append(RequestHead.Method.getCode());
			requestInfo.append("=");
			requestInfo.append(method);
			
			log.debug(requestInfo.toString());
		}
		
		// 流程执行
		StringBuilder responseData = new StringBuilder(1000);
		try {
			InputStream in = getInputStream(request, method, queryString, charset);
			responseData.append(doExecute(flow, requestData, in));
		} catch (Exception e) {
			responseData.append(doError(requestData, e));
		}
		
		// 输出数据
		byte[] bytes = responseData.toString().getBytes(charset);
		response.setContentType("text/plain");
		response.setContentLength(bytes.length);
		response.setCharacterEncoding(charset);
		response.setHeader("Connection", "close");
		
		OutputStream os = response.getOutputStream();
		os.write(bytes);
		os.flush();
		os.close();
	}
	
	/**
	 * 获取请求输入流，若为GET模式，则转换成ByteArrayInputStream
	 * @param request
	 * @param method
	 * @param queryString
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	private InputStream getInputStream(HttpServletRequest request, String method, String queryString, String charset) throws Exception {
		InputStream in = null;
		if ("GET".equals(method)) {
			String decode = URLDecoder.decode(queryString, charset);
			if (log.isDebugEnabled()) {
				log.debug("decode request:" + decode);
			}
			in = new ByteArrayInputStream(decode.getBytes());
		} else {
			in = request.getInputStream();
		}
		return in;
	}
	
	
	/**
	 * 流程执行
	 * @param flow
	 * @param request
	 * @param in
	 * @return
	 */
	private String doExecute(String flow, Map<String, String> request, InputStream in) {
		StringBuilder response = new StringBuilder(1000);
		try {
			if (null == flow || flow.trim().length() == 0) {
				throw new FlowException(FlowErr.flow10001.getCode(), FlowErr.flow10001.getInfo("流程名不能为空"));
			}
			
			FlowService service = new FlowService(flow, request);
			service.setFilters(filters);
			byte[] responseData = service.execute(in);
			
			return response.append(new String(responseData, request.get(RequestHead.Charset.getCode()))).toString();
		} catch (Exception e) {
			return response.append(doError(request, e)).toString();
		}
	}
	
	
	/**
	 * 触发异常处理
	 * @param request
	 * @param e
	 */
	private String doError(Map<String, String> request, Exception e) {
		log.error("流程调用异常", e);
		
		StringBuilder response = new StringBuilder(1000);
		
		response.append("{\"head\":{");
		Iterator<String> iter = request.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String value = request.get(key);
			
			response.append("\"").append(key).append("\":\"").append(value).append("\"");
			if (iter.hasNext()) {
				response.append(",");
			}
		}
		response.append("}");
		
		response.append(",\"data\":{");
		if (e instanceof FlowException) {
			FlowException fe = (FlowException) e;
			
			response.append("\"X_RESULTCODE\":\"").append(fe.getErrCode()).append("\",");
			response.append("\"X_RESULTINFO\":\"").append(fe.getErrInfo()).append("\"");
			
			response.append("}");
			response.append("}");
			return response.toString();
		} else if (e instanceof BaseException) {
			BaseException be = (BaseException) e;
			
			response.append("\"X_RESULTCODE\":\"").append(be.getCode()).append("\",");
			response.append("\"X_RESULTINFO\":\"").append(be.getInfo()).append("\"");
			
			response.append("}");
			response.append("}");
			return response.toString();
		} else {
			response.append("\"X_RESULTCODE\":\"").append("-1").append("\",");
			response.append("\"X_RESULTINFO\":\"").append("未定义的异常编码：").append(e.getMessage()).append("\"");
			
			response.append("}");
			response.append("}");
			return response.toString();
		}
	}
	
	
	/**
	 * 获取客户端IP
	 * @param req
	 * @return
	 */
	private static String getClientIP(HttpServletRequest req) {
		if (req == null)
			return "";
		
		String ip = req.getHeader("WADE-Client-IP");
		if (ip == null || ip.length() == 0){
			ip = req.getParameter("CLIENT_IP_ADDR");
		}
		
		if (ip == null || ip.length() == 0){
			ip = req.getHeader("X-Forwarded-For");
			if(ip != null && !"".equals(ip)){ //处理通过2次四层交换的情况，取","号前第一个IP
				int idx = ip.indexOf(",");
				if(idx > -1){
					ip = ip.substring(0, idx);
				}
			}
		}
		
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")){
			ip = req.getHeader("Proxy-Client-IP");
		}
		
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")){
			ip = req.getHeader("WL-Proxy-Client-IP");
		}
		
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")){
			ip = req.getRemoteAddr();
		}
		return ip;
	}

}
