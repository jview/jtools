package org.jview.jtool.ta_replace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;


public class RepCkExist extends IRep implements ITask {
	private static Logger log4 = Logger.getLogger(RepCkExist.class);
	public static int OPER_ID=4;
	public static String CODE="ckExist";
	public static String HELP_INFO="source dest ,检查文件已存在";
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return CODE;
	}

	@Override
	public int getTaskId() {
		// TODO Auto-generated method stub
		return OPER_ID;
	}

	@Override
	public String getHelpInfo() {
		// TODO Auto-generated method stub
		return HELP_INFO;
	}

	@Override
	public List<String> doExecute(String rValue) {
		List<String> dataList = new ArrayList<String>();
		// TODO Auto-generated method stub
		if(!ErrorCode.isEmpty(rValue)&& !rValue.equals(this.CODE)){
			rValue = rValue.trim();
		}
		else{
//			log4.error("Error: empty para!");
			log4.info(this.CODE+", "+this.HELP_INFO);
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
//				rValue = this.doFile(rValue);
//				dataList.add(rValue);
				dataList.addAll(this.doCheck(rValue));
			}

		} else {
//			rValue = this.doFtp(rValue);
			dataList.addAll(this.doCheck(rValue));
		}
		return dataList;
	}

	private List<String> doCheck(String rValue){
		if(rValue.indexOf(" ")<0){
			log4.info(this.getCode()+" "+this.getHelpInfo());
			return new ArrayList();
		}
		String sourcePath = rValue.substring(0, rValue.indexOf(" ")).trim();
		String destPath = rValue.substring(rValue.indexOf(" ")).trim();
		Path.sourcePathStart=null;				
		List dataList=Path.checkFileExists(sourcePath, destPath);
//		System.out.println("=========="+this.dataList.size());

		return dataList;
	}

	private String doFile(String rValue) {
		File file = new File(rValue);
		if (file.exists()) {
			try {
				List<String> writeList = new ArrayList<String>();
//				List<String> lineList = CommMethod.readLineFile(file);
//				for (String line : lineList) {
//					if (line != null) {
//						line = this.doFtp(line);
//					}
//					if (!ErrorCode.isEmpty(line)) {
//						writeList.add(line);
//					}
//				}
				writeList.addAll(this.doCheck(rValue));
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
