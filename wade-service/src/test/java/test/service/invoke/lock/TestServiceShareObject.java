/*
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * http://www.wadecn.com/
 * WADE4.0
 */
package test.service.invoke.lock;

import org.apache.log4j.Logger;

import com.ailk.service.session.app.ISessionShareObject;

/**
 * App线程共享对象
 * 
 * @className: TestServiceShareObject.java
 * @author: liaosheng
 * @date: 2014-6-9
 */
public class TestServiceShareObject implements ISessionShareObject {
	
	private static final transient Logger log = Logger.getLogger(TestServiceShareObject.class);
	
	private StringBuilder obj = new StringBuilder("hello ");
	
	public void testToDo(String key) {
		obj.append(key);
	}
	
	
	/* (non-Javadoc)
	 * @see com.ailk.service.session.app.ISessionShareObject#clean()
	 */
	@Override
	public void clean() {
		if (log.isDebugEnabled()) {
			log.debug(obj.toString());
		}
	}

}
