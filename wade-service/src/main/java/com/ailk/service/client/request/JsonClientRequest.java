/**
 * 
 */
package com.ailk.service.client.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yifur
 *
 */
public class JsonClientRequest {
	
	private static final byte CR = '\r';
	private static final byte LF = '\n';
	private static final byte[] CRLF = { CR, LF };
	
	private static Map<String, URL> urls = new HashMap<String, URL>(5);
	private static final int SO_TIMEOUT = 60 * 1000;
	private static final int CONN_TIMEOUT = 60 * 1000;
	
	private static final String DEF_CONTENT_TYPE = "binary/json-stream;charset=UTF-8";
	
	public static String request(String url, String svcname, String data, String charset) {
		return request(url, svcname, data, charset, SO_TIMEOUT, DEF_CONTENT_TYPE, CONN_TIMEOUT);
	}
	
	public static String request(String url, String svcname, String data, String charset, int readTimeout, String contentType, int connectTimeout) {
		URL cacheUrl = null;
		
		try {
			cacheUrl = getCacheURL(url);
		} catch (Exception e) {
			throw new IllegalArgumentException("服务调用地址错误", e);
		}
		
		if (svcname == null || svcname.length() <= 0) {
			throw new IllegalArgumentException("服务名不能为空");
		}
		
		if (data == null || data.length() <= 0) {
			throw new IllegalArgumentException("服务入参不能为空");
		}
		
		try {
			return post(cacheUrl.getHost(), cacheUrl.getPort(), cacheUrl.getPath(), svcname, data, charset, readTimeout, contentType, connectTimeout);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("远程服务地址错误，" + cacheUrl, e);
		} catch (IOException e) {
			throw new IllegalArgumentException("网络异常，" + cacheUrl, e);
		} catch (Exception e) {
			throw new IllegalArgumentException("服务调用异常(JSON)", e);
		}
	}
	
