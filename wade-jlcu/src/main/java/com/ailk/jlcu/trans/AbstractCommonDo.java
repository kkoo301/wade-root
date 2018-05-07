package com.ailk.jlcu.trans;

import java.util.Map;

import org.apache.log4j.Logger;

public abstract class AbstractCommonDo implements ICommonDo {
	
	private static final Logger log = Logger.getLogger(AbstractCommonDo.class);
	protected final Map databus;
	
	public AbstractCommonDo(Map databus) throws Exception{
		this.databus = databus;
	}
}
