package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.tools.FtpUtil;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;


/**
 * ftp
 * @author chenjh
 *
 */
public class ToolFtp extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolFtp.class);
	public static int TASK_ID=1;
	public static String CODE="ftp";
	public static String HELP_INFO="[-f] (path/file) login host user password port/dir path/download sourceFile destPath,模拟登入";
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
				dataList.addAll(this.doFtp(rValue));
			}

		} else {
			dataList.addAll(this.doFtp(rValue));
		}
		return dataList;
	}
	private FtpUtil ftpUtil;
	private List doFtp(String rValue){
		List<String> dataList = new ArrayList<String>();
		try {
			if(rValue.startsWith("login")){
				rValue = rValue.substring("login".length()).trim();
				String[] strs = rValue.split(" ");
				String host = strs[0];
				String username=strs[1];
				String password=strs[2];
				int port = 21;
				if(strs.length>3){
					port = Integer.parseInt(strs[3]);
				}
				ftpUtil = new FtpUtil(host, port, username, password);
				ftpUtil.login();	
				rValue="登入";
				dataList.add(rValue);		
			}
			else if(rValue.startsWith("download")){
				rValue = rValue.substring("download".length()).trim();
				String[] strs = rValue.split(" ");
				String sourceFileName=strs[0];
				String destinationFileName=null;
				if(strs.length>1){
					destinationFileName=strs[1];
				}
				if(ftpUtil!=null){
					if(destinationFileName!=null){
						rValue = ftpUtil.downFile(sourceFileName, destinationFileName);
					}
					else{
						destinationFileName=sourceFileName.substring(sourceFileName.lastIndexOf("/")+1);
						File file = new File("temp.txt");
						String dict_path = file.getAbsolutePath();
//						System.out.println("dict_path="+dict_path);
						dict_path = dict_path.replaceAll("\\\\", "/");
						dict_path = dict_path.substring(0, dict_path.lastIndexOf("/"));
						destinationFileName=dict_path+"/"+destinationFileName;						
						rValue = ftpUtil.downFile(sourceFileName, destinationFileName);														
					}					
				}
				else{
					rValue="未登入";
					dataList.add(rValue);					
				}
			}
			else if(rValue.startsWith("dir")){
				rValue = rValue.substring("dir".length()).trim();
				String[] strs = rValue.split(" ");
				String path=strs[0];
				if(ftpUtil!=null){
					dataList.addAll(ftpUtil.getNameList(path));
				}
				else{
					rValue="未登入";
					dataList.add(rValue);		
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dataList.add(e.getMessage());
		}
		return dataList;
	}

	private String doFile(String rValue) {
		File file = new File(rValue);
		if (file.exists()) {
			try {
				List<String> writeList = new ArrayList<String>();
				List<String> lineList = CommMethod.readLineFile(file);
				for (String line : lineList) {
					if (line != null) {						
						writeList.addAll(this.doFtp(line));
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
