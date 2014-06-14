package org.jview.jtool.ta_dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.ErrorCode;



public class DbInsert extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbInsert.class);
	public static int OPER_ID=6;
	public static String CODE="insert";
	public static String HELP_INFO="tableName成生insert sql语句";
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

		if(tableName.trim().toLowerCase().startsWith("select")){
			log4.error("Invalid tableName:"+tableName);
			log4.info(this.CODE+", "+this.HELP_INFO);
			sList.add(this.CODE+", "+this.HELP_INFO);
			return sList;
		}
		
		String sql = "select * from "+ tableName;
		try {
			
			Statement ps = dbTool.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);		
//			Statement ps = conn.createStatement();	
			java.sql.ResultSet rs = ps.executeQuery(sql);
			
			rs.last();					
			if(rs.getRow()>dbTool.getMaxTotalRow()){
			log4.info("Out of max total row limit:"+rs.getRow());				
				rs.close();
				ps.close();
				return sList;
			}
			rs.first();
			rs.beforeFirst();
			java.sql.ResultSetMetaData rsm = rs.getMetaData();
			int columnCount = rsm.getColumnCount();
			String insertStart = "insert into "+tableName+" (";
//			String jType = null;
			for(int i=1;i<=columnCount;i++){				
				insertStart=insertStart+rsm.getColumnName(i)+", ";
			}
			insertStart = insertStart.trim();
			if(insertStart.endsWith(",")){
				insertStart=insertStart.substring(0, insertStart.length()-1);
			}
			insertStart = insertStart + ") values (";		
			
			String dataLine = null;
			int cType = -1;
			while(rs.next()){
				dataLine = "";				
				for(int i=1; i<=columnCount; i++){
					cType = rsm.getColumnType(i);
					if(cType==Types.VARCHAR||cType==Types.VARBINARY
							||cType==Types.CHAR||cType==Types.BLOB
							||cType==Types.DATE||cType==Types.TIME
							||cType==Types.TIMESTAMP){
						dataLine=dataLine+"'"+rs.getString(i)+"', ";
					}
					else{
						dataLine=dataLine+rs.getString(i)+", ";
					}
				}
				dataLine = dataLine.trim();
				if(dataLine.endsWith(",")){
					dataLine=dataLine.substring(0, dataLine.length()-1);
				}
				sList.add(insertStart+dataLine+");");
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {			
//			System.err.println("error:sql="+sql);
			sList.add("Invalid tableName:"+tableName);
			log4.error(e.getMessage());
			if(TaskManager.debug){
				e.printStackTrace();
			}
		}
		
		return sList;
	}
	
	
	
	
}
