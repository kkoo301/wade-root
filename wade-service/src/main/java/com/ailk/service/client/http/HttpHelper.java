package com.ailk.service.client.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.util.ClazzUtil;
import com.ailk.service.ServiceConstants;
import com.ailk.service.protocol.ServiceException;

public class HttpHelper{
	private transient static final Logger log = Logger.getLogger(HttpHelper.class);
	
	private static IHttpClientAdapter _adapter;
	protected static IHttpClientAdapter getHttpAdapter(){
		if(_adapter==null){
			String className=GlobalCfg.getProperty("service.http.adapter","com.ailk.service.client.http.HttpClientAdapter");
			_adapter=(IHttpClientAdapter)ClazzUtil.load(className,null);
		}
		return _adapter;
	}
	
	/**
	 * call http
	 * 
	 * @param name
	 * @param in
	 * @return
	 */
	public static IDataOutput callHttpSvc(String name,IDataInput in) throws Exception{
		return callHttpSvc(name,in,false);
	}
	
	/**
	 * call http
	 * 
	 * @param name
	 * @param in
	 * @return
	 */
	public static IDataOutput callHttpSvc(String name,IDataInput in,boolean iscatch) throws Exception{
		in.getHead().put(ServiceConstants.X_SERVICE_NAME, name);
		in.getHead().put("_IS_CATCH", iscatch);
		
		IHttpClientAdapter adapter=getHttpAdapter();
		String datastr=adapter.buildPostDataString(in);
		String url=adapter.getServiceUrl(in);
		String charset=in.getHead().getString(ServiceConstants.X_CHARSET,GlobalCfg.getCharset());
		
		return adapter.buildDataOutput(in,httpCall(url,charset,datastr));
	}
	
	/**
	 * uri [http://ip:port/wsdl]
	 * datastr service input head string
	 * @param uri
	 * @param datastr
	 * @return
	 */
	private static String httpCall(String uri, String charset,String datastr) throws ServiceException {
		if(log.isDebugEnabled()){
			log.debug(">>>Begin HttpRequest(Post) On:\"" + uri + "\"");
			log.debug(">>>Charset:"+ charset +",Post Data:" + datastr);
		}
		
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(HTTP.CONTENT_ENCODING, charset);
		
		HttpResponse response = null;
		HttpPost post = null;
		
		post = new HttpPost(uri);
		post.getParams().setParameter("http.socket.timeout", new Integer(500000));
		post.setHeader("Content-type", "text/plain; charset=" + charset);
		post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
		post.setHeader("Connection", "close");
		
		InputStreamEntity reqEntity = null;
		try {
			reqEntity = new InputStreamEntity(new ByteArrayInputStream(datastr.getBytes(charset)), -1);
		} catch (UnsupportedEncodingException e) {
			throw new ServiceException(e);
		}
		reqEntity.setContentType("binary/octet-stream");
		reqEntity.setChunked(true);
		post.setEntity(reqEntity);
		
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			throw new ServiceException(e);
		} catch (IOException e) {
			throw new ServiceException(e);
		}
		
		HttpEntity entity = response.getEntity();
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();
		if (entity != null) {
			try {
				in = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
			} catch (UnsupportedEncodingException e) {
				throw new ServiceException(e);
			} catch (IllegalStateException e) {
				throw new ServiceException(e);
			} catch (IOException e) {
				throw new ServiceException(e);
			}
			String line = null;
			try {
				while ((line = in.readLine()) != null) {
					buffer.append(line);
				}
			} catch (IOException e) {
				throw new ServiceException(e);
			}
		}
		if(log.isDebugEnabled()){
			log.debug(">>>HttpRequest(Post) Result:\"" + buffer.toString() + "\"");
		}
		return buffer.toString();
	}	
}
