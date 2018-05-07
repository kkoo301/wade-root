package com.wade.dfs.client.util;

public class ResponseHead {
	public byte errno;
	public long bodyLen;

	public ResponseHead(byte errno, long bodyLen) {
		this.errno = errno;
		this.bodyLen = bodyLen;
	}
}
