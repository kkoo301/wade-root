package com.ailk.service.client.hessian;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.ailk.common.BaseException;
import com.ailk.common.Constants;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.service.client.IProtocalClient;
import com.ailk.service.hessian.io.Hessian2Input;
import com.ailk.service.hessian.io.Hessian2Output;
import com.ailk.service.hessian.io.SerializerFactory;

/**
 *
 * @author yifur
 *
 */
public class HessianClient implements IProtocalClient {

	private static final byte CR = '\r';
	private static final byte LF = '\n';
	private static final byte[] CRLF = { CR, LF };

	private static SerializerFactory inputFactory = new SerializerFactory(IDataInput.class.getClassLoader());
	private static SerializerFactory outputFactory = new SerializerFactory(IDataOutput.class.getClassLoader());
	private static Map<String, URL> urls = new HashMap<String, URL>(5);

	private static String CLIENT_IP = null;
	private static String CLIENT_MAC = null;
	private static String SERVER_KEY = null;

	public static final int SO_TIMEOUT = 600 * 1000;
	public static final int CONNECT_TIMEOUT = 1000;

	public String getRoute(String url, String svcname, IData params) {
		return null;
	}

	public IDataOutput request(String url, String svcname, IDataInput input, int soTimeout) throws Exception {
		return request(url, svcname, input, soTimeout, CONNECT_TIMEOUT);
	}

	public IDataOutput request(String url, String svcname, IDataInput input) throws Exception {
		return request(url, svcname, input, SO_TIMEOUT, CONNECT_TIMEOUT);
	}

	public IDataOutput request(String url, String svcname, IDataInput input, int soTimeout, int connectTimeout) throws Exception {
		URL cacheUrl = getCacheURL(url);

		int sTimeout = soTimeout;
		if (sTimeout <= 0) {
			sTimeout = SO_TIMEOUT;
		}

		int cTimeout = connectTimeout;
		if (cTimeout <= 0) {
			cTimeout = CONNECT_TIMEOUT;
		}

		return post(cacheUrl.getHost(), cacheUrl.getPort(), cacheUrl.getPath(), svcname, input, soTimeout, connectTimeout);
	}

	/**
	 * 发送Head请求,根据客户端信息从服务端获取验证码
	 * @param host
	 * @param port
	 * @param contextRoot
	 * @param key
	 * @param value
	 * @param resKey
	 * @return
	 */
	String head(String host, int port, String contextRoot, String key, String resKey) {
		Socket socket = null;
		OutputStream out = null;
		InputStream in = null;
		BufferedInputStream bis = null;
		String serverKey = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream(100);

		try {
			socket = new Socket(host, port);

			socket.setTcpNoDelay(true);
			socket.setSoTimeout(60 * 1000);

			out = socket.getOutputStream();
			in = socket.getInputStream();

			bis = new BufferedInputStream(in);

			CLIENT_IP = getIPAddrss(socket.getInetAddress());
			CLIENT_MAC = getMacAddress();

			writeHead(out, host + ":" + port, contextRoot, key, CLIENT_IP);

			Map<String, String> header = readHeaders(baos, in);

			serverKey = header.get(resKey);

			bis.close();
			out.close();
			baos.close();
		} catch (Exception e) {
			try {
				if (bis != null) bis.close();
				if (out != null) out.close();
				if (baos != null) baos.close();
			} catch (IOException ex) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return serverKey;
	}


	/**
	 * 通过Socket模拟HTTP请求将数据Post到服务端,数据传输采用Hessian序列化
	 *
	 * @param host
	 * @param port
	 * @param bytes
	 * @return
	 */
	private IDataOutput post(String host, int port, String contextRoot, String svcname, IDataInput input, int timeout, int connectTimeout) throws Exception {
		IDataOutput output = new DataOutput();

		// 获取Socket连接
		Socket socket = null;
		try {
			socket = new Socket();

			SocketAddress addr = new InetSocketAddress(host, port);
			socket.connect(addr, connectTimeout);

			socket.setTcpNoDelay(true);
			socket.setSoTimeout(timeout);

			input.getHead().put(Constants.X_CLIENT_IP, CLIENT_IP);
			input.getHead().put(Constants.X_CLIENT_MAC, CLIENT_MAC);
			input.getHead().put(Constants.X_CLIENT_KEY, SERVER_KEY);
		} catch (IOException e) {
			throw new IOException("服务调用地址[http://" + host + ":" + port + contextRoot + "/" + svcname + "]", e);
		}

		OutputStream out = null;
		InputStream in = null;
		BufferedInputStream bis = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
		try {
			out = socket.getOutputStream();
			in = socket.getInputStream();
			bis = new BufferedInputStream(in);

			writeRequest(out, host + ":" + port, contextRoot, svcname, createByteInputStream(svcname, input));

			output = readResponse(baos, bis);
		} catch (Exception e) {
			throw new Exception("网络异常或服务调用超时:" + svcname, e);
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (out != null) {
					out.close();
				}
				if (socket != null) {
					socket.close();
				}
				baos.close();
			} catch (IOException e) {
				throw e;
			}
		}

		return output;
	}


