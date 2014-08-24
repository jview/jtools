package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;


/**
 * sort
 * @author chenjh
 *
 */
public class ToolSort extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolSort.class);
	public static int TASK_ID=1;
	public static String CODE="sort";
	public static String HELP_INFO="[-f] file/ [v1,v2,v3,v4],排序,支持字符排序，纯数字排序，支持类型[,;#/|]，如果是文件会将内容排序后复盖原文件";
	public int getTaskId(){
		return TASK_ID;
	}
	public String getCode(){
		return CODE;
	}
	public String getHelpInfo(){
		return HELP_INFO;
	}
	

	public List<String> doExecute(String rValue) {		
		List<String> dataList = new ArrayList<String>();
		// TODO Auto-generated method stub
		if(!ErrorCode.isEmpty(rValue)&& !rValue.equals(this.CODE)){
			rValue = rValue.trim();			
		}
		else{
//			log4.error("Error: empty para!");
			log4.info(this.CODE+", "+this.HELP_INFO);
			dataList.add(this.CODE+", "+this.HELP_INFO);
			return dataList;
		}
		if (rValue.startsWith("-f")) {
			try {
				List<String> pathList=this.loadFilePath(rValue);
				for(String path:pathList){
					rValue=this.sortFile(path);
					dataList.add(rValue);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				dataList.add(e.getMessage());
				return dataList;
			}

			

		} else {
			rValue = this.sort(rValue);
			dataList.add(rValue);
		}
		return dataList;
	}

	/**
	 * 如果文件只有一行，则对该行的内容进行排序
	 * 如果文件有多行，则对所有行按行进行排序
	 * @author chenjh
	 * @param rValue
	 * @return
	 */
	private String sortFile(String rValue) {
		File file = new File(rValue);
		if (file.exists()) {
			try {
				List<String> writeList = new ArrayList<String>();
				List<String> lineList = CommMethod.readLineFile(file);
				if(lineList.size()==1
						||lineList.size()==2 && ErrorCode.isEmpty(lineList.get(1))) {
					for (String line : lineList) {
						if (line != null) {
							line = line.trim();
							line = this.sort(line);
						}
						if (!ErrorCode.isEmpty(line)) {
							writeList.add(line);
						}
					}
				}
				else {
					writeList=lineList;
					Collections.sort(writeList);
				}
				CommMethod.writeLineFile(file, writeList);
				rValue = rValue + "文件排序成功!";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				rValue = rValue + "--error:" + e.getLocalizedMessage();
			}

		} else {
			rValue = rValue + "文件不存在!";
		}
		return rValue;
	}
	
	private String sort(String src_value){
		String rValue = null;
		String split_str=" ";
		if(src_value.indexOf(",")>0){
			split_str=",";
		}
		else if(src_value.indexOf(";")>0){;
			split_str=";";
		}
		else if(src_value.indexOf("#")>0){;
			split_str="#";
		}
		else if(src_value.indexOf("|")>0){;
			split_str="|";
		}
		else if(src_value.indexOf("/")>0){;
			split_str="/";
		}
		String[] values =null;
		if("|".equals(split_str)) {
			values=src_value.split("\\|");
		}
		else {
			values=src_value.split(split_str);
		}
		//检查是否包含字母
		if(src_value.replaceFirst("[A-Za-z]", "-").indexOf("-")>=0){									
			Arrays.sort(values);
			rValue=CommMethod.getArrayContent(values, split_str);	
		}
		else if(src_value.indexOf(".")>0){
			Float[] floatArrays = CommMethod.getFloatArray(values);
			Arrays.sort(floatArrays);
			rValue=CommMethod.getArrayContent(floatArrays, split_str);
		}
		else{
			Integer[] intArrays = CommMethod.getIntArray(values);
			Arrays.sort(intArrays);
			rValue=CommMethod.getArrayContent(intArrays, split_str);	
		}	
		return rValue;
	}

}
