package org.jview.jtool.ta_dbs;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.ErrorCode;



public class DbSql extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbSql.class);
	public static int OPER_ID=8;
	public static String CODE="sql";
	public static String HELP_INFO="sql执行sql语句[select|update|delete]";
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
	public List<String> doExecute(String sql) {
		String rValue =  sql;
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
				
		if(!(sql.startsWith("select")||sql.startsWith("update")||sql.startsWith("delete"))){
			log4.error("Error:Invalid sql:"+sql+", must start width:select/update/delete");
			log4.info(this.CODE+", "+this.HELP_INFO);
			return sList;
		}
		sql = sql.trim();
		if(sql.startsWith("select")){
			String task_key = "dbs";
			ITask dbOper = null;
			if(TaskManager.getTaskMap()!=null){
				dbOper = (ITask)TaskManager.getTaskMap().get(task_key+"_"+"data");
			}
			if(dbOper==null){
				dbOper = new DbData();
			}
//			System.out.println("=sql="+sql);
//			sList.add(dbTool.getListContent(dbTool.getTableData(sql), dbTool.getShowDataCount(), true));
			return dbOper.doExecute(sql);
		}
		String rResult = null;
		Statement ps;
		try {
			ps = dbTool.getConn().createStatement();
			int value = ps.executeUpdate(sql);
			ps.close();
			rResult = "Operate success "+value;
			sList.add(rResult);
		} catch (SQLException e) {			
//			System.err.println("error:Invalid sql="+sql);
			log4.error(e.getMessage());
			if(TaskManager.debug){
				e.printStackTrace();
			}
			sList.add("error:Operate fail: sql="+sql+","+e.getMessage());
		}
		return sList;	
	}
	
	
	
	
}
