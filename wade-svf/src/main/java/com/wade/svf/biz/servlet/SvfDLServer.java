package com.wade.svf.biz.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.config.GlobalCfg;
import com.wade.relax.registry.SystemUtil;
import com.wade.svf.biz.reader.BizFlowReader;
import com.wade.svf.flow.FlowConfigure;

public class SvfDLServer extends HttpServlet {
	
	private transient static final Logger log = LoggerFactory.getLogger(SvfDLServer.class);
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * do post
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if (log.isDebugEnabled())
			log.debug(">>>start wsdl post...");

		doExecute(req, res, req.getInputStream());
	}

	/**
	 * do get
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if (log.isDebugEnabled())
			log.debug(">>>start wsdl get...");

		doExecute(req, res, req.getInputStream());
	}

	/**
	 * http://ip:port/wsdl]
	 */
	public void doExecute(HttpServletRequest req, HttpServletResponse res, InputStream instream) throws ServletException, IOException {
		String info = null;
		try {
			BizFlowReader reader = (BizFlowReader) FlowConfigure.getReader();
			info = buildHtml(reader.getFlowNames());
		} catch (Exception e) {
			info = e.getMessage();
			log.error(info, e);
		}

		doFinish(req, res, instream, info, "html");
	}

	/**
	 * do finish
	 * 
	 * @param req
	 * @param res
	 * @param instream
	 * @param info
	 * @param type
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doFinish(HttpServletRequest req, HttpServletResponse res, InputStream instream, String info, String type)
			throws ServletException, IOException {

		instream.close();

		res.setContentType("text/" + type + ";charset=" + GlobalCfg.getCharset());
		res.setContentLength(info.getBytes(GlobalCfg.getCharset()).length);
		res.setCharacterEncoding(GlobalCfg.getCharset());
		res.setHeader("Connection", "close");
		res.getWriter().write(info);
		res.getWriter().flush();
	}

	/**
	 * build html
	 * 
	 * @param list
	 * @return
	 */
	private String buildHtml(Set<String> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + GlobalCfg.getCharset() + "\" />");
		sb.append("</head>");

		sb.append("<body>");
		sb.append("Register Service Name: (total " + list.size() + "), Center Name : (" + SystemUtil.getCenterName() + ")");
		sb.append("<ul>");
		for (String name : list) {
			sb.append("<li>");
			sb.append(name);
			sb.append("</li>");
		}
		sb.append("</ul>");
		sb.append("</body>");

		sb.append("</html>");

		return sb.toString();
	}

	public void destroy() {
		super.destroy();
	}

}
