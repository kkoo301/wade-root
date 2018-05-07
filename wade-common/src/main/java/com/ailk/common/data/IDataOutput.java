package com.ailk.common.data;

import java.io.Serializable;

public interface IDataOutput extends Serializable {
	
	public IDataset getData();
	
	public IData getHead();
	
	public long getDataCount();
	
	public String toString();
}
