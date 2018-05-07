package com.ailk.common.trace;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 日志跟踪器
 * 
 * @author $Id: Tracer.java 1 2015-05-19 14:47:02Z liaosheng $
 * 
 */

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Tracer {
	
	Class<?> tracer() ;
	
}
