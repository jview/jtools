package org.jview.jtool.ta_tools;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;


/**
 * date日期计算
 * @author chenjh
 *
 */
public class ToolFileModifyTime extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolFileModifyTime.class);
	public static int TASK_ID=1;
	public static String CODE="fileModifyTime";
	public static String HELP_INFO=" fileName [-datetime/-date/-time] [2011-01-01 00:00:00/2011-01-01/00:00:00]修改文件修改时间";
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
//		System.out.println("==============rValue="+rValue);
		List<String> dataList = new ArrayList<String>();
		// TODO Auto-generated method stub
		if(!ErrorCode.isEmpty(rValue)&& !rValue.equals(this.CODE)){
			rValue = rValue.trim();
		}
		else{
//			log4.error("Error:empty para!");
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
				if (!file.exists()) {
					dataList.add(rValue+"的"+path + "目录不存在");
					return dataList;
				}
				if (!file.isDirectory()) {
					dataList.add(rValue+"的"+path + "文件不是目录");
					return dataList;
				}
				String[] tempList = file.list();

//				File temp = null;
				rValue = "";

//				int count=0;
//				//*通配处理，有顺序的分段，从前面开始找，前面找到后，继续往后面找
//				for (String tempName : tempList) {
//					fName = tempName;
//					count=0;
//					for(String nameKey:nameKeys){
//						if (tempName.indexOf(nameKey) >= 0) {
//							tempName=tempName.substring(tempName.indexOf(nameKey)+nameKey.length());
//							count++;							
//						}
//					}
//					if(count==nameKeys.length){
//						rValue = rValue + this.date(path+"/"+fName)+"\n";
//						dataList.add(rValue);
//					}
//				}

			} else {
//				rValue = this.date(rValue);
//				dataList.add(rValue);
			}

		} else {
//			System.out.println("---rValue="+rValue);
			String type="";
			String fileName=rValue.substring(0, rValue.indexOf(" "));
			rValue=rValue.substring(fileName.length());
			rValue=rValue.trim();
			String time=null;
			rValue=rValue.trim();
			if(rValue.startsWith("-datetime")){
				type="datetime";
				rValue=rValue.substring("-datetime".length());
				time=rValue.trim();
			}
			else if(rValue.startsWith("-date")){
				type="date";
				rValue=rValue.substring("-date".length());
				time=rValue.trim();
			}
			else if(rValue.startsWith("-time")){
				type="time";
				rValue=rValue.substring("-time".length());
				time=rValue.trim();
			}
			
//			System.out.println("---fileName="+fileName+" time="+time+" type="+type);
			rValue=this.fileModifyTime(fileName, time, type);
			dataList.add(rValue);
		}
		return dataList;
	}
	
	private String fileModifyTime(String fileName, String time, String type){
		File f = new File("temp.txt");
		String path = f.getAbsolutePath();
//		System.out.println("dict_path="+dict_path);
		path = path.replaceAll("\\\\", "/");
		path = path.substring(0, path.lastIndexOf("/"));
		path=path+"/"+fileName;
		path=path.replaceAll("//", "/");
		f=new File(path);
		if(!f.exists()){
			return "warn:"+path+" file not found!";
		}
		Date fileDate = new Date(f.lastModified());
		Date date=null;
		String format=null;
		if("datetime".equals(type)||type==null){
			format="yyyy-MM-dd HH:mm:ss";
			date = CommMethod.parseDate(time, format);
			if(date==null){
				return "warn:"+time+" format error for format:"+format;
			}
			else{
				f.setLastModified(date.getTime());
				return fileName+" modifyTime change to "+CommMethod.format(date, format);
			}
		}
		else if("date".equals(type)){
			format="yyyy-MM-dd";
			date = CommMethod.parseDate(time, format);
			if(date==null){
				return "warn:"+time+" format error for format:"+format;
			}
			else{
				format="yyyy-MM-dd HH:mm:ss";
				String timeStr=CommMethod.format(fileDate, "HH:mm:ss");
				String dateTimestr=time+" "+timeStr;
				date = CommMethod.parseDate(dateTimestr, format);
				f.setLastModified(date.getTime());
				return fileName+" modifyTime change to "+dateTimestr;
			}
		}
		else if("time".equals(type)){
			format="HH:mm:ss";
			Time timeCur = CommMethod.parseTime(time);
			if(timeCur==null){
				return "warn:"+time+" format error for format:"+format;
			}
			else{
				format="yyyy-MM-dd HH:mm:ss";
				String dateStr=CommMethod.format(fileDate, "yyyy-MM-dd");
				String dateTimestr=dateStr+" "+time;
				date = CommMethod.parseDate(dateTimestr, format);
				f.setLastModified(date.getTime());
				return fileName+" modifyTime change to "+dateTimestr;
			}
		}
		else{
			return "warn:"+ type+" invalid type";
		}
		
		
	}
	

	

}
