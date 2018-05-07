package com.ailk.service.server.http;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.service.ServiceManager;
import com.ailk.service.hessian.io.Hessian2Input;
import com.ailk.service.hessian.io.Hessian2Output;
import com.ailk.service.hessian.io.SerializerFactory;
import com.ailk.service.invoker.ServiceInvoker;

public class Http2Server implements Filter {
	private static transient final Logger log = Logger.getLogger(Http2Server.class);
	private static String servivceRegister = "com.ailk.biz.service.BizServiceRegister";
	private static SerializerFactory inputFactory = new SerializerFactory(IDataInput.class.getClassLoader());
	private static SerializerFactory outputFactory = new SerializerFactory(IDataOutput.class.getClassLoader());

	public void init(FilterConfig config) throws ServletException {
		try {
			
			//从web.xml读取初始化配置
			String configRegister = config.getInitParameter("register");
			if (configRegister != null && !"".equals(configRegister))
				servivceRegister = configRegister;
			
			//注册服务
			ServiceManager.createRegister(servivceRegister);
			ServiceManager.register();
			
			if (log.isDebugEnabled()) {
				log.debug("开始注册服务...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		String contentType = req.getContentType();

		// 如果是Hessian客户端调用
		if ("binary/octet-stream".equals(contentType)) {

			Hessian2Input in = null;
			Hessian2Output out = null;
			try {
				in = new Hessian2Input(req.getInputStream());
				in.setSerializerFactory(inputFactory);

				IDataInput input = (IDataInput) in.readObject(IDataInput.class);
				String svcname = input.getHead().getString("X_TRANS_CODE");

				IDataOutput output = ServiceInvoker.mainServiceInvoke(svcname, input);

				out = new Hessian2Output(res.getOutputStream());
				out.setSerializerFactory(outputFactory);
				out.writeObject(output);
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			}

		} else if ("binary/hessian".equals(contentType)) {
			try {
				Hessian2Input in = new Hessian2Input(req.getInputStream());
				in.setSerializerFactory(inputFactory);
				
				IDataInput input = (IDataInput) in.readObject(IDataInput.class);
				String svcname = input.getHead().getString("X_TRANS_CODE");

				IDataOutput output = ServiceInvoker.mainServiceInvoke(svcname, input);
				
				HttpServletResponse hres = (HttpServletResponse) res;
				ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
				Hessian2Output out = new Hessian2Output(baos);
				out.setSerializerFactory(outputFactory);
				out.writeObject(output);
				out.flush();
				byte[] bytes = baos.toByteArray();
				out.close();
				
				hres.setContentType("binary/octet-stream");
				hres.setContentLength(bytes.length);
				hres.setCharacterEncoding(GlobalCfg.getCharset());
				hres.setHeader("Connection", "keep-alive");
				
				BufferedOutputStream bos = new BufferedOutputStream(hres.getOutputStream());
				bos.write(bytes);
				bos.flush();
				bos.close();
				hres.flushBuffer();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				
			}
		}
		// 什么都不是，则查询已注册的服务列表，跳转到wsdl
		else {
			RequestDispatcher dispatcher = req.getRequestDispatcher("/wsdl");
			dispatcher.forward(req, res);
		}
	}

	public void destroy() {

	}

}
