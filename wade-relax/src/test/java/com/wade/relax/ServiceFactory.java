package com.wade.relax;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ailk.org.apache.commons.io.IOUtils;
import com.wade.relax.registry.consumer.ConsumerRuntime;
import com.wade.relax.registry.consumer.SockSite;
import com.wade.relax.tm.context.XContext;

public class ServiceFactory {

	private static final Logger LOG = Logger.getLogger(ServiceFactory.class);

	private static final Set<String> localServiceNames = new HashSet<String>();

	public static final void setLocalServiceNames(String localServiceName) {
		localServiceNames.add(localServiceName);
	}

	public static final void call(String serviceName, Map<String, String> params) throws Exception {

		if (localServiceNames.contains(serviceName)) {
			LOG.debug("调用本地服务: " + serviceName);

			Class clazz = Class.forName(serviceName);
			Method m = clazz.getDeclaredMethod("doService", new Class[] { Map.class });
			m.invoke(clazz.newInstance(), params);

		} else {
			LOG.debug("调用远程服务: " + serviceName);
			SockSite sock = ConsumerRuntime.nextAvailableAddress(serviceName);
			String tid = XContext.getInstance().getTID();
			remoteCall(sock.toString(), tid, serviceName);
		}

	}

	private static final boolean remoteCall(String strUrl, String tid, String serviceName) throws Exception {

		LOG.debug("开始远程服务调用, 地址: " + strUrl + ", TID: " + tid + ", ServiceName: " + serviceName);
		
		InputStream is = null;
		HttpURLConnection httpURLConnection = null;
		try {

			URL url = new URL(strUrl + "?TID=" + tid + "&ServiceName=" + serviceName);
			URLConnection urlConnection = url.openConnection();
			httpURLConnection = (HttpURLConnection) urlConnection;
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setRequestProperty("Accept", "*/*");
			httpURLConnection.setRequestProperty("Connection", "close"); // 短链接
			httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
			httpURLConnection.connect();

			int statusCode = httpURLConnection.getResponseCode();

			if (LOG.isDebugEnabled()) {
				LOG.debug(url.toString() + ", statusCode:" + statusCode);
			}

			is = httpURLConnection.getInputStream();

			if (200 == statusCode) {
				return true;
			} else {
				String exceptionInfo = getContent(is);
				LOG.error(exceptionInfo);
				throw new Exception(exceptionInfo);
			}

		} finally {
			httpURLConnection.disconnect();
			IOUtils.closeQuietly(is);
		}

	}

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

}
