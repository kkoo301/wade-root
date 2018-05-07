/**
 * 
 */
package com.ailk.common.data;

/**
 * @author yifur
 *
 */
public interface IResult {
	
	public String getResultCode();
	
	public void setResultCode(String resultCode);
	
	public String getResultInfo();
	
	public void setResultInfo(String resultInfo);
	
	public long getResultCount();
	
	public void setResultCount(long resultCount);
	
}
