package org.jview.jtool.ta_tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 通用工具接口
 * @author chenjh
 *
 */
public class ITool{
	public String type="tools";
	public List<String> loadFilePath(String rValue) throws Exception{
		List<String> dataList=new ArrayList();
		rValue = rValue.substring("-f".length()).trim();
		if (rValue.indexOf("*") >= 0) {
			rValue = rValue.replaceAll("\\\\", "/");
			String path = rValue.substring(0, rValue.lastIndexOf("/"));
			String fName = rValue.substring(rValue.lastIndexOf("/") + 1);
			String[] nameKeys = fName.split("\\*");

			File file = new File(path);
			if (!file.exists()) {
//				dataList.add(rValue+"的"+path + "目录不存在");
//				return dataList;
				throw new Exception(rValue+"的"+path + "目录不存在");
			}
			if (!file.isDirectory()) {
//				dataList.add(rValue+"的"+path + "文件不是目录");
//				return dataList;
				throw new Exception(rValue+"的"+path + "文件不是目录");
			}
			String[] tempList = file.list();

//			File temp = null;
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
//					rValue = rValue + this.date(path+"/"+fName)+"\n";
					dataList.add(path+"/"+fName);
				}
			}
	}
		else{
			dataList.add(rValue);
		}
		return dataList;
	}
}
