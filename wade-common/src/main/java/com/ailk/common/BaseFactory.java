package com.ailk.common;

import com.ailk.common.BaseFactory;

public class BaseFactory {

	private static BaseFactory	factory	= null;

	static {
		if (factory == null) {
			factory = new BaseFactory();
		}
	}

	protected BaseFactory() {
		
	}
}
