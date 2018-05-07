/**
 * 
 */
package com.ailk.service.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataInput;

/**
 * 专门提供给非WADE的Java客户端使用，利用Java自带的序列化机制将数据流转换成IDataInput,IDataOutput对象
 * @author yifur
 *
 */
public class JavaToIOData {
	
	public JavaToIOData() {
		
	}
	
	
	/**
	 * 从输入流里读取JSON串{}
	 * @param in
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes"})
	public IDataInput read(InputStream in) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(in);
		
		IDataInput input = new DataInput();
		
		try {
			IData head = DataHelper.mapToIData((Map) ois.readObject());
			IData data = DataHelper.mapToIData((Map) ois.readObject());
			
			input.getHead().putAll(head);
			input.getHead().putAll(data);
			input.getData().putAll(data);
			
		} catch (IOException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw new IOException("Java类型转换错误[" + e.getMessage() + "]");
		} finally {
			ois.close();
		}
		
		return input;
	}
	
	
	
	/**
	 * 将IDataOutput对象转换成JSON串[]，IDataOutput.getHead()将拼到第一条数据里
	 * @param output
	 * @return
	 */
	@SuppressWarnings({ "rawtypes"})
	public byte[] write(IDataOutput output) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		
		byte[] bytes = null;
		
		try {
			Map head = DataHelper.idataToMap(output.getHead());
			List data = DataHelper.datasetToList(output.getData());
			
			oos.writeObject(head);
			oos.writeObject(data);
			
			oos.flush();
			bytes = baos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			oos.close();
		}
		
		return bytes;
	}

}
