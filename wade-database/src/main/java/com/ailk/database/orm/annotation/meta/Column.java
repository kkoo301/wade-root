/**
 * Copyright: Copyright (c) 2017 Asiainfo
 * 
 * @version: v1.0.0
 * @date: 2017年4月20日
 * 
 * Just Do IT.
 */
package com.ailk.database.orm.annotation.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description 为BO对象添加数据库表字段映射功能<br>
 * 字段名，字段类型
 */

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Column {

	/**
	 * 数据库表名
	 * 
	 * @return
	 */
	public String name() default "";

	/**
	 * 字段类型
	 * @return
	 */
	public int type();
	
	/**
	 * 字段长度
	 * @return
	 */
	public int length();
	
	
	/**
	 * 默认值
	 * @return
	 */
	public String defval() default "";
	
	public String desc() default "";
}
