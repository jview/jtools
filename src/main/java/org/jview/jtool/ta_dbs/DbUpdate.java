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
import org.jview.jtool.model.LineVO;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.ErrorCode;


/**
 * 
 * @author chenjh
 *
 */
public class DbUpdate extends IDb implements ITask{
	private static Logger log4 = Logger.getLogger(DbUpdate.class);
	public static int OPER_ID=9;
	public static String CODE="update";
	public static String HELP_INFO="tableName&key,key1,!key2成生update sql语句";
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
	 * 生成update sql语句
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

			List<LineVO> conList = new ArrayList<LineVO>();
			boolean isExist=false;
			boolean isIgnore=false;
			//检查字段是否存在
			if(keys!=null){
				LineVO con = null;				
				for(String key:keys.split(",")){
					isExist=false;
					isIgnore=false;
					key = key.trim();
					if(key.startsWith("!")){
						key = key.substring(1);
						isIgnore=true;
					}
					for(int i=1;i<=columnCount;i++){				
						if(key.equalsIgnoreCase(rsm.getColumnName(i))){
							isExist=true;
							con = new LineVO();
							con.setIndex(i);
							con.setLine(key);
							con.setIgnore(isIgnore);
							conList.add(con);
							break;
						}
					}
					if(!isExist){
						log4.info(key+" column not found on table "+tableName);
						rs.close();
						ps.close();
						return sList;
					}
				}
			}
			String updateStart = "update "+tableName+" set ";	

			
			String dataLine = null;
			String conSql=null;
			int cType = -1;
			
			while(rs.next()){
				dataLine = "";
				conSql="";
				for(int i=1; i<=columnCount; i++){
					cType = rsm.getColumnType(i);
					isIgnore=false;
					for(LineVO con:conList){
						if(con.isIgnore()&&con.getIndex()==i){
							isIgnore=true;
						}
					}
					if(isIgnore){
						continue;
					}
					
					if(cType==Types.VARCHAR||cType==Types.VARBINARY
							||cType==Types.CHAR||cType==Types.BLOB
							||cType==Types.DATE||cType==Types.TIME
							||cType==Types.TIMESTAMP){
						dataLine=dataLine+rsm.getColumnName(i)+"='"+rs.getString(i)+"', ";
					}
					else{
						dataLine=dataLine+rsm.getColumnName(i)+"="+rs.getString(i)+", ";
					}
				}
				dataLine = dataLine.trim();
				if(dataLine.endsWith(",")){
					dataLine=dataLine.substring(0, dataLine.length()-1);
				}
				for(LineVO con:conList){
					if(con.isIgnore()){
						continue;
					}
					cType = rsm.getColumnType(con.getIndex());
					if(cType==Types.VARCHAR||cType==Types.VARBINARY
							||cType==Types.CHAR||cType==Types.BLOB
							||cType==Types.DATE||cType==Types.TIME
							||cType==Types.TIMESTAMP){
						conSql=conSql+rsm.getColumnName(con.getIndex())+"='"+rs.getString(con.getIndex())+"' and ";
					}
					else{
						conSql=conSql+rsm.getColumnName(con.getIndex())+"="+rs.getString(con.getIndex())+" and ";
					}
				}
				conSql = conSql.trim();
				if(conSql.endsWith("and")){
					conSql=conSql.substring(0, conSql.length()-"and".length());
					conSql = conSql.trim();
				}
				sList.add(updateStart+dataLine+" where "+conSql+";");
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {			
//			System.err.println("error:sql="+sql);
			sList.add("Invalid tableName_key:"+tableName_key+","+e.getMessage());
			log4.error(e.getMessage());
			if(TaskManager.debug){
				e.printStackTrace();
			}
		}
		
		return sList;
	}
	
	
	
	
}
