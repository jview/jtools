package org.jview.jtool.biz;

import org.jview.jtool.tools.AlertMessage;


/**
 * 执行dos命令
 * @author chenjh
 *
 */
public interface ISystemCmdBO {
	/**
	 * 执行dos或linux shell命令
	 * 
	 * @param command
	 */
	public  void doCmd(String command, String prepare);

	/**
	 * 执行dos或linux shell命令
	 * 
	 * @param command
	 */
	public  void doCmd(String command, String prepare,
			AlertMessage aMessage);

	public  void doCmdWin(String command, String prepare);

	/**
	 * 执行dos命令
	 *如果prepare==null,则不用prepare直接执行dos命令
	 *如果prepare!=null,则使用prepare,再执行dos命令
	 * @param command
	 * 命令 exp:cd path
	 */
	public  void doCmdWin(String command, String prepare,
			AlertMessage aMessage);

	/**
	 * 执行shell命令
	 * 
	 * @param command
	 *            命令 exp:cd path
	 */
	public  void doCmdLinux(String command, String prepare,
			AlertMessage aMessage);

}
