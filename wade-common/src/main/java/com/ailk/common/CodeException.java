package com.ailk.common;

import com.ailk.common.config.CodeCfg;

public class CodeException extends RuntimeException {
	
	private static final long serialVersionUID = 221346948301977838L;
	
	private String message;
	private String code;
	private String info;

	public CodeException(String code, String[] params) {
		this.code = code;
		this.message = CodeCfg.getProperty(code, params, code);
	}

	public String getMessage() {
		return this.message;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getInfo() {
		return this.info;
	}
	
	
	
}
