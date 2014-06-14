package org.jview.jtool.model;

import java.util.Comparator;

/**
 * 字符串列表排序List<String>
 * @author chenjh
 *
 */
public class StringListComparator implements Comparator<String>{  
	/**
	 * 
	 * @param sort
	 * @param colCount
	 * @param isFirst
	 */
	public StringListComparator(boolean sort, int colCount){
		this.colCount=colCount;
		this.sort=sort;
	}
	private boolean sort = false;
	private int colCount=0;	
	private String value1, value2;
	private int v0, v1;

//	@Override
	public int compare(String arg1, String arg2) {
//		String arg0=(String)o1;
//		String arg1=(String)o2;
		// TODO Auto-generated method stub
		String[] arrays1 = arg1.split("\t");
    	String[] arrays2 = arg2.split("\t");    
    	value1 = arrays1[colCount];
    	value2 = arrays2[colCount];
        int result =-1;
        if(value1.matches("\\d+")){
        	v0 = Integer.parseInt(value1.trim());
        	v1 = Integer.parseInt(value2.trim());
	        if(sort){        
	        	result = v0-v1;
	        }
	        else{
	        	result = v1-v0;
	        }
        }
        else{
        	if(sort){        
	        	result = value1.compareTo(value2);
	        }
	        else{
	        	result = value2.compareTo(value1);
	        }
        }
        return result;
	}

}   