/**
 * 
 */
package com.ailk.service.client.request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * @author yifur
 *
 */
public class HttpClientRequest {
	
	private static final Logger log = Logger.getLogger(HttpClientRequest.class);
	
	private static Map<String, URL> urls = new HashMap<String, URL>(5);
	
	/**
	 * request
	 * @param url
	 * @param svcname
	 * @param head
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List request(String url, String svcname, RequestHead head, Map data) {
		return request(url, svcname, head.getHead(), data);
	}

	/**
	 * request
	 * @param url
	 * @param svcname
	 * @param head
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List request(String url, String svcname, Map head, Map data) {
		List output = new ArrayList();

		URL cacheUrl = urls.get(url);
		if (cacheUrl == null) {
			try {
				cacheUrl = new URL(url);
				urls.put(url, cacheUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		try {
			output = post(url, createByteInputStream(svcname, head, data));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;
	}

	/**
	 * 将input用Hessian序列化成byte流
	 * 
	 * @param svcname
	 *            服务名
	 * @param input
	 *            输入
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static byte[] createByteInputStream(String svcname, Map head, Map data) throws IOException {

		head.put("X_TRANS_CODE", svcname);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		
		byte[] bytes = null;
		try {
			out.writeObject(head);
			out.writeObject(data);
			out.flush();
			
			bytes = bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}

		return bytes;
	}

	/**
	 * 发送Post请求
	 * 
	 * @param uri
	 *            服务地址
	 * @param bytes
	 *            请求数据流
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List post(String url, byte[] bytes) throws Exception {
		HttpClient client = new DefaultHttpClient(connectionManager, params);
		HttpPost post = new HttpPost(url);
		try {
			InputStreamEntity reqEntity = new InputStreamEntity(new ByteArrayInputStream(bytes), bytes.length);
			reqEntity.setContentType("binary/java-stream");
			//reqEntity.setChunked(true);
			post.setEntity(reqEntity);

			post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
			post.setHeader("Connection", "close");

			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != 200) {
				post.abort();
				return null;
			}

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				ObjectInputStream hi = new ObjectInputStream(new ByteArrayInputStream(EntityUtils.toByteArray(resEntity)));
				
				List data = null;
				try {
					Map head = (Map) hi.readObject();
					data = (List) hi.readObject();
					
					if (log.isDebugEnabled()) {
						log.debug("远程服务返回头：" + head.toString());
						log.debug("远程服务返回体：" + data.toString());
					}
					
					if (data != null && head != null) {
						if (data.isEmpty()) {
							data.add(head);
						} else {
							Map first = (Map) data.get(0);
							first.putAll(head);
						}
					}
					
				} catch (IOException e) {
					throw e;
				} finally {
					hi.close();
					resEntity.consumeContent();
				}
				return data;
			}
			return null;

		} catch (Exception e) {
			post.abort();
			throw e;
		}
	}

	public static void shutdown() {
		connectionManager.shutdown();
	}
	
	
	private static ThreadSafeClientConnManager connectionManager;
	private static HttpParams params;
	
	/**
	 * 连接超时时间
	 */
	private final static int CONNECT_TIMEOUT = 1 * 1000;

	/**
	 * 连接超时时间
	 */
	private final static int SO_TIMEOUT = 10 * 60 * 1000;

	/**
	 * HTTP连接池
	 */
	static {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		
		params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);  
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
		connectionManager = new ThreadSafeClientConnManager(params, registry);
	}
	
	public static void destroy() {
		connectionManager.shutdown();
	}
	
	
	
	/**
	 * main
	 * @param args
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		String url = "http://10.154.50.213:10003/service";
		Map head = new HashMap();
		
		head.put("TRADE_ROUTE_VALUE", "INTFS");
		head.put("TRADE_TERMINAL_ID", "10.154.92.35");
		head.put("TRADE_CITY_CODE", "INTF");
		head.put("ROUTE_EPARCHY_CODE", "0731");
		head.put("TRADE_ROUTE_TYPE", "00");
		head.put("TRADE_DEPART_ID", "16539");
		
		head.put("X_TRANS_CODE", "SYS.Probe.TestSvc");
		head.put("TRADE_DEPART_PASSWD", "Linkage123");
		head.put("IN_MODE_CODE", "2");
		head.put("TRADE_EPARCHY_CODE", "0731");
		head.put("CHANNEL_TRADE_ID", "E018201307161003111234sccc");
		head.put("SERIAL_NUMBER", "13873113605");
		
		head.put("TRADE_STAFF_ID", "ITFSM000");
		head.put("PROVINCE_CODE", "HNAN");
		
		head.put("CUST_NAME", "HNAN");
		
		List out = HttpClientRequest.request(url, "SYS.Probe.TestSvc", head, head);
		System.out.println("服务返回数据：" + out);
	}
}
