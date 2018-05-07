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
import com.ailk.service.serializer.Json2ToIOData;

/**
 * 
 * @author yifur
 * 
 */
public class Json2Client implements IProtocalClient {

	/**
	 * 缓存调用的URL地址
	 */
	private static Map<String, URL> urls = new HashMap<String, URL>(5);
	
	/**
	 * Socket的read超时设置
	 */
	public static final int SO_TIMEOUT = 60 * 1000;
	
	/**
	 * Socket的连接超时设置
	 */
	public static final int CONNECT_TIMEOUT = 60 * 1000;
	
	/**
	 * HTTP请求头的contentType属性值，服务端通过它来判断走JSON2格式的序列化反序列化逻辑
	 */
	private static final String DEF_CONTENT_TYPE = "binary/json2-stream";
	
	/**
	 * 获取路由地址
	 * @deprecated
	 */
	public String getRoute(String url, String svcname, IData params) {
		return null;
	}
	
	
	/**
	 * 通过Socket模拟Http请求发送
	 * 将DataInput对象转换成JSON2的格式,并将返回的JSON2格式反序列化成DataOutput对象a
	 */
	public IDataOutput request(String url, String svcname, IDataInput input, int readTimeout, int connectTimeout) throws Exception {
		Json2ToIOData io = new Json2ToIOData();
		
		URL cacheUrl = getCacheURL(url);
		
		String host = cacheUrl.getHost();
		int port = cacheUrl.getPort();
		String contextRoot = cacheUrl.getPath();
		String charset = System.getProperty("wade.json2.charset", "UTF-8");
		io.setCharset(charset);
		
		String json = io.read(input);
		
		int soTimeout = readTimeout;
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
	 * 通过Socket模拟Http请求发送
	 * 将DataInput对象转换成JSON2的格式,并将返回的JSON2格式反序列化成DataOutput对象a
	 */
	public IDataOutput request(String url, String svcname, IDataInput input) throws Exception {
		return request(url, svcname, input, SO_TIMEOUT, CONNECT_TIMEOUT);
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
