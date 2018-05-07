/**
 * $
 */
package com.wade.httprpc.client.conn.factory;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wade.httprpc.client.conn.factory.HttpSocketConnection;
import com.wade.httprpc.client.conn.HttpConnection;
import com.wade.httprpc.client.conn.HttpErrorCallback;
import com.wade.httprpc.client.conn.config.HttpConfigure;
import com.wade.httprpc.client.conn.pool.SocketPool;
import com.wade.httprpc.util.SerializeUtil;


/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: HttpSocketConnection.java
 * @description: 通过Socket发送HTTP报文
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-22
 */
public class HttpSocketConnection implements HttpConnection {
	
	private static final Logger log = Logger.getLogger(HttpSocketConnection.class);
	
	private static final byte CR = '\r';
	private static final byte LF = '\n';
	private static final byte[] CRLF = { CR, LF };
	
	private HttpConfigure configure = null;
	private InetSocketAddress endpoint = null;
	private boolean isError = false;
	
	/**
	 * 当使用KeepAlive模式时, 存在Socket被动断开的可能, 当发生时重试三次, 并Sleep 1 ms
	 */
	//private int socketKeepAliveErrorTimes = 0;
	
	public HttpSocketConnection (HttpConfigure configure) {
		this.configure = configure;
		this.endpoint = new InetSocketAddress(configure.getHost(), configure.getPort());
	}
	
	/**
	 * @return the configure
	 */
	public HttpConfigure getConfigure() {
		return configure;
	}
	
	
	@Override
	public HttpErrorCallback getCallBack() {
		return null;
	}
	
	/**
	 * 创建Socket对象
	 * @return
	 * @throws IOException
	 */
	@Override
	public Socket createSocket() throws IOException {
		SocketPool pool = configure.getSocketPool();
		Socket socket = null;
		
		if (null != pool) {
			socket = pool.borrowSocket();
		} else {
			socket = new Socket();
		}
		
		if (socket.isConnected() && !socket.isClosed()) {
			
		} else {
			socket.connect(endpoint, configure.getConnectTimeout());
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(configure.isKeepAlive());
			socket.setSoTimeout(configure.getSoTimeout());
		}
		return socket;
	}
	
	
	@Override
	public void closeSocket(Socket socket) throws IOException {
		if (null == socket)
			return ;
		
		SocketPool pool = configure.getSocketPool();
		if (null != pool) {
			pool.returnSocket(socket);
		}
		
		socket.close();
	}
	
	
	
	@Override
	public String get(String request) throws IOException {
		try {
			return socketSend(request, "", String.class);
		} catch (IOException e) {
			if ("0".equals(e.getMessage())) {
				return get(request);
			} else {
				throw e;
			}
		}
	}
	
	/**
	 * 默认为字符串的请求和响应数据
	 */
	@Override
	public String post(String request) throws IOException {
		return post(request, String.class);
	}
	
	
	/**
	 * 支持Java序列化的数据传输, 并针对KeepAlive的模式重发3次失败的数据
	 */
	@Override
	public <T extends Serializable>T post(Serializable request, Class<T> clazz) throws IOException {
		try {
			return socketSend(null, request, clazz);
		} catch (IOException e) {
			if ("0".equals(e.getMessage())) {
				return post(request, clazz);
			} else {
				throw e;
			}
		}
	}
	
	
	/**
	 * 支持Java序列化的数据
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable>T socketSend(String query, Serializable request, Class<T> clazz) throws IOException {
		Socket socket = null;
		byte[] response = null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
		try {
			socket = createSocket();
			
			OutputStream out = socket.getOutputStream();
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			
			byte[] data = SerializeUtil.serialize(request);
			
			//configure.setDataType(clazz.getSimpleName().toUpperCase());
			writeRequest(out, query, data);
			
			boolean isText = "java.lang.String".equals(clazz.getName());
			response = readResponse(baos, bis, isText);
			//释放资源
			out.close();
			bis.close();
			
			if (isError) {
				HttpErrorCallback callback = configure.getCallback();
				if (null != callback) {
					return (T) callback.callback(SerializeUtil.deserialize(response, Exception.class));
				}
			}
			
			return isText ? (T)new String(response, configure.getCharset()) : SerializeUtil.deserialize(response, clazz);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				closeSocket(socket);
				baos.close();
			} catch (IOException e) {
				throw e;
			}
		}
	
	}
	
	/**
	 * 向Socket流写入请求数据
	 * @param out
	 * @param bytes
	 * @throws IOException
	 */
	protected void writeRequest(OutputStream out, String query, byte[] bytes) throws IOException {
		boolean isPost = null == query || query.trim().length() == 0;
		// 请求行,请求头的每一行都是以CRLF结尾的
		if (isPost) {
			out.write(("POST " + configure.getPath() + " HTTP/1.1").getBytes());
		} else {
			out.write(("GET " + configure.getPath() + "?" + query + " HTTP/1.1").getBytes());
		}
		out.write(CRLF);

		// 请求头
		out.write(("Content-Type: " +configure.getContentType()).getBytes());
		out.write(CRLF);
		out.write(("Accept-Language: " + configure.getCharset()).getBytes());
		out.write(CRLF);
		out.write(("Request-Type: " + configure.getDataType()).getBytes());
		out.write(CRLF);
		
		Map<String, String> header = configure.getHeader();
		Iterator<String> keys = header.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			String value = header.get(key);
			if (null != value) {
				out.write((key + ": " + value).getBytes());
				out.write(CRLF);
			}
		}
		
