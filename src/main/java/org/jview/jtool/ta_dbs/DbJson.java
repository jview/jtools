package org.jview.jtool.ta_dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.ErrorCode;



public class DbJson extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbJson.class);
	public static int OPER_ID=2;
	public static String CODE="json";
	public static String HELP_INFO="tableName&sql 数据生成json对象";
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
	/**
	 * {id:0, value:'教员'},
	 */
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
			
			Statement ps = dbTool.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);			
			java.sql.ResultSet rs = ps.executeQuery(sql);
			
			rs.last();					
			if(rs.getRow()>dbTool.getMaxTotalRow()){
				log4.info("Out of max total row limit:"+rs.getRow());				
				rs.close();
				ps.close();
				return sList;
			}
			
			java.sql.ResultSetMetaData rsm = rs.getMetaData();
			int columnCount = rsm.getColumnCount();
			String dataLine = "";
			String jsonInfo=null;		
			rs.first();
			rs.beforeFirst();
			while(rs.next()){
				dataLine = "";
				jsonInfo="{";
				for(int i=1; i<=columnCount; i++){
					jsonInfo+=dbTool.columnAttr(rsm.getColumnName(i))+":'"+rs.getString(i)+"',";
//					dataLine=dataLine+rs.getString(i)+"\t";
				}
				if(jsonInfo.endsWith(",")){
					jsonInfo=jsonInfo.substring(0, jsonInfo.length()-1);
				}
				jsonInfo +="},";
				dataLine += jsonInfo+"\t";
				sList.add(dataLine);
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {			
			log4.error(e.getMessage());
			sList.clear();
			sList.add("error:Invalid sql="+sql+","+e.getMessage());
			if(TaskManager.debug){
				e.printStackTrace();
			}
		}
//		System.out.println("------------dbData-doExecute--end-");
		
		return sList;
	}
	
	
	
	
}
