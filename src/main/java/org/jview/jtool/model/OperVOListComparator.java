package org.jview.jtool.model;

import java.util.Comparator;

import org.jview.jtool.model.OperVO;


/**
 * 字符串列表排序List<OperVO>
 * @author chenjh
 *
 */
public class OperVOListComparator implements Comparator<OperVO>{  
	/**
	 * 
	 * @param sort	
	 */
	public OperVOListComparator(boolean sort){
		this.sort=sort;
	}
	private boolean sort = false;

	public int compare(OperVO t1, OperVO t2) {
		int result=-1;
        if(sort){        
        	result = t2.getOperId()-t1.getOperId();
        }
        else{
        	result = t1.getOperId()-t2.getOperId();
        }
        return result;
	}

}   