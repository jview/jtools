package org.jview.jtool.ta_tools;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;


/**
 * time时间计算
 * @author chenjh
 *
 */
public class ToolTime extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolTime.class);
	public static int TASK_ID=1;
	public static String CODE="time";
	public static String HELP_INFO="[-h/-hh/-m/-mm] time1-time2[00:00/00:00:00/now]时间计算";
	public int getTaskId(){
		return TASK_ID;
	}
	public String getCode(){
		return CODE;
	}
	public String getHelpInfo(){
		return HELP_INFO;
	}
	
	private int type;//1秒,2分钟,3小时
	private boolean isFormat=false;

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
		isFormat=false;
		if(rValue.indexOf("-hh")>=0){
			type=3;
			isFormat=true;
			rValue = rValue.substring("-hh".length());
		}
		else if(rValue.indexOf("-mm")>=0){
			type=2;
			isFormat=true;
			rValue = rValue.substring("-mm".length());
		}
		else if(rValue.indexOf("-ss")>=0){
			type=1;
			isFormat=true;
			rValue = rValue.substring("-ss".length());
		}
		else if(rValue.indexOf("-h")>=0){
			type=3;			
			rValue = rValue.substring("-h".length());
		}
		else if(rValue.indexOf("-m")>=0){
			type=2;
			rValue = rValue.substring("-m".length());
		}
		else if(rValue.indexOf("-s")>=0){
			type=1;
			rValue = rValue.substring("-s".length());
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

				int count=0;
				//*通配处理，有顺序的分段，从前面开始找，前面找到后，继续往后面找
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
						rValue = rValue + this.time(path+"/"+fName)+"\n";
						dataList.add(rValue);
					}
				}

			} else {
				rValue = this.time(rValue);
				dataList.add(rValue);
			}

		} else {
			if(rValue.indexOf("+")>0){
				rValue = this.calTime(rValue, "+");
			}
			else if(rValue.indexOf("-")>0){
				rValue = this.calTime(rValue, "-");
			}
			else{
				rValue=rValue+" oper error";
			}
			dataList.add(rValue);
		}
		return dataList;
	}
	
	private Time getTime(String timeStr){
		timeStr=timeStr.trim();
		Time time = null;
		if(timeStr.equals("now")){
			time = CommMethod.getCurrentTime();
		}
		else if(timeStr.length()=="00:00".length()
				||timeStr.length()=="00:00:00".length()){
			time = CommMethod.parseTime(timeStr);
		}				
		return time;
	}
	
	private Time calTime(Time time, int value, String oper){
		if(oper.equals("+")){
			time.setTime(time.getTime()+value);
		}
		else if(oper.equals("-")){
			time.setTime(time.getTime()-value);
		}
		return time;
	}
	
	private String calTime(String timeStr, String oper){
		timeStr=timeStr.trim();
		Time time1=null, time2=null;
		String[] times = null;
		
		int unit=1000*60;
		Long value = 0l;
		if(type==1){
			unit=1000;
		}
		else if(type==2){
			unit = 1000*60;
		}
		else if(type==3){
			unit = 1000*60*60;
		}
		
		if(oper.equals("+")){
			times = timeStr.split("\\+");
		}
		else{
			times = timeStr.split(oper);
		}
		if(times.length>1){
			time1=this.getTime(times[0]);
			String str = times[1];
			str = str.trim();
			if(CommMethod.matchNumberstr(str)){
				return timeStr+"="+this.calTime(time1, Integer.parseInt(str)*unit, oper);
			}
			else{
				time2 = this.getTime(times[1]);
			}
		}
		if(time1==null||time2==null){
			return timeStr+" invalid parameter";
		}
		String rInfo;
		
		if(oper.equals("+")){
			value = (time1.getTime()+time2.getTime());
			if(this.isFormat){
//				Time time = CommMethod.getCurrentTime();
//				time.setTime(value);
				rInfo = timeStr+"="+CommMethod.getTime(value/1000);
			}
			else{
				rInfo = timeStr+"="+(value/unit);
			}
			
		}
		else if(oper.equals("-")){
			value = (time1.getTime()-time2.getTime());
			if(this.isFormat){
//				Time time = CommMethod.getCurrentTime();
//				time.setTime(value);
				rInfo = timeStr+"="+CommMethod.getTime(value/1000);
			}
			else{
				rInfo = timeStr+"="+value/unit;
			}
			
		}
		else{
			rInfo=timeStr+" oper error";
		}
		return rInfo;
	}

	private String time(String rValue) {
		File file = new File(rValue);
		
		if (file.exists()) {
			try {
				String[] times = null;
				List<String> writeList = new ArrayList<String>();
				List<String> lineList = CommMethod.readLineFile(file);
				for (String line : lineList) {
					if (line != null) {
						if(line.indexOf("+")>0){
							line = this.calTime(line, "+");
						}
						else if(line.indexOf("-")>0){
							line = this.calTime(line, "-");
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
