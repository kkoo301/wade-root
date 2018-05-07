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
 * @description 为BO对象添加数据库表属性映射功能，包括：<br>
 * 表名，数据库用户，数据库方言，操作用户，主键字段，唯一索引字段，分表字段
 */

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Table {

	/**
	 * 数据库表名
	 * 
	 * @return
	 */
	public String name() default "";

	
	/**
	 * 数据库方言
	 * @return
	 */
	public String dialect() default "oracle";
	
	/**
	 * 主键字段，多个以","号分隔
	 * @return
	 */
	public String primary() default "";
	
	/**
	 * 唯一索引字段，多个以","号分隔
	 * @return
	 */
	public String unique() default "";

}
