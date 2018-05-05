package com.wade.relax.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Enumeration;
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

import org.apache.log4j.Logger;

import com.ailk.org.apache.commons.io.IOUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.wade.relax.ServiceFactory;
import com.wade.relax.registry.Constants;
import com.wade.relax.registry.consumer.ConsumerRuntime;
import com.wade.relax.registry.provider.ProviderRuntime;
import com.wade.relax.tm.context.XContext;

public class DemoServlet extends HttpServlet {

	private static final long serialVersionUID = 8835250956217723595L;

	private static final Logger LOG = Logger.getLogger(DemoServlet.class);

	@Override
	public void init(ServletConfig config) {

		String strServiceNames = config.getInitParameter("ServiceNames");
		Set<String> serviceNames = new HashSet<String>();
		for (String serviceName : StringUtils.split(strServiceNames, ';')) {
			serviceNames.add(serviceName);
			com.wade.relax.ServiceFactory.setLocalServiceNames(serviceName);
			LOG.debug("serviceName: " + serviceName);
		}
		
		String now = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
		System.setProperty(Constants.IS_PREPARED, "StartTime:" + now);

		ProviderRuntime.start();
		//ProviderRuntime.reportCenterServiceNames(serviceNames, "wade");

		
		ConsumerRuntime.start();
		
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String tid = request.getParameter("TID");
		String serviceName = request.getParameter("ServiceName");		
		Map<String, String> params = getParams(request);
		
		ServletOutputStream sos = null;
		
		if (StringUtils.isBlank(tid)) {
			LOG.debug("TID为空，为入口APP");
			
			try {
				
				XContext.build(300);
				ServiceFactory.call(serviceName, params);
				
				LOG.debug("开始提交事务!");
				XContext.getInstance().commit();
				
			} catch (Exception e) {
				e.printStackTrace();
				
				response.setContentType("text/html;charset=utf-8");
			    response.setStatus(500);
			      
				String exceptionInfo = formatException(e);
			    sos = response.getOutputStream();
			    sos.write(exceptionInfo.getBytes());
				
				try {
					LOG.debug("开始回滚事务!");
					XContext.getInstance().rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
					exceptionInfo = formatException(e1);
				    sos = response.getOutputStream();
				    sos.write(exceptionInfo.getBytes());
				}
			} finally {
				IOUtils.closeQuietly(sos);
			}
			
		} else {
			LOG.debug("TID不为空，进入从属APP");
			XContext.build(tid);
			try {
				ServiceFactory.call(serviceName, params);
			} catch (Exception e) {
				e.printStackTrace();
				response.setContentType("text/html;charset=utf-8");
			    response.setStatus(500);
			    String exceptionInfo = formatException(e);
			    sos = response.getOutputStream();
			    sos.write(exceptionInfo.getBytes());
			    IOUtils.closeQuietly(sos);
			}
			
		}
		
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		service(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		service(request, response);
	}
	
	private Map<String, String> getParams(HttpServletRequest request) {
		
		Map<String, String> map = new HashMap<String, String>();  
        Enumeration paramNames = request.getParameterNames();
        
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();  
  
            String[] paramValues = request.getParameterValues(paramName);  
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];  
                if (paramValue.length() != 0) {  
                    map.put(paramName, paramValue);  
                }  
            }  
        }
        
        return map;
	}
	
	private String formatException(Throwable throwable) {
		
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
