/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年7月27日
 * 
 * Just Do IT.
 */
package com.wade.svf.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wade.svf.biz.BaseSVF;
import com.wade.svf.flow.FlowConfigure;
import com.wade.svf.flow.exception.FlowErr;
import com.wade.svf.flow.exception.FlowException;
import com.wade.svf.flow.executor.IFlowExecutor;
import com.wade.svf.flow.filter.IFlowFilter;
import com.wade.svf.server.http.RequestHead;
import com.wade.svf.server.serializer.DataSerializerFactory;
import com.wade.svf.server.serializer.IDataSerializer;

/**
 * @description 流程执行
 */
public class FlowService {
	
	private static final Logger log = LoggerFactory.getLogger(FlowService.class);

	/**
	 * 请求请求头内容
	 */
	private Map<String, String> context = new HashMap<String, String>(20);
	
	/**
	 * 添加过滤器
	 */
	private List<IFlowFilter> filters = new ArrayList<IFlowFilter>(5);
	
	/**
	 * 流程名
	 */
	private String flow = null;

	public FlowService(String flow, Map<String, String> context) {
		this.flow = flow;
		this.context = context;
	}

	/**
	 * @return the flow
	 */
	public String getFlow() {
		return flow;
	}
	
	/**
	 * @param filters the filters to set
	 */
	public void setFilters(List<IFlowFilter> filters) {
		this.filters = filters;
	}

	/**
	 * 执行请求，返回字符数组，并转换所有异常<br>
	 * 1、根据请求头反序列化请求对象；<br>
	 * 2、执行流程调用；<br>
	 * 3、异常转换；<br>
	 * 4、根据请求头序列化响应对象；<br>
	 * 
	 * @param context
	 * @param in
	 * @return
	 * @throws FlowException
	 */
	public byte[] execute(InputStream in) throws FlowException {
		// 数据序列化与格式匹配
		String dataType = this.context.get(RequestHead.ContentType.getCode());
		if (null == dataType || dataType.trim().length() == 0) {
			throw new FlowException(FlowErr.flow10011.getCode(), FlowErr.flow10011.getInfo(getFlow(), dataType));
		}
		IDataSerializer serializer = DataSerializerFactory.getFactory().getSerializer(dataType);
		if (null == serializer) {
			throw new FlowException(FlowErr.flow10011.getCode(), FlowErr.flow10011.getInfo(getFlow(), dataType));
		}

		// 数据转换
		Serializable source = serializer.serialize(read(in));
		Map<String, Object> request = serializer.getData(source);
		request.putAll(context);
		
		if (log.isDebugEnabled()) {
			log.debug("接收流程{}请求入参:{}", new Object[] {getFlow(), request});
		}
		
		// 根据流程名创建流程对象
		BaseSVF svf = new BaseSVF(getFlow());
		
		// 添加流程过滤器
		for (IFlowFilter filter : filters) {
			FlowConfigure.addFilter(filter);
		}
		
		// 流程执行
		IFlowExecutor executor = FlowConfigure.getExecutor();
		Map<String, Object> response = executor.execute(svf, request);
		
		// 数据反序列化
		source = serializer.toData(response);
		return serializer.deserialize(source);
	}

	/**
	 * 从输入流读取Byte数组
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private byte[] read(InputStream in) throws FlowException {
		BufferedInputStream bufin = new BufferedInputStream(in);
		int buffSize = 1024;
		ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);

		try {
			byte[] temp = new byte[buffSize];
			int size = 0;
			while ((size = bufin.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
		} catch (Exception e) {
			throw new FlowException(FlowErr.flow10013.getCode(), FlowErr.flow10013.getInfo(getFlow(), e.getMessage()), e);
		} finally {
			try {
				bufin.close();
			} catch (Exception e2) {
				throw new FlowException(FlowErr.flow10013.getCode(), FlowErr.flow10013.getInfo(getFlow(), e2.getMessage()), e2);
			}
		}

		byte[] content = out.toByteArray();
		return content;
	}

}
