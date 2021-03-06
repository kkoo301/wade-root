package com.ailk.jlcu.trans;

import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.jlcu.util.DatasBuffer;

public class CommonSubDo extends AbstractCommonDo {
	public CommonSubDo(Map databus) throws Exception {
		super(databus);
	}

	public Object[] beginDo(String xTransCode, Object... data) throws Exception {
		for (int i = 0, len = data.length; i < len; i++) {
			data[i] = transInData(data[i]);
		}
		return data;
	}

	public Object endDo(String xTransCode, Object result) throws Exception {
		return result;
	}

	private Object transInData(Object param) throws Exception {
		if (param instanceof IData) {
			IData map = (IData) param;
			if ("personserv".equals(map.getString("SUBSYS_CODE", ""))) {
				DatasBuffer databuffer = new DatasBuffer();
				databuffer.add(param);
				return databuffer;
			}
			return param;
		}
		return param;
	}
}
