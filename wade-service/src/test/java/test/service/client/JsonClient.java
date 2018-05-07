/**
 * 
 */
package test.service.client;

import net.sf.json.JSONObject;

/**
 * @author yifur
 *
 */
public class JsonClient {
	
	public static void main(String[] args) {
		String url = "http://10.154.50.213:10005/service";
		
		JSONObject object = new JSONObject();
		object.put("STAFF_ID", "SUPERUSR");		//工号，生产时必传
		object.put("PASSWORD", "lc");			//密码，生产时必传
		object.put("CUST_NAME", "liaos");
		object.put("CUST_ID", "123");
		object.put("X_TRANS_CODE", "SYS.Probe.TestSvc");
		
		String data = com.ailk.service.client.request.JsonClientRequest.request(url, "SYS.Probe.TestSvc", object.toString(), "utf-8");
		System.out.println("服务返回:" + data);
	}
}
