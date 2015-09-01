package org.jview.jtool.ta_dbs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.ErrorCode;

import com.jview.paras.service.impl.ConstGenMapImpl;


public class DbConstClass extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbConstClass.class);
	public static int OPER_ID=2;
	public static String CODE="constClass";
	public static String HELP_INFO="packageName [className] select key, value, [default_value] from tableName&sql 数据生成constant静态常量类";
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
	public List<String> doExecute(String sql) {
		String rValue = sql;
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
		
		if(sql.indexOf("select")>0){
			rValue = sql.substring(0, sql.indexOf("select"));
			sql = sql.substring(sql.indexOf("select"));
		}
		DBTool dbTool = TaskManager.getDBTool();
		String tableName = null;
		if(rValue.split(" ").length<=1 && sql.indexOf("from ")>0){
			tableName = sql.substring(sql.indexOf("from ")+"from ".length()).trim();
			if(tableName.indexOf(" ")>0){
				tableName=tableName.substring(0, tableName.indexOf(" "));
			}
			tableName = dbTool.columnAttr(tableName);
			tableName = dbTool.upperCaseStart(tableName);
			rValue = rValue.trim()+" "+tableName;
		}

		//如果未初始化数据库，初始化一下		
		if(!dbTool.isInit){
			String msg=dbTool.init();
			if(msg!=null){
				sList.add(msg);
				return sList;
			}
		}
	
		String sqlStr = dbTool.getSqlSelect(sql);
		
		try {
			
			Statement ps = dbTool.getConn().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ps.setMaxRows(1);
			java.sql.ResultSet rs = ps.executeQuery(sqlStr);
			
			rs.last();					
			if(rs.getRow()>dbTool.getMaxTotalRow()){
				log4.info("Out of max total row limit:"+rs.getRow());				
				rs.close();
				ps.close();
				return sList;
			}
			java.sql.ResultSetMetaData rsm = rs.getMetaData();
			int columnCount = rsm.getColumnCount();
			Map mapValue = new HashMap();
			Map mapDefault = new HashMap();
			String dataLine = "";
			String jsonInfo=null;		
			rs.first();
			rs.beforeFirst();
			String key = null;
			String value = null;
			String value_default = null;
			while(rs.next()){
				key = rs.getString(1);
				value = rs.getString(2);				
				mapValue.put(key, value);
				if(columnCount>2){
					value_default = rs.getString(3);
					mapValue.put(key, value_default);
				}
				
			}
			rs.close();
			ps.close();
			this.doConstClass(rValue, mapValue, mapDefault);
		} catch (SQLException e) {			
			log4.error(e.getMessage());
			sList.clear();
			sList.add("error:Invalid sql="+sql);
			if(TaskManager.debug){
				e.printStackTrace();
			}
		}
//		System.out.println("------------dbData-doExecute--end-");
		
		return sList;
	}
	
	
	private String doConstClass(String rValue, Map mapValue, Map mapDefault){
		String[] strs = rValue.split(" ");
		String genFilePath = null;
		String packageName = null;
		if(strs.length>=1){
			packageName=strs[0];
		}
		String className=null;
		if(strs.length>=2){
			className = strs[1];
		}
		
		ConstGenMapImpl propGen = new ConstGenMapImpl(genFilePath, packageName, className);
		propGen.setMapDefault(mapDefault);
		propGen.setMapValue(mapValue);
		propGen.generateClass();
		return rValue;
	}
	
}
