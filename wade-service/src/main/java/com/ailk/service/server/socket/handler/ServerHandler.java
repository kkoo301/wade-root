package com.ailk.service.server.socket.handler;

import java.io.EOFException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.rpc.org.jboss.netty.channel.ChannelHandlerContext;
import com.ailk.rpc.org.jboss.netty.channel.ExceptionEvent;
import com.ailk.rpc.org.jboss.netty.channel.MessageEvent;
import com.ailk.rpc.org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.ailk.common.Constants;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.ClazzUtil;
import com.ailk.service.ServiceManager;
import com.ailk.service.invoker.ServiceInvoker;
import com.ailk.service.protocol.impl.ServiceEntity;
import com.ailk.service.server.bcc.ServiceBCCIntercept;
import com.ailk.service.server.sec.IServiceSecurity;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: ServerHandler
 * @description: 
 * 
 * @version: v1.0.0
 * @author: xiedx
 * @date: 2013-7-4
 */
public class ServerHandler extends SimpleChannelUpstreamHandler {

	private static Logger log = Logger.getLogger(ServerHandler.class);	
	
	private static IServiceSecurity sec = null;
	private static ServiceBCCIntercept bcc = null;
	
	private static String secClazzName = GlobalCfg.getProperty("service.sec.clazz");
	private static String bccClazzName = GlobalCfg.getProperty("service.bcc.clazz");
	
	static{
		if(secClazzName !=null && !"".equals(secClazzName)){
			sec = (IServiceSecurity) ClazzUtil.load(secClazzName, null);
		}
		
		if(bccClazzName != null && !"".equals(bccClazzName)){
			bcc = (ServiceBCCIntercept) ClazzUtil.load(bccClazzName, null);
		}
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e) {
		IDataInput input = null;
		IDataOutput output = null;
		
		long seq = 0;
		int heartbeat = 0;
		
		String serviceName = null;
		String clientIp = null;
		String clientMac = null;
		
		try {
			input = (IDataInput) e.getMessage();
			IData head = input.getHead();
			
			seq = head.getLong("_SOCKET_PROXY_SEQ");
			heartbeat = head.getInt("_SOCKET_HEART_BEAT");
			
			if(heartbeat>0){
				IData outHead = new DataMap();
				outHead.put("_SOCKET_HEART_BEAT", heartbeat);
				output = new DataOutput(outHead, new DatasetList());
				writeData(e,output,seq);
				return;
			}
			
			serviceName = head.getString(Constants.X_TRANS_CODE);
			String inModeCode = head.getString(Constants.IN_MODE_CODE);
			
			clientIp = head.getString(Constants.X_CLIENT_IP);
			clientMac = head.getString(Constants.X_CLIENT_MAC);
			
			String transSerial = head.getString(Constants.X_TRANSSERIAL);

			if (doSecurity(input)) {
				output = doService(input);
				writeData(e,output,seq);
				return;
			} else {
				IData outHead = new DataMap();
				outHead.put(Constants.IN_MODE_CODE, inModeCode);
				outHead.put(Constants.X_RESULTCODE, "-999");
				outHead.put(Constants.X_RESULTCOUNT, "0");
				outHead.put(Constants.X_RESULTINFO, "未经授权的服务[" + serviceName + "]");
				outHead.put(Constants.X_RESULTSIZE, "0");
				output = new DataOutput(outHead, new DatasetList());
				writeData(e,output,seq);
				return;
			}
		} catch (EOFException ex) {
			ex.printStackTrace();
			
			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, "-997");
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO,
					"非法请求,当前操作已记录在案！[" + new Date(System.currentTimeMillis()).toString() + "][" + clientIp + "]["
							+ clientMac + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(e,output,seq);
			return;
		} catch (Exception ex) {
			ex.printStackTrace();

			IData outHead = new DataMap();
			outHead.put(Constants.X_RESULTCODE, "-996");
			outHead.put(Constants.X_RESULTCOUNT, "0");
			outHead.put(Constants.X_RESULTINFO, "服务调用失败[" + serviceName + "]");
			outHead.put(Constants.X_RESULTSIZE, "0");
			output = new DataOutput(outHead, new DatasetList());
			writeData(e,output,seq);
			return;
		} finally {
			//if (e != null)
			//	e.getChannel().close();
		}
	}
	
	
	private IDataOutput doService(IDataInput input) throws Exception {
		IData head = input.getHead();

		String serviceName = head.getString(Constants.X_TRANS_CODE);
		String inModeCode = head.getString(Constants.IN_MODE_CODE);
		String transSerial = head.getString(Constants.X_TRANSSERIAL);
		IDataOutput output = null;

		if (bcc != null) {
			ServiceEntity entity = ServiceManager.find(serviceName);
			if (entity != null) {
				if (bcc.invokeBefore(serviceName, inModeCode, transSerial)) {
					output = ServiceInvoker.mainServiceInvoke(serviceName, input);
					bcc.invokeAfter(serviceName, inModeCode, transSerial);
				} else {
					IData outHead = new DataMap();
					outHead.put(Constants.IN_MODE_CODE, inModeCode);
					outHead.put(Constants.X_RESULTCODE, "-999");
					outHead.put(Constants.X_RESULTCOUNT, "0");
					outHead.put(Constants.X_RESULTINFO, "服务调用并发控制,请稍候再试");
					outHead.put(Constants.X_RESULTSIZE, "0");
					output = new DataOutput(outHead, new DatasetList());
				}
			} else {
				IData outHead = new DataMap();
				outHead.put(Constants.IN_MODE_CODE, inModeCode);
				outHead.put(Constants.X_RESULTCODE, "-996");
				outHead.put(Constants.X_RESULTCOUNT, "0");
				outHead.put(Constants.X_RESULTINFO, "服务不存在[" + serviceName + "]");
				outHead.put(Constants.X_RESULTSIZE, "0");
				output = new DataOutput(outHead, new DatasetList());
			}
		} else {
			output = ServiceInvoker.mainServiceInvoke(serviceName, input);
		}

		return output;
	}

	
	/**
	 * do security
	 * 
	 * @param input
	 * @return
	 */
	private boolean doSecurity(IDataInput input) {
		if (sec == null)
			return true;

		IData head = input.getHead();

		String clientKey = head.getString(Constants.X_CLIENT_KEY);
		return sec.isValidKey(clientKey, "");
	}
	
	
	private void writeData(MessageEvent e, IDataOutput output, long seq){
		output.getHead().put("_SOCKET_PROXY_SEQ", seq);
		e.getChannel().write(output);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		log.warn("Unexpected exception from downstream." + e.getCause());
		e.getChannel().close();
	}
	
}