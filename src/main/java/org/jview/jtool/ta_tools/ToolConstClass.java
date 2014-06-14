package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;

import com.jview.paras.service.IConstGen;
import com.jview.paras.service.impl.ConstGenJsonImpl;
import com.jview.paras.service.impl.ConstGenPropImpl;
import com.jview.paras.service.impl.ConstGenXmlImpl;

/**
 * ftp
 * @author chenjh
 *
 */
public class ToolConstClass extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolConstClass.class);
	public static int TASK_ID=1;
	public static String CODE="constClass";
	public static String HELP_INFO="[-f path] [-xml/-prop/-json] genFilePath packageName [className],生成constant静态常量类";
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
						rValue = rValue + this.doFile(path+"/"+fName)+"\n";
						dataList.add(rValue);
					}
				}

			} else {
				rValue = this.doFile(rValue);
				dataList.add(rValue);
			}

		} else {
			rValue = this.doConstClass(rValue);
			dataList.add(rValue);
		}
		return dataList;
	}
	
	private IConstGen propGen;

	private String doConstClass(String rValue){
		List<String> typeList = new ArrayList<String>();
		typeList.add("xml");
		typeList.add("prop");
		typeList.add("json");
		int type=-1;
		int index=0;
		for(String typeCode:typeList){			
			if(rValue.indexOf("-"+typeCode)>=0){				
				rValue = rValue.replace("-"+typeCode, "").trim();
				type=index;
			}
			index++;
		}
				
		
		String[] strs = rValue.split(" ");
		String genFilePath=strs[0];
		String packageName=strs[1];
		String className=null;
		if(strs.length>2){
			className = strs[2];
		}
		
		if(type==-1){
			index=0;
			for(String typeCode:typeList){		
				if(genFilePath.endsWith("."+typeCode)){
					type=index;
				}
				index++;
			}
			
		}
		
		if(type==0){
			propGen = new ConstGenXmlImpl(genFilePath, packageName, className);
		}
		else if(type==1){
			propGen = new ConstGenPropImpl(genFilePath, packageName, className);
		}
		else if(type==2){
			propGen = new ConstGenJsonImpl(genFilePath, packageName, className);
		}		
		if(propGen!=null){
			propGen.generateClass();
			log4.info("---文件生成成功");
		}
		return rValue;
	}

	private String doFile(String rValue) {
		File file = new File(rValue);
		if (file.exists()) {
			try {
				List<String> writeList = new ArrayList<String>();
				List<String> lineList = CommMethod.readLineFile(file);
				for (String line : lineList) {
					if (line != null) {
						line = this.doConstClass(line);
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
