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



public class DbSelect extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbSelect.class);
	public static int OPER_ID=7;
	public static String CODE="select";
	public static String HELP_INFO="tableName&sql生成查询语句(字段别名为类属性)";
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
		List<String> dataList = new ArrayList<String>();
		String sql = dbTool.getSqlSelect(tableName);
		
		
//		List<String> sList = dbTool.getTableSelect(tableName);
//		List<String> sList = new ArrayList<String>();
		try {			
			PreparedStatement ps = dbTool.getConn().prepareStatement(sql);
			java.sql.ResultSet rs = ps.executeQuery();
			java.sql.ResultSetMetaData rsm = rs.getMetaData();			
			String columnName = null;
			String escName = null;
			for(int i=1;i<=rsm.getColumnCount();i++){			
				columnName = rsm.getColumnName(i).toLowerCase();				
				if(columnName.indexOf("_")>0){
					sList.add(columnName+" as "+dbTool.columnAttr(columnName)+", ");
				}
				else{
					escName = dbTool.getEscape(columnName);
					sList.add(escName+", ");
				}	
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {			
//			System.err.println("error:sql="+sql);
			rValue = "Invalid tableName:"+tableName;
			sList.add(rValue);
			log4.error(e.getMessage());
			if(TaskManager.debug){
				e.printStackTrace();
			}
		}
		
		rValue = dbTool.getListContent(sList, false, false).trim();
		if(rValue.endsWith(",")){
			rValue = rValue.substring(0, rValue.length()-1);
		}
		rValue = "select "+rValue+" "+sql.substring(sql.indexOf("from"));
		dataList.add(rValue);
		return dataList;
	}
	
	
	
	
}
