package com.ailk.common.prepare;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

import com.ailk.common.config.CodeCfg;
import com.ailk.common.config.SystemCfg;
import com.ailk.common.util.ClazzUtil;

public class PrepareServlet extends GenericServlet {

	private static final long serialVersionUID = 2052626049931080257L;
	private static transient final Logger log = Logger.getLogger(PrepareServlet.class);
	
	private static Map<String,IPrepareJob> prepareJobs = new LinkedHashMap<String,IPrepareJob>();
	private boolean load = false;

	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		@SuppressWarnings("unchecked")
		Enumeration<String> names = config.getInitParameterNames();
		while (names.hasMoreElements()) {
			String name=names.nextElement();
			String clazzName=config.getInitParameter(name);
			
			IPrepareJob obj = (IPrepareJob) ClazzUtil.load(clazzName, null);
			if (obj != null) {
				prepareJobs.put(clazzName, obj);
			}
		}
		
		if (!SystemCfg.isDataPreloadOn) {
			executeJobs(true);
		}else{
			log.info(CodeCfg.getProperty("com.ailk.common.prepare.PrepareServlet.info"));
		}
	}

	public static void executeJobs(boolean forceExecute) {
		try {
			for (Map.Entry<String,IPrepareJob> jobData : prepareJobs.entrySet()) {
				String clazzName=jobData.getKey();
				prepareJobs.get(clazzName).run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		//判断是否需要预加载
		if (SystemCfg.isDataPreloadOn && !load) {
			res.getWriter().print('0');
			res.flushBuffer();
			return;
		} else {
			executeJobs(true);
		}
	}
	
	
	@Override
	public void destroy() {
		try {
			for(Map.Entry<String,IPrepareJob> jobData : prepareJobs.entrySet()) {
				String clazzName=jobData.getKey();
				prepareJobs.get(clazzName).destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
