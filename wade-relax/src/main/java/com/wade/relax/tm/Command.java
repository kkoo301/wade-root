package com.wade.relax.tm;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: Command
 * @description:
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-12-15
 */
public interface Command {
	
	/**
	 * 提交指令
	 */
	public static final String COMMIT = "COMMIT";
	
	/**
	 * 回滚指令
	 */
	public static final String ROLLBACK = "ROLLBACK";
	
}
