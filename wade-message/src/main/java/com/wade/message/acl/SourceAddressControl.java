package com.wade.message.acl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public final class SourceAddressControl {

	private static final Logger log = Logger.getLogger(SourceAddressControl.class);
	
	/**
	 * 物理地址控制
	 */
	private static final List<AclNode> phyNodes = new ArrayList<AclNode>();
	
	/**
	 * X-Forwarded-For地址控制
	 */
	private static final List<AclNode> xffNodes = new ArrayList<AclNode>();

	/** 是否检查remote address */
	private static boolean phyCheckEnable = false;
	
	/** 是否检查X-Forwarded-For */
	private static boolean xffCheckEnable = false;
	
	/**
	 * 访问控制配置节点
	 */
	private final static class AclNode {
		int net;
		int len;

		AclNode(int net, int len) {
			this.net = net;
			this.len = len;
		}
	}

	private SourceAddressControl() {}

	public static final void initialize(String aclFile) {

		if (null == aclFile || "".equals(aclFile.trim())) { // 没有定义访问控制配置文件，不做任何控制。
			return;
		}
		
		//如果不是路径则取classLoader下的文件
		if(aclFile.indexOf("/") < 0 && aclFile.indexOf("\\") < 0){
			URL url = SourceAddressControl.class.getClassLoader().getResource(aclFile);
			aclFile = url.getFile();
		}
		
		File file = new File(aclFile);
		if (!file.exists()) {
			log.warn(aclFile + " is not exist!");
			return;
		}
		
		log.info("+----------------------------- NOTICE ------------------------------+");
		log.info("  access control file: " + aclFile);
		log.info("+-------------------------------------------------------------------+");
		
		boolean remoteStart = false;
		boolean xforStart = false;
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				
				if (line.startsWith("#")) {
					continue;
				}
				
				log.info("  " + line);
				
				if (line.equals("[remote-address]")) {
					remoteStart = true;
					xforStart = false;
					continue;
				}
				
				if (line.equals("[x-forward-for]")) {
					remoteStart = false;
					xforStart = true;
					continue;
				}
				
				if (remoteStart) {
					AclNode aclNode = parseAclNode(line);
					phyNodes.add(aclNode);	
				}
								
				if (xforStart) {
					AclNode aclNode = parseAclNode(line);
					xffNodes.add(aclNode);
				}
				
			}
			
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (phyNodes.size() > 0) {
			phyCheckEnable = true; // 打开校验物理地址开关
		}
		
		if (xffNodes.size() > 0) {
			xffCheckEnable = true; // 打开校验x-forward-for地址开关
		}
		
		log.info("+-------------------------------------------------------------------+");
	}
	
	/**
	 * 解析访问控制节点配置
	 * 
	 * @param line
	 * @return
	 */
	private static final AclNode parseAclNode(String line) {

		int pos = line.indexOf("/");
		
		String addr;
		String mask;
		if (-1 != pos) {
			addr = line.substring(0, pos);
			mask = line.substring(pos + 1);
		} else {
			addr = line;
			mask = "32";
		}

		int net = 0;
		String[] slice = split(addr, '.');
		for (String s : slice) {
			net = (net << 8) + Integer.parseInt(s);
		}
		
	    int imask = Integer.parseInt(mask);
	    int rtn = 0;	    
	    for (int i = 31; i >= (32 - imask); i--) {
	        rtn += (0x1 << i);
	    }
	    
	    net = net & rtn;
	    return new AclNode(net, imask);
	}
	
	public static boolean isPhyCheckEnable() {
		return phyCheckEnable;
	}
	
	public static boolean isXffCheckEnable() {
		return xffCheckEnable;
	}
	
	private static final boolean isPermit(List<AclNode> aclNodes, String strIp) {
		
		if (null == strIp || strIp.trim().equals("")) {
			return false;
		}
		
		String[] slice = split(strIp, '.');

		if (4 != slice.length) {
			throw new IllegalArgumentException("invalid ip: " + strIp);
		}

		int n = 0;
		for (String s : slice) {
			int i = Integer.parseInt(s);
			n = (n << 8) + i;
		}

		for (AclNode node : aclNodes) {

			int len = 32 - node.len;
			int k = 31;
			while (k >= len) {
				if (((0x1 << k) & node.net) != ((0x1 << k) & n)) {
					break;
				}

				k--;
			}

			if (k == (len - 1)) {
				return true;
			}

		}

		return false;
	}
		
	public static final boolean isPhyPermit(String strIp) {
		return isPermit(phyNodes, strIp);
	}
	
	public static final boolean isXffPermit(String strIp) {
		return isPermit(xffNodes, strIp);
	}
	
	/**
	 * 按指定字符分割字符串
	 * 
	 * @param str
	 * @param separatorChar
	 * @return
	 */
	private static final String[] split(String str, char separatorChar) {
		if (null == str) {
			return null;
		}

		int len = str.length();
		if (0 == len) {
			return new String[0];
		}

		List<String> list = new ArrayList<String>();
		int i = 0, start = 0;
		boolean match = false;

		while (i < len) {
			if (str.charAt(i) == separatorChar) {
				if (match) {
					list.add(str.substring(start, i));
					match = false;
				}
				start = ++i;
				continue;
			}

			match = true;
			i++;
		}

		if (match) {
			list.add(str.substring(start, i));
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * 32位IP地址用二进制方式打印
	 * 
	 * @param ip
	 */
	@SuppressWarnings("unused")
	private static final void display(int ip) {

		for (int k = 31; k >= 0; k--) {
			if (((0x1 << k) & ip) != 0) {
				System.out.print("1");
			} else {
				System.out.print("0");
			}

			if (k % 8 == 0) {
				System.out.print(" ");
			}
		}

	}
	
}
