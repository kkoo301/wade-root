package com.wade.dfs.client.util;

public class ResponseBody {
	public byte errno;
	public byte[] body;

	public ResponseBody(byte errno, byte[] body) {
		this.errno = errno;
		this.body = body;
	}
}
