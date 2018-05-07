package com.linkage.safe;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletResponse;

import com.linkage.safe.parameter.ParameterRequestWrapper;

/**
 * 
 * @author $Id: ISafeBrowser.java 1 2014-02-20 08:34:02Z huangbo $
 *
 */
public abstract class ISafeBrowser {
  //请求的验证状态
  final public static Long ValidData = 0L;
  final public static Long noValidData = 1L;
  final public static Long DirectReturn = 2L;

  //主键的验证类型
  public long validType = 0L;
	
  //初始化
  public abstract void init(FilterConfig config)
  	throws Exception;
  	
  //获取异常返回HTML
  public abstract String getExceptHtml();
 
  //检测请求
  public abstract Long validRequest(ParameterRequestWrapper request,HttpServletResponse response)
  	throws Exception;

  //执行doFilter之后的处理
  public void afterDoFilter(ParameterRequestWrapper request,HttpServletResponse response)
	throws Exception {};
  
  //释放
  public abstract void destroy()
	throws Exception;
    
}