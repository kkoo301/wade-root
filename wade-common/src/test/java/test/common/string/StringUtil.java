/**
 * 
 */
package test.common.string;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.serializer.JsonToIOData;

/**
 * @author yifur
 *
 */
public class StringUtil {
	
	static org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
	
	public static void main(String[] args) throws Exception {
		jsonToIOData();
	}
	
	public static void jsonToIOData() throws IOException {
		Map<String, Object> object = new HashMap<String, Object>();
		object.put("STAFF_ID", "SUPERUSR");
		object.put("PASSWORD", "lc");
		object.put("CUST_NAME", "liaos");
		object.put("CUST_ID", "123");
		object.put("X_TRANS_CODE", "QCS_CustMgrByName");
		
		String s = mapper.writeValueAsString(object);
		
		System.out.println("原始串：" + s);
		
		JsonToIOData io = new JsonToIOData();
		IDataInput input = io.read(new ByteArrayInputStream(s.getBytes()));
		System.out.println("序列化成Input对象：" + input);
		
		IDataOutput output = new DataOutput();
		output.getHead().putAll(input.getHead());
		output.getData().add(input.getData());
		byte[] ary = io.write(output);
		System.out.println("反序列化成Output对象：" + new String(ary));
	}
	
	public static void jackson() throws Exception {
		long start = System.currentTimeMillis();
		
		IData data = new DataMap();
		data.put("a", "b");
		data.put("b", "c");
		
		IDataset ds = new DatasetList();
		ds.add(data);
		
		data = new DataMap();
		data.put("ds", ds);
		
		
		Map<String, Object> a = new HashMap<String, Object>();
		
		String s = mapper.writeValueAsString(data);
		
		Map<String, Object> m = mapper.readValue(s, Map.class);
		
		System.out.println("jackson cost time:" + (System.currentTimeMillis() - start));
	}

	public static void jsonLib() {
		long start = System.currentTimeMillis();
		
		IData data = new DataMap();
		data.put("a", "b");
		data.put("b", "c");
		
		IDataset ds = new DatasetList();
		ds.add(data);
		
		data = new DataMap();
		data.put("ds", ds);
		
		net.sf.json.JSONObject obj = net.sf.json.JSONObject.fromObject(data);
		String s = obj.toString();
		
		net.sf.json.JSONObject obj1 = net.sf.json.JSONObject.fromObject(s);
		
		System.out.println("json-lib cost time:" + (System.currentTimeMillis() - start));
	}
	
	
	public static void replaceTr() {
		Throwable e = new Throwable();
		StringWriter sw = new StringWriter();
		e.printStackTrace(new ErrorPrintWriter(sw));
		String stack = sw.toString();
		System.out.println(stack);
	}
	
	static class ErrorPrintWriter extends PrintWriter {
		/**
		 * @param out
		 */
		public ErrorPrintWriter(Writer out) {
			super(out);
		}
		
		/* (non-Javadoc)
		 * @see java.io.PrintWriter#println(java.lang.String)
		 */
		@Override
		public void println(String x) {
			super.println("superusr- " + x);
		}
		
	}
	
	
	
	public static void replaceMultBlankChar() {
		String s = "ad a a    s s s工s       2 ";
		
		char[] ary = s.toCharArray();
		StringBuilder sb = new StringBuilder(ary.length);
		boolean tag = false;
		for (int i=0; i<ary.length; i++) {
			char c1 = ary[i];
			if (c1 == ' ') {
				if (tag) {
					continue;
				} else {
					tag = true;
					sb.append(c1);
				}
			} else {
				tag = false;
				sb.append(c1);
			}
		}
		
		System.out.println(sb.toString());
	}
}
