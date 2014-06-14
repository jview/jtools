package org.jview.jtool.model;

import java.util.Comparator;

import org.jview.jtool.model.TaskVO;


/**
 * 字符串列表排序List<TaskVO>
 * @author chenjh
 *
 */
public class TaskVOListComparator implements Comparator<TaskVO>{  
	/**
	 * 
	 * @param sort	
	 */
	public TaskVOListComparator(boolean sort){
		this.sort=sort;
	}
	private boolean sort = false;

	public int compare(TaskVO t1, TaskVO t2) {
		int result=-1;
        if(sort){        
        	result = t2.getTaskId()-t1.getTaskId();
        }
        else{
        	result = t1.getTaskId()-t2.getTaskId();
        }
        return result;
	}

}   