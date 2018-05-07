/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com
 * http://www.wadecn.com
 */
package com.ailk.jlcu.mapunit;

/**
 * 总线参数
 * 
 * @author steven zhou
 * @since 1.0
 */
public class Buffvar {
	
	private String name;

	private String type;

	private String ioType;

	public Buffvar(String name, String type, String ioType) {
		this.name = name;
		this.type = type;
		this.ioType = ioType;
	}
	
	public String getIoType() {
		return ioType;
	}

	public void setIoType(String ioType) {
		this.ioType = ioType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
