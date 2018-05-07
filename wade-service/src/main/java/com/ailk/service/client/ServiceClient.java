package com.ailk.service.client;

import com.ailk.common.BaseException;
import com.ailk.common.Constants;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.Utility;
import com.ailk.service.client.hessian.HessianClient;
import com.ailk.service.client.json.Json2Client;

public class ServiceClient {

	private static IProtocalClient client = null;
	
	public static final int SERVICE_PROTOCOL_JSON = 0;
	public static final int SERVICE_PROTOCOL_HESSIAN = 1;
	
	private static IProtocalClient jsonClient = new Json2Client();
	private static IProtocalClient hessianClient = new HessianClient();
	
	static {
		client = jsonClient;
	}
	
	public static void setProtocol(int protocol) {
		switch (protocol) {
			case SERVICE_PROTOCOL_JSON:
				client = jsonClient;
				break;
			case SERVICE_PROTOCOL_HESSIAN:
				client = hessianClient;
				break;
			default:
				client = jsonClient;
				break;
		}
	}

	/**
	 * 服务调用
	 * @param url
	 * @param svcname
	 * @param input
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagination) throws Exception {
		return call(url, svcname, input, pagination, false, Json2Client.SO_TIMEOUT, Json2Client.CONNECT_TIMEOUT);
	}
	
	/**
	 * 服务调用
	 * @param url
	 * @param svcname
	 * @param input
	 * @param pagination
	 * @param soTimeout
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagination, int soTimeout) throws Exception {
		return call(url, svcname, input, pagination, false, soTimeout, Json2Client.CONNECT_TIMEOUT);
	}
	
	/**
	 * 服务调用
	 * @param url
	 * @param svcname
	 * @param input
	 * @param pagination
	 * @param soTimeout
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagination, int soTimeout, int connectTimeout) throws Exception {
		return call(url, svcname, input, pagination, false, soTimeout, connectTimeout);
	}
	
	
	/**
	 * 服务调用
	 * @param url
	 * @param svcname
	 * @param input
	 * @param pagin
	 * @param iscatch
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagin, boolean iscatch) throws Exception {
		return call(url, svcname, input, pagin, iscatch, Json2Client.SO_TIMEOUT, Json2Client.CONNECT_TIMEOUT);
	}
	
	/**
	 * 服务调用
	 * @param url
	 * @param svcname
	 * @param input
	 * @param pagin
	 * @param iscatch
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagin, boolean iscatch, int soTimeout) throws Exception {
		return call(url, svcname, input, pagin, iscatch, soTimeout, Json2Client.CONNECT_TIMEOUT);
	}


	/**
	 * 服务调用
	 * 
	 * 1.如果服务在当前JVM上则不跨网络调用
	 * 2.如果指定参数isremote为true则走远程调用
	 * 
	 * @param url
	 * @param svcname
	 * @param input
	 * @param pagin
	 * @param iscatch
	 * @param isremote
	 * @return
	 * @throws Exception
	 */
	
	/**
	 * 服务调用
	 * 1.如果服务在当前JVM上则不跨网络调用
	 * 2.如果指定参数isremote为true则走远程调用
	 * 3.soTimeout 设置read的超时时间，单位毫秒
	 * 4.connectTimeout 设置建立连接的超时时间，单位毫秒
	 * @param url
	 * @param svcname
	 * @param input
	 * @param pagin
	 * @param iscatch
	 * @param soTimeout
	 * @param connectTimeout
	 * @return
	 * @throws Exception
	 */
	public static IDataOutput call(String url, String svcname, IDataInput input, Pagination pagin, boolean iscatch, int soTimeout, int connectTimeout) throws Exception {
		IDataOutput output = new DataOutput();

		if (svcname == null || "".equals(svcname))
			throw new Exception("服务名为空");

		if (input == null)
			throw new Exception("输入参数为空");

		// 设置分页参数
		if (pagin != null) {
			input.setPagination(pagin);
		}

		try {
			IData head = input.getHead();
			String inModeCode = head.getString(Constants.IN_MODE_CODE);
			if (null == inModeCode || inModeCode.length() <= 0) {
				head.put(Constants.IN_MODE_CODE, "0");
			}
			head.put(Constants.X_TRANS_CODE, svcname);
			
			//四个公共参数
			head.put("TRADE_STAFF_ID", head.getString("TRADE_STAFF_ID", head.getString("STAFF_ID")));
			head.put("TRADE_CITY_CODE", head.getString("TRADE_CITY_CODE", head.getString("CITY_CODE")));
			head.put("TRADE_DEPART_ID", head.getString("TRADE_DEPART_ID", head.getString("DEPART_ID")));
			head.put("TRADE_EPARCHY_CODE", head.getString("TRADE_EPARCHY_CODE", head.getString("STAFF_EPARCHY_CODE")));

			output = remoteCall(url, svcname, input, head.getString("TRADE_STAFF_ID"), soTimeout, connectTimeout);
			String xResultCode = output.getHead().getString(Constants.X_RESULTCODE);
			String xResultInfo = output.getHead().getString(Constants.X_RESULTINFO);
			
			if (null == xResultCode)
				xResultCode = "-1";
			
			if (!"0".equals(xResultCode)) {
				throw new BaseException(xResultCode, null, xResultInfo);
			}
		} catch (Exception e) {
			Utility.print(e);

			Throwable t = Utility.getBottomException(e);
			if (!iscatch) {
				throw new BaseException(t);
			} else {
				output = new DataOutput();
				IData head = output.getHead();
				head.putAll(input.getHead());
				
				if (e instanceof BaseException) {
					BaseException be = (BaseException) e;
					head.put(Constants.X_RESULTCODE, be.getCode());
					head.put(Constants.X_RESULTINFO, be.getInfo());
				} else {
					head.put(Constants.X_RESULTCODE, "-1");
					head.put(Constants.X_RESULTINFO, t.getMessage());
				}
			}
			
		}

		return output;
	}
	
	/**
	 * 远程调用
	 * @param url
	 * @param svcname
	 * @param input
	 * @param staffId
	 * @param soTimeout
	 * @param connectTimeout
	 * @return
	 * @throws Exception
	 */
	private static final IDataOutput remoteCall(String url, String svcname, IDataInput input, String staffId, int soTimeout, int connectTimeout) throws Exception {
		return client.request(url, svcname, input, soTimeout, connectTimeout);
	}

	
	public static void main(String[] args) {
		String url = "http://127.0.0.1:8080/app/service";
		
		ServiceClient.setProtocol(ServiceClient.SERVICE_PROTOCOL_HESSIAN);
		
		IDataInput input = new DataInput();
		input.getData().put("NAME", "zhang3");
		try {
			System.out.println(ServiceClient.call(url, "HelloService", input, null, false));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
