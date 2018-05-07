package com.wade.message.websocket.server.codec;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.ailk.rpc.org.jboss.netty.util.internal.CaseIgnoringComparator;
import com.wade.message.websocket.server.util.AntiXSSEncoder;

public class  HttpPostRequestDecoder extends io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
{
	private transient static final Logger log = Logger.getLogger(HttpPostRequestDecoder.class);
	
	private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<String, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
	
	private List<InterfaceHttpData> bodyListHttpData;
	
	public HttpPostRequestDecoder(HttpRequest request) throws ErrorDataDecoderException, IncompatibleDataDecoderException {
		super(new HttpDataFactory(), request);
	}
	
	@Override
	public List<InterfaceHttpData> getBodyHttpDatas()
	{
		if(bodyListHttpData == null){
			bodyListHttpData = super.getBodyHttpDatas();
			if(bodyListHttpData != null){
				for(InterfaceHttpData data : bodyListHttpData){
					if( data instanceof Attribute){
						try {
							((Attribute) data).setValue(AntiXSSEncoder.encode(((Attribute) data).getValue()));
						} catch (IOException e) {
							log.error("AntiXSSEncode Error:", e);
						}
					}
				}
			}
		}
		
		return bodyListHttpData;	
	}
	
	@Override
	public List<InterfaceHttpData> getBodyHttpDatas(String name)
	{
		List<InterfaceHttpData> datas = bodyMapHttpData.get(name);
		if(datas == null){
			datas = super.getBodyHttpDatas(name);
			if(datas != null){
				for(InterfaceHttpData data : datas){
					if( data instanceof Attribute){
						try {
							((Attribute) data).setValue(AntiXSSEncoder.encode(((Attribute) data).getValue()));
						} catch (IOException e) {
							log.error("AntiXSSEncode Error:", e);
						}
					}
				}
			}
		}
		
		return datas;	
	}
	
	@Override
	public InterfaceHttpData getBodyHttpData(String name){
		List<InterfaceHttpData> list = getBodyHttpDatas(name);
		if (list != null) {
			return list.get(0);
		}
		return null;
	}
	
	public static class HttpDataFactory extends DefaultHttpDataFactory
	{
		public HttpDataFactory(){
			super();
		}
		
		public HttpDataFactory(boolean useDisk){
			super(useDisk);
		}
		
		public HttpDataFactory(long minSize){
			super(minSize);
		}
		
	}

}