	/**
	 * 通过Socket模拟HTTP请求将数据Post到服务端，返回指定字符集的字符串
	 * @param host
	 * @param port
	 * @param contextRoot
	 * @param svcname
	 * @param input
	 * @param charset
	 * @param readTimeout
	 * @param contentType
	 * @return
	 * @throws Exception
	 */
	public static String post(String host, int port, String contextRoot, String svcname, String input, String charset, int readTimeout, String contentType, int connectTimeout) throws Exception {
		// 获取Socket连接
		Socket socket = null;
		String output = null;
		
		OutputStream out = null;
		InputStream in = null;
		BufferedInputStream bis = null;
		
		try {
			socket = new Socket();
			
			SocketAddress addr = new InetSocketAddress(host, port);
			socket.connect(addr, connectTimeout);
			
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(readTimeout);
		} catch (UnknownHostException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
		try {
			out = socket.getOutputStream();
			in = socket.getInputStream();
			bis = new BufferedInputStream(in);
			
			byte[] data = input.getBytes(charset);
			writeRequest(out, host + ":" + port, contextRoot + "/" + svcname, data, charset, contentType);
			
			output = readResponse(baos, bis, charset);
			
			//处理结尾特殊字符 '\n' '\t' '\r' 及  空格
			int badCharLen = 0;
			int length = output.length();
			for (int i = length - 1; i >= 0; i--) {
				int ch = output.charAt(i);
				if (ch == 9 || ch == 13 || ch == 10 || ch == 32) {
					badCharLen ++;
					continue;
				}
				break;
			}
			
			if (length - badCharLen > 0)
				output = output.substring(0, length - badCharLen);
			
			return output;
		} catch (Exception e) {
			throw e;
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

	}
	

	/**
	 * 模拟发送Http请求
	 * 
	 * @param out
	 * @param host
	 * @param bytes
	 * @throws IOException
	 */
	private static void writeRequest(OutputStream out, String host, String contextRoot, byte[] bytes, String charset, String contentType)
			throws IOException {
		// 请求行,请求头的每一行都是以CRLF结尾的
		out.write(("POST " + contextRoot + " HTTP/1.1").getBytes());
		out.write(CRLF);

		// 请求头
		out.write(("Content-Type: " + contentType + ";charset=" + charset).getBytes());
		out.write(CRLF);
		out.write(("Accept-Language: " + charset).getBytes());
		out.write(CRLF);
		out.write(("Connection: close").getBytes());
		out.write(CRLF);
		out.write(("Host: " + host).getBytes());
		out.write(CRLF);
		out.write(("Content-Length: " + bytes.length).getBytes());
		out.write(CRLF);

		out.write(CRLF); // 单独的一行CRLF表示请求头的结束

		// 可选的请求体。GET方法没有请求体
		out.write(bytes);

		out.flush();
	}

	
	/**
	 * 按长度读取响应内容
	 * @param in
	 * @param contentLength
	 * @return
	 * @throws IOException
	 */
	private static byte[] readLengthBody(InputStream in, int contentLength) throws IOException {
		byte[] datas = new byte[contentLength];
		int cnt = 0;
		while (cnt < contentLength) {
			cnt += in.read(datas, cnt, (contentLength - cnt));
		}
		return datas;
	}

	/**
	 * 读取Chunked的请求响应里的Body
	 * Chunked格式是以每三行为一个完整的块，这三行分别是：
	 * 第一行内容为当前块的长度值（需转成16进制）；
	 * 第二行是正文内容，且长度是第一行读出的数据；
	 * 第三行是结束符/r/n，读完后通过in.available() > 0来判断是否还有后续的块
	 * @param in
	 * @param contentLength
	 * @return
	 * @throws IOException
	 */
	private static byte[] readChunkedBody(InputStream in, ByteArrayOutputStream baos) throws IOException {
		byte[] line = readLine(baos, in);
		int chunkSize = Integer.parseInt(new String(line), 16);
		
		ByteArrayOutputStream body = new ByteArrayOutputStream(1024);
		try {
			while (chunkSize > 0) {
				body.write(readLengthBody(in, chunkSize));
				
				if (in.available() > 0)
					readLine(baos, in);
				
				if (in.available() > 0) {
					line = readLine(baos, in);
					chunkSize = Integer.parseInt(new String(line), 16);
				} else {
					chunkSize = 0;
				}
			}
			body.flush();
			return body.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			body.close();
		}
	}

	/**
	 * 读取请求响应,并用Hessian将数据序列化成IDataOutput
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static String readResponse(ByteArrayOutputStream baos, InputStream in, String charset) throws IOException {
		// 响应正文
		byte[] body = null;
		
		// 读取状态行
		byte[] status = readStatusLine(baos, in);
		
		//如果第9位是2，即byte值为50 (HTTP/1.1 200 OK)
		if (status[9] != 50) {
			return null;
		}

		// 消息报头, Key 和  Value已转成小写
		Map<String, String> headers = readHeaders(baos, in);
		
		String contentLength = headers.get("content-length");
		String transferEncoding = headers.get("transfer-encoding");
		
		if ("chunked".equals(transferEncoding)) {
			body = readChunkedBody(in, baos);
		} else {
			int length = Integer.parseInt(contentLength);
			body = readLengthBody(in, length);
		}
		
		return new String (body, charset);
	}

	/**
	 * 读取请求返回的Head信息,并以Map返回,
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static Map<String, String> readHeaders(ByteArrayOutputStream baos, InputStream in) throws IOException {
		Map<String, String> headers = new HashMap<String, String>();

		String[] array;

		// 请求的Head和Body之间有个""字符,以此为分隔
		// 头部字段的名值都是以(冒号+空格)分隔的
		while (null != (array = readHeadLine(baos, in))) {
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
	private static byte[] readStatusLine(ByteArrayOutputStream baos, InputStream in) throws IOException {
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
	
	private static byte[] readLine(ByteArrayOutputStream baos, InputStream in) throws IOException {
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
	 * read line
	 * @param baos
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static String[] readHeadLine(ByteArrayOutputStream baos, InputStream in) throws IOException {
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
			array = new String[] {new String(Arrays.copyOfRange(rtn, 0, slip - 1)).toLowerCase(), new String(Arrays.copyOfRange(rtn, slip + 1, rtn.length)).toLowerCase()};
		}
		
		return array;
	}
	
	
	/**
	 * get cache url
	 * @param url
	 * @return
	 */
	private static URL getCacheURL(String url) throws MalformedURLException {
		URL cacheUrl = urls.get(url);
		if (cacheUrl == null) {
			try {
				cacheUrl = new URL(url);
				urls.put(url, cacheUrl);
			} catch (MalformedURLException e) {
				throw e;
			}
		}
		return cacheUrl;
	}
	
	public static void main(String[] args) {
		String data = "{\"NAME\":\"测试\", \"X_TRANS_CODE\":\"HelloService\", \"IN_MODE_CODE\":\"IL\", \"TRADE_CITY_CODE\":\"INTF\", \"TRADE_DEPART_ID\":\"00309\", \"TRADE_STAFF_ID\":\"IBOSS000\", \"TRADE_EPARCHY_CODE\":\"INTF\"}";
		String url = "http://127.0.0.1:8080/app/service";
		String svcname = "HelloService";
		String out = JsonClientRequest.request(url, svcname, data, "UTF-8");
		System.out.println("单行数据返回:" + out);
	}
}
