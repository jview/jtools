package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;


/**
 * 显示文件内容
 * @author chenjh
 *
 */
public class ToolCat extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolCat.class);
	public static int TASK_ID=1;
	public static String CODE="cat";
	public static String HELP_INFO="cat [*.*]";
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
		if(ErrorCode.isEmpty(rValue)){
			File file = new File("temp.txt");
			String dict_path = file.getAbsolutePath();
//			System.out.println("dict_path="+dict_path);
			dict_path = dict_path.replaceAll("\\\\", "/");
			dict_path = dict_path.substring(0, dict_path.lastIndexOf("/"));
			rValue=dict_path;
		}
		rValue = rValue.replaceAll("\\\\", "/");
		if(rValue.indexOf("*")<0&&!rValue.endsWith("/")){
			rValue = rValue+"/";
		}
		
		String path = rValue.substring(0, rValue.lastIndexOf("/"));
		String fName = rValue.substring(rValue.lastIndexOf("/") + 1);
		String[] nameKeys = fName.split("\\*");
		File file = new File(path);
		String checkStr = Path.checkFile(path);
		if(checkStr!=null){//检查文件是否存在，是否是文件
			dataList.add(rValue+checkStr);
			return dataList;
		}
		try {
			List<String> tempList = CommMethod.readLineFile(file);

//		File temp = null;
			rValue = "";
			int count=0;
			
			for (String tempName : tempList) {
				fName = tempName;
				count=0;
				for(String nameKey:nameKeys){
					if (tempName.indexOf(nameKey) >= 0) {
						tempName=tempName.substring(tempName.indexOf(nameKey)+nameKey.length());
						count++;					
					}
				}
				if(count==nameKeys.length){
					rValue =  fName;
					dataList.add(rValue);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			rValue=e.getMessage();
		}

		
		
		return dataList;
	}
}
