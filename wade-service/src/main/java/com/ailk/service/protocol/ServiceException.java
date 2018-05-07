package com.ailk.service.protocol;

import com.ailk.common.BaseException;

public class ServiceException extends BaseException {
	
	private static final long serialVersionUID = 1L;

	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(Throwable throwable) {
		super(throwable);
	}
	
	public ServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
