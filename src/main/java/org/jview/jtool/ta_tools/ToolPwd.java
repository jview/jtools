package org.jview.jtool.ta_tools;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;



public class ToolPwd extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolPwd.class);
	public static int TASK_ID=1;
	public static String CODE="pwd";
	public static String HELP_INFO="pwd,显示当前路径!";
	public int getTaskId(){
		return TASK_ID;
	}
	public String getCode(){
		return CODE;
	}
	public String getHelpInfo(){
		return HELP_INFO;
	}

	
	@Override
	public List<String> doExecute(String rValue) {
		List<String> dataList = new ArrayList<String>();
		
		rValue = rValue.replaceAll("\\\\", "/");
		String path = System.getProperty("user.dir");
		
		dataList.add(path);
		
		
		return dataList;
	}
	
	
	/**
	 * 转换路径,取消..并向上一级
	 * @return
	 */
	private String convertUpPath(String path){
		if(path.endsWith("..")){
			path = path.substring(0, path.lastIndexOf("/"));
			path = path.substring(0, path.lastIndexOf("/"));
		}
		return path;
	}
	
	
}
