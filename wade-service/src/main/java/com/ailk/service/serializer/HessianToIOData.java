/**
 * 
 */
package com.ailk.service.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataInput;
import com.ailk.service.hessian.io.Hessian2Input;
import com.ailk.service.hessian.io.Hessian2Output;
import com.ailk.service.hessian.io.SerializerFactory;

/**
 * 专门提供给WADE的Java客户端使用，利用Hessian将数据流序列化成IDataInput, IDataOutput对象
 * @author yifur
 *
 */
public class HessianToIOData {
	
	private static SerializerFactory dataInputFactory = new SerializerFactory(IDataInput.class.getClassLoader());
	private static SerializerFactory dataOutputFactory = new SerializerFactory(IDataOutput.class.getClassLoader());
	
	
	public HessianToIOData() {
		
	}
	
	
	/**
	 * 从输入流里读取JSON串{}
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public IDataInput read(InputStream in) throws IOException {
		Hessian2Input hi = new Hessian2Input(in);
		
		IDataInput input = new DataInput();
		
		try {
			hi.setSerializerFactory(dataInputFactory);
			
			input = (IDataInput) hi.readObject(IDataInput.class);
		} catch (IOException e) {
			throw e;
		} finally {
			hi.close();
		}
		
		return input;
	}
	
	
	
	/**
	 * 将IDataOutput对象转换成JSON串[]，IDataOutput.getHead()将拼到第一条数据里
	 * @param output
	 * @return
	 */
	public byte[] write(IDataOutput output) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		
		Hessian2Output out = new Hessian2Output(baos);
		out.setSerializerFactory(dataOutputFactory);
		byte[] bytes = null;

		try {
			out.writeObject(output);
			out.flush();
			bytes = baos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
		
		return bytes;
	}

}
