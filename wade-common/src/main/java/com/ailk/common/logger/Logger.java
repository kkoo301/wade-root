package com.ailk.common.logger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 日志记录器
 * 
 * @author $Id: Logger.java 1 2014-02-20 08:34:02Z huangbo $
 * 
 */

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Logger {
	
	Class<?> logger() ;
	
}
