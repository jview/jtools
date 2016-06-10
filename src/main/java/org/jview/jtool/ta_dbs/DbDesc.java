package org.jview.jtool.ta_dbs;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.ErrorCode;



public class DbDesc extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbDesc.class);
	public static int OPER_ID=1;
	public static String CODE="desc";
	public static String HELP_INFO="tableName显示表结构";
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
	
		
		StringBuilder sb = new StringBuilder();
		sb.append("drop table if exists ").append(tableName).append(";").append("\n");
		sb.append("create table ").append(tableName).append("(").append("\n\t");
		try {
			String sql = "select * from " + tableName;
			PreparedStatement ps = dbTool.getConn().prepareStatement(sql);
			ps.setMaxRows(1);
			java.sql.ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			for (int i = 1; i <= columnCount; i++) {
				sb.append(rsmd.getColumnName(i)).append("\t").append(
						rsmd.getColumnTypeName(i)).append("(").append(
						rsmd.getColumnDisplaySize(i)).append(")");
				// 判断字段是否能为空
				if (rsmd.isNullable(i) == ResultSetMetaData.columnNoNulls) {
					sb.append(" ").append("not null");
				}
				// 判断字段是否递增
				if (rsmd.isAutoIncrement(i)) {
					sb.append(" ").append("auto_increment");
				}
				// 最后一列去掉逗号
				if (i != columnCount) {
					sb.append(", \n\t");
				} else {
					sb.append("\n");
				}
			}

			sb.append(");\n");
			rs.close();
			
			
		} catch (SQLException e) {
			log4.error(e.getMessage());
			sb=new StringBuilder();
			sList.add("error:tableName="+tableName+","+e.getMessage());
			if(TaskManager.debug){
				e.printStackTrace();
			}
		}
		sList.add(sb.toString());
		return sList;
	}
	
	
	
	
}
