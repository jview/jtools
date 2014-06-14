package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;



/**
 * 文件复制
 * @author chenjh
 *
 */
public class ToolCp extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolCp.class);
	public static int TASK_ID=1;
	public static String CODE="cp";
	public static String HELP_INFO="cp source dest 文件复制";
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
//			log4.error("Error:empty para!");
			log4.info(this.CODE+", "+this.HELP_INFO);
			dataList.add(this.CODE+", "+this.HELP_INFO);
			return dataList;
		}
		String source = null;
		String dest = null;
		String[] values = rValue.split(" ");
		if(rValue.split(" ").length>1){
			source = values[0];
			dest = values[1];
			source = source.replaceAll("\\\\", "/");
			dest = dest.replaceAll("\\\\", "/");			
		}
		else{
			dataList.add("Invalid cmd:"+rValue);
			return dataList;
		}
		
		File fSource = new File(source);
		if(!fSource.exists()){
			dataList.add("Source file not found:"+source);
			return dataList;
		}
		
		File fDest = new File(dest);
		if(!fDest.exists()){
			dataList.add("Dest file exists:"+dest);
			return dataList;
		}
		
		
		
		Path.copyFile(fSource, dest);
		dataList.add("");
		return dataList;
	}
}
