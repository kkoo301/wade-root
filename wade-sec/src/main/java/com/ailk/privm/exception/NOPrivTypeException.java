package com.ailk.privm.exception;

public class NOPrivTypeException extends Exception{
	private static final long serialVersionUID= 1L;
	private String message = "错误的权限类型";
	
	public NOPrivTypeException() {
		
	}
	
	public NOPrivTypeException(String privType) {
		message = "错误的权限类型" + ":" + privType;
	}
	
	public String getMessage(){
		return message;
	}
	
}
