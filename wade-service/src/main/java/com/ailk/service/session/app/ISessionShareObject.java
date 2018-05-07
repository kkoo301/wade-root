/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package com.ailk.service.session.app;

/**
 * 线程会话共享对象
 * 
 * @className: ISessionShareObject.java
 * @author: liaosheng
 * @date: 2014-6-7
 */
public interface ISessionShareObject {
	
	/**
	 * 事务提交后会执行该方法
	 * 该方法里无法通过线程获取数据库连接对象，可使用Connection conn = new DBConnection(...)，且自已控制事务
	 */
	public void clean();
}
