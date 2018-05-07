package com.ailk.service.server.wsdl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.ailk.common.config.CodeCfg;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.ServiceManager;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.wade.relax.registry.SystemUtil;

public class WSDLServer extends HttpServlet {
	private transient static final Logger log = Logger.getLogger(WSDLServer.class);
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

		InputStream instream = req.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(instream, GlobalCfg.getCharset()));
		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String datastr = sb.toString();

		doExecute(req, res, instream, datastr);
	}

	/**
	 * do get
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if (log.isDebugEnabled())
			log.debug(">>>start wsdl get...");

		InputStream instream = req.getInputStream();
		IData datastr = new DataMap();

		Enumeration<?> e = req.getParameterNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = req.getParameter(key);
			datastr.put(key, value);
		}

		doExecute(req, res, instream, datastr.toString());
	}

	/**
	 * http://ip:port/wsdl&service=xxx[&param=xxx...]
	 */
	public void doExecute(HttpServletRequest req, HttpServletResponse res, InputStream instream, String datastr)
			throws ServletException, IOException {
		if (log.isDebugEnabled())
			log.debug(">>>start wsdl execute param :" + datastr);

		String info = null;
		IData param = new DataMap();
		JSONObject map = JSONObject.fromObject(datastr);
		param.putAll(map);

		String serviceName = param.getString("service");

		if (serviceName != null && !"".equals(serviceName)) {
			ServiceEntity service = null;
			try {
				service = ServiceManager.find(serviceName);
			} catch (Exception e) {
				info = e.getMessage();
			}
			if (service != null) {
				info = service.getProtocol().toXml();
			} else
				info = CodeCfg.getProperty("service.wsdl.name.notfind", "can't find service name [" + serviceName + "]");

			doFinish(req, res, instream, info, "xml");
		} else {
			try {
				info = buildHtml(ServiceManager.serviceList());
			} catch (Exception e) {
				info = e.getMessage();
				log.error(info, e);
			}

			doFinish(req, res, instream, info, "html");
		}
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
		String pubTime = System.getProperty("wade.svc.pubtime");
		for (String name : list) {
			ServiceEntity entity = ServiceManager.find(name);
			sb.append("<li>");
			sb.append("<a href='wsdl?service=");
			sb.append(name);
			sb.append("'>");
			sb.append(name);
			sb.append(" [ 子系统:").append(entity.getGroup());
			sb.append(", 中心名称:").append(entity.getCenter());
			sb.append(", 并发数:").append(entity.getThreshold());
			sb.append(", 超时:").append(entity.getTimeout());
			sb.append(", 版本:").append(entity.getVersion());
			sb.append(", 描述:").append(entity.getDesc());
			sb.append(", 状态:");
			
			if (entity.getStatus() == ServiceEntity.STATUS_DISABLED) {
				sb.append("<font color='red'>已下线</font>");
			} else if (entity.getStatus() == ServiceEntity.STATUS_ENABLED) {
				sb.append("已发布");
			} else {
				sb.append("已注册");
			}
			
			sb.append(", 发布时间:").append(pubTime);
			sb.append(" ]");
			sb.append(" </a>");
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