		if (configure.isKeepAlive()) {
			out.write("Connection: Keep-Alive".getBytes());
		} else {
			out.write("Connection: close".getBytes());
		}
		out.write(CRLF);
		out.write(("Host: " + configure.getHost()).getBytes());
		out.write(CRLF);
		
		if (null != bytes && bytes.length > 0)
			out.write(("Content-Length: " + bytes.length).getBytes());
		else 
			out.write(("Content-Length: 0").getBytes());
		
		out.write(CRLF);

		out.write(CRLF); // 单独的一行CRLF表示请求头的结束
		
		// 可选的请求体。GET方法没有请求体
		if (null != bytes)
			out.write(bytes);

		out.flush();
	}
	
	/**
	 * 从Socket流读出byte数组
	 * @param baos
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected byte[] readResponse(ByteArrayOutputStream baos, BufferedInputStream in, boolean isText) throws IOException {
		// 响应正文
		byte[] body = null;
		
		// 读取状态行
		byte[] status = readStatusLine(baos, in);
		if (status.length < 9) {
			throw new IOException("0");
		}
		
		//如果第9位是2，即byte值为50 (HTTP/1.1 200 OK)
		if (status[9] != 50) {
			byte[] info = readError(baos, in);
			
			String error = new String(info, configure.getCharset());
			throw new IOException(String.format("HTTP服务端%s响应状态异常:%s", configure.getPath(), error));
		}

		// 消息报头, Key 和  Value已转成小写
		Map<String, String> headers = readHeaders(baos, in);
		
		String contentLength = headers.get("content-length");
		String transferEncoding = headers.get("transfer-encoding");
		String responseError = headers.get("response-error");

		if (log.isDebugEnabled()) {
			log.debug("HTTP响应头信息:" + headers.toString());
		}
		
		if (null == contentLength || contentLength.length() == 0) {
			body = readBody(in, isText);
		} else {
			if ("chunked".equals(transferEncoding)) {
				body = readChunkedBody(in, baos);
			} else {
				int length = Integer.parseInt(contentLength);
				body = readLengthBody(in, length);
			}
		}
		
		// 判断响应是否为异常
		if ("true".equals(responseError)) {
			this.isError = true;
		}
		
		return body;
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
	private byte[] readChunkedBody(InputStream in, ByteArrayOutputStream baos) throws IOException {
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
	 * 读取所有内容
	 * @param in
	 * @param contentLength
	 * @return
	 * @throws IOException
	 */
	private static byte[] readBody(BufferedInputStream in, boolean isText) throws IOException {
		int buffSize = 1024;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(buffSize);
		
		try {
			byte[] buff = new byte[buffSize];
			
			int size = 0;
			int MARK = -1;
			
			if (isText) {
				
				while ((size = in.read(buff)) != -1) {
					baos.write(Arrays.copyOfRange(buff, 0, size - 1));
				}
				
			} else {
				while ((size = in.read(buff)) != -1) {
					
					if (MARK == -83) {
						continue;
					}
					
					//先读一行，然后才是消息体
					for (int i = 0; i < size; i++) {
						//判断开始行
						if (MARK < 1) {
							if (13 == buff[i]) {
								MARK = 0;
								continue;
							} else {
								if ((MARK == 0) && (10 == buff[i])) {
									MARK = 1;
									continue;
								}
							}
						} else {
							//判断结束行
							if (13 == buff[i]) {
								MARK = 2;
								baos.write(buff[i]);
								continue;
							} else {
								if ((MARK == 2) && (10 == buff[i])) {
									MARK = -83;
									continue;
								} else {
									MARK = 1;
									baos.write(buff[i]);
									continue;
								}
							}
						}
					}
				}
			}
			
			baos.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			baos.close();
		}
		
		byte[] data = baos.toByteArray();
		
		if (data.length == 0)
			return data;
		
		return isText ? data : Arrays.copyOfRange(data, 0, data.length - 1);
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
	 * 读取请求就向的Header
	 * @param baos
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
			headers.put(array[0].toLowerCase(), array[1].toLowerCase());
		}
		return headers;
	}
	
	/**
	 * 读取响应的请求状态
	 * @param baos
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
	
	/**
	 * 读取异常信息
	 * @param baos
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static byte[] readError(ByteArrayOutputStream baos, InputStream in) throws IOException {
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
	 * 读取一行数据
	 * @param baos
	 * @param in
	 * @return
	 * @throws IOException
	 */
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
	 * 读取HTTP响应头
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

}
