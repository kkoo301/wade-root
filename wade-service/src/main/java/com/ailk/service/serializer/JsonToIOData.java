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
import java.util.Iterator;
import java.util.List;
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
public class JsonToIOData {
	
	static org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
	
	public JsonToIOData() {
		
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
	
	@SuppressWarnings("unchecked")
	public IDataOutput write(String json) throws Exception {
		IDataOutput output = new DataOutput();
		
		IData head = output.getHead();
		IDataset data = output.getData();
		
		List<Map<String, Object>> list = null;
		
		try {
			list = mapper.readValue(json, List.class);
			if (null != list) {
				int index = 0;
				for (Map<String, Object> map : list) {
					IData id = new DataMap();
					id.putAll(map);
					data.add(id);
					
					if (index == 0) {
						head.putAll(map);
					}
					index ++;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		
		return output;
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
		
		// 深度转换
		IData data = map2IData(map);
		
		IData head = input.getHead();
		head.putAll(data);
		
		input.getData().putAll(data);
		
		return input;
	}
	
	/**
	 * 深度遍历每个key，将对应的Value转换后，返回一个IData
	 * 转换规则：
	 * 1、String 原样返回；
	 * 2、Map 深度遍历后返回IData；
	 * 3、List 深度遍历后返回IDataset；
	 * 4、其它 返回value.toString();
	 * @param map
	 * @return
	 */
	private IData map2IData(Map map) {
		IData data = new DataMap();
		Iterator iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			Object value = map.get(key);
			if (null == value) {
				data.put(key, value);
			}
			
			if (value instanceof Map) {
				data.put(key, map2IData((Map)value));
			} else if (value instanceof List) {
				data.put(key, list2IDataset((List) value));
			} else {
				data.put(key, value.toString());
			}
		}
		return data;
	}
	
	
	public IDataset list2IDataset(List list) {
		IDataset ds = new DatasetList();
		for (Object obj : list) {
			if (obj instanceof Map) {
				ds.add(map2IData((Map) obj));
			} else {
				ds.add(obj);
			}
		}
		return ds;
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
			IDataset data = new DatasetList();
			IData head = new DataMap();
			head.put(Constants.X_RESULTCODE, "-1");
			head.put(Constants.X_RESULTINFO, "无数据");
			data.add(head);
			
			String value;
			try {
				value = mapper.writeValueAsString(data);
			} catch (JsonGenerationException e) {
				value = "[]";
				
				e.printStackTrace();
			} catch (JsonMappingException e) {
				value = "[]";
				
				e.printStackTrace();
			} catch (IOException e) {
				value = "[]";
				
				e.printStackTrace();
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
				value = mapper.writeValueAsString(data);
			} catch (JsonGenerationException e) {
				value = "[]";
			} catch (JsonMappingException e) {
				value = "[]";
			} catch (IOException e) {
				value = "[]";
			}
			return value;
		}
	}
	
	public static void main(String[] args) throws Exception {
		_testStringToInput();
	}
	
	
	static void _testStringToInput() throws Exception {
		String str = "{\"TRADE_CITY_CODE\":\"HNSJ\",\"TRADE_DEPART_ID\":\"36601\",\"X_DES_ORG_ID\":\"\",\"IN_MODE_CODE\":\"0\",\"X_SRC_ORG_ID\":\"HNWNZ841\",\"TRADE_STAFF_ID\":\"SUPERUSR\",\"X_BUSI_TYPE\":\"ASIGOUT_B2B\",\"X_TRANS_CODE\":\"RC.resource.ITermB2BOperateSV.termB2BOutInstockCheck\",\"INFOS\":[{\"INVID\":\"861217021816176\",\"RESTYPEID\":\"P0001887.0001\"},{\"INVID\":\"865000020135442\",\"RESTYPEID\":\"P0003855.0001\"}],\"X_INV_ID\":[\"861217021816176\",\"865000020135442\"],\"X_RES_TYPE_ID\":[\"P0001887.0001\",\"P0003855.0001\"],\"TRADE_EPARCHY_CODE\":\"0898\"}}";
		ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
		
		JsonToIOData io = new JsonToIOData();
		IDataInput input = io.read(bais);
		System.out.println(input.getData().get("INFOS").getClass().getName());
		System.out.println(input.getData().getString("INFOS"));
		System.out.println(input.getData().getDataset("INFOS"));
	}
}
