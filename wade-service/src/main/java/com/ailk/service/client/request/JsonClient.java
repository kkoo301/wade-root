package com.ailk.service.client.request;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class JsonClient {
	private static Map<String, URL> urls = new HashMap(5);
	private static ThreadSafeClientConnManager connectionManager;
	private static HttpParams params;
	private static final int CONNECT_TIMEOUT = 1000;
	private static final int SO_TIMEOUT = 600000;

	public static String request(String url, String svcname, String data, String charset) {
		String output = "";
		URL cacheUrl = (URL) urls.get(url);
		if (cacheUrl == null) {
			try {
				cacheUrl = new URL(url);
				urls.put(url, cacheUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		try {
			url = url + "/" + svcname;
			output = post(url, data.getBytes(charset), charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	private static String post(String url, byte[] bytes, String charset) throws Exception {
		HttpClient client = new DefaultHttpClient(connectionManager, params);
		HttpPost post = new HttpPost(url);
		try {
			InputStreamEntity reqEntity = new InputStreamEntity(new ByteArrayInputStream(bytes), bytes.length);
			reqEntity.setContentType("application/json;charset=utf8");
			reqEntity.setContentEncoding("utf8");
			reqEntity.setChunked(true);
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
				byte[] array = EntityUtils.toByteArray(resEntity);
				resEntity.consumeContent();
				return new String(array, charset);
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

	static {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		params = new BasicHttpParams();
		params.setParameter("http.connection.timeout", Integer.valueOf(1000));
		params.setParameter("http.socket.timeout", Integer.valueOf(600000));
		connectionManager = new ThreadSafeClientConnManager(params, registry);
	}

	public static void destroy() {
		connectionManager.shutdown();
	}

	public static void main(String[] args) {
		String url = "http://10.131.156.155:20000/service";
		String request = "{\"IDTYPE\":\"00\",\"IDVALUE\":\"610630199201011458\",\"X_TRANS_CODE\":\"SS.AbilityPlatSVC.checkComeInNet\"}";
		String data = request(url, "SS.AbilityPlatSVC.checkComeInNet", request, "utf-8");
		System.out.println("服务返回数据：" + data);
	}
}
