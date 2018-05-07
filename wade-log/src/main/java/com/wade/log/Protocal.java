package com.wade.log;

public enum Protocal
{
	SOCKET("socket"), UDP("udp"), UDT("udt"), TCP("tcp");
	
	private final String value;
	
	Protocal(String val) {
        value = val;
    }
    
    public String getValue() {
        return value;
    }
}