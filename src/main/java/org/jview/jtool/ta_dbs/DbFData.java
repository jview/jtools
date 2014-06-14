package org.jview.jtool.ta_dbs;

import java.util.ArrayList;
import java.util.List;



import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.ErrorCode;



public class DbFData extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbFData.class);
	public static int OPER_ID=5;
	public static String CODE="fdata";
	public static String HELP_INFO="tableName&sql显示表数据(form)";
	public int getTaskId(){
		return OPER_ID;
	}
	public String getCode(){
		return CODE;
	}
	public String getHelpInfo(){
		return HELP_INFO;
	}

	
	@Override
	public List<String> doExecute(String tableName) {
		String rValue = tableName;
		List<String> sList = new ArrayList<String>();
		if(!ErrorCode.isEmpty(rValue)&& !rValue.equals(this.CODE)){
			rValue = rValue.trim();			
		}
		else{
//			log4.error("Error: empty para!");
			log4.info(this.CODE+", "+this.HELP_INFO);
			sList.add(this.CODE+", "+this.HELP_INFO);
			return sList;
		}
		
		//如果未初始化数据库，初始化一下
		DBTool dbTool = TaskManager.getDBTool();
		if(!dbTool.isInit){
			String msg=dbTool.init();
			if(msg!=null){
				sList.add(msg);
				return sList;
			}
		}
	
//		String sql = dbTool.getSqlSelect(tableName);
//		sql = dbTool.getTableSelectString(sql);
		String task_key = "dbs";
		ITask dbOper =null;
		if(TaskManager.getTaskMap()!=null){
			dbOper = (ITask)TaskManager.getTaskMap().get(task_key+"_"+DbSelect.CODE);
		}
		if(dbOper==null){
			dbOper = new DbSelect();
		}
		String sql = dbOper.doExecute(tableName).get(0);
		if(TaskManager.getTaskMap()!=null){
			dbOper = (ITask)TaskManager.getTaskMap().get(task_key+"_"+DbData.CODE);
		}
		if(dbOper==null){
			dbOper = new DbData();
		}
		return dbOper.doExecute(sql);
//		return dbTool.getTableData(sql);			
		
		
	}
	
	
	
	
}
