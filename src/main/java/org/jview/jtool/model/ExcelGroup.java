package org.jview.jtool.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelGroup {
	private String lang;
	private List columnList;
	private Map headMap;
	private void init(){
		this.headMap=new HashMap();
		ExcelColumn ec = null;
		for(int i=0; i<this.columnList.size(); i++){
			ec = (ExcelColumn)this.columnList.get(i);
			this.headMap.put(ec.getCode(), ec.getColumn());
		}
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public List getColumnList() {
		return columnList;
	}
	public void setColumnList(List columnList) {
		this.columnList = columnList;
	}
	public Map getHeadMap() {
		if(this.headMap==null){
			this.init();
		}
		return headMap;
	}
	public void setHeadMap(Map headMap) {
		this.headMap = headMap;
	}
	

}

