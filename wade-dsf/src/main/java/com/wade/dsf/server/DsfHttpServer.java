/**
 * $
 */
package com.wade.dsf.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.dsf.request.DsfRequestHeader;
import com.wade.dsf.server.DsfAction;
import com.wade.dsf.server.DsfHttpServer;
import com.wade.dsf.exception.DsfErr;
import com.wade.dsf.exception.DsfException;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: DsfHttpServer.java
 * @description: Dsf的HTTP服务端，接收HTTP请求，执行DsfAction
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-4-25
 */
public class DsfHttpServer extends HttpServlet {

	private static final long serialVersionUID = 4998520262482782263L;
	private static final Logger log = LoggerFactory.getLogger(DsfHttpServer.class);
	
	/**
	 * 真正的请求处理对象，提供
	 */
	private IDsfAction action = DsfAction.getInstance();
	private Set<String> methods = new HashSet<String>(5);
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		// 初始化服务
		try {
			action.init();
		} catch (Exception e) {
			log.error("服务初始化失败", e);
			throw new ServletException(e);
		}
		
		// 初始化Get和Post请求
		String method = config.getInitParameter("method");
		if (null == method || method.length() == 0) {
			methods.add("post");
		} else {
			String[] ary = method.split(",");
			for (String item : ary) {
				methods.add(item);
			}
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if (methods.contains("get")) {
			doService(req, res);
		} else {
			PrintWriter write = res.getWriter();
			write.write("不支持Get方式");
			write.flush();
		}
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if (methods.contains("post")) {
			doService(req, res);
		} else {
			PrintWriter write = res.getWriter();
			write.write("不支持POST方式");
			write.flush();
		}
	}
	
	
	/**
	 * 将请求数据转换成ServieRequest, 并将ServiceResponse以byte[]返回给客户端
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		ServletOutputStream out = res.getOutputStream();
		
		Serializable sreq = null;
		Serializable sres = null;
		
		Map<String, String> requestHeader = new HashMap<String, String>(10);
		// 处理请求头信息，Context-Type, Client-IP, Client-Mac, charset
		String contentType = req.getHeader("Context-Type");
		if (null == contentType || contentType.trim().length() == 0) {
			contentType = req.getContentType();
		}
		if (null == contentType || contentType.trim().length() == 0) {
			contentType = "default";
		}
		
		String charset = req.getCharacterEncoding();
		if (null == charset || charset.length() == 0) {
			charset = "utf-8";
		}
		
		String queryString = req.getQueryString();
		if (null == queryString || queryString.trim().length() == 0) {
			queryString = "";
		}
		
		String method = req.getMethod();
		
		// 从请求URL获取服务名, 并验证其格式是否正确
		String serviceName = getServiceName(req.getRequestURI());
		
		requestHeader.put(DsfRequestHeader.Charset.getCode(), charset);
		requestHeader.put(DsfRequestHeader.QueryString.getCode(), queryString);
		requestHeader.put(DsfRequestHeader.ContextType.getCode(), contentType);
		requestHeader.put(DsfRequestHeader.ClientIp.getCode(), req.getHeader(DsfRequestHeader.ClientIp.getCode()));
		requestHeader.put(DsfRequestHeader.ClientMac.getCode(), req.getHeader(DsfRequestHeader.ClientMac.getCode()));
		requestHeader.put(DsfRequestHeader.ServiceName.getCode(), serviceName);
		
		if (log.isDebugEnabled()) {
			StringBuilder requestInfo = new StringBuilder(100);
			requestInfo.append("服务请求：");
			requestInfo.append(serviceName);
			requestInfo.append(",");
			requestInfo.append(DsfRequestHeader.ContextType.getCode());
			requestInfo.append("=");
			requestInfo.append(contentType);
			requestInfo.append(",");
			requestInfo.append(DsfRequestHeader.Charset.getCode());
			requestInfo.append("=");
			requestInfo.append(charset);
			requestInfo.append(",");
			requestInfo.append(DsfRequestHeader.QueryString.getCode());
			requestInfo.append("=");
			requestInfo.append(queryString);
			requestInfo.append(",");
			requestInfo.append(DsfRequestHeader.Method.getCode());
			requestInfo.append("=");
			requestInfo.append(method);
			
			log.debug(requestInfo.toString());
		}
		
		if (null == serviceName || serviceName.length() == 0) {
			action.error(serviceName, requestHeader, out, new DsfException(DsfErr.dsf10001.getCode(), DsfErr.dsf10001.getInfo()));
		} else if (serviceName.indexOf("/") != -1) {
			action.error(serviceName, requestHeader, out, new DsfException(DsfErr.dsf10007.getCode(), DsfErr.dsf10007.getInfo(serviceName)));
		} else {
			ByteArrayInputStream bais = null;
			
			try {
				
				if ("GET".equals(method)) {
					bais = new ByteArrayInputStream(queryString.getBytes(charset));
					sreq = action.read(serviceName, requestHeader, bais);
				} else {
					sreq = action.read(serviceName, requestHeader, req.getInputStream());
				}
				
				sres = action.execute(serviceName, requestHeader, sreq);
				
				action.write(serviceName, requestHeader, out, sres);
			} catch (Exception e) {
				action.error(serviceName, requestHeader, out, e);
				
				log.error("服务{}执行异常", serviceName, e);
			} finally {
				bais = null;
			}
			
		}
		
		String keepAlive = req.getHeader("Keep-Alive");
		if ("true".equals(keepAlive)) {
			res.addHeader("Keep-Alive", "true");
		}
		
		out.flush();
		out.close();
	}
	
	
	/**
	 * 通过URI获取服务名http://ip:port/xx/dsf/ServceName
	 * @param uri
	 * @return
	 */
	private static final String getServiceName(String uri) {
		if (log.isDebugEnabled()) {
			log.debug("解析服务请求URI:" + uri);
		}
		
		int index = uri.indexOf("dsf/");
		if (index != -1) {
			return uri.substring(index + 4);
		}
		
		return null;
	}

}
