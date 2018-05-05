package com.wade.relax.zk;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.ailk.org.apache.commons.lang3.StringUtils;

public class Main {
	
	private static Set<String> HOSTS = new HashSet<String>();
	
	public static void main(String[] args) {
		String deployHosts = "";
		HOSTS.addAll(Arrays.asList(StringUtils.split(deployHosts, ',')));
		System.out.println(HOSTS);
	}
}
