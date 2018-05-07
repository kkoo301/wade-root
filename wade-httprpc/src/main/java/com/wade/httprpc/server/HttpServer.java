/**
 * $
 */
package com.wade.httprpc.server;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wade.httprpc.server.IHttpAction;
import com.wade.httprpc.util.SerializeUtil;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: Server.java
 * @description: 将Request和Response按Java的序列化方式传输
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-14
 */
public class HttpServer extends HttpServlet {
	
	private static final long serialVersionUID = -3697393616556660534L;
	
	private IHttpAction<Serializable, Serializable> action = null;
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		String actionName = config.getInitParameter("action");
		try {
			action = (IHttpAction) Class.forName(actionName).newInstance();
		} catch (Exception e) {
			throw new ServletException("初始化Action失败," + actionName, e);
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter write = res.getWriter();
		write.write("OK");
		write.flush();
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doService(req, res);
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
		
		String keepAlive = req.getHeader("Keep-Alive");
		try {
			sreq = SerializeUtil.deserialize(req.getInputStream());
			sres = action.execute(req, sreq);
		} catch (Exception e) {
			sres = action.error(sreq, e);
		}
		
		byte[] data = SerializeUtil.serialize(sres);
	
		if ("true".equals(keepAlive)) {
			res.addHeader("Keep-Alive", "true");
		}
		res.addHeader("Content-Length", String.valueOf(data.length));
		
		out.write(data);
		
		out.flush();
		out.close();
	}
	
}
