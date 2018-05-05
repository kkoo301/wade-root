package com.wade.relax.tm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.org.apache.commons.io.IOUtils;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: RemoteXSupervise
 * @description:
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-12-15
 */
public final class RemoteXSupervise {

	private static final Logger LOG = LoggerFactory.getLogger(RemoteXSupervise.class);

	/**
	 * 发送提交指令
	 * 
	 * @param address
	 * @param tid
	 * @return
	 */
	public static final boolean commit(String address, String tid) {
		String url = buildXServletUrl(address);
		return sendXCmd(url, tid, Command.COMMIT);
	}

	/**
	 * 发送回滚指令
	 * 
	 * @param address
	 * @param tid
	 * @return
	 */
	public static final boolean rollback(String address, String tid) {
		String url = buildXServletUrl(address);
		return sendXCmd(url, tid, Command.ROLLBACK);
	}

	/**
	 * 发送事务指令
	 * 
	 * @param strUrl
	 * @param tid
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	private static final boolean sendXCmd(String strUrl, String tid, String cmd) {

		InputStream is = null;
		HttpURLConnection httpURLConnection = null;
		try {

			URL url = new URL(strUrl + "?tid=" + tid + "&cmd=" + cmd);
			URLConnection urlConnection = url.openConnection();
			httpURLConnection = (HttpURLConnection) urlConnection;
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setRequestProperty("Accept", "*/*");
			httpURLConnection.setRequestProperty("Connection", "close"); // 短链接
			httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
			httpURLConnection.connect();

			int statusCode = httpURLConnection.getResponseCode();
			
			LOG.debug("{}, statusCode:{}", url.toString(), statusCode);

			is = httpURLConnection.getInputStream();
			
			if (200 == statusCode) {
				return true;
			} else {
				String exceptionInfo = getContent(is);
				LOG.error(exceptionInfo);
			}

		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			httpURLConnection.disconnect();
			IOUtils.closeQuietly(is);
		}

		return false;
	}

	/**
	 * 获取响应内容
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	private static final String getContent(InputStream is) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuilder buff = new StringBuilder(200);

		String line;
		while ((line = in.readLine()) != null) {
			buff.append(line);
		}
		
		IOUtils.closeQuietly(in);
		return buff.toString();

	}

	/**
	 * 构建事务URL地址
	 * 
	 * @param address
	 * @return
	 */
	private static final String buildXServletUrl(String address) {
		return "http://" + address + "/xservlet";
	}
	
	public static void main(String[] args) {
		System.out.println("==");
		boolean s = commit("www.baidu.com", "asdfasdf");
		System.out.println(s);
	}
}
