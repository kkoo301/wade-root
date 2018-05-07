/**
 * Copyright (c) 2010-8-15 AsiaInfo, Inc.
 * All rights reserved. 
 *
 * http://www.asiainfo.com 
 * http://www.wadecn.com
 */
package com.ailk.jlcu.mapunit;

/**
 * 节点间的连线类
 * 
 * @author steven zhou
 * @since 1.0
 */
public class Link {

	/** 线段标识 */
	private String linkId;

	/** 线段描述 */
	private String linkDesc;

	/** 线段起点 */
	private String from;

	/** 线段终点 */
	private String to;

	public Link(String id, String desc, String from, String to) {
		this.linkId = id;
		this.linkDesc = desc;
		this.from = from;
		this.to = to;
	}

	public String getLinkId() {
		return linkId;
	}

	public String getLinkDesc() {
		return linkDesc;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}
}
