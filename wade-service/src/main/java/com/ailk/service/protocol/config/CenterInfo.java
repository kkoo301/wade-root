package com.ailk.service.protocol.config;

public class CenterInfo {
	
	/**
	 * 中心名称
	 */
	private String centerName;
	
	/**
	 * 组名称
	 */
	private String groupName;
	
	/**
	 * 中心描述
	 */
	private String centerDesc;
	
	/**
	 * 是否允许跨中心调用，默认为false
	 */
	private boolean isCrossCentre = false;
	
	private String version;
	
	public CenterInfo() {
		
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getCenterDesc() {
		return centerDesc;
	}
	
	public void setCenterDesc(String centerDesc) {
		this.centerDesc = centerDesc;
	}
	
	public String getCenterName() {
		return centerName;
	}
	
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	/**
	 * @return the isCrossCentre
	 */
	public boolean isCrossCentre() {
		return isCrossCentre;
	}
	
	/**
	 * @param isCrossCentre the isCrossCentre to set
	 */
	public void setCrossCentre(boolean isCrossCentre) {
		this.isCrossCentre = isCrossCentre;
	}
	
}
