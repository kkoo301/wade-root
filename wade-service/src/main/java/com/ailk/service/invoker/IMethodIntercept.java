/**
 * 
 */
package com.ailk.service.invoker;


/**
 * @author yifur
 *
 */
public interface IMethodIntercept {
	
	public boolean invokeBefore(Object... obj) throws Exception;
	
	public boolean invokeAfter(Object... obj) throws Exception;

}
