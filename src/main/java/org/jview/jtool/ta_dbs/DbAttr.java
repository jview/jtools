package org.jview.jtool.ta_dbs;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.ErrorCode;



public class DbAttr extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbAttr.class);
	public static int OPER_ID=2;
	public static String CODE="attr";
	public static String HELP_INFO="tableName&sql将表字段转成java类属性";
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
		java.sql.ResultSet rs=null;
		Map<String, String> commMap =new HashMap();
		try {			
			DatabaseMetaData dbmd=dbTool.getConn().getMetaData();
			String commentSql="";
			if(dbTool.isPostgresql()){
				commentSql="SELECT a.attname AS column_name, col_description (a.attrelid, a.attnum) AS comments"
						+ " FROM  pg_class AS c,  pg_attribute AS a"
						+ " WHERE a.attrelid = c.oid AND a.attnum > 0  and c.relname=lower('"+tableName+"')";
			}
			else if(dbTool.isOracle()){
				commentSql="select column_name, comments from user_col_comments where table_name=upper('"+tableName+"')";
			}
			else if(dbTool.isMysql()){
				String url=dbTool.getConn().getMetaData().getURL();
				String scheme=url.substring(url.lastIndexOf("/"));
				if(scheme.startsWith("/")){
					scheme=scheme.substring(1);
				}
				commentSql="select column_name, COLUMN_COMMENT as comments from information_schema.columns where table_schema ='"+scheme+"'  and table_name = '"+tableName+"'";
			}
			if(commentSql.length()>0){
//				System.out.println("-----commentSql="+commentSql);
				PreparedStatement psdb =dbTool.getConn().prepareStatement(commentSql);
				rs=psdb.executeQuery();
				
				String col=null;
				while(rs.next()){
					col=rs.getString("column_name");
					commMap.put(col, rs.getString("comments"));
				}
			}

			
			PreparedStatement ps = dbTool.getConn().prepareStatement(sql);
			rs = ps.executeQuery();
			ps.setMaxRows(1);
			java.sql.ResultSetMetaData rsm = rs.getMetaData();
			
			String colName=null;
			String comment=null;
			for(int i=1;i<=rsm.getColumnCount();i++){
				colName=rsm.getColumnName(i);
				comment=commMap.get(colName);
				if(comment!=null){
					comment="//"+comment;
				}
				else{
					comment="";
				}
				sList.add("private "+dbTool.getJavaType(rsm.getColumnType(i))+" "+dbTool.columnAttr(colName)+";	"+comment);				
			}
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
