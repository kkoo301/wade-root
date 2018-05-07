package com.ailk.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.ailk.common.config.GlobalCfg;

public final class HttpUtil {
	
	private HttpUtil() {}
	
	/**
	 * get response headers
	 * @param uri
	 * @param datastr
	 * @param headers
	 * @param header
	 * @param timeout
	 * @return
	 */
	public static final Header[] getResponseHeaders(String uri, String datastr, Map<String, String> headers, String header, int timeout) {
		HttpResponse response = post(uri, datastr, headers, timeout);
		return response.getHeaders(header);
	}
	
	/**
	 * get response
	 * @param uri
	 * @param datastr
	 * @param headers
	 * @param timeout
	 * @return
	 */
	public static final String getResponsesString(String uri, String datastr, Map<String, String> headers, int timeout) {
		HttpResponse response = post(uri, datastr, headers, timeout);
		HttpEntity entity = response.getEntity();
		BufferedReader in = null;
		StringBuilder buffer = new StringBuilder(500);
		if (entity != null) {
			try {
				in = new BufferedReader(new InputStreamReader(entity.getContent(), GlobalCfg.getCharset()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String line = null;
			try {
				while ((line = in.readLine()) != null) {
					buffer.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String result = buffer.toString();
		
		return result;
	}
	
	/**
	 * post
	 * @param uri
	 * @param datastr
	 * @param headers
	 * @param timeout
	 * @return
	 */
	public static final HttpResponse post(String uri, String datastr, Map<String, String> headers, int timeout) {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(HTTP.CONTENT_ENCODING, GlobalCfg.getCharset());
		
		HttpResponse response = null;
		HttpPost post = null;
		
		post = new HttpPost(uri);
		HttpParams postParams = post.getParams();
		post.setHeader("Content-type", "text/plain; charset=" + GlobalCfg.getCharset());
		post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
		post.setHeader("Connection", "close");
		
		postParams.setParameter("http.socket.timeout", new Integer(timeout));
		//设置连接超时时间(单位毫秒)     
		HttpConnectionParams.setConnectionTimeout(postParams, timeout);
		//设置读数据超时时间(单位毫秒)     
		HttpConnectionParams.setSoTimeout(postParams, timeout);
		
		
		if (headers != null) {
			Iterator<String> iter = headers.keySet().iterator();
			while (iter.hasNext()) {
				String header = iter.next();
				post.setHeader(header, headers.get(header));
			}
		}
		
		InputStreamEntity reqEntity = null;
		try {
			reqEntity = new InputStreamEntity(new ByteArrayInputStream(datastr.getBytes(GlobalCfg.getCharset())), -1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		reqEntity.setContentType("binary/octet-stream");
		reqEntity.setChunked(true);
		post.setEntity(reqEntity);
		
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		
		return response;
	}
	
	
	/**
	 * head
	 * @param uri
	 * @param datastr
	 * @param headers
	 * @param timeout
	 * @return
	 */
	public static final HttpResponse head(String uri, String key, String value, Map<String, String> headers, int timeout) {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(HTTP.CONTENT_ENCODING, GlobalCfg.getCharset());
		
		HttpResponse response = null;
		HttpHead head = null;
		
		head = new HttpHead(uri);
		HttpParams postParams = head.getParams();
		head.setHeader("Content-type", "text/plain; charset=" + GlobalCfg.getCharset());
		head.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
		head.setHeader("Connection", "close");
		
		postParams.setParameter("http.socket.timeout", new Integer(timeout));
		//设置连接超时时间(单位毫秒)
		HttpConnectionParams.setConnectionTimeout(postParams, timeout);
		//设置读数据超时时间(单位毫秒)
		HttpConnectionParams.setSoTimeout(postParams, timeout);
		
		if (headers != null) {
			Iterator<String> iter = headers.keySet().iterator();
			while (iter.hasNext()) {
				String header = iter.next();
				head.setHeader(header, headers.get(header));
			}
		}
		
		head.setHeader(key, value);
		
		try {
			response = client.execute(head);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		
		return response;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		//模拟预加载页面的处理脚本
		/*String uri = "http://localhost:8080/ae";
		String datastr = "";
		
		Header[] headers = getResponseHeaders(uri, datastr, null, "LoadingNumber", 1000);
		Map<String, String> header = new HashMap<String, String>();
		for(Header head :headers) {
			header.put(head.getName(), head.getValue());
		}
		getResponsesString(uri, datastr, header, 3000);*/
		
		
		/**
		String uri="http://localhost:8080/service/service";
		Map<String, String> header = new HashMap<String, String>();
		header.put("WADE-ServiceName", "SS_queryCustInfo");
		
		HttpResponse response = post(uri, "", header, 10);
		long s = System.currentTimeMillis();
		System.out.println(Arrays.asList(response.getHeaders("WADE-ServiceRoute")).toString() + ", cost time : "+(System.currentTimeMillis() - s));
		
		**/
	}

}
