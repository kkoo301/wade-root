package com.wade.svf.biz.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.config.CodeCfg;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.config.SystemCfg;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.wade.relax.registry.consumer.ConsumerRuntime;
import com.wade.relax.registry.provider.ProviderRuntime;
import com.wade.svf.biz.reader.BizFlowReader;
import com.wade.svf.flow.FlowConfigure;

public class SvfPrepareServlet extends GenericServlet {

	private static final long serialVersionUID = 2052626049931080257L;
	
	private static transient final Logger log = LoggerFactory.getLogger(SvfPrepareServlet.class);
	
	
	/**
	 * -1:未初始化
	 * 0:正在初始化
	 * 1:初始化完成
	 */
	private int load = -1;

	/**
	 * init
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		if (SystemCfg.isDataPreloadOn) {
			log.info("APP服务需要初始化操作[/appprepare&user=xx&passwd=xxx]");
		}
		
		log.info("APP初始化系统配置...");
		SystemCfg.getInstance();
		
		log.info("APP初始化全局配置...");
		GlobalCfg.getInstance();
		
		log.info("APP初始化编码定义...");
		CodeCfg.getInstance();

		String proc = System.getProperty("wade.server.name");
		log.info("APP初始化进程[" + proc + "]的数据库连接...");
		ConnectionManagerFactory.getConnectionManager();
	}
	
	
	/**
	 * service
	 */
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		HttpServletRequest hreq = (HttpServletRequest) req;
		HttpServletResponse hres = (HttpServletResponse) res;
		
		//判断是否需要先初始化服务
		if (load == -1) {
			if (log.isDebugEnabled()) {
				log.debug(">>>APP服务未初始化");
			}
			//执行预加载逻辑
			if (doPrepare(hreq, hres)) {
				return ;
			}
		} else if (load == 0) {
			String info = "loading";
			hres.setContentLength(info.getBytes().length);
			hres.getOutputStream().print(info);
			return ;
		} else {
			String info = "OK : " + System.getProperty("wade.server.name", "");
			hres.setContentLength(info.getBytes().length);
			hres.getOutputStream().print(info);
			return ;
		}
	}
	
	
	
	/**
	 * do preload
	 * @param hreq
	 * @param hres
	 * @return
	 * @throws IOException
	 */
	private boolean doPrepare(HttpServletRequest hreq, HttpServletResponse hres) throws IOException {
		String url = hreq.getRequestURI();
		String user = hreq.getParameter("user");
		String passwd = hreq.getParameter("passwd");
		
		if (StringUtils.isNotBlank(url) && StringUtils.isNotBlank(user) && StringUtils.isNotBlank(passwd)) {
			
			if (url.endsWith("appprepare") && "prepare".equals(user) && "123".equals(passwd)) {
				boolean result = true;
				try {
					load = 0;
					log.info("开始初始化APP...");
					
					BizFlowReader reader = (BizFlowReader) FlowConfigure.getReader();
					reader.loadFlow();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = sdf.format(new Date(System.currentTimeMillis()));
			        System.setProperty("isPrepared", "StartTime:" + time);
			        
			        // 连接注册中心
			        log.info("开始连接注册中心...");
		    		ProviderRuntime.start();
		    		
		    		Set<String> services = reader.getFlowNames();
		    		log.info("开始发布中心服务,共" + services.size() + "个");
		    		ProviderRuntime.reportCenterServiceNames(services);
		    		
		    		ConsumerRuntime.start();
		    		log.info("注册中心连接成功");
					
					log.info("APP所有初始化完成");
				} catch (Exception e) {
					result = false;
					load = -1;
					log.error(e.getMessage(), e);
				}
				
				if (result) {
					load = 1;
				}
				
				String info = result ? "ok" : "error";
				hres.setContentLength(info.getBytes().length);
				hres.getOutputStream().print(info);
				return true;
			} else {
				String info = "Permission denied";
				hres.setContentLength(info.getBytes().length);
				hres.getOutputStream().print(info);
				return true;
			}
		}
		return false;
	}
	
}
