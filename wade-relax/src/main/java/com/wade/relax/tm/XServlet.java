package com.wade.relax.tm;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.org.apache.commons.io.IOUtils;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: XServlet
 * @description: 事务Servlet
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-12-15
 */
public class XServlet extends HttpServlet {

	private static final long serialVersionUID = 8835250956217723595L;

	private static final Logger LOG = LoggerFactory.getLogger(XServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String cmd = request.getParameter("cmd"); // 指令
		String tid = request.getParameter("tid"); // 参数

		ServletOutputStream sos = null;
		
		try {

			if (Command.COMMIT.equals(cmd)) {
				LocalXSupervise.commit(tid);
			} else if (Command.ROLLBACK.equals(cmd)) {
				LocalXSupervise.rollback(tid);
			} else {
				throw new Exception("未知的指令! cmd=" + cmd);
			}

			LOG.debug("cmd:{}, tid:{}", cmd, tid);			
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(200);
			
		} catch (Exception e) {
			
			response.setContentType("text/html;charset=utf-8");
		    response.setStatus(500);
		      
		    String exceptionInfo = formatException(e);
		    sos = response.getOutputStream();
		    sos.write(exceptionInfo.getBytes());
		    
		    LOG.error(exceptionInfo);
		    
		    
		} finally {
			IOUtils.closeQuietly(sos);
		}
		
	}

	/**
	 * 格式化异常信息
	 * 
	 * @param throwable
	 * @return
	 */
	protected String formatException(Throwable throwable) {
		
		String rtn = null;
		
		Writer writer = null;
		PrintWriter printWriter = null;
		
		try {
			
			writer = new StringWriter();
			printWriter = new PrintWriter(writer);
			throwable.printStackTrace(printWriter);
			printWriter.flush();
			rtn = writer.toString();
			
		} finally {
			
			try {
				writer.flush();
				writer.close();		
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return rtn;
	}
	
}
