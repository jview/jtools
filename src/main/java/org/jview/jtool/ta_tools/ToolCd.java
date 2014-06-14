package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;

import com.jview.paras.util.ErrorCode;

/**
 * 改变当前目录
 * @author chenjh
 *
 */
public class ToolCd extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolCd.class);
	public static int TASK_ID=1;
	public static String CODE="cd";
	public static String HELP_INFO="cd directory";
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
		if(this.getCode().equals(rValue.trim())){
			rValue="";
		}
		else if(rValue.startsWith(this.getCode()+" ")){
			rValue=rValue.substring(this.getCode().length()+1);
			rValue=rValue.trim();
		}
		
		if(ErrorCode.isEmpty(rValue)){
			String path = System.getProperty("user.dir");
			path = path.replaceAll("\\\\", "/");
			dataList.add(path);
			return dataList;
		}
		rValue = rValue.replaceAll("\\\\", "/");
		String path = System.getProperty("user.dir");
		path = path.replaceAll("\\\\", "/");
		path =  path+"/"+rValue;
		File file = new File(path);
		if(file.exists()){
			path = file.getAbsolutePath();
			path = path.replaceAll("\\\\", "/");
			path = this.convertUpPath(path);
			System.setProperty("user.dir", path);
			dataList.add(path);
		}
		else{
			dataList.add(path+" not exists");
		}
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
