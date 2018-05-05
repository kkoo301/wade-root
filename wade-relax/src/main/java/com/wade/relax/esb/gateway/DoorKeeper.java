package com.wade.relax.esb.gateway;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_GATEWAY;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.config.GlobalCfg;
import net.sf.json.JSONObject;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.relax.registry.consumer.ConsumerRuntime;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Values;

/**
 * Copyright: Copyright (c) 2016 Asiainfo
 *
 * @desc: 看门
 * @auth: steven.zhou
 * @date: 2016-11-24
 */
public class DoorKeeper {
	
	private static final Logger LOG = LoggerFactory.getLogger(DoorKeeper.class);
	private static final Set<String> DEPLOY_HOSTS = new HashSet<String>();
	
	static {
		String deployHosts = GlobalCfg.getProperty("esb.gateway.deploy.hosts", "127.0.0.1");
		DEPLOY_HOSTS.addAll(Arrays.asList(StringUtils.split(deployHosts, ',')));
	}
	
    /**
     * 心跳探针处理
     * 
     * @param ctx
     * @param
     */
    public static final boolean isProbeAccess(String uri, ChannelHandlerContext ctx) {
    	
    	if (!uri.startsWith("/probe.jsp")) {
    		return false;
    	}
    	
		FullHttpResponse response = null;
		
		if (uri.equals("/probe.jsp?command=GracefullyShutdown")) {
		
			String address = ctx.channel().remoteAddress().toString();
			String strIp = address.substring(1, address.lastIndexOf(":"));
			
			if (DEPLOY_HOSTS.contains(strIp) || 0 == DEPLOY_HOSTS.size()) {
				
				// 优雅下线
				ServiceGateway.isOpenState = false;
				ServiceGateway.offline();
				
				response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("esb instance offline success!".getBytes()));
				
			} else {
				
				String info = "deny illegal access, " + strIp + " not in global.properties esb.gateway.deploy.hosts";
		        LOG.warn(info);
		        response = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN, Unpooled.wrappedBuffer(info.getBytes()));
		        response.headers().set(CONNECTION, Values.CLOSE);    	        
		        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		        return true;
		        
			}
	        
		} else if (uri.equals("/probe.jsp?command=UpdateLocalServiceMapping")) {
			
			// notify-esb.sh脚本通知ESB，ESB更新服务名与中心名映射清单。
			ConsumerRuntime.updateLocalServiceMapping();
			
			// 让ESB检查临时节点是否存在,如果不存在就创建一个。
			ServiceGateway.checkOnline();
			
			String info = "UpdateLocalServiceMapping OK";
			response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(info.getBytes()));
			
		} else if (uri.equals("/probe.jsp?command=getServiceMapping")) {
			String info = ConsumerRuntime.getServiceMapping().toString();
			response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(info.getBytes()));
		} else if (uri.equals("/probe.jsp?command=EsbMonitor")) {
			String statData = getStatData();
			response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(statData.getBytes()));
		} else { // 监控检查
			
			if (ServiceGateway.isOpenState) { // RUNNING
				String info = System.getProperty("isPrepared") + " " + System.getProperty("wade.server.name");
				response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(info.getBytes()));
			} else { // NOT PREPARED
				response = new DefaultFullHttpResponse(HTTP_1_1, BAD_GATEWAY, Unpooled.wrappedBuffer("esb instance is not prepared!".getBytes()));
			}
			    
		}
		
		response.headers().set(CONNECTION, Values.CLOSE);    	        
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        
		return true;
		
    }
    
    /**
     * 返回统计数据JSON格式
     * 
     * @return
     */
    private static final String getStatData() {
    	
    	StringBuilder statData = new StringBuilder(5000);
    	
    	statData.append("{");
    	statData.append("\"srcStat\" : " + JSONObject.fromObject(EsbMonitor.getSrcStat()) + ", ");
    	statData.append("\"dstStat\" : " + JSONObject.fromObject(EsbMonitor.getDstStat()));
    	statData.append("}");
    	
    	return statData.toString();
    }
}

