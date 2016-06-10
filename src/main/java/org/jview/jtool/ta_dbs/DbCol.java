package org.jview.jtool.ta_dbs;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.ErrorCode;



public class DbCol extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbCol.class);
	public static int OPER_ID=3;
	public static String CODE="col";
	public static String HELP_INFO="tableName&sql显示表字段";
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
	
		String sql = dbTool.getSqlSelect(tableName);
		try {
			
			PreparedStatement ps = dbTool.getConn().prepareStatement(sql);
			ps.setMaxRows(1);
			java.sql.ResultSet rs = ps.executeQuery();
			java.sql.ResultSetMetaData rsm = rs.getMetaData();
			String columnName = null;
			for(int i=1;i<=rsm.getColumnCount();i++){
				columnName = rsm.getColumnName(i);
				sList.add(columnName+"\t"+dbTool.columnAttr(columnName.toLowerCase()));
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {			
//			System.err.println("error:sql="+sql);
			sList.clear();
			sList.add("error:Invalid sql="+sql+","+e.getMessage());
			log4.error(e.getMessage());
			if(TaskManager.debug){
				e.printStackTrace();
			}
		}
		return sList;
	}
	
	
	
	
}
