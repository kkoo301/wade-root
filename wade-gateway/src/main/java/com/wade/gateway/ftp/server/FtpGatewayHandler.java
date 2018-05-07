package com.wade.gateway.ftp.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.wade.gateway.ftp.Constants;
import com.wade.gateway.ftp.server.op.IOperation;
import com.wade.container.server.Request;
import com.wade.container.server.handler.AbstractHandler;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: FtpGatewayHandler
 * @description: FTP网关
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public final class FtpGatewayHandler extends AbstractHandler implements Constants {

	private static final Logger log = Logger.getLogger(FtpGatewayHandler.class);
	
	private static final Map<String, IOperation> OPERATIONS = new HashMap<String, IOperation>();
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		String opKey = request.getHeader(OP_KEY);
		String siteId = request.getHeader(SITE_ID);
		
        if (null == opKey) {
            log.warn("OP_KEY is null!");
            return;
        }
        
		try {
			IOperation op = OPERATIONS.get(opKey);
			if (null == op) {
				Class<?> clazz = Class.forName(opKey);
				op = (IOperation)clazz.newInstance();
				OPERATIONS.put(opKey, op);
			}
			op.execute(siteId, baseRequest, request, response);
		} catch (Exception e) {
			log.error("Operation Exception: " + opKey + " ", e);
		}
	}
	
}