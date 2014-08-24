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
 * trim
 * @author chenjh
 *
 */
public class ToolTrim extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolTrim.class);
	public static int TASK_ID=1;
	public static String CODE="trim";
	public static String HELP_INFO="[-f] (path/file)/value,去空格或空行";
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
//			log4.error("Error:rValue is empty!");
			log4.info(this.CODE+", "+this.HELP_INFO);
			dataList.add(this.CODE+", "+this.HELP_INFO);
			return dataList;
		}
		if (rValue.startsWith("-f")) {
			try {
				List<String> pathList=this.loadFilePath(rValue);
				for(String path:pathList){
					rValue=this.trim(path);
					dataList.add(rValue);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				dataList.add(e.getMessage());
				return dataList;
			}
		} else {
			rValue = rValue.trim();
			dataList.add(rValue);
		}
		return dataList;
	}

	private String trim(String rValue) {
		File file = new File(rValue);
		if (file.exists()) {
			try {
				List<String> writeList = new ArrayList<String>();
				List<String> lineList = CommMethod.readLineFile(file);
				for (String line : lineList) {
					if (line != null) {
						line = line.trim();
					}
					if (!ErrorCode.isEmpty(line)) {
						writeList.add(line);
					}
				}
				CommMethod.writeLineFile(file, writeList);
				rValue = rValue + "文件去除首尾空格或空行成功!";
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
