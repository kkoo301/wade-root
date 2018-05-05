package com.wade.relax.exception;

/**
 * Copyright: Copyright (c) 2016 Asiainfo
 *
 * @desc:
 * @auth: steven.zhou
 * @date: 2016-11-24
 */
public class NotFoundCenterException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public NotFoundCenterException() {
		super();
	}

	public NotFoundCenterException(String s) {
		super(s);
	}

	public NotFoundCenterException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundCenterException(Throwable cause) {
		super(cause);
	}
}
