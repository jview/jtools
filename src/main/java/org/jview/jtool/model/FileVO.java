package org.jview.jtool.model;

import java.io.File;

public class FileVO {
	public FileVO(String rValue){
		if(rValue!=null){
			rValue = rValue.replaceAll("\\\\", "/");
			this.path = rValue.substring(0, rValue.lastIndexOf("/"));		
			this.fileName = rValue.substring(rValue.lastIndexOf("/") + 1);
		}	
	}
		
	public boolean checkExist(File file){
		return false;
	}
		
	private String fileName;
	private String path;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
