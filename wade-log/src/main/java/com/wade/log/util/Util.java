package com.wade.log.util;

import com.wade.log.Protocal;

public class Util
{
	/**
	 * 根据字符串获取Protocal枚举类型
	 * @param protocal
	 * @return
	 */
	public static Protocal getProtocal(String protocal)
	{
		if(protocal == null || "".equals(protocal))
			return null;
		
		if( Protocal.UDP.getValue().equals(protocal.toLowerCase()) ){
			return Protocal.UDP;
		}else if( Protocal.UDT.getValue().equals(protocal.toLowerCase()) ){
			return Protocal.UDT;
		}else if( Protocal.TCP.getValue().equals(protocal.toLowerCase()) 
				|| Protocal.SOCKET.getValue().equals(protocal.toLowerCase()) ){
			return Protocal.TCP;
		}
		return null;
	}
}