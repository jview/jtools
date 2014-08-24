package org.jview.jtool.ta_tools;

import java.io.File;
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
public class ToolDate extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolDate.class);
	public static int TASK_ID=1;
	public static String CODE="date";
	public static String HELP_INFO="[-f file] date1-date2[11-01/2011-00-00/now]日期计算";
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
			try {
				List<String> pathList=this.loadFilePath(rValue);
				for(String path:pathList){
					rValue=this.date(path);
					dataList.add(rValue);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				dataList.add(e.getMessage());
				return dataList;
			}

		} else {
			if(rValue.indexOf("+")>0){
				rValue = this.calDate(rValue, "+");
			}
			else if(rValue.indexOf("-")>0){
				rValue = this.calDate(rValue, " - ");
			}
			else{
				rValue=rValue+" 1oper error";
			}
			dataList.add(rValue);
		}
		return dataList;
	}
	
	private Date getDate(String dateStr){
		dateStr = dateStr.trim();
		Date date = null;
		if(dateStr.equals("now")){
			date = new Date();
		}
		else if(dateStr.length()=="00-00".length()){
			date = CommMethod.parseDate(dateStr, "MM-dd");
		}	
		else if(dateStr.length()=="2010-00-00".length()){
			date = CommMethod.parseDate(dateStr, "yyyy-MM-dd");
		}
		return date;
	}
	
	private Date calDate(Date date, int value, String oper){
		if(oper.equals("+")){
			date.setTime(date.getTime()+value);
		}
		else if(oper.equals(" - ")){
			date.setTime(date.getTime()-value);
		}
		return date;
	}
	
	private String calDate(String dateStr, String oper){
		dateStr=dateStr.trim();
		Date date1=null, date2=null;
		String[] dates;
		if(oper.equals("+")){
			dates = dateStr.split("\\+");
		}
		else{
			dates = dateStr.split(oper);
		}
		int unit = 1000*60*60*24;
		String str;
		if(dates.length>1){
			str = dates[0];
			date1=this.getDate(str);
			str= dates[1];
			str=str.trim();
			if(CommMethod.matchNumberstr(str)){
				Date rDate = this.calDate(date1, Integer.parseInt(str)*unit, oper);				
				return dateStr+"="+CommMethod.format(rDate, null);
			}
			else{
				date2 = this.getDate(dates[1]);
			}
		}
		if(date1==null||date2==null){
			return dateStr+" invalid parameter";
		}
		String rInfo;
		
		if(oper.equals("+")){
			rInfo = dateStr+"="+(date1.getTime()+date2.getTime())/unit;
		}
		else if(oper.equals(" - ")){
			rInfo = dateStr+"="+(date1.getTime()-date2.getTime())/unit;
		}
		else{
			rInfo=dateStr+" 2oper error";
		}
		return rInfo;
	}

	private String date(String rValue) {
		File file = new File(rValue);
		
		if (file.exists()) {
			try {
				String[] dates = null;
				List<String> writeList = new ArrayList<String>();
				List<String> lineList = CommMethod.readLineFile(file);
				for (String line : lineList) {
					if (line != null) {
						if(line.indexOf("+")>0){
							line = this.calDate(line, "+");
						}
						else if(line.indexOf(" - ")>0){
							line = this.calDate(line, " - ");
						}
						else{
							line = line+" invalid oper code";
						}
						
					}
					if (!ErrorCode.isEmpty(line)) {
						writeList.add(line);
					}
				}
				CommMethod.writeLineFile(file, writeList);
				rValue = rValue + "文件时间计算成功!";
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

}
