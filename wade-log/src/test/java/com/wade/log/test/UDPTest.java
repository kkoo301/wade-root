package com.wade.log.test;

import java.net.InetAddress;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.wade.log.LogFactory;
import com.wade.log.Protocal;
import com.wade.log.impl.LogData;

public class UDPTest
{
	static final int port = 1984;
   
	public static void main(String[] args) throws Exception{
		
		InetAddress addr = InetAddress.getByName("127.0.0.1");
		
		//String dataStr = "{\"ACCEPT_MONTH\":8,\"REMARK\":\"[用户正常登录系统成功]\",\"SUBSYS_CODE\":\"NBS\",\"LOG_DAY\":\"4\",\"CLIENT_TYPE\":\"0\",\"LOGIN_IP\":null,\"MAIN_ID\":null,\"LOGIN_MAC\":null,\"IN_TIME\":\"2015-08-04 21:20:15\",\"LOG_ID\":\"20150804\",\"STAFF_ID\":\"TESTTJ16\",\"TICKET\":null,\"IN_IP\":\"127.0.0.1\",\"LOGIN_TYPE\":\"1\",\"X_KEY\":\"in\"}";
	
		String log_id = "201508045656"; //get random logid

		IData data = new DataMap();
	
		data.put("LOG_ID", log_id);
		data.put("STAFF_ID", "TESTTJ16");
		data.put("IN_TIME", "2015-08-04 21:20:15");
		data.put("SUBSYS_CODE", "NBS");
		data.put("IN_IP", "192.168.102.111");
		data.put("REMARK", "[用户正常登录系统成功]");
		/*data.put("CLIENT_TYPE", "0");
		data.put("LOGIN_IP", null);
		data.put("LOGIN_MAC", null);
		data.put("MAIN_ID", null);
		data.put("TICKET", "e61e8785-e2fb-4cdf-8b2f-555fba63741e");
		data.put("ACCEPT_MONTH", 8);
		data.put("LOGIN_TYPE", "1"); //xiedx 2014/08/16 增加LOGIN_TYPE字段
		data.put("LOG_DAY", 4); //按日分区字段
		data.put("X_KEY", "in");*/
		
		IData logData = new DataMap();
		logData.put("LOG_ID", "2012122885547297"); // LOG_ID
		logData.put("PAGE_ID", "model4a_00003"); // PAGE_ID
		logData.put("LOG_TYPE", "1"); // LOG_TYPE
		logData.put("REQUESTCODE", "CRMCRM012020120704234937284"); // REQUESTCODE
		logData.put("OPERATOR", "AVYA0062"); // OPERATOR
		logData.put("SYSTEMNAME", "CRM"); // SYSTEMNAME
		logData.put("BUSINESSOP", "CRM0020"); // BUSINESSOP
		logData.put("OPOBJECT", "model4a_00014"); // OPOBJECT
		logData.put("OPREASON", "申请授权"); // OPREASON
		logData.put("ERRCODE", "0"); // OPREASON
		logData.put("ERRDES", ""); // ERRDES
		logData.put("AUTHTYPE", "5"); // AUTHTYPE
		logData.put("COOPERATOR", "liunanyun| AEY00291| 刘南云&gongchao| AEY02019| 巩超&cuijunli| AEY00303| 崔均丽&liuwei5| AEY00727| 刘炜&lujing| AEY00439| 卢婧&sunnan| AEY00721| 孙楠&wangshan2| AEY00053| 王姗&wangxia1| AEY00997| 王霞&xuyan1| AEY00911| 徐妍&jinying1| AEY00796| 靳颖&zhourong| AEY00126|周荣&chenzhuo| AEY00007| 陈卓&haoxin| AEY00022| 郝欣"); // COOPERATOR
		logData.put("MAIN_ID", ""); // UID
		logData.put("AUTHCODE", ""); // AUTHCODE
		logData.put("BILLNO", ""); // BILLNO
		logData.put("PASSWORD", ""); // PASSWORD
		logData.put("OPER_DATE", "2012-07-04 23:52:12"); // OPER_DATE
		logData.put("OPER_DEPART_ID", "0023"); // OPER_DEPART_ID
		logData.put("RSRV_STR1", "10.143.186.123"); // OPER_DEPART_ID

		logData.put("RSRV_TAG1", "n");
		logData.put("RSRV_DATE1", "2012-07-04 23:52:12");
		  
		System.out.println(">>>"  + data);
		
		StringBuilder mnt = new StringBuilder(100);
		 mnt.append("svcName").append(">").append("tenantCode").append(":").append("knowledgeBaseCode").append("|");
		 long startTime = System.currentTimeMillis();
		 mnt.append(startTime).append("|");
		 long costTime = System.currentTimeMillis()-startTime;
		 mnt.append(costTime);
		
		
		LogData sendData = new LogData("1001");
		sendData.setContent(data);
		
		//byte[] bytes = JavaEncoder.encodeByLen(sendData);
		
		LogFactory.sendLog(addr, port, Protocal.UDP, sendData);
		
		System.out.println(">>>send complete");
		
		//String str = EscapeUtil.unescape(null);
		//System.out.println(">>>" + str);
	}
	
}