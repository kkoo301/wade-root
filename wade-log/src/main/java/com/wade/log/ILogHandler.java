package com.wade.log;

/**
 * 日志执行Handler接口类
 * @author Shieh
 *
 */
public interface ILogHandler
{
	/**
	 * 日志类型
	 * @return
	 * @throws Exception
	 */
	public String getType();
	
	/**
	 * 设置日志类型
	 * @param val
	 */
	public void setType(String val);
	
	/**
	 * 定时调度表达式
	 * @return
	 * @throws Exception
	 */
	public String getCron();
	
	/**
	 * 设置调度表达式
	 */
	public void setCron(String val);
	
	/**
	 * 获取生成新文件时间间隔 (单位 - 秒)
	 * @return
	 */
	public int getSPF();

	/**
	 * 设置创建新文件时间间隔(单位 - 秒)
	 * @param val
	 */
	public void setSPF(int val);
	
	/**
	 * 执行日志入库操作
	 * @param data
	 * @throws Exception
	 */
	public void execute(ILogData data) throws Exception;
}