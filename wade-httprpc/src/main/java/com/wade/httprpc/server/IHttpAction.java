/**
 * $
 */
package com.wade.httprpc.server;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: IHttpAction.java
 * @description: 
 * 处理Http请求的实现类, 提供序列化及反序列化逻辑<br>
 * 注意: 需要自己处理
 * 
 * @version: v1.0.0
 * @author: liaosheng
 * @date: 2016-1-18
 */
public interface IHttpAction<Req extends Serializable, Res extends Serializable> {
	
	/**
	 * 将HttpRequest里的输入流转换成可序列化的对象<br>
	 * 重载示例:<br>
	 * <code>
	 * try {<br>
	 * 		//todo<br>
	 * } catch (Exception e) {<br>
	 * 		return error(request, e);<br>
	 * }
	 * </code>
	 * @param uri	Http请求的URI
	 * @param request	反序列化后的输入对象
	 * @return
	 */
	public Res execute(HttpServletRequest httpRequest, Req request);
	
	/**
	 * 处理异常信息
	 * @param request	通过HttpRequest里输入的流转换成的可序列化的对象, 如果反序列化失败, 则为NULL
	 * @param e	执行异常
	 * @return
	 */
	public Res error(Req request, Exception e);
	
}
