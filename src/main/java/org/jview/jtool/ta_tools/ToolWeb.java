package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.tools.WebUtil;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;



/**
 * web模拟登入
 * @author chenjh
 *
 */
public class ToolWeb extends ITool implements ITask {
	private static Logger log4 = Logger.getLogger(ToolWeb.class);
	public static int TASK_ID=1;
	public static String CODE="web";
	public static String HELP_INFO="[login url 模拟登入] [-cookie] [-f file] url访问网站";
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
			dataList.addAll(this.doWeb(rValue));
		}
		return dataList;
	}
	private WebUtil webUtil = new WebUtil();
	private List doWeb(String rValue){
		boolean isByCookie=false;
		if(rValue.indexOf("-cookie")>=0){
			isByCookie=true;
			rValue = rValue.replaceAll("-cookie", "");
			rValue = rValue.trim();
		}
		
//		System.out.println("------doWeb-------"+rValue);
		List<String> dataList = new ArrayList<String>();
			
		if(rValue.startsWith("login")){			
			rValue = rValue.substring("login".length()).trim();
			String[] strs = rValue.split(",");
			String url = strs[0];
			String loginUrl = url.substring(url.lastIndexOf("/"));
			url = url.substring(0, url.lastIndexOf("/"));
//			System.out.println("-------------03-"+strs.length);
//			System.out.println("---baseUrl="+url+" loginUrl="+loginUrl);
			webUtil.setBase_url(url);
			webUtil.setLogin_url(loginUrl);
			if(strs.length>=3){
				webUtil.setUsername(strs[1]);
				webUtil.setPassword(strs[2]);
			}
//			System.out.println("-------------04-");
			try {
//				System.out.println("-------------0-");
				webUtil.login();
//				System.out.println("--------------1=");
				dataList.add("do login finish");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				dataList.add(e.getMessage());
			}
			
		}
		else{
//			System.out.println("--------------2=");
			String url=rValue;
			String filter = null;
			if(rValue.indexOf(" ")>0){
				url = url.substring(0, url.indexOf(" "));
				filter = rValue.substring(rValue.indexOf(" ")).trim(); 
			}
			String visitUrl = url.substring(url.lastIndexOf("/"));
			url = url.substring(0, url.lastIndexOf("/"));
			webUtil.setUrl(visitUrl);
			webUtil.setBase_url(url);
			List<String> htmlList=null;
			try {
				htmlList = webUtil.visit(isByCookie);
				if(!ErrorCode.isEmpty(filter)){
					htmlList =CommMethod.filterValue(htmlList ,filter);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(htmlList!=null)
				dataList=htmlList;
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
						writeList.addAll(doWeb(line));
					}
//					if (!ErrorCode.isEmpty(line)) {
//						writeList.add(line);
//					}
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
