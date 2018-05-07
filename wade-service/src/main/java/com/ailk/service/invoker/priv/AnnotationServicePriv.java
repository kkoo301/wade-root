/**
 * 
 */
package com.ailk.service.invoker.priv;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author $Id: AnnotationServicePriv.java 1 2014-02-20 08:34:02Z huangbo $
 * 
 */

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AnnotationServicePriv {
	
	/**
	 * 
	 * @return
	 */
	Class<?> servicePriv() ;
	
}
