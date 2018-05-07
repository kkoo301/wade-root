package com.ailk.database.jdbc;

import java.util.UUID;

public class TraceInfo {
	
	private UUID uuid;
	private long traceTime;
	private String stack;
	
	public TraceInfo() {
		
	}

	
	public UUID getUuid() {
		return uuid;
	}
	
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	public long getTraceTime() {
		return traceTime;
	}
	
	public void setTraceTime(long traceTime) {
		this.traceTime = traceTime;
	}
	
	public String getStack() {
		return stack;
	}
	
	public void setStack(String stack) {
		this.stack = stack;
	}
}
