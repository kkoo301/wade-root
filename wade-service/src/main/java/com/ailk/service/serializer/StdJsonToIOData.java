/**
 * 
 */
package com.ailk.service.serializer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.ailk.common.Constants;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.DatasetList;

/**
 * 专门提供给WADE3.0系统使用，将JSON串序列化成IDataInput,IDataOutput对象
 * 
 * @author yifur
 *
 */
public class StdJsonToIOData {
	
	static org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
	
	public StdJsonToIOData() {
		
	}
	
	
	/**
	 * 从输入流里读取JSON串{}
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public IDataInput read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			br.close();
		}
		
		return toDataInput(sb.toString());
	}
	
	
	/**
	 * 从DataInput对象里读取JSON串
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public String read(IDataInput input) throws Exception {
		IData head = input.getHead();
		IData data = input.getData();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(data);
		map.putAll(head);
		
		String json = null;
		try {
			json = mapper.writeValueAsString(map);
		} catch (Exception e) {
			throw e;
		}
		
		return json;
	}
	
	
	/**
	 * 将JSON串{}转换成IDataInput对象
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private IDataInput toDataInput(String json) {
		IDataInput input = new DataInput();
		
		Map<String, Object> map = null;
		try {
			map = mapper.readValue(json, Map.class);
		} catch (JsonParseException e) {
			map = new HashMap<String, Object>();
			
			e.printStackTrace();
		} catch (JsonMappingException e) {
			map = new HashMap<String, Object>();
			
			e.printStackTrace();
		} catch (IOException e) {
			map = new HashMap<String, Object>();
			
			e.printStackTrace();
		}
		
		IData head = input.getHead();
		head.putAll(map);
		
		input.getData().putAll(map);
		
		return input;
	}
	
	
	/**
	 * 
	 * @param output
	 * @return
	 */
	public byte[] write(IDataOutput output) throws IOException {
		byte[] bytes = null;
		
		try {
			String data = fromDataOutput(output);
			bytes = data.getBytes(GlobalCfg.getCharset());
		} catch (IOException e) {
			throw e;
		}
		
		return bytes;
	}
	
	
	/**
	 * 将IDataOutput对象转换成JSON串[]，IDataOutput.getHead()将拼到第一条数据里
	 * @param output
	 * @return
	 */
	private String fromDataOutput(IDataOutput output) {
		if (null == output) {
			IData data = new DataMap();
			data.put(Constants.X_RESULTCODE, "-1");
			data.put(Constants.X_RESULTINFO, "无数据");
			
			String value;
			try {
				value = mapper.writeValueAsString(data);
			} catch (JsonGenerationException e) {
				value = "{}";
			} catch (JsonMappingException e) {
				value = "{}";
			} catch (IOException e) {
				value = "{}";
			}
			
			return value;
		} else {
			IDataset data = output.getData();
			IData head = output.getHead();
			if (null == data || data.isEmpty()) {
				data = new DatasetList();
				data.add(new DataMap());
			}
			
			IData first = (IData) data.get(0);
			if (null == first) {
				first = new DataMap();
				first.putAll(head);
				data.add(first);
			} else {
				String xResultCode = first.getString("X_RESULTCODE", "");
				String xResultInfo = first.getString("X_RESULTINFO", "");
				String xRspDesc = first.getString("X_RSPDESC", "");
				String xRspType = first.getString("X_RSPTYPE", "");
				String xRspCode = first.getString("X_RSPCODE", "");
				
				first.putAll(head);
				
				if (!"".equals(xResultCode)) {
					first.put("X_RESULTCODE", xResultCode);
				}
				if (!"".equals(xResultCode)) {
					first.put("X_RESULTINFO", xResultInfo);
				}
				if (!"".equals(xRspDesc)) {
					first.put("X_RSPDESC", xRspDesc);
				}
				if (!"".equals(xRspType)) {
					first.put("X_RSPTYPE", xRspType);
				}
				if (!"".equals(xRspCode)) {
					first.put("X_RSPCODE", xRspCode);
				}
			}
			
			String value;
			try {
				if (data.size() <= 1) {
					value = mapper.writeValueAsString(first);
				} else {
					value = "{" + mapper.writeValueAsString(data) + "}";
				}
			} catch (JsonGenerationException e) {
				value = "{}";
			} catch (JsonMappingException e) {
				value = "{}";
			} catch (IOException e) {
				value = "{}";
			}
			
			return value;
		}
	}
	
	public static void main(String[] args) throws IOException {
		StdJsonToIOData io = new StdJsonToIOData();
		String request = "{\"NET_TYPE_CODE\":\"00\",\"ROUTE_EPARCHY_CODE\":\"0731\",\"SERIAL_NUMBER\":\"13549679903\", \"TRADE_CITY_CODE\":\"A31B\", \"TRADE_DEPART_ID\":\"59554\", \"TRADE_EPARCHY_CODE\":\"0731\", \"TRADE_ID\":\"3113071900000018\", \"TRADE_STAFF_ID\":\"A1BZ0461\", \"TRADE_TYPE_CODE\":\"7301\", \"USER_ID\":\"3109120269320583\", \"X_TRANS_CODE\":\"CS.PackageSVC.getMemberPackageElements\"}";
		ByteArrayInputStream bais = new ByteArrayInputStream(request.getBytes());
		IDataInput input = io.read(bais);
		System.out.println(input);
		
		IDataOutput output = new DataOutput();
		IData data = new DataMap();
		data.put("A", "string");
		data.put("B", new DatasetList());
		output.getData().add(data);
		output.getData().add(data);
		System.out.println(new String(io.write(output)));
	}
}
