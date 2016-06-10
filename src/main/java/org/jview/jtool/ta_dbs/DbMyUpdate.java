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


/**
 * 
 * @author chenjh
 *
 */
public class DbMyUpdate extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbMyUpdate.class);
	public static int OPER_ID=12;
	public static String CODE="myupdate";
	public static String HELP_INFO="tableName&key,key1,!key2成生mybatis xml update sql语句";
	public int getTaskId(){
		return OPER_ID;
	}
	public String getCode(){
		return CODE;
	}
	public String getHelpInfo(){
		return HELP_INFO;
	}

	/**
	 * 生成mybatis update sql语句
	 * @param tableName_key
	 * @return
	 */
	@Override
	public List<String> doExecute(String tableName_key) {
		String rValue = tableName_key;
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

		if(tableName_key.trim().toLowerCase().startsWith(this.CODE)){
			log4.error("Error:tableName is "+tableName_key);
			log4.info(this.CODE+", "+this.HELP_INFO);
			sList.add(this.CODE+", "+this.HELP_INFO);
			return sList;
		}
		
		String tableName=null,keys=null;
		if(tableName_key.indexOf("&")>0){
			tableName = tableName_key.substring(0, tableName_key.indexOf("&"));
			keys = tableName_key.substring(tableName_key.indexOf("&")+1);
		}
		else{
		log4.error("tableName "+tableName_key+" have no key of column");
			return sList;
		}
		
		String sql = dbTool.getSqlSelect(tableName);
		try {
//			String sql = dbTool.getSqlSelect(tableName);
//			System.out.println("---sql="+sql);
			PreparedStatement ps = dbTool.getConn().prepareStatement(sql);
			ps.setMaxRows(1);
			java.sql.ResultSet rs = ps.executeQuery();
			java.sql.ResultSetMetaData rsm = rs.getMetaData();
			int colCount=rsm.getColumnCount();
			String updateSql="update "+tableName+" set \n	";
			String cType=null;
			String colName=null;
			boolean isExist=false;
			for(int i=1;i<=colCount;i++){
				colName=rsm.getColumnName(i);
				isExist=false;
				for(String key:keys.split(",")){
					if(key.equalsIgnoreCase("!"+colName)){
						isExist=true;
						break;
					}
				}
				if(isExist){
					continue;
				}
				cType=rsm.getColumnTypeName(i).toUpperCase();
				updateSql+=colName+"=	#{"+dbTool.columnAttr(colName)
					+",jdbcType="+cType+"}\n	,";
			}
			updateSql=updateSql.trim();
			if(updateSql.endsWith(",")){
				updateSql=updateSql.substring(0, updateSql.length()-1);
				updateSql=updateSql.trim();
			}
			
			//condition
			String conSql="";
			for(int i=1;i<=colCount;i++){
				colName=rsm.getColumnName(i);
				isExist=false;
				for(String key:keys.split(",")){
					if(key.equalsIgnoreCase(colName)){
						isExist=true;
						break;
					}
				}
				if(isExist){
					cType=rsm.getColumnTypeName(i).toUpperCase();
					conSql+=colName+"=#{"+dbTool.columnAttr(colName)
					+",jdbcType="+cType+"}\n and ";
				}
			}
			if(conSql.length()>0){
				conSql=conSql.trim();
				conSql=conSql.substring(0, conSql.length()-"and".length());
			}
			
			sList.add(updateSql+"\n where "+conSql);
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
