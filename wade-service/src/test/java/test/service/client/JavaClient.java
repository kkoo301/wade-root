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
public class JavaClient {
	
	/**
	 * main
	 * @param args
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		String url = "http://127.0.0.1:8080/service";
		Map head = new HashMap();
		head.put("STAFF_ID", "SUPERUSR");	//工号，生产时必传
		head.put("PASSWORD", "lc");			//密码，生产时必传
		
		Map data = new HashMap();
		data.put("CUST_NAME", "1575431");
		//data.put("CUST_ID", "123");
		data.put("X_TRANS_CODE", "QCS_CustMgrByName");
		List out = com.ailk.service.client.request.JavaClientRequest.request(url, "QCS_CustMgrByName", head, data);
		System.out.println("服务返回数据：" + out);
	}
}
