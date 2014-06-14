package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
	public static String HELP_INFO="[-f] file/ [v1,v2,v3,v4],排序,支持字符排序，纯数字排序";
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
			rValue = rValue.substring("-f".length()).trim();
			if (rValue.indexOf("*") >= 0) {
				rValue = rValue.replaceAll("\\\\", "/");
				String path = rValue.substring(0, rValue.lastIndexOf("/"));
				String fName = rValue.substring(rValue.lastIndexOf("/") + 1);
				String[] nameKeys = fName.split("\\*");

				File file = new File(path);
				String checkStr = Path.checkDir(path);
				if(checkStr!=null){//检查文件是否存在，是否是目录
					dataList.add(rValue+checkStr);
					return dataList;
				}
				String[] tempList = file.list();

//				File temp = null;
				rValue = "";

				int count=0;
				
				for (String tempName : tempList) {
//					 System.out.println(tempName);
					fName = tempName;
					count=0;
					for(String nameKey:nameKeys){
						if (tempName.indexOf(nameKey) >= 0) {
							tempName=tempName.substring(tempName.indexOf(nameKey)+nameKey.length());
	//						System.out.println(path+"/"+tempName);
//							System.out.println(nameKey+"-------"+tempName);
							count++;
							
						}
					}
					if(count==nameKeys.length){
						rValue = rValue + this.sortFile(path+"/"+fName)+"\n";
						dataList.add(rValue);
					}
				}

			} else {
				rValue = this.sortFile(rValue);
				dataList.add(rValue);
			}

		} else {
			rValue = this.sort(rValue);
			dataList.add(rValue);
		}
		return dataList;
	}

	private String sortFile(String rValue) {
		File file = new File(rValue);
		if (file.exists()) {
			try {
				List<String> writeList = new ArrayList<String>();
				List<String> lineList = CommMethod.readLineFile(file);
				for (String line : lineList) {
					if (line != null) {
						line = line.trim();
						line = this.sort(line);
					}
					if (!ErrorCode.isEmpty(line)) {
						writeList.add(line);
					}
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
		String[] values =src_value.split(split_str);	
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
