package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.biz.ISystemCmdBO;
import org.jview.jtool.biz.SystemCmdBOImpl;
import org.jview.jtool.tools.AlertMessage;



/**
 * 执行dos命令
 * @author chenjh
 *
 */
public class ToolCmd extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolCmd.class);
	public static int TASK_ID=1;
	public static String CODE="cmd";
	public static String HELP_INFO="cmd (dos command)";
	public int getTaskId(){
		return TASK_ID;
	}
	public String getCode(){
		return CODE;
	}
	public String getHelpInfo(){
		return HELP_INFO;
	}
	
	private ISystemCmdBO systemCmdBo;
	
	@Override
	public List<String> doExecute(String rValue) {
		List<String> dataList = new ArrayList<String>();
		systemCmdBo = new SystemCmdBOImpl();
		AlertMessage aMessage = new AlertMessage();
		
		rValue = rValue.replaceAll("\\\\", "/");
		if("\\".equals(File.separator)){
//			systemCmdBo.doCmdWin("cd "+rValue, null);
			this.systemCmdBo.doCmd("cmd /c "+rValue, null, aMessage);
		}
		else{
//			this.systemCmdBo.doCmd(""+rValue, "/bin/sh", aMessage);
			this.systemCmdBo.doCmdLinux(rValue, "/bin/sh", aMessage);
		}
		for(String msg:aMessage.getMessages()){
			dataList.add(msg);
		}
//		this.systemCmdBo.doCmd("cd "+rValue, null, aMessage);
		
		return dataList;
	}
	
	
}
