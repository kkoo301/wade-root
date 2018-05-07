package com.wade.dfs.client.store;

import java.net.InetSocketAddress;

public class StoreSite {
	
	private InetSocketAddress address;
	private int storePathIndex = 0;
	
	public InetSocketAddress getAddress() {
		return address;
	}
	
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
	
	public int getStorePathIndex() {
		return storePathIndex;
	}
	
	public void setStorePathIndex(int storePathIndex) {
		this.storePathIndex = storePathIndex;
	}
	
	public String toString() {
		return address.toString() + ", storePathIndex=" + this.storePathIndex;
	}
}
