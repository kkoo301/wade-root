package com.ailk.common.prepare;

public interface IPrepareJob{
	
	public void run() throws Exception;
	
	public void destroy() throws Exception;
	
}