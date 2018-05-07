package com.ailk.privm.util;

public class Calculator {
	public static int getHashValue(String key, int prime)
	 {
		int hash_value = key.hashCode() % prime;
		return hash_value < 0 ? -hash_value : hash_value;
	 }
}
