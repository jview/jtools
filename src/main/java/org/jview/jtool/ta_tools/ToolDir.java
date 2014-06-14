package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;



/**
 * 显示当前目录
 * @author chenjh
 *
 */
public class ToolDir extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolDir.class);
	public static int TASK_ID=1;
	public static String CODE="dir";
	public static String HELP_INFO="dir [*.*] [-time/-date/-datetime/-length] (.当前目录)";
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
			return dataList;
		}
		if(ErrorCode.isEmpty(rValue)){
			File file = new File("temp.txt");
			String dict_path = file.getAbsolutePath();
//			System.out.println("dict_path="+dict_path);
			dict_path = dict_path.replaceAll("\\\\", "/");
			dict_path = dict_path.substring(0, dict_path.lastIndexOf("/"));
			rValue=dict_path;
			log4.info(rValue);
		}
		else{
			if(rValue.startsWith(this.CODE)){
				rValue=rValue.substring(this.CODE.length());
			}
		}
		
		String type=null;
		if(rValue.startsWith("-datetime")){
			type="datetime";
			rValue=rValue.substring("-datetime".length()+1);
			rValue=rValue.trim();
		}
		else if(rValue.startsWith("-date")){
			type="date";
			rValue=rValue.substring("-date".length()+1);
			rValue=rValue.trim();
		}
		else if(rValue.startsWith("-time")){
			type="time";
			rValue=rValue.substring("-time".length()+1);
			rValue=rValue.trim();
		}
		else if(rValue.startsWith("-length")){
			type="length";
			rValue=rValue.substring("-length".length()+1);
			rValue=rValue.trim();
		}
		
		rValue = rValue.replaceAll("\\\\", "/");
		if(rValue.indexOf("*")<0&&!rValue.endsWith("/")){
			rValue = rValue+"/";
		}
		
		rValue=rValue.trim();
		String path =null;
		String fName=null;
		String[] nameKeys =null;
		
//		System.out.println("----"+rValue);
		if("./".equals(rValue)){
			File file = new File("temp.txt");
			String dict_path = file.getAbsolutePath();
//			System.out.println("dict_path="+dict_path);
			dict_path = dict_path.replaceAll("\\\\", "/");
			dict_path = dict_path.substring(0, dict_path.lastIndexOf("/"));
			rValue=dict_path;
//			log4.info(rValue);
//			System.out.println("-----rValue="+rValue);
			dataList.add(rValue);
			return dataList;
		}
		else if("*".equals(rValue)){
			File file = new File("temp.txt");
			String dict_path = file.getAbsolutePath();
//			System.out.println("dict_path="+dict_path);
			dict_path = dict_path.replaceAll("\\\\", "/");
			dict_path = dict_path.substring(0, dict_path.lastIndexOf("/"));
			path=dict_path;
			nameKeys=new String[0];
		}
		else{
			path = rValue.substring(0, rValue.lastIndexOf("/"));
			fName = rValue.substring(rValue.lastIndexOf("/") + 1);
			nameKeys = fName.split("\\*");
		}
		
		File file = new File(path);
		
		String checkStr = Path.checkDir(path);
		if(checkStr!=null){//检查文件是否存在，是否是目录
			dataList.add(rValue+checkStr);
			return dataList;
		}
		
		File[] tempList = file.listFiles();

//		File temp = null;
		rValue = "";
		int count=0;
		Date date =null;
		for (File f : tempList) {
			
			fName = f.getName();
			date=new Date(f.lastModified());
			if("datetime".equals(type)){
				fName=fName+"	"+CommMethod.format(date, "yyyy-MM-dd HH:mm:ss");
			}
			else if("date".equals(type)){
				fName=fName+"	"+CommMethod.format(date, "yyyy-MM-dd");
			}
			else if("time".equals(type)){
				fName=fName+"	"+CommMethod.format(date, "HH:mm:ss");
			}
			else if("length".equals(type)){
				fName=fName+"	"+f.length();
			}
//			count=0;
//			if(nameKeys.length>0){
//				for(String nameKey:nameKeys){
//					if (tempName.indexOf(nameKey) >= 0) {
//						tempName=tempName.substring(tempName.indexOf(nameKey)+nameKey.length());
//						count++;					
//					}
//				}
//			}
//			else{
//				count++;
//			}
//			if(count==nameKeys.length){
//				rValue =  fName;
//				dataList.add(rValue);
//			}
			dataList.add(fName);
		}
//		System.out.println("----dataList="+dataList.size());
		
		
		return dataList;
	}
}
