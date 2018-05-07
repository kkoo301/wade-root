package com.ailk.search.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wade.container.server.Request;
import com.wade.container.server.handler.AbstractHandler;

import com.ailk.search.SearchMonitorResponse;

/**
 * Copyright: Copyright (c) 2014 Asiainfo
 * 
 * @className: SearchServerMonitorHandler
 * @description: 搜索服务端监控处理类
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2014-9-16
 */
public class SearchServerMonitorHandler extends AbstractHandler {

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		SearchMonitorResponse info = SearchServer.monitor();
		Map<String, SearchRecorder> statistics = info.getSearchStatistics();
		
		PrintWriter out = response.getWriter();
		
		out.print("BOOT_TIME=" + info.getBootTime() + '^');
		for (String key : statistics.keySet()) {
			SearchRecorder recorder = statistics.get(key);
			
			// SEARCH_CODE1=CNT,TTC^SEARCH_CODE2=CNT,TTC^
			out.print(key + "=" + recorder.getCnt() + ',' + recorder.getTtc() + '^');
		}
	}
}