	/**
	 * 将key=value拼成HttpHead,发送到服务端
	 * @param out
	 * @param host
	 * @param contextRoot
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	private static void writeHead(OutputStream out, String host, String contextRoot, String key, String value) throws IOException {
		// 请求行,请求头的每一行都是以CRLF结尾的
		out.write(("HEAD " + contextRoot + " HTTP/1.1").getBytes());
		out.write(CRLF);

		// 请求头
		out.write(("Content-type: text/plain").getBytes());
		out.write(CRLF);
		out.write(("Connection: close").getBytes());
		out.write(CRLF);
		out.write(("Host: " + host).getBytes());
		out.write(CRLF);
		out.write(("Client-IP: " + CLIENT_IP).getBytes());
		out.write(CRLF);
		out.write(("Client-MAC: " + CLIENT_MAC).getBytes());
		out.write(CRLF);
		out.write((key + ": " + value).getBytes());
		out.write(CRLF);

		out.write(CRLF); // 单独的一行CRLF表示请求头的结束
	}

	/**
	 * 模拟发送Http请求
	 *
	 * @param out
	 * @param host
	 * @param bytes
	 * @throws IOException
	 */
	private void writeRequest(OutputStream out, String host, String contextRoot, String svcname, byte[] bytes)
			throws IOException {
		// 请求行,请求头的每一行都是以CRLF结尾的
		out.write(("POST " + contextRoot + "/" + svcname + " HTTP/1.1").getBytes());
		out.write(CRLF);

		// 请求头
		out.write(("Content-Type: binary/hessian-stream").getBytes());
		out.write(CRLF);
		out.write(("Accept-Language: utf-8").getBytes());
		out.write(CRLF);
		out.write(("Connection: close").getBytes());
		out.write(CRLF);
		out.write(("Host: " + host).getBytes());
		out.write(CRLF);
		out.write(("Content-Length: " + bytes.length).getBytes());
		out.write(CRLF);
		out.write(("Client-IP: " + CLIENT_IP).getBytes());
		out.write(CRLF);
		out.write(("Client-MAC: " + CLIENT_MAC).getBytes());
		out.write(CRLF);

		out.write(CRLF); // 单独的一行CRLF表示请求头的结束

		// 可选的请求体。GET方法没有请求体
		out.write(bytes);

		out.flush();
	}

	/**
	 * 将请求数据用Hessian序列化成Byte数组
	 *
	 * @param svcname
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	private byte[] createByteInputStream(String svcname, IDataInput input) throws IOException {
		input.getHead().put("X_TRANS_CODE", svcname);

		ByteArrayOutputStream bos = null;
		Hessian2Output out = null;
		byte[] bytes = null;

		try {
			bos = new ByteArrayOutputStream();
			out = new Hessian2Output(bos);

			out.setSerializerFactory(inputFactory);

			out.writeObject(input);
			out.flush();

			bytes = bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != out) {
				out.close();
			}
		}

		return bytes;
	}

	/**
	 * 读取请求响应里的Body
	 *
	 * @param in
	 * @param contentLength
	 * @return
	 * @throws IOException
	 */
	private static byte[] readResponseBody(InputStream in, int contentLength) throws IOException {
		byte[] datas = new byte[contentLength];
		int cnt = 0;
		while (cnt < contentLength) {
			cnt += in.read(datas, cnt, (contentLength - cnt));
		}
		return datas;
	}

