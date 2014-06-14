package org.jview.jtool.model;

import java.util.Comparator;

/**
 * 字符串排序，支持纯数字排序
 * @author chenjh
 *
 */
public class StringArrayComparator implements Comparator<String>{  
	/**
	 * 
	 * @param sort
	 * @param colCount
	 * @param isFirst
	 */
	public StringArrayComparator(boolean sort){
		this.sort=sort;
	}
	private boolean sort = false;
//	private String value0, value1;
	private int v0, v1;

//	@Override
	public int compare(String value1, String value2) {
//		value0=(String)o1;
//		value2=(String)o2;
		// TODO Auto-generated method stub 

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