package com.ailk.service.server.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.util.ClazzUtil;
import com.ailk.service.ServiceConstants;

public class HttpServer extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private transient static final Logger log = Logger.getLogger(HttpServer.class);
	private static final String DEFAULT_ADAPTER="com.ailk.service.server.http.HttpServerAdapter";
	
	private static IHttpServerAdapter _adapter;
	
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		
		String className=config.getInitParameter("adapter-class");
		if(className==null || "".equals(className)){
			className=DEFAULT_ADAPTER;
		}
		
		_adapter=(IHttpServerAdapter)ClazzUtil.load(className, null);

		load();
	}
	
	protected void load() {
		if (log.isInfoEnabled())
			log.info("load service");
	}
	
	/**
	 * do get
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) log.debug(">>>进入 HttpServer Request[Get]");
		
		InputStream instream = request.getInputStream();
		String datastr = request.getParameter("data");
		if(datastr!=null && !"".equals(datastr)){
			doExecute(request, response, instream, datastr);
		}else{
			RequestDispatcher dispatcher = request.getRequestDispatcher("/wsdl");
			dispatcher.forward(request, response); 
		}
	}
	
	/**
	 * do post
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (log.isDebugEnabled()) log.debug(">>>进入 HttpServer Request[Post]");
		
		InputStream instream = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String datastr = sb.toString();
		doExecute(request, response, instream, datastr);
	}
	
	/**
	 * do execute
	 * @param request
	 * @param response
	 * @param instream
	 * @param datastr
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void doExecute(HttpServletRequest request, HttpServletResponse response, InputStream instream, String datastr) throws ServletException, IOException {
		IDataInput input=null;
		String outstr = null;
		String charset="utf-8";
		try {
			if (log.isDebugEnabled()) log.debug(">>>获取 HttpServer Request Inparam：" + datastr);
			
			input = _adapter.createDataInput(datastr);
			_adapter.handleInput(input);
			
			charset=input.getHead().getString(ServiceConstants.X_CHARSET,charset);
			
			IDataOutput output=_adapter.invoke(input);
			_adapter.handleOutput(input,output);
			
			outstr=_adapter.createOutStr(input,output);
			
		} catch ( Exception e) {
			log.error(e.getMessage(), e);
			
			input=new DataInput();
			_adapter.handleInput(input);
			charset=input.getHead().getString(ServiceConstants.X_CHARSET,charset);
			
			IDataOutput output=new DataOutput();
			
			IData data=new DataMap();
			data.put("ERROR_FLAG", "0");
			data.put("ERROR_INFO", e.getLocalizedMessage());
			
			//设置 Head
			output.getHead().put(ServiceConstants.X_RESULTCODE, "-1");
			output.getHead().put(ServiceConstants.X_RESULTINFO, e.getLocalizedMessage());
			output.getHead().put(ServiceConstants.X_RESULTTYPE, ServiceConstants.SERVICE_RESULT_TYPE.IData);
			
			output.getData().add(data);
			
			try{
				_adapter.handleOutput(null,output);
			}catch(Exception ex){
				//TODO handl exception
			}
			outstr=_adapter.createOutStr(input,output);
			/*
			if (input!=null && "1".equals(input.getHead().getString("JSON_SPEC",""))) {
				outstr = outparam.toString();
			} else {
				outstr = DataCompatible.toWadeString(outparam);
			}*/
		} finally {
			if(instream!=null){
				instream.close();
			}
			if (log.isDebugEnabled()) log.debug("<<< 完成 HttpServer Request");
		}
		/*
		if("UTF-8".equals(charset)){
			outstr=(new String(outstr.getBytes("utf-8"),"utf-8"));
		}
 		*/
		if(charset!=null && "gbk".equals(charset.toLowerCase())){
			outstr=(new String(outstr.getBytes("gbk"),"gbk"));
		}
		
		response.setHeader("Content-Type", "text/plain; charset=" + charset);
		response.setHeader("Content-Length", "" + outstr.getBytes(charset).length);
		response.setHeader("Connection", "close");
		response.setCharacterEncoding(charset);
		response.getWriter().print(outstr);
		response.getWriter().flush();
	}
	
	private Class<?> loadClass(String className) throws ClassNotFoundException {
	    ClassLoader loader = getContextClassLoader();
	    if(loader != null) {
	      return Class.forName(className, false, loader);
	    }
	    return Class.forName(className);
	}

    private ClassLoader getContextClassLoader(){
       return Thread.currentThread().getContextClassLoader();
    }
}