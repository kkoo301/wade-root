/**
 * 
 */
package test.service.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yifur
 *
 */
public class HttpClient {
	
	/**
	 * main
	 * @param args
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		String url = "http://10.154.50.213:10003/service";
		Map head = new HashMap();
		
		head.put("TRADE_ROUTE_VALUE", "INTFS");
		head.put("TRADE_TERMINAL_ID", "10.154.92.35");
		head.put("TRADE_CITY_CODE", "INTF");
		head.put("ROUTE_EPARCHY_CODE", "0731");
		head.put("TRADE_ROUTE_TYPE", "00");
		head.put("TRADE_DEPART_ID", "16539");
		
		head.put("X_TRANS_CODE", "SYS.Probe.TestSvc");
		head.put("TRADE_DEPART_PASSWD", "Linkage123");
		head.put("IN_MODE_CODE", "2");
		head.put("TRADE_EPARCHY_CODE", "0731");
		head.put("CHANNEL_TRADE_ID", "E018201307161003111234sccc");
		head.put("SERIAL_NUMBER", "13873113605");
		
		head.put("TRADE_STAFF_ID", "ITFSM000");
		head.put("PROVINCE_CODE", "HNAN");
		
		head.put("CUST_NAME", "HNAN");
		
		List out = com.ailk.service.client.request.HttpClientRequest.request(url, "SYS.Probe.TestSvc", head, head);
		System.out.println("服务返回数据：" + out);
	}
}
