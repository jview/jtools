package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.biz.ExcelBizImpl;
import org.jview.jtool.biz.ExcelDataBizImpl;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.ta_dbs.DbSelect;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;


/**
 * export按xls模板导出sql结果对应的excel文件
 * 
 * @author chenjh
 *
 */
public class ToolExport extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolExport.class);
	public static int TASK_ID=1;
	public static String CODE="export";
	public static String HELP_INFO="[-code code] [-name name] [-lang cn/en] [-xlt xltFileName] [-out outFileName] select [column1,column2] from tableName";
	private String xltFileName;
	private String outFileName;
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
//			log4.info(this.CODE+", "+this.HELP_INFO);
			dataList.add(this.CODE+", "+this.HELP_INFO);
			dataList.add(this.CODE+", "+this.HELP_INFO);
			return dataList;
		}
		
		DBTool dbTool = TaskManager.getDBTool();
		if(!dbTool.isInit){
			dbTool.init();
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
						rValue = rValue + this.executeFile(path+"/"+fName)+"\n";
						dataList.add(rValue);
					}
				}

			} else {
				rValue = this.executeFile(rValue);
				dataList.add(rValue);
			}

		} else {
			
			rValue = this.exportExcel(rValue);
			
			dataList.add(rValue);
		}
		return dataList;
	}
	
	private String exportExcel(String rValue){
		ExcelDataBizImpl exData = new ExcelDataBizImpl();
		exData.setExcelBo(new ExcelBizImpl());
		String formatXls = "format_mail_send_group.xlt";
		String outFile = "d:/data/jtool/mail_send.xls";
		String keys = "ccar";
		String code="code";
		String name="name";
		String lang="cn";
		rValue = CommMethod.merchBlank(rValue);
		if(rValue.indexOf("-xlt ")>=0){
			formatXls=CommMethod.getParameterByKey(rValue, "-xlt");
		}
		if(rValue.indexOf("-out ")>=0){
			outFile=CommMethod.getParameterByKey(rValue, "-out");
		}
		if(rValue.indexOf("-lang ")>=0){
			lang=CommMethod.getParameterByKey(rValue, "-lang");
		}
		if(rValue.indexOf("-code ")>=0){
			code=CommMethod.getParameterByKey(rValue, "-code");
		}
		if(rValue.indexOf("-name ")>=0){
			name=CommMethod.getParameterByKey(rValue, "-name");
		}
		if(rValue.indexOf("select ")>=0){
			rValue = rValue.substring(rValue.indexOf("select"));
		}
		
		
		String dataSql =rValue;
		if(dataSql.indexOf("*")>0){
			String task_key = "dbs";
			ITask dbOper =null;
			if(TaskManager.getTaskMap()!=null){
				dbOper = (ITask)TaskManager.getTaskMap().get(task_key+"_"+"select");
			}
			if(dbOper==null){
				dbOper = new DbSelect();
			}
			dataSql = dbOper.doExecute(dataSql).get(0);
		}
		
		int startLine=2;
		try {
			exData.export(keys, dataSql, formatXls, lang, code, name, startLine, outFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
			return e.getMessage();
		}
		return "success";
	}
	
	
	private String executeFile(String rValue) {
		File file = new File(rValue);
		
		if (file.exists()) {
			try {
				String[] times = null;
				List<String> writeList = new ArrayList<String>();
				List<String> lineList = CommMethod.readLineFile(file);
				for (String line : lineList) {
					if (line != null) {						
						line = this.exportExcel(line);
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
