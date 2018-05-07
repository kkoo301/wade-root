package com.wade.gateway.ftp.server.op;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wade.container.server.Request;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: IOperation
 * @description: FTP操作接口
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-11-24
 */
public interface IOperation {
	void execute(String siteId, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
