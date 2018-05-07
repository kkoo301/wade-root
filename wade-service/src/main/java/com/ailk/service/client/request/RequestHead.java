/**
 * 
 */
package com.ailk.service.client.request;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yifur
 *
 */
public class RequestHead {
	
	public Map<String, String> head = null;
	
	private String staffId;//工号
	private String password;//密码
	private String provinceCode; //省别编码
	private String inModeCode;//接入编码
	private String serialNumber;//服务号码
	
	public RequestHead() {
		this.head = new HashMap<String, String>();
	}
	
	/**
	 * @return the staffId
	 */
	public String getStaffId() {
		return staffId;
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @return the inModeCode
	 */
	public String getInModeCode() {
		return inModeCode;
	}
	
	/**
	 * @return the provinceCode
	 */
	public String getProvinceCode() {
		return provinceCode;
	}
	
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	
	
	/**
	 * @param inModeCode the inModeCode to set
	 */
	public void setInModeCode(String inModeCode) {
		this.inModeCode = inModeCode;
		this.head.put("IN_MODE_CODE", inModeCode);
	}
	
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
		this.head.put("PASSWORD", password);
	}
	
	/**
	 * @param provinceCode the provinceCode to set
	 */
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
		this.head.put("PROVINCE_CODE", provinceCode);
	}
	
	/**
	 * @param staffId the staffId to set
	 */
	public void setStaffId(String staffId) {
		this.staffId = staffId;
		this.head.put("STAFF_ID", staffId);
	}
	
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		this.head.put("SERIAL_NUMBER", serialNumber);
	}
	
	
	/**
	 * @return the head
	 */
	public Map<String, String> getHead() {
		return head;
	}
}
