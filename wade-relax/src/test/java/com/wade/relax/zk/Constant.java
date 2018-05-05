package com.wade.relax.zk;

import java.util.HashSet;
import java.util.Set;

public class Constant {
	public static final String zkServerAddr = "10.143.24.15:21810,10.143.24.16:21810,10.143.24.17:21810";
	public static final Set<String> upcServices = new HashSet<String>();
	public static final Set<String> resServices = new HashSet<String>();

	static {
		upcServices.add("Upc.ServiceName.A");
		upcServices.add("Upc.ServiceName.B");
		
		resServices.add("Res.ServiceName.A");
		resServices.add("Res.ServiceName.B");
	}
	
	
}
