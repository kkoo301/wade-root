package com.ailk.service.server.hessian;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class Hessian2ServerRequestWrapper extends HttpServletRequestWrapper{

	public Hessian2ServerRequestWrapper(HttpServletRequest request) {
		super(request);
		// TODO Auto-generated constructor stub
	}
	
}