/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com 
 * http://www.wadecn.com
 */
package com.ailk.jlcu.mapunit;

import java.util.List;

/**
 * 参数映射
 * 
 * @author steven zhou
 * @since 1.0
 */
public class Varmap {

	/** 参数名 */
	private String name;

	/** 数据总线映射名 */
	private String mapname;
	
	/** 传参方式 */
	private boolean isclone;
	
	/** 预处理动作 */
	private List<String> opers;

	public Varmap(String name, String mapname) {
		this.name = name;
		this.mapname = mapname;
	}

	public String getName() {
		return name;
	}

	public String getMapname() {
		return mapname;
	}
	
	public String toString() {
		return "name=" + name + " mapname=" + mapname;
	}

	public void setOpers(List<String> opers) {
		this.opers = opers;
	}

	public List<String> getOpers() {
		return opers;
	}

	public void setMapname(String mapname) {
		this.mapname = mapname;
	}

	public boolean isIsclone() {
		return isclone;
	}

	public void setIsclone(boolean isclone) {
		this.isclone = isclone;
	}
}
