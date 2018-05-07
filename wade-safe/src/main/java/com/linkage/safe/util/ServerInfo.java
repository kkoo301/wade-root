package com.linkage.safe.util;

import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.naming.InitialContext;

/**
 * 
 * @author $Id: ServerInfo.java 1 2014-02-20 08:34:02Z huangbo $
 *
 */
public class ServerInfo {

	//获取weblogic服务信息
	public static String[] getWebLogicServInfo(){
		String servInfo[]=null;
		InitialContext ctx = null;
		try{
			ObjectName service = new ObjectName("com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean");   
			ctx = new InitialContext();   
			MBeanServer server = (MBeanServer)ctx.lookup("java:comp/env/jmx/runtime");   
			ObjectName rt =  (ObjectName)server.getAttribute(service,"ServerRuntime");
			servInfo = new String[3];
			servInfo[0] = (String)server.getAttribute(rt,"Name");
			servInfo[1] = (String)server.getAttribute(rt,"ListenAddress");
			servInfo[2] = String.valueOf(server.getAttribute(rt,"ListenPort"));  
		}catch(Exception e){
			e.printStackTrace();
		}finally{ 
			try{
				if(ctx!=null){ 
					ctx.close();
				}
			}catch(Exception e){e.printStackTrace();}
		}
		return servInfo;
	}
	
	public static String[] getTomcatServInfo(){
		//获取本地MBeanServer;
        MBeanServer mBeanServer = null;   
        if (MBeanServerFactory.findMBeanServer(null).size() > 0) {   
            mBeanServer =(MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);   
        } else {   
            mBeanServer = MBeanServerFactory.createMBeanServer();   
        }   
        if(mBeanServer==null) return null;
    	String servInfo[] =null;
		Set<ObjectName> objectNames = null;
		try {
			objectNames = mBeanServer.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
			if(objectNames == null || objectNames.size() <= 0) {
				System.out.println("没有发现JVM中关联的MBeanServer : " + mBeanServer.getDefaultDomain() + " 中的对象名称.");
			}
			servInfo = new String[3];
			for (ObjectName objectName : objectNames) {
				Object _protocol = mBeanServer.getAttribute(objectName, "protocol");
				Object _scheme = mBeanServer.getAttribute(objectName, "scheme");
				
				if(_protocol.equals("HTTP/1.1") && _scheme.equals("http")){		
					//System.out.println("_scheme:"+ _scheme+"_protocol:"+ _protocol +"=port="+mBeanServer.getAttribute(objectName, "port"));
					servInfo[2] = String.valueOf(mBeanServer.getAttribute(objectName, "port"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return servInfo;
		
		

        /*
    	String servInfo[]=new String[3];
		Set<ObjectName> objectNames = null;
		
		try {
			if(serverType == 1) {
				objectNames = mBeanServer.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
			}
			else if(serverType == 2) {
				objectNames = mBeanServer.queryNames(new ObjectName("jboss.web:type=Connector,*"), null);
			}
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		if(objectNames == null || objectNames.size() <= 0) {
			throw new IllegalStateException("没有发现JVM中关联的MBeanServer : " + mBeanServer.getDefaultDomain() + " 中的对象名称.");
		}
		
		try {
			for (ObjectName objectName : objectNames) {
				Object _protocol = mBeanServer.getAttribute(objectName, "protocol");
				Object _scheme = mBeanServer.getAttribute(objectName, "scheme");
				
				if(protocol.equals(_protocol) && scheme.equals(_scheme)) {
					return (Integer) mBeanServer.getAttribute(objectName, "port");
				}
			}
		} catch (AttributeNotFoundException e) {
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (MBeanException e) {
			e.printStackTrace();
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
        */
	}
}
