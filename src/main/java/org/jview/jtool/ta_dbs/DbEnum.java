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



public class DbEnum extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbEnum.class);
	public static int OPER_ID=2;
	public static String CODE="enum";
	public static String HELP_INFO="tableName&sql 数据生成enum对象";
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
	 * TOOLS("tools", "common tool", "常用工具"),
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
			String enumInfo=null;
			rs.first();
			rs.beforeFirst();
			while(rs.next()){
				dataLine = "";
				enumInfo=rs.getString(1).toUpperCase()+"(";				
				for(int i=1; i<=columnCount; i++){
					if(rsm.getColumnType(i)==Types.BIGINT
							||rsm.getColumnType(i)==Types.INTEGER
							||rsm.getColumnType(i)==Types.LONGVARCHAR
							||rsm.getColumnType(i)==Types.NUMERIC
							||rsm.getColumnType(i)==Types.DECIMAL
							||rsm.getColumnType(i)==Types.BOOLEAN
							||rsm.getColumnType(i)==Types.SMALLINT){
						enumInfo+=dbTool.columnAttr(rs.getString(i))+",";
					}
					else{
						enumInfo+="\""+dbTool.columnAttr(rs.getString(i))+"\",";
					}
//					dataLine=dataLine+rs.getString(i)+"\t";
				}
				if(enumInfo.endsWith(",")){
					enumInfo=enumInfo.substring(0, enumInfo.length()-1);
				}
				enumInfo +="),";
				dataLine += enumInfo+"\t";
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
