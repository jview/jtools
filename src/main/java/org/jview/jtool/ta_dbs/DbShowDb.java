package org.jview.jtool.ta_dbs;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.tools.DBTool;



public class DbShowDb extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbShowDb.class);
	public static int OPER_ID=0;
	public static String CODE="showDb";
	public static String HELP_INFO="显示所有的DataSource";
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
	public List<String> doExecute(String rValue) {
		List<String> sList = new ArrayList<String>();
		
		//如果未初始化数据库，初始化一下
		DBTool dbTool = TaskManager.getDBTool();
		if(!dbTool.isInit){
			String msg=dbTool.init();
			if(msg!=null){
				sList.add(msg);
				return sList;
			}
		}

		sList.addAll(dbTool.getDbNames());
	
		return sList;
	}
	
	
	
	
}
