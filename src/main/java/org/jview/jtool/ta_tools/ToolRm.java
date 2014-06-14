package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;


public class ToolRm extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolRm.class);
	public static int TASK_ID=1;
	public static String CODE="rm";
	public static String HELP_INFO="rm [*.*]";
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
//		System.out.println("===================="+rValue);
		if(!ErrorCode.isEmpty(rValue)&& !rValue.equals(this.CODE)){
			rValue = rValue.trim();
		}
		else{
//			log4.error("Error: empty para!");
			log4.info(this.CODE+", "+this.HELP_INFO);
			dataList.add(this.CODE+", "+this.HELP_INFO);
			return dataList;
		}
		
		if(rValue.indexOf("-f")>=0){
			rValue = rValue.substring(rValue.indexOf("-f")+2);
		}
		
		File fSource = new File(rValue);
		if(!fSource.exists()){
			dataList.add("file not found:"+rValue);
			return dataList;
		}
		Path.delFile(rValue);
		
		dataList.add("");
		return dataList;
	}
}
