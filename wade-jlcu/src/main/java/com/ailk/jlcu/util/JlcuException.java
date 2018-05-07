package com.ailk.jlcu.util;

public class JlcuException extends Exception {
	
	StringBuilder message;

	public JlcuException() {
		super();
		this.message = new StringBuilder();
	}

	public JlcuException(String message) {
		this();
		this.message.append(message);
	}

	public JlcuException(Throwable cause) {
		super(cause);
		this.message = new StringBuilder(); 
		this.message.append(getBottomException(cause).getMessage());
	}

	public JlcuException(String message, Throwable cause) {
		super(cause);
		this.message = new StringBuilder(); 
		this.message.append(getBottomException(cause).getMessage()+message);
	}

	public void addMessage(String message) {
		this.message.append(message);
	}

	@Override
	public String getMessage() {
		return message.toString();
	}
	
	protected static Throwable getBottomException(Throwable exception) {
		if (exception == null)
			return null;
		if (exception.getCause() != null) {
			exception = exception.getCause();
			return getBottomException(exception);
		}
		return exception;
	}
}