	/**
	 * 读取请求响应,并用Hessian将数据序列化成IDataOutput
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private IDataOutput readResponse(ByteArrayOutputStream baos, InputStream in) throws IOException {
		// 读取状态行
		byte[] status = readStatusLine(baos, in);

		//如果第9位是2，即byte值为50 (HTTP/1.1 200 OK)
		if (status[9] != 50) {
			/*DataOutput output = new DataOutput();
			output.getHead().put(Constants.X_RESULTCODE, BaseException.CODE_SVC_RESPONSE_5);
			output.getHead().put(Constants.X_RESULTINFO, BaseException.INFO_SVC_RESPONSE_5 + "[" + new String(status).substring(9, 12) + "]");
			return output;*/
			throw new IOException("远程服务响应异常");
		}

		// 消息报头
		Map<String, String> headers = readHeaders(baos, in);

		int contentLength = Integer.valueOf(headers.get("Content-Length"));

		// 可选的响应正文
		byte[] body = readResponseBody(in, contentLength);

		Hessian2Input hi = new Hessian2Input(new ByteArrayInputStream(body));
		hi.setSerializerFactory(outputFactory);
		return (IDataOutput) hi.readObject(IDataOutput.class);
	}

	/**
	 * 读取请求返回的Head信息,并以Map返回,
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private Map<String, String> readHeaders(ByteArrayOutputStream baos, InputStream in) throws IOException {
		Map<String, String> headers = new HashMap<String, String>();

		String[] array;

		// 请求的Head和Body之间有个""字符,以此为分隔
		// 头部字段的名值都是以(冒号+空格)分隔的
		while (null != (array = readLine(baos, in))) {
			headers.put(array[0], array[1]);
		}

		return headers;
	}

	/**
	 * 读取请求返回的状态,即返回结果的第一行,该方法必须是第一个read
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private byte[] readStatusLine(ByteArrayOutputStream baos, InputStream in) throws IOException {
		byte[] rtn = null;

		boolean eol = false;
		byte[] b = new byte[1];
		while (in.read(b, 0, 1) != -1) {
			if (13 == b[0]) {
				eol = true;
				continue;
			} else {
				if ((eol) && (10 == b[0])) {
					break;
				}
				eol = false;
			}

			baos.write(b, 0, 1);
		}

		rtn = baos.toByteArray();
		baos.reset();

		return rtn;
	}

	/**
	 * read error
	 * @param baos
	 * @param in
	 * @return
	 * @throws IOException
	 */
	byte[] readError(ByteArrayOutputStream baos, InputStream in) throws IOException {
		byte[] rtn = null;
		byte[] b = new byte[1];
		while (in.read(b, 0, 1) != -1) {
			baos.write(b, 0, 1);
		}

		rtn = baos.toByteArray();

		baos.reset();
		return rtn;
	}

	/**
	 * read line
	 * @param baos
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private String[] readLine(ByteArrayOutputStream baos, InputStream in) throws IOException {
		byte[] rtn = null;

		int index = 0;
		int slip = 0;
		boolean eol = false;
		byte[] b = new byte[1];
		while (in.read(b, 0, 1) != -1) {
			index ++;
			if (13 == b[0]) {
				eol = true;
				continue;
			} else {
				if ((eol) && (10 == b[0])) {
					index = 0;
					break;
				}
				if (58 == b[0] && slip == 0) {
					slip = index;
				}
				eol = false;
			}

			baos.write(b, 0, 1);
		}

		rtn = baos.toByteArray();

		baos.reset();

		String[] array = null;

		if (slip > 0) {
			array = new String[] {new String(Arrays.copyOfRange(rtn, 0, slip - 1)), new String(Arrays.copyOfRange(rtn, slip + 1, rtn.length))};
		}

		return array;
	}

	/**
	 * 获取客户端的Mac地址
	 *
	 * @param ia
	 * @return
	 * @throws SocketException
	 */
	private static String getMacAddress() throws SocketException {
		StringBuffer sb = new StringBuffer();

		try {
			Enumeration<NetworkInterface> iter = NetworkInterface.getNetworkInterfaces();

			while (iter.hasMoreElements()) {
				NetworkInterface ni = iter.nextElement();

				byte[] macs = ni.getHardwareAddress();

				if (macs != null && macs.length > 0 && ni.isUp() && !ni.isVirtual()) {
					sb.append(ni.getName()).append(":");
					sb.append(byteToHexString(macs));
					sb.append(",");
					sb.deleteCharAt(sb.length() - 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 获取客户端IP地址
	 *
	 * @param ia
	 * @return
	 */
	private static String getIPAddrss(InetAddress ia) {
		return ia.getHostAddress();
		//return byteToHexString(ia.getAddress());
	}

	/**
	 * 将字节数组转成16进制的String
	 *
	 * @param macs
	 * @return
	 */
	private static String byteToHexString(byte[] macs) {
		StringBuffer sb = new StringBuffer();
		for (byte b : macs) {
			String s = Integer.toHexString(b);
			int len = s.length();
			for (int i = len; i < 8; i++) {
				s = "0" + s;
			}
			sb.append(s.substring(6));
		}
		return sb.toString();
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

}
