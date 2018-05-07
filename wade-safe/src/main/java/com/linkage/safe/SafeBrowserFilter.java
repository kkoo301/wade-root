package com.linkage.safe;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;

import javax.crypto.SecretKey;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.linkage.safe.parameter.ParameterRequestWrapper;
import com.linkage.safe.parameter.ParameterResponseWrapper;
import com.linkage.safe.util.Security;
import com.linkage.safe.util.ServerDetector;
import com.linkage.safe.util.ServerInfo;
import com.linkage.safe.util.WadeSafeLog;

/**
 * 
 * @author $Id: SafeBrowserFilter.java 683 2014-09-23 06:00:18Z xiedx $
 *
 */
public class SafeBrowserFilter implements Filter{
	private FilterConfig config;
	private static String buildTime="";
	private static String hostName="";
	private static WadeSafeLog wsl = null;
	private static String localName = null;
	private static SecretKey secretKey = null;
	
	private static boolean safeMode=true;
	private static boolean isDefuse=true;
	private static byte[] ExcpteHtml =null;
	public static String defaultEnc = "GBK";
	public static String staticKey = null;
	
	//安全传输
	private static boolean safeTrans = false;
	
	private static Class<?> clsSafeBrowser=null;
	private static Object 	objSafeBrowser = null;
	private static Method 	metdafterDoFilter=null;
	private static Method 	metdValidRequest=null;
	private static Method 	metdExHtml=null;
	private static Method 	metdInit=null;
	private static Method 	metdDestroy=null;
	private static Method 	metdSafeLog=null;
	private static Method   metdIsDefuse=null;
	
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		try{	
			//获取主机名,方便查问题;
			InetAddress ia =InetAddress.getLocalHost();
			hostName = ia.getHostName();
			
	        String[] servInfo=null;
			//获取服务信息-暂时只支持weblogic,tomcat
			if(ServerDetector.getServerId().equals("weblogic")){
				servInfo = ServerInfo.getWebLogicServInfo();
			}else if(ServerDetector.getServerId().startsWith("tomcat")){
				servInfo = ServerInfo.getTomcatServInfo();
			}
			String servPort="";//servIP=null,servName=null;
			if(servInfo!=null){
				//servName = servInfo[0];
				//servIP =  servInfo[1];
				servPort = servInfo[2];
			}
			//获取的是web.xml display-name
			localName = config.getServletContext().getServletContextName();
			//设置日志文件
			WadeSafeLog.setServName(localName,servPort);
			wsl = WadeSafeLog.getInstance();
			//获取ANT编译时间
			buildTime = this.getClass().getPackage().getImplementationVersion();
			//打印初始化日志
			if(wsl!=null) wsl.Log("The Wade-safe initiated!");
			if(wsl!=null) wsl.Log("Build Time:"+ buildTime);
			
			//如果过滤器参数中未配置safemode 默认为安全模式(safeMode=true)
			String strSafeMode = this.config.getInitParameter("SafeMode");
			if(strSafeMode!=null) safeMode = strSafeMode.equalsIgnoreCase("false")?false:true;
			if(wsl!=null) wsl.Log("SafeMode:"+safeMode);
			//获取默认字符集
			String strEnc = this.config.getInitParameter("DefaultEncoding");
			if(strEnc!=null) defaultEnc = strEnc;
			if(wsl!=null) wsl.Log("DefaultEncoding:"+defaultEnc);
			//获取固定密钥，有的话就使用固定KEY，没有就使用动态密钥
			staticKey = this.config.getInitParameter("StaticKey");
			if(staticKey==null || staticKey.equals("")){
				staticKey = "ailk-jmj";
			}
			secretKey = Security.getKey(staticKey);
			if(wsl!=null) wsl.Log( "static key:" + staticKey );
			
			//传输加密开关
			String strSafeTrans = this.config.getInitParameter("SafeTransfer");
			if(strSafeTrans!=null) safeTrans = strSafeTrans.equalsIgnoreCase("true")?true:false;
			if(wsl!=null) wsl.Log("SafeResponse:"+safeTrans);
			
			//初始化抽象类
			String strCPKclsName = this.config.getInitParameter("SafeBrowserCls");
			if(strCPKclsName==null) strCPKclsName = "com.linkage.safe.Wade3SafeBrowser";
			if(wsl!=null) wsl.Log("SafeBrowserCls:"+strCPKclsName);
			
			//获取类类型
			clsSafeBrowser=Class.forName(strCPKclsName);
			//创建类实例
			objSafeBrowser=clsSafeBrowser.newInstance();
			//获取方法
			metdValidRequest = clsSafeBrowser.getMethod("validRequest", ParameterRequestWrapper.class,HttpServletResponse.class);
			metdafterDoFilter = clsSafeBrowser.getMethod("afterDoFilter", ParameterRequestWrapper.class, HttpServletResponse.class);
			
			//如果metdafterDoFilter没重载就设置为null
			if(ISafeBrowser.class.getMethod("afterDoFilter", ParameterRequestWrapper.class, HttpServletResponse.class).equals(metdafterDoFilter)){
				metdafterDoFilter = null;
				if(wsl!=null) wsl.Log("don't need to invoke [afterDoFilter]");
			}
			
			//安全日志处理,加入try-catch 避免初始化出错 2014/09/23 xiedx
			try{
				metdSafeLog = clsSafeBrowser.getMethod("safeLog", ParameterRequestWrapper.class, HttpServletResponse.class, String.class, String.class);
				if(ISafeBrowser.class.getMethod("safeLog", ParameterRequestWrapper.class, HttpServletResponse.class, String.class, String.class).equals(metdSafeLog)){
					metdSafeLog = null;
					if(wsl!=null) wsl.Log("don't need to invoke [safeLog]");
				}	
			}catch(Exception ex){
				if(wsl!=null){
					wsl.Log("init safeLog method error:" + ex.getMessage());
				}else{
					ex.printStackTrace();
				}
			}
			
			//获取异常自定义的HTML代码
			metdExHtml=clsSafeBrowser.getMethod("getExceptHtml");
			String strExHtml = (String)metdExHtml.invoke(objSafeBrowser);
			if(strExHtml!=null && strExHtml.length()>0){
				ExcpteHtml = strExHtml.getBytes(defaultEnc);
			}else{
				ExcpteHtml = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=gbk\" /><link href=\"/component/ecl/skin/defaultSkin/defaultColor/color.css\" rel=\"stylesheet\" type=\"text/css\" media=\"screen\"/><link href=\"/component/ecl/skin/defaultSkin/defaultColor/colorPublic.css\" rel=\"stylesheet\" type=\"text/css\" media=\"screen\"/><title>请使用安全浏览器</title></head><body><div class=\"e_wrapper\"><div class=\"e_wrapper2\"></div></div><!-- 弹窗 开始 --><div class=\"c_popup\" id=\"popup\" style=\"display:block;\"><iframe frameborder=\"0\"></iframe><div class=\"c_popupPopup\" style=\"width:400px; margin-left:-200px; height:200px; margin-top:-110px;\"><div class=\"c_popupFillet\"><div class=\"c_popupFilletTop\"><div class=\"c_popupFillet11\"></div><div class=\"c_popupFillet12\"></div></div></div><div class=\"c_popupTop\"><div class=\"c_popupTitle\">服务器端提示</div><div class=\"c_popupFct\"></div></div><div class=\"c_popupWrapper\"><div class=\"c_popupBody\"><div class=\"l_padding\"><!-- 内容 开始 --><div class=\"c_warning\"><div class=\"warning\"><div class=\"minHeight\"></div><div class=\"tip\"><ul><li>请使用安全浏览器</li><li>请使用安全浏览器</li><li>请使用安全浏览器</li></ul></div><div class=\"content\">您的工号限制必须使用安全浏览器<br/>如果您还未安装安全浏览器<br />请点击：<a href=\"#DOWNLOAD_URL\">[下载]</a></div></div></div><!-- 内容 结束 --></div></div></div><div class=\"c_popupBottom\"></div><div class=\"c_popupFillet\"><div class=\"c_popupFilletBottom\"><div class=\"c_popupFillet21\"></div><div class=\"c_popupFillet22\"></div></div></div></div></div><!-- 弹窗 结束 --> <div id=\"exceDescPageContent\" style=\"visibility:hidden;position:absolute;\"> <div class=\"x-dlg-hd\">&nbsp;</div> <div class=\"x-dlg-bd\" style=\"text-align:center;\"><div id=\"exceDescMsgContent\" class=\"x-errormsg\" contentheight=\"80px\" style=\"text-align:left;\"><table id=\"exceDescPageContent\" style=\"border:none;width:100%;height100%;\"><tr><td valign=\"top\" style=\"height:33px;padding-left:42px;word-break:break-all;word-wrap:break-word;\">请使用安全浏览器!<br>您的工号限制必须使用安全浏览器!<br>如果您还未安装安全浏览器!<br>请点击：<a href=\"#DOWNLOAD_URL\">[下载]</a>!</td></tr></table></div> </div> <div clas=\"x-dlg-ft\"></div></div></body><script language=\"javascript\"><!--System.onLoad(function(){ Wade.excedesc.initExceDesc();});//--></script></html>".getBytes(defaultEnc);
			}
			
			//获取自定义类销毁方法
			metdDestroy = clsSafeBrowser.getMethod("destroy");
			//执行自定义类初始化方法
			metdInit = clsSafeBrowser.getMethod("init",FilterConfig.class);
			metdInit.invoke(objSafeBrowser,config);
			
			if(wsl!=null) wsl.Log("The Wade-Safe was successfully initialized!");
		} catch (Exception e) {e.printStackTrace();}
	}
	
	
	//@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterchain) throws IOException, ServletException {

		//设置返回服务信息请求头
		addWadeHeader((HttpServletResponse)servletResponse);

		//读取全局配置BizEnv.wade_safe_defuse, 2014/09/23 xiedx
		try{
			metdIsDefuse=clsSafeBrowser.getMethod("isDefuse");
			isDefuse = (Boolean)metdIsDefuse.invoke(objSafeBrowser);
		}catch(Exception ex){
			if(wsl!=null){
				wsl.Log("invoke isDefuse method error:" + ex.getMessage());
			}else{
				ex.printStackTrace();
			}
		}	
		
		//非安全模式直接放过
		if(!safeMode || !isDefuse){
			filterchain.doFilter(servletRequest, servletResponse);
			return ;
		}
		
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		ParameterRequestWrapper request = new ParameterRequestWrapper((HttpServletRequest)servletRequest);
	
		Long validStat=0L;
		try{
			//开发人员自定义方法
			validStat = (Long)metdValidRequest.invoke(objSafeBrowser, request,response);
		}catch (Exception e) {
		    e.printStackTrace();
		}
		
		//无需验证，如首页，退出等请求
		if(validStat == ISafeBrowser.noValidData) {
			filterchain.doFilter(request, response);
			if(metdafterDoFilter !=null){
				try{
					metdafterDoFilter.invoke(objSafeBrowser,request,response);
				}catch(Exception e){e.printStackTrace();};
			}
			request.clearParamMap();
			return;
		}
		//直接返回类型,啥都不处理
		if(validStat == ISafeBrowser.DirectReturn){
			request.clearParamMap();
			return ;
		}	
		
		//如果配置了安全传输，POST数据必须为密文
		if(safeTrans){
			if(request.getMethod().equalsIgnoreCase("post")){
				String wcd = request.getHeader("WADE-Client-Date");
				if(wcd == null || wcd.equals("")){
						responseExcept(request, response, "", "");
						request.clearParamMap();
						return ;
				}
			}
		}
		
		//令牌验证逻辑
		if(!checkRequest(request,response)){
			return ;
		}
		
		//传输加密
	    if(safeTrans){
		    ParameterResponseWrapper ParamResponse= new ParameterResponseWrapper(response);
				filterchain.doFilter(request, ParamResponse);
				ParamResponse.flushBuffer();
				ParamResponse.finish();
	    }else{
	    	filterchain.doFilter(request, response);
	    }
            
		if(metdafterDoFilter !=null){
			try{
				metdafterDoFilter.invoke(objSafeBrowser,request,response);
			}catch(Exception e){e.printStackTrace();};
		}
		request.clearParamMap();
	}
	
	public void destroy() {
		try{
			metdDestroy.invoke(objSafeBrowser);
		}catch(Exception e){e.printStackTrace();}
		if(wsl!=null){
			wsl.Log("The Wade-Safe was successfully destory!");
			wsl.Close();
		}
	}
	
	public static void responseExcept(ParameterRequestWrapper request,HttpServletResponse response,String strSafeGet,String strSafePost) throws IOException{
		response.setContentType("text/html;charset=" + defaultEnc);
		ServletOutputStream sos =response.getOutputStream();
		sos.write(ExcpteHtml);
		sos.flush();
		if(wsl == null) return;
		
		String safeLog = null;
		if(metdSafeLog!=null){
			try{
				safeLog = (String)metdSafeLog.invoke(objSafeBrowser,request,response,strSafeGet,strSafePost);
			}catch(Exception e){e.printStackTrace();}
		}
		
		String errLog = "";
		errLog = String.format("\n-------开始记录验证失败日志------\n");
		if(safeLog!=null) errLog = errLog + safeLog + "\n";
		errLog = errLog +("URL:" + request.getRequestURI()+ '?' + request.getQueryString() + "\n" );
		errLog = errLog +("Method:"+ request.getMethod() + "\n");
		errLog = errLog +("SAFE_QUERYSTRING:"+strSafeGet + "\n");
		if(strSafeGet!=null ){
			String strMd5QryStr = Security.strDecrypt(secretKey,strSafeGet);
			errLog = errLog +("decrypt(SAFE_QUERYSTRING):"+strMd5QryStr + "\n");
			errLog = errLog +("encrypt(request.getQueryString()):"+Security.md5s(request.getQueryString()) + "\n");
		}
		errLog = errLog +("SAFE_POST:"+strSafePost + "\n");
		if(strSafePost!=null ){
			String strMd5Post = Security.strDecrypt(secretKey,strSafePost);
			errLog = errLog +("decrypt(SAFE_POST):"+strMd5Post + "\n");
			errLog = errLog +("encrypt(request.PostData):"+request.postDataMd5 + "\n");
			errLog = errLog +("decrypt(request.asyncSafePost[head]):"+Security.strDecrypt(secretKey,request.asyncSafePost) + "\n");
		}
		errLog = errLog +("PostLen:"+ request.getContentLength() + "\n");
		if(request.getRequestData()!=null){
			errLog = errLog +("PostDate:" + new String(request.getRequestData())  + "\n");
		}
		errLog = errLog +("-------结束记录验证失败日志------\n");
		wsl.Log(errLog);
	}

	
	//设置服务信息请求
	public static void addWadeHeader(HttpServletResponse response){
		//返回增加主机名,方便查问题
		response.addHeader("WADE-Host-Name", hostName);
		//返回wade-safe.jar编译时间
		response.addHeader("WADE-Safe-Version", buildTime);
		//返回IP
		//response.addHeader("WADE-Server-IP", localIP);
		//返回服务名
		response.addHeader("WADE-Server-Name", localName);
		//返回端口
		//response.addHeader("WADE-Server-Port", localPort);
		}

	//请求验证逻辑
	public static boolean checkRequest(ParameterRequestWrapper request,HttpServletResponse response)  throws IOException
	{
		//获取令牌验证串
		String strSafeQryStr = request.getHeader("SAFE_QUERYSTRING");
		String strSafePost = request.getHeader("SAFE_POST");

		//没有SAFE_QUERYSTRING也没有SAFE_POST,肯定就没有使用安全浏览器
		if((strSafeQryStr == null || strSafeQryStr.equals(""))&&(strSafePost == null || strSafePost.equals("")) ){
			responseExcept(request,response,null,null);
			return false;
		}
		//如果有strSafeQryStr,就表示需要验证QuerySting;
		if(strSafeQryStr != null){
			String strMd5QryStr = Security.strDecrypt(secretKey,strSafeQryStr);
			if(strMd5QryStr==null){
				responseExcept(request,response,strSafeQryStr,strSafePost);
				request.clearParamMap();
				return false;
			}
			//if(!Security.md5b(request.getQueryString().getBytes("ISO8859_1")).equals(strMd5QryStr)){
			if(!Security.md5s(request.getQueryString()).equals(strMd5QryStr)){
							responseExcept(request,response,strSafeQryStr,strSafePost);
					    request.clearParamMap();
					    return false;
			}
		}
		/*
		//必须这样处理,否则非GBK会异常
		char cQryStr[] = request.getQueryString().toCharArray();
		byte bQryStr[] = new byte[cQryStr.length];
		for(int i = 0; i < cQryStr.length;i++){
			bQryStr[i] = (byte) cQryStr[i];
		}
		if(!Security.md5b(bQryStr).equals(strMd5QryStr)){
				    responseExcept(request,response,strSafeQryStr,strSafePost);
				    request.clearParamMap();
				    return ;
		}
		*/
		
		//如果有strSafePost,并且bufLen大于0,就表示有需要验证POST;
		if(strSafePost!=null || request.bufLen>0){
			String strMd5SafePost = Security.strDecrypt(secretKey,strSafePost);
			if(strMd5SafePost==null){
				responseExcept(request,response,strSafeQryStr,strSafePost);
				request.clearParamMap();
				return false;
			}
			if(strMd5SafePost.equals("async_ajax_posts")){
				if(!request.postDataMd5.equals(Security.strDecrypt(secretKey,request.asyncSafePost))){
					responseExcept(request,response,strSafeQryStr,strSafePost);
					request.clearParamMap();
					return false;
				}
			}else if(!request.postDataMd5.equals(strMd5SafePost)){
				responseExcept(request,response,strSafeQryStr,strSafePost);
				request.clearParamMap();
				return false;
			}
		}
		return true;
	}
	
}

