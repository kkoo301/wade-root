package com.wade.message.comet.server.codec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wade.message.comet.server.util.AntiXSSEncoder;

public class QueryStringDecoder extends io.netty.handler.codec.http.QueryStringDecoder
{

	Map<String, List<String>> params;
	
	public QueryStringDecoder(String uri) {
		super(uri);
	}

	
	@Override
	public Map<String, List<String>> parameters() {
		if(params == null){
			params = super.parameters();
			for(String name : params.keySet()){
				List<String> values = params.get(name);
				if(values != null && values.size() > 0){
					List<String> encoded = new ArrayList<String>();
					for(String value : values){
						value = AntiXSSEncoder.encode(value);
						encoded.add(value);
					}
					params.put(name, encoded);
				}
			}
		}	
		return params;
	}
	
}