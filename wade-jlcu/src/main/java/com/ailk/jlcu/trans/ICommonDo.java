package com.ailk.jlcu.trans;

public interface ICommonDo {
	public Object[] beginDo(String xTransCode, Object... data) throws Exception;

	public Object endDo(String xTransCode, Object result) throws Exception;
}
