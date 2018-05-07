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

import net.sf.json.JSONObject;

import com.ailk.common.Constants;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataAdapter;
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
public class Json2ToIOData {
	
	private String charset = "UTF-8";
	
	public Json2ToIOData() {
		
	}
	
	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}
	
	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	
	/**
	 * 从输入流里读取JSON串{}
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public IDataInput read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in, getCharset()));
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
		
		/** 请求头 **/
		Map<String, Object> reqHead = new HashMap<String, Object>();
		reqHead.putAll(head);
		
		/** 请求体 **/
		Map<String, Object> reqBody = new HashMap<String, Object>();
		reqBody.putAll(data);
		
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("head", reqHead);
		request.put("data", reqBody);
		
		String json = null;
		try {
			//xiedx 2017/3/3 判断DataMap类型
			if(DataAdapter.isDataMapUseWadeJSONObject()){
				Object obj = DataAdapter.constructWadeJSONObject(request);
				json = DataAdapter.wadeJSONObjectToString(obj);
			}else{
				//net.sf.json.JSONObject对象转换
				JSONObject object = JSONObject.fromObject(request);
				//json = mapper.writeValueAsString(request);
				json = object.toString();
			}
		} catch (Exception e) {
			throw e;
		}
		
		return json;
	}
	
	
	/**
	 * 将Output对象转换成Json格式
	 * @param output
	 * @return
	 * @throws Exception
	 */
	public String read(IDataOutput output) throws Exception {
		IData head = output.getHead();
		IDataset data = output.getData();
		
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("head", head);
		request.put("data", data);
		
		String json = null;
		try {
			//xiedx 2017/3/3 判断DataMap类型
			if(DataAdapter.isDataMapUseWadeJSONObject()){
				Object obj = DataAdapter.constructWadeJSONObject(request);
				json = DataAdapter.wadeJSONObjectToString(obj);
			}else{
				//net.sf.json.JSONObject对象转换
				JSONObject object = JSONObject.fromObject(request);
				//json = mapper.writeValueAsString(request);
				json = object.toString();
			}
		} catch (Exception e) {
			throw e;
		}
		return json;
	}
	
	public IDataOutput write(String json) throws Exception {
		IDataOutput output = new DataOutput();
		
		IData head = output.getHead();
		IDataset data = output.getData();
		
		try {
			IData request = new DataMap(json);
			
			if (null == request)
				return output;
			
			IData reqHead = request.getData("head");
			IDataset reqData = request.getDataset("data");
			
			if (null != reqHead)
				head.putAll(reqHead);
			else 
				reqHead = new DataMap();
			
			if (null != reqData) {
				data.addAll(reqData);
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
	private IDataInput toDataInput(String json) {
		IDataInput input = new DataInput();
		
		try {
			IData request = new DataMap(json);
			
			IData reqHead = request.getData("head");
			if (null != reqHead) {
				input.getHead().putAll(reqHead);
			}
			
			IData reqData = request.getData("data");
			if (null != reqData) {
				input.getData().putAll(reqData);
			}
		} catch (Exception e) {
			input.getHead().put(Constants.X_RESULTCODE, "-101");
			input.getHead().put(Constants.X_RESULTINFO, "无数据");
			e.printStackTrace();
		}
		
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
	private String fromDataOutput(IDataOutput output) throws IOException {
		if (null == output) {
			output = new DataOutput();
			IData head = output.getHead();
			head.put(Constants.X_RESULTCODE, "-101");
			head.put(Constants.X_RESULTINFO, "无数据");
		} else {
			IDataset data = output.getData();
			if (null == data || data.isEmpty()) {
				data = new DatasetList();
				data.add(new DataMap());
			}
			
			IData first = (IData) data.get(0);
			if (null == first) {
				first = new DataMap();
				data.add(first);
			} else {
				String xResultCode = first.getString("X_RESULTCODE", "");
				String xResultInfo = first.getString("X_RESULTINFO", "");
				String xRspDesc = first.getString("X_RSPDESC", "");
				String xRspType = first.getString("X_RSPTYPE", "");
				String xRspCode = first.getString("X_RSPCODE", "");
				
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
		}
		
		String json = null;
		try {
			json = read(output);
		} catch (Exception e) {
			throw new IOException("服务返回数据格式异常，无法序列化");
		}
		return json;
	}
	
	
	public static void main(String[] args) throws Exception {
		_testStringToInput();
		/*_testInput();
		_testOutput();
		_testInvoke();*/
	}
	
	static void _testStringToInput() throws Exception {
		String str = "{\"head\":{\"TRADE_CITY_CODE\":\"HNSJ\",\"TRADE_DEPART_ID\":\"36601\",\"DEPART_ID\":\"36601\",\"X_DES_ORG_ID\":\"\",\"CITY_CODE\":\"HNSJ\",\"CHANNEL_TYPE_ID\":null,\"IN_MODE_CODE\":\"0\",\"X_SRC_ORG_ID\":\"HNWNZ841\",\"TRADE_STAFF_ID\":\"SUPERUSR\",\"X_BUSI_TYPE\":\"ASIGOUT_B2B\",\"X_TRANS_CODE\":\"RC.resource.ITermB2BOperateSV.termB2BOutInstockCheck\",\"STAFF_ID\":\"SUPERUSR\",\"INFOS\":[{\"INVID\":\"861217021816176\",\"RESTYPEID\":\"P0001887.0001\"},{\"INVID\":\"865000020135442\",\"RESTYPEID\":\"P0003855.0001\"}],\"X_INV_ID\":[\"861217021816176\",\"865000020135442\"],\"X_RES_TYPE_ID\":[\"P0001887.0001\",\"P0003855.0001\"],\"X_PTRADE_ID\":\"\",\"X-Forwarded-For\":null,\"STAFF_EPARCHY_CODE\":\"0898\",\"TRADE_EPARCHY_CODE\":\"0898\"},\"data\":{\"TRADE_CITY_CODE\":\"HNSJ\",\"TRADE_DEPART_ID\":\"36601\",\"X_DES_ORG_ID\":\"\",\"IN_MODE_CODE\":\"0\",\"X_SRC_ORG_ID\":\"HNWNZ841\",\"TRADE_STAFF_ID\":\"SUPERUSR\",\"X_BUSI_TYPE\":\"ASIGOUT_B2B\",\"X_TRANS_CODE\":\"RC.resource.ITermB2BOperateSV.termB2BOutInstockCheck\",\"INFOS\":[{\"INVID\":\"861217021816176\",\"RESTYPEID\":\"P0001887.0001\"},{\"INVID\":\"865000020135442\",\"RESTYPEID\":\"P0003855.0001\"}],\"X_INV_ID\":[\"861217021816176\",\"865000020135442\"],\"X_RES_TYPE_ID\":[\"P0001887.0001\",\"P0003855.0001\"],\"TRADE_EPARCHY_CODE\":\"0898\"}} ";
		ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes());
		
		Json2ToIOData io = new Json2ToIOData();
		IDataInput input = io.read(bais);
		System.out.println(input.getData().get("INFOS").getClass().getName());
		System.out.println(input.getData().get("INFOS"));
	}
	
	
	/**
	 * 测试JSON与IDataInput的相互转换
	 * @throws Exception
	 */
	static void _testInput() throws Exception {
		IData data = new DataMap();
		data.put("1", "1");
		data.put("2", "2");
		
		IDataset ds = new DatasetList();
		IData d = new DataMap();
		d.put("A", "a");
		d.put("B", "b");
		ds.add(d);
		
		d = new DataMap();
		d.put("C", "c");
		d.put("D", "d");
		ds.add(d);
		
		data.put("3", ds);
		
		IDataInput input = new DataInput(data, data);
		Json2ToIOData io = new Json2ToIOData();
		String json = io.read(input);
		System.out.println(json);
	}
	
	/**
	 * 测试JSON与IDataOutput的相互转换
	 * @throws Exception
	 */
	static void _testOutput() throws Exception {
		IData data = new DataMap();
		data.put("1", "1");
		data.put("2", "2");
		
		IDataset ds = new DatasetList();
		IData d = new DataMap();
		d.put("A", "a");
		d.put("B", "b");
		ds.add(d);
		
		d = new DataMap();
		d.put("C", "c");
		d.put("D", "d");
		ds.add(d);
		
		IDataOutput output = new DataOutput(data, ds);
		Json2ToIOData io = new Json2ToIOData();
		String json = io.read(output);
		System.out.println(io.write(json));
	}
	
	
	static void _testInvoke() throws Exception {
		IDataInput input = new DataInput();
		IData data = new DataMap();
		data.put("1", "1");
		data.put("2", "2");
		input.getHead().putAll(data);
		input.getData().putAll(data);
		
		Json2ToIOData io = new Json2ToIOData();
		
		//将IDataInput转换成客户端的JSON串
		String jsonin = io.read(input);
		System.out.println("json 输入成功" + jsonin);
		
		IDataOutput output = new DataOutput();
		output.getHead().putAll(data);
		output.getData().addAll(_createData());
		
		//将IDataoutput转换成字节数组返回给客户端
		byte[] b = io.write(output);
		
		String out = new String(b);
		IDataOutput op = io.write(out);
		System.out.println("json 输出成功" + op);
		System.out.println(op.getData().getData(0).get("datamap").getClass());
		System.out.println(op.getData().getData(0).get("dataset").getClass());
	}
	
	private static IDataset _createData() {
		IDataset ds = new DatasetList();
		
		IData d1 = new DataMap();
		d1.put("NAME", "Hello ");
		
		IDataset ds1 = new DatasetList();
		IData data1 = new DataMap();
		data1.put("datamap", "Hello");
		ds1.add(data1);
		d1.put("dataset", ds1);
		
		IData data2 = new DataMap();
		data2.put("datamap", "Hello");
		d1.put("datamap", data2);
		ds.add(d1);
		return ds;
	}
	
}
