package com.linkage.safe.parameter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import com.linkage.safe.util.Security;
import com.linkage.safe.util.WadeSafeLog;
/**
 * 
 * @author $Id: ParameterRequestWrapper.java 62 2014-04-29 14:53:58Z jiangmj $
 *
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {
    private Map params = null; 
    private byte[] buffer = null;
   	public int bufLen = 0;
    //private ServletInputStream in = null;
    //private BufferedReader reader = null;
    private boolean isPost = false;
    public String postDataMd5 = "";
    private static byte extParam[] ={'-','-','-','-','-','-','-','-','-','a','s','y','n','c','_','a','j','a','x','_','p','o','s','t','-','-','-','-','-','-','-','-','-'};
    public String asyncSafePost = "";
    private static WadeSafeLog wsl = null;
    //private String contentType = null;
  
    public ParameterRequestWrapper(HttpServletRequest request) throws IOException {
        super((HttpServletRequest)request);
        //上传模式的POST不处理
        String contentType = request.getHeader("Content-Type");
        if (request.getMethod().equals("POST") && contentType!=null && contentType.indexOf("multipart/form-data;")==-1 ) {//&& request.getContentLength()>0
        	isPost = true;// fileupload
          readInputStream(request);
          //客户端是鸟语传输的
          if( request.getHeader("WADE-Client-Date")!= null){
          	long len = request.getContentLength();
          	for(int i=0;i<len;i++ ){
          		buffer[i] = (byte)(buffer[i] ^ len);
          	}
          }
					//ajax_post
         	if(bufLen> 053 && buffer[bufLen-43] == 38 && buffer[bufLen-42] == 0x53
          	&& buffer[bufLen-41] == 0x41 && buffer[bufLen-40] == 0106 && buffer[bufLen-39] == 69
          		&& buffer[bufLen-38] == 95 && buffer[bufLen-37] == 0x50 && buffer[bufLen-36] == 0117
          			&& buffer[bufLen-35] == 0123 && buffer[bufLen-34] == 84 && buffer[bufLen-33] == 0x3D){
	        		String strSafPost = new String(buffer,buffer.length-32,32);
	        		asyncSafePost =  strSafPost;
	        		for(int i=0;i<32;i++){
	        			buffer[buffer.length-32+i]= extParam[i];
	        		}
      				postDataMd5 = Security.md5b(buffer);
        	}else{//普通POST
        		postDataMd5 = Security.md5b(buffer);
        	}
        }
    }
  
    /**
     * 读取request流到buffer中
     * 
     * @param request
     *            HttpServletRequest
     * @throws IOException
     *             if throws IOException
     */
    private void readInputStream(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        ServletInputStream sis = request.getInputStream();
        int maxReadSize = 1024;// buffer size
        buffer = new byte[contentLength];
        int end = 0;
        int len = 0;
        while (end <= contentLength) {
            len = sis.read(buffer, end, maxReadSize);
            if (len == -1) {
                end = contentLength;
                break;
            }
            end += len;
            try {
                TimeUnit.MILLISECONDS.sleep(0);// 轮询
            } catch (InterruptedException e) {
                // ignroe
            }
        }
        bufLen = end;
        sis.close();// closed?
    }
  
    /**
     * if isMultipart return request inputstream to byte[]
     * 
     * @return default null
     */
    public byte[] getRequestData() {
        return buffer;
    }
  
    // 重载父类获取inputStream的方法
    @Override
    public ServletInputStream getInputStream() throws IOException {
    	if(isPost){
    		 ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);// real                                                            // servletInputStream
             return new WrapperServletInputstream(inputStream);
    	}
    	return super.getInputStream();
        //return isMultipart ? in : super.getInputStream();
    }
  
    // 重载父类获取reader的方法
    @Override
    public BufferedReader getReader() throws IOException {
    	if(isPost){
   		 ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);// real                                                            // servletInputStream
            return new BufferedReader(new InputStreamReader(inputStream));
    	}
    	return super.getReader();
        //return isMultipart ? reader : super.getReader();
    }
  
    /**
     * 判断此次请求是否是POST
     * 
     * @return
     */
    public boolean isPost() {
        return isPost;
    }
  
    class WrapperServletInputstream extends ServletInputStream {
        private InputStream in = null;
  
        public WrapperServletInputstream(InputStream in) throws IOException {
            this.in = in;
        }
  
        @Override
        public int read() throws IOException {
            int read = in.read();
            return read;
        }
  
        @Override
        public synchronized void reset() throws IOException {
            in.reset();
            super.reset();
        }
    }

	private void CheckInitParam(){
        if(params==null){
      	  params = new HashMap(super.getParameterMap());
      	  WadeParameters wpe = new WadeParameters();
      	  wpe.setEncoding(super.getContentType());
      	  Map postMap = wpe.getPostMap(buffer, 0, bufLen);
      	  if(postMap!=null)
      		  params.putAll( postMap);
        }
    }
    public Map getParameterMap() {
    	CheckInitParam();
  	  	return params;
  	}
  	public Enumeration getParameterNames() {
  		CheckInitParam();
  		Vector l=new Vector(params.keySet());
  		return l.elements();
  	}
  	public String[] getParameterValues(String name) {
  		CheckInitParam();
  	  Object v = params.get(name);
  	  if(v==null){
  	    return null;
  	  }else if(v instanceof String[]){
  	    return (String[]) v;
  	  }else if(v instanceof String){
  	    return new String[]{(String) v};
  	  }else{
  	    return new String[]{v.toString()};
  	  }
  	}
  	public String getParameter(String name) {
  		CheckInitParam();
  	  Object v = params.get(name);
  	  if(v==null){
  	    return null;
  	  }else if(v instanceof String[]){        	
  	    String []strArr=(String[]) v;
  	    if(strArr.length>0){
  	      return strArr[0];
  	    }else{
  	      return null;
  	    }
  	  }else if(v instanceof String){
  	    return (String) v;
  	  }else{
  	    return v.toString();
  	  }
  	}     
  	public void clearParamMap(){
  		if(params!=null){
  			params.clear();
  			params = null;
  		}
  	}
  	
}