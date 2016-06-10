package org.jview.jtool.ta_dbs;

import java.sql.PreparedStatement;
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



public class DbMyInsert extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbMyInsert.class);
	public static int OPER_ID=11;
	public static String CODE="myinsert";
	public static String HELP_INFO="tableName成生mybatis insert sql语句";
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
		
		String sql = dbTool.getSqlSelect(tableName);
		try {			
			PreparedStatement ps = dbTool.getConn().prepareStatement(sql);
			ps.setMaxRows(1);
			java.sql.ResultSet rs = ps.executeQuery();
			java.sql.ResultSetMetaData rsm = rs.getMetaData();
			
			String pKeyInfo="<selectKey resultType=\"Java.lang.Long\" order=\"BEFORE\" keyProperty=\"id\">\n"
					+"	SELECT SEQ_TEST.NEXTVAL FROM DUAL\n"
					+"</selectKey>\n";
			String insertStart=pKeyInfo+"\n insert into "+tableName+"\n(";
			int colCount=rsm.getColumnCount();
//			System.out.println("----colCount="+colCount);
			for(int i=1;i<=colCount;i++){
				insertStart+=rsm.getColumnName(i)+"\n	,";
			}
			insertStart=insertStart.trim();
			if(insertStart.endsWith(",")){
				insertStart=insertStart.substring(0, insertStart.length()-1);
			}
			insertStart+=")\n values \n(";
			String cType=null;
			String dataLine="";
			for(int i=1;i<=colCount;i++){
				cType=rsm.getColumnTypeName(i).toUpperCase();
				dataLine+="#{"+dbTool.columnAttr(rsm.getColumnName(i))
					+",jdbcType="+cType+"}\n	,";
			}
			dataLine=dataLine.trim();
			if(dataLine.endsWith(",")){
				dataLine=dataLine.substring(0, dataLine.length()-1);
			}
			sList.add(insertStart+dataLine+");");
			rs.close();
			ps.close();
			
		} catch (SQLException e) {			
//			System.err.println("error:sql="+sql);
			sList.add("error:Invalid sql="+sql+","+e.getMessage());
			log4.error(e.getMessage());
			if(TaskManager.debug){
				e.printStackTrace();
			}
		}
		
		return sList;
	}
	
	
	
	
}
