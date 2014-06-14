package org.jview.jtool.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jview.jtool.tools.AutoTool;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;






public class ListCache {
	
	public ListCache(){
		this.init();
	}
	private String fileName="d:/log4j/monitor.log";
	private String split_str=",";
	private boolean lower_case=false;
	private AutoTool aTool = new AutoTool();
	public void init(){
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
		}
		aTool.clear();
	}
	
	
	public void dispDataList(String filter) throws Exception{
		File file = new File(fileName);
		List<String> list = CommMethod.readLineFile(file);
		list = CommMethod.filterContentLower(list, filter, split_str);
		CommMethod.print(list);
		
	}
	
	/**
	 * 从文件中查询数据
	 * 多个条件实现and查询
	 * @param filter，支持多个，如filter1,filter2,filter3...
	 * @throws IOException
	 */
	public void dispData(String filter) throws IOException{		
		File file = new File(fileName);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		String flt = null;
		boolean flt_exist=false;
		
		if(ErrorCode.isEmpty(filter)){
			
		}
		else if(filter.equals("%")){
		
		}
		else{
			flt = filter.trim();
			if(this.lower_case){
				flt = flt.toLowerCase();
			}
			flt_exist=true;
		}
//		System.out.println("filter"+dataList.get(0));		

		this.aTool.clear();
		String line = null;
		String line_info=null;
		boolean isExist=false;
		boolean isFound=false;
		int count=0;
		int i=0;
		int j=0;
	
		int flt_count=0;
		String[] flt_array = flt.split(split_str);
		while((line = br.readLine())!=null){
			isExist=false;
			if(this.lower_case){
				line_info = line.toLowerCase();
			}
			else{
				line_info = line;
			}
			
			if(!flt_exist){
				isExist=true;
			}
			else {
				flt_count=0;
				j=0;
				isFound=false;
				for(String fstr:flt_array){
					
					
					if(line_info.indexOf(fstr)>=0){
						flt_count++;
						
						
						
						if((!isFound &&j==0)|| (isFound&&j>0)){
							this.aTool.addMapCount(fstr);
						}
						
						isFound=true;
//						isExist=true;
					
						
						
						
						
						if(flt_count==flt_array.length){
							isExist=true;
						}
						
					}
					j++;
//					System.out.println(fstr+" isExist="+isExist+"-------"+line_info);
				}
				
			}
			if(isExist){
//				i++;
				System.out.println(line);
			}
		}
		for(String fstr:flt_array){
			System.out.println(this.aTool.getMapCount(fstr));
		}
	
		
		br.close();
		fr.close();
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		ListCache lc = new ListCache();
		lc.init();
		try {
//			lc.dispData("javacError");
			lc.setFileName("d:/log4j/monitor_all.log");
			lc.dispData("TaskListener,monitor,type=3");
//			lc.dispDataList("TaskListener,monitor,type=3");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("time="+(System.currentTimeMillis()-time));
	}
	
}
