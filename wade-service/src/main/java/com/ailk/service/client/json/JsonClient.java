package com.ailk.service.client.json;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.service.client.IProtocalClient;
import com.ailk.service.client.request.JsonClientRequest;
import com.ailk.service.serializer.JsonToIOData;

/**
 * 
 * @author yifur
 * 
 */
public class JsonClient implements IProtocalClient {

	private static Map<String, URL> urls = new HashMap<String, URL>(5);
	
	private static final int SO_TIMEOUT = 60 * 1000;
	
	/**
	 * Socket的连接超时设置
	 */
	public static final int CONNECT_TIMEOUT = 60 * 1000;
	
	private static final String DEF_CONTENT_TYPE = "binary/json-stream;charset=UTF-8";
	
	
	public String getRoute(String url, String svcname, IData params) {
		return null;
	}
	
	
	public IDataOutput request(String url, String svcname, IDataInput input) throws Exception {
		return request(url, svcname, input, SO_TIMEOUT);
	}
	
	
	public IDataOutput request(String url, String svcname, IDataInput input, int timeout, int connectTimeout) throws Exception {
		JsonToIOData io = new JsonToIOData();
		
		URL cacheUrl = getCacheURL(url);
		
		String host = cacheUrl.getHost();
		int port = cacheUrl.getPort();
		String contextRoot = cacheUrl.getPath();
		String charset = "UTF-8";
		String json = io.read(input);
		
		int soTimeout = timeout;
		if (soTimeout <= 0) {
			soTimeout = SO_TIMEOUT;
		}
		
		int connTimeout = connectTimeout;
		if (connTimeout <= 0) {
			connTimeout = CONNECT_TIMEOUT;
		}
		
		String data = JsonClientRequest.post(host, port, contextRoot, svcname, json, charset, soTimeout, DEF_CONTENT_TYPE, connTimeout);
		return io.write(data);
	}
	
	/**
	 * get cache url
	 * @param url
	 * @return
	 */
	private static URL getCacheURL(String url) {
		URL cacheUrl = urls.get(url);
		if (cacheUrl == null) {
			try {
				cacheUrl = new URL(url);
				urls.put(url, cacheUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return cacheUrl;
	}


	public IDataOutput request(String url, String svcname, IDataInput input, int timeout) throws Exception {
		return request(url, svcname, input, timeout, CONNECT_TIMEOUT);
	}
}
