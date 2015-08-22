package org.jview.jtool.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jview.jtool.model.LineVO;
import org.jview.jtool.util.ErrorCode;


import java.sql.Types;





/**
 * 数据库处理
 * @author chenjh
 *
 */
public class DBTool {
	private static Logger log4 = Logger.getLogger(DBTool.class);
	public static final String DB_CONFIG_FILE="database.properties";
	private static int MAX_TOTAL_ROW=1000;
	private static int UPDATE_BATCH_COUNT=30;
	private boolean debug=false;
	public boolean isInit=false;
	
	public DBTool(){
		this.loadConfig();
//		this.init();
	}
	
	public DBTool(String host, String user, String pwd, String port){
		this.loadConfig();
		this.host=host;
		this.user=user;
		this.pwd=pwd;
		this.port=port;
		this.url=this.getNewUrl(this.url, host, port);
//		this.init();
	}
	
	/**
	 * 载入配置文件
	 */
	private void loadConfig() {
		Properties config = new Properties();
		String rconf = System.getProperty("rconf");
		try {
			InputStream in = null;
			if (ErrorCode.isEmpty(rconf)) {
				in = this.getClass().getClassLoader().getResourceAsStream(
						DB_CONFIG_FILE);
			} else {
				File file = new File(rconf+File.separator+DB_CONFIG_FILE);
				in = new FileInputStream(file);
			}
			config.load(in);
			in.close();
			
			this.url=config.getProperty("url");
			this.driver=config.getProperty("driver");
			this.user=config.getProperty("user");
			this.pwd=config.getProperty("password");
			this.dsPath=config.getProperty("dsPath");
			
		} catch (Exception e) {
			log4.error(e.getMessage());
			if(this.debug){
				e.printStackTrace();
			}
			
		}
	}
	private Connection conn = null;
	public String init(){
		try{
			Class.forName(this.driver);			
			conn = DriverManager.getConnection(this.url, this.user, this.pwd);
			log4.info(this.url);
			this.isInit=true;
			return null;
		}catch(Exception e){
			log4.error(e.getMessage());
			if(this.debug){
				e.printStackTrace();
			}
			return this.url+" "+this.user+" "+e.getMessage();
		}
	}
	
	public Connection getConn(){
		return this.conn;
	}
	
	public boolean isOracle(){
		return this.driver.indexOf("oracle")>=0;
	}
	
	public boolean isPostgresql(){
		return this.driver.indexOf("postgres")>=0;
	}
	public boolean isMysql(){
		return this.driver.indexOf("mysql")>=0;
	}
	
	/**
	 * 如果是表转成查询表的select语句
	 * @param tableName
	 * @return
	 */
	public String getSqlSelect(String tableName){
		
		
		String sql = "select * from "+tableName;
		if(tableName.startsWith("select")){
			sql = tableName;
		}
		return sql;
	}
	
//	/**
//	 * 得到数据库所有的表
//	 * @return
//	 */
//	public List<String> getTables(){
//		if(!this.isInit){
//			this.init();
//		}
//		
//		List<String> sList = new ArrayList<String>();
//		try {
//			
//			DatabaseMetaData dbmd = conn.getMetaData();
//
//			String schemaPattern = null;
//			if(this.driver.indexOf("oracle")>=0){
//				schemaPattern=this.user.toUpperCase();
//			}
//			else if(this.driver.indexOf("postgres")>=0){
//				schemaPattern="public";
//			}
//			String[] types = {"TABLE"};
//			ResultSet rs = dbmd.getTables(null, schemaPattern, "%", types);
//			
//			while (rs.next()) {
//				String table = rs.getString(3);
//				sList.add(table);				
//			}
//			rs.close();
//			
//		} catch (SQLException e) {			
//			log4.error(e.getMessage());
//			if(this.debug){
//				e.printStackTrace();
//			}
//		}
//		return sList;
//	}
	
//	/**
//	 * 生成表结构
//	 * 
//	 * @param tableName
//	 *            表名称
//	 * @return 返回表结构sql语句
//	 */
//	public String getTableDesc(String tableName) {
//		if(!this.isInit){
//			this.init();
//		}
//		
//		StringBuilder sb = new StringBuilder();
//		sb.append("drop table if exists ").append(tableName).append(";").append("\n");
//		sb.append("create table ").append(tableName).append("(").append("\n\t");
//		try {
//			String sql = "select * from " + tableName;
//			ResultSet rs = conn.prepareStatement(sql).executeQuery();
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//
//			for (int i = 1; i <= columnCount; i++) {
//				sb.append(rsmd.getColumnName(i)).append("\t").append(
//						rsmd.getColumnTypeName(i)).append("(").append(
//						rsmd.getColumnDisplaySize(i)).append(")");
//				// 判断字段是否能为空
//				if (rsmd.isNullable(i) == ResultSetMetaData.columnNoNulls) {
//					sb.append(" ").append("not null");
//				}
//				// 判断字段是否递增
//				if (rsmd.isAutoIncrement(i)) {
//					sb.append(" ").append("auto_increment");
//				}
//				// 最后一列去掉逗号
//				if (i != columnCount) {
//					sb.append(", \n\t");
//				} else {
//					sb.append("\n");
//				}
//			}
//
//			sb.append(");\n");
//			rs.close();
//			
//			
//		} catch (SQLException e) {
//			log4.error(e.getMessage());
//			sb=new StringBuilder();
//			sb.append("error:Invalid tableName="+tableName);
//			if(this.debug){
//				e.printStackTrace();
//			}
//		}
//
//		return sb.toString();
//	}


	
//	/**
//	 * 表或sql数据查询
//	 * @param tableName
//	 * @return
//	 */
//	public List<String> getTableData(String tableName){
//		if(!this.isInit){
//			this.init();
//		}
//		
//		String sql = this.getSqlSelect(tableName);
//		List<String> sList = new ArrayList<String>();
//		try {
//			
//			Statement ps = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);			
//			java.sql.ResultSet rs = ps.executeQuery(sql);
//			
//			rs.last();					
//			if(rs.getRow()>maxTotalRow){
//				log4.info("Out of max total row limit:"+rs.getRow());				
//				rs.close();
//				ps.close();
//				return sList;
//			}
//			
//			java.sql.ResultSetMetaData rsm = rs.getMetaData();
//			int columnCount = rsm.getColumnCount();
//			String dataLine = "";
//			for(int i=1;i<=columnCount;i++){
//				dataLine=dataLine+rsm.getColumnName(i)+"\t";
//			}
//			sList.add(dataLine);			
//			rs.first();
//			rs.beforeFirst();
//			while(rs.next()){
//				dataLine = "";
//				for(int i=1; i<=columnCount; i++){
//					dataLine=dataLine+rs.getString(i)+"\t";
//				}
//				sList.add(dataLine);
//			}
//			rs.close();
//			ps.close();
//			
//		} catch (SQLException e) {			
//			log4.error(e.getMessage());
//			sList.clear();
//			sList.add("error:Invalid sql="+sql);
//			if(this.debug){
//				e.printStackTrace();
//			}
//		}
//		
//		return sList;
//	}
//	
//	/**
//	 * 表或数据查询(form)
//	 * @param tableName
//	 * @return
//	 */
//	public List<String> getTableDataForm(String tableName){
//		if(!this.isInit){
//			this.init();
//		}
//		
//		String sql = this.getSqlSelect(tableName);
//		sql = this.getTableSelectString(sql);
//		return this.getTableData(sql);			
//	}
	
//	/**
//	 * 返回表或查询的所有字段
//	 * @param tableName
//	 * @return
//	 */
//	public List<String> getTableColumn(String tableName){
//		if(!this.isInit){
//			this.init();
//		}
//		
//		String sql = this.getSqlSelect(tableName);
//		List<String> sList = new ArrayList<String>();
//		try {
//			
//			PreparedStatement ps = conn.prepareStatement(sql);
//			java.sql.ResultSet rs = ps.executeQuery();
//			java.sql.ResultSetMetaData rsm = rs.getMetaData();
//			String columnName = null;
//			for(int i=1;i<=rsm.getColumnCount();i++){
//				columnName = rsm.getColumnName(i);
//				sList.add(columnName+"\t"+this.columnAttr(columnName.toLowerCase()));
//			}
//			rs.close();
//			ps.close();
//			
//		} catch (SQLException e) {			
////			System.err.println("error:sql="+sql);
//			sList.clear();
//			sList.add("error:Invalid sql="+sql);
//			log4.error(e.getMessage());
//			if(this.debug){
//				e.printStackTrace();
//			}
//		}
//		return sList;
//	}
	
//	/**
//	 * 数据库字段转成java类属性
//	 * @param tableName
//	 * @return
//	 */
//	public List<String> getTableColumnJavaAttr(String tableName){
//		if(!this.isInit){
//			this.init();
//		}
//		
//		String sql = this.getSqlSelect(tableName);
//		List<String> sList = new ArrayList<String>();
//		try {			
//			PreparedStatement ps = conn.prepareStatement(sql);
//			java.sql.ResultSet rs = ps.executeQuery();
//			java.sql.ResultSetMetaData rsm = rs.getMetaData();
//			for(int i=1;i<=rsm.getColumnCount();i++){
//				sList.add("private "+this.getJavaType(rsm.getColumnType(i))+" "+this.columnAttr(rsm.getColumnName(i))+";");				
//			}
//			rs.close();
//			ps.close();
//			
//		} catch (SQLException e) {			
////			System.err.println("error:sql="+sql);
//			sList.add("error:Invalid sql="+sql);
//			log4.error(e.getMessage());
//			if(this.debug){
//				e.printStackTrace();
//			}
//		}
//		
//		return sList;
//	}
	
//	/**
//	 * 取得所有字段的select字段部份：字段转换，如temp_code转成tempCode
//	 * @param tableName(sql)
//	 * @return
//	 */
//	public List<String> getTableSelect(String tableName){
//		if(!this.isInit){
//			this.init();
//		}
//		
//		String sql = this.getSqlSelect(tableName);
//		
//		List<String> sList = new ArrayList<String>();
//		String rValue = null;
//		try {			
//			PreparedStatement ps = conn.prepareStatement(sql);
//			java.sql.ResultSet rs = ps.executeQuery();
//			java.sql.ResultSetMetaData rsm = rs.getMetaData();			
//			String columnName = null;
//			String escName = null;
//			for(int i=1;i<=rsm.getColumnCount();i++){			
//				columnName = rsm.getColumnName(i).toLowerCase();				
//				if(columnName.indexOf("_")>0){
//					sList.add(columnName+" as "+this.columnAttr(columnName)+", ");
//				}
//				else{
//					escName = this.getEscape(columnName);
//					sList.add(escName+", ");
//				}	
//			}
//			rs.close();
//			ps.close();
//			
//		} catch (SQLException e) {			
////			System.err.println("error:sql="+sql);
//			rValue = "Invalid tableName:"+tableName;
//			sList.add(rValue);
//			log4.error(e.getMessage());
//			if(this.debug){
//				e.printStackTrace();
//			}
//		}
//		return sList;
//	}
//	/**
//	 * 根据tableName|sql生成新的查询句语,生成select sql语句
//	 * @param tableName
//	 * @return
//	 */
//	public String getTableSelectString(String tableName){
//		if(!this.isInit){
//			this.init();
//		}
//		
//		String sql = this.getSqlSelect(tableName);
//		List<String> sList = this.getTableSelect(tableName);
//		String rValue = this.getListContent(sList, false, false).trim();
//		if(rValue.endsWith(",")){
//			rValue = rValue.substring(0, rValue.length()-1);
//		}
//		rValue = "select "+rValue+" "+sql.substring(sql.indexOf("from"));
//		
//		return rValue;
//	}
	
//	/**
//	 * 生成insert sql语句
//	 * @param tableName
//	 * @return
//	 */
//	public List<String> getInsertSql(String tableName){
//		if(!this.isInit){
//			this.init();
//		}
//		
////		String sql = this.getSqlSelect(tableName);
//		List<String> sList = new ArrayList<String>();
//		if(tableName==null||tableName.trim().toLowerCase().startsWith("select")){
//			log4.error("Invalid tableName:"+tableName);
//			return sList;
//		}
//		
//		String sql = "select * from "+ tableName;
//		try {
//			
//			Statement ps = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);		
////			Statement ps = conn.createStatement();	
//			java.sql.ResultSet rs = ps.executeQuery(sql);
//			
//			rs.last();					
//			if(rs.getRow()>this.maxTotalRow){
//			log4.info("Out of max total row limit:"+rs.getRow());				
//				rs.close();
//				ps.close();
//				return sList;
//			}
//			rs.first();
//			rs.beforeFirst();
//			java.sql.ResultSetMetaData rsm = rs.getMetaData();
//			int columnCount = rsm.getColumnCount();
//			String insertStart = "insert into "+tableName+" (";
////			String jType = null;
//			for(int i=1;i<=columnCount;i++){				
//				insertStart=insertStart+rsm.getColumnName(i)+", ";
//			}
//			insertStart = insertStart.trim();
//			if(insertStart.endsWith(",")){
//				insertStart=insertStart.substring(0, insertStart.length()-1);
//			}
//			insertStart = insertStart + ") values (";		
//			
//			String dataLine = null;
//			int cType = -1;
//			while(rs.next()){
//				dataLine = "";				
//				for(int i=1; i<=columnCount; i++){
//					cType = rsm.getColumnType(i);
//					if(cType==Types.VARCHAR||cType==Types.VARBINARY
//							||cType==Types.CHAR||cType==Types.BLOB
//							||cType==Types.DATE||cType==Types.TIME
//							||cType==Types.TIMESTAMP){
//						dataLine=dataLine+"'"+rs.getString(i)+"', ";
//					}
//					else{
//						dataLine=dataLine+rs.getString(i)+", ";
//					}
//				}
//				dataLine = dataLine.trim();
//				if(dataLine.endsWith(",")){
//					dataLine=dataLine.substring(0, dataLine.length()-1);
//				}
//				sList.add(insertStart+dataLine+");");
//			}
//			rs.close();
//			ps.close();
//			
//		} catch (SQLException e) {			
////			System.err.println("error:sql="+sql);
//			sList.add("Invalid tableName:"+tableName);
//			log4.error(e.getMessage());
//			if(this.debug){
//				e.printStackTrace();
//			}
//		}
//		
//		return sList;
//		
//	}
	
//	/**
//	 * 生成update sql语句
//	 * @param tableName_key
//	 * @return
//	 */
//	public List<String> getUpdateSql(String tableName_key){
//		if(!this.isInit){
//			this.init();
//		}
//		
////		String sql = this.getSqlSelect(tableName);
//		List<String> sList = new ArrayList<String>();
//		if(tableName_key==null||tableName_key.trim().toLowerCase().startsWith("update")){
//			log4.error("tableName is null");
//			return sList;
//		}
//		
//		String tableName=null,keys=null;
//		if(tableName_key.indexOf("&")>0){
//			tableName = tableName_key.substring(0, tableName_key.indexOf("&"));
//			keys = tableName_key.substring(tableName_key.indexOf("&")+1);
//		}
//		else{
//		log4.error("tableName "+tableName_key+" have no key of column");
//			return sList;
//		}
//		
//		String sql = "select * from "+ tableName;
//		try {
//			
//			Statement ps = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);		
////			Statement ps = conn.createStatement();	
//			java.sql.ResultSet rs = ps.executeQuery(sql);
//			
//			rs.last();					
//			if(rs.getRow()>this.maxTotalRow){
//				log4.info("Out of max total row limit:"+rs.getRow());				
//				rs.close();
//				ps.close();
//				return sList;
//			}
//			rs.first();
//			rs.beforeFirst();
//			java.sql.ResultSetMetaData rsm = rs.getMetaData();
//			int columnCount = rsm.getColumnCount();
//
//			List<LineVO> conList = new ArrayList<LineVO>();
//			boolean isExist=false;
//			boolean isIgnore=false;
//			//检查字段是否存在
//			if(keys!=null){
//				LineVO con = null;				
//				for(String key:keys.split(",")){
//					isExist=false;
//					isIgnore=false;
//					key = key.trim();
//					if(key.startsWith("!")){
//						key = key.substring(1);
//						isIgnore=true;
//					}
//					for(int i=1;i<=columnCount;i++){				
//						if(key.equalsIgnoreCase(rsm.getColumnName(i))){
//							isExist=true;
//							con = new LineVO();
//							con.setIndex(i);
//							con.setLine(key);
//							con.setIgnore(isIgnore);
//							conList.add(con);
//							break;
//						}
//					}
//					if(!isExist){
//						log4.info(key+" column not found on table "+tableName);
//						rs.close();
//						ps.close();
//						return sList;
//					}
//				}
//			}
//			String updateStart = "update "+tableName+" set ";	
//
//			
//			String dataLine = null;
//			String conSql=null;
//			int cType = -1;
//			
//			while(rs.next()){
//				dataLine = "";
//				conSql="";
//				for(int i=1; i<=columnCount; i++){
//					cType = rsm.getColumnType(i);
//					isIgnore=false;
//					for(LineVO con:conList){
//						if(con.isIgnore()&&con.getIndex()==i){
//							isIgnore=true;
//						}
//					}
//					if(isIgnore){
//						continue;
//					}
//					
//					if(cType==Types.VARCHAR||cType==Types.VARBINARY
//							||cType==Types.CHAR||cType==Types.BLOB
//							||cType==Types.DATE||cType==Types.TIME
//							||cType==Types.TIMESTAMP){
//						dataLine=dataLine+rsm.getColumnName(i)+"='"+rs.getString(i)+"', ";
//					}
//					else{
//						dataLine=dataLine+rsm.getColumnName(i)+"="+rs.getString(i)+", ";
//					}
//				}
//				dataLine = dataLine.trim();
//				if(dataLine.endsWith(",")){
//					dataLine=dataLine.substring(0, dataLine.length()-1);
//				}
//				for(LineVO con:conList){
//					if(con.isIgnore()){
//						continue;
//					}
//					cType = rsm.getColumnType(con.getIndex());
//					if(cType==Types.VARCHAR||cType==Types.VARBINARY
//							||cType==Types.CHAR||cType==Types.BLOB
//							||cType==Types.DATE||cType==Types.TIME
//							||cType==Types.TIMESTAMP){
//						conSql=conSql+rsm.getColumnName(con.getIndex())+"='"+rs.getString(con.getIndex())+"' and ";
//					}
//					else{
//						conSql=conSql+rsm.getColumnName(con.getIndex())+"="+rs.getString(con.getIndex())+" and ";
//					}
//				}
//				conSql = conSql.trim();
//				if(conSql.endsWith("and")){
//					conSql=conSql.substring(0, conSql.length()-"and".length());
//					conSql = conSql.trim();
//				}
//				sList.add(updateStart+dataLine+" where "+conSql+";");
//			}
//			rs.close();
//			ps.close();
//			
//		} catch (SQLException e) {			
////			System.err.println("error:sql="+sql);
//			sList.add("Invalid tableName_key:"+tableName_key);
//			log4.error(e.getMessage());
//			if(this.debug){
//				e.printStackTrace();
//			}
//		}
//		
//		return sList;
//		
//	}
	
	/**
	 * 批量执行sql语句
	 * @param sqlList
	 * @return
	 */
	public String executeSql(List<String> sqlList){
		if(!this.isInit){
			this.init();
		}
		
		if(sqlList==null){
			return "Invalid can't null:";
		}
		
		String rResult = null;
		Statement ps;
		String sql="";
		try {
			ps = conn.createStatement();			
			int count=0;
			int sum = 0;
			for(int i=0; i<sqlList.size(); i++){				
				count++;
				sql = sql+sqlList.get(i);
				if(count==UPDATE_BATCH_COUNT){					
					sum = sum+ps.executeUpdate(sql);
					sql="";
					count=0;
				}
			}
			sum = sum + ps.executeUpdate(sql);
			ps.close();			
			rResult = "Operate success "+sum;
		} catch (SQLException e) {			
//			System.err.println("error:Invalid sql="+sql);
			rResult = "Operate fail";
			log4.error(e.getMessage());
			if(this.debug){
				e.printStackTrace();
			}
		}
		
		return rResult;
	}
	
//	/**
//	 * 执行sql语句
//	 * @param sql
//	 * @return
//	 */
//	public String executeSql(String sql){
//		if(!this.isInit){
//			this.init();
//		}
//		
//		if(sql==null){
//			return "Invalid sql:"+null;
//		}
//		sql = sql.trim();
//		if(sql.startsWith("select")){
//			return this.getListContent(this.getTableData(sql), showDataCount, true);
//		}
//		String rResult = null;
//		Statement ps;
//		try {
//			ps = conn.createStatement();
//			int value = ps.executeUpdate(sql);
//			ps.close();
//			rResult = "Operate success "+value;
//		} catch (SQLException e) {			
////			System.err.println("error:Invalid sql="+sql);
//			log4.error(e.getMessage());
//			if(this.debug){
//				e.printStackTrace();
//			}		
//			rResult = "Operate fail";
//			
//		}
//		return rResult;	
//	}
	
	public String getListContent(List<String> sList, boolean showCount, boolean changeLine){
		return getListContent(sList, showCount, changeLine, 0, 0);
	}
	
	/**
	 * 将sList的所有string拼在一起
	 * @param sList
	 * @param showCount 是否显示行号
	 * @param changeLine 是否换行
	 * @return
	 */
	public String getListContent(List<String> sList, boolean showCount, boolean changeLine, int pageRow, int pageNum){
		StringBuffer sb = new StringBuffer();
		int i=0;
		List<String> tList = new ArrayList<String>();
		int pageStart = pageRow * (pageNum - 1);
		int pageEnd = pageRow * pageNum;
		if(pageRow>0 && pageNum>0){
			if(sList.size()<pageEnd){
				pageEnd=sList.size();
			}
			tList = sList.subList(pageStart, pageEnd);
		}
		else if(pageRow>0&&pageNum==0){
			tList=sList;
		}
		else{
			tList=sList;
		}
		for(String str: tList){			
			//是否显示行号
			if(showCount){
				i++;
				sb.append(i+" ");
			}						
			sb.append(str);
			
			//是否换行
			if(changeLine){
				sb.append("\n");
			}
		}
		if(pageRow!=0&&pageNum!=0){
			sb.append("page:"+pageNum+"/"+(sList.size()/pageRow+1));
		}
		else if(pageRow>0&&pageNum==0){
			sb.append("row:"+sList.size());
		}
		
		return sb.toString();
	}
	
	/**
	 * 数据库保留字段加引号规避
	 * @param columnName
	 * @return
	 */
	public String getEscape(String columnName){	
		columnName = columnName.trim();
		String[] escapes = {"from", "to", "if", "for", "and", "or", "order", "by", "select", "insert", "delete"};
		for(String esc:escapes){
			if(esc.equals(columnName)){
				return "\""+columnName+"\"";
			}
		}
		return columnName;
	}
	
	/**
	 * 根据字段类型返回java类型
	 * @param type
	 * @return
	 */
	public String getJavaType(int type){
		String value = "String";
		switch (type) {
		case Types.INTEGER:
			value = "Integer";
			break;
		case Types.NUMERIC:
			value = "Integer";
			break;
		case Types.LONGVARCHAR:
			value = "Long";
			break;
		case Types.FLOAT:
			value = "Float";
			break;
		case Types.CHAR:
			value = "Char";
			break;
		case Types.VARCHAR:
			value = "String";
			break;
		case Types.VARBINARY:
			value = "String";
			break;
		case Types.BOOLEAN:
			value = "Boolean";
			break;
		case Types.CLOB:
			value = "String";
			break;
		case Types.BLOB:
			value = "String";
			break;
		case Types.DATE:
			value = "Date";
			break;
		case Types.TIMESTAMP:
			value = "Date";
			break;
		case Types.DOUBLE:
			value = "Double";
			break;
		case Types.NULL:
			value = "null";
			break;
		case Types.DECIMAL:
			value = "Integer";
			break;
		case Types.SMALLINT:
			value = "Integer";
			break;
		case Types.TIME:
			value = "Time";
			break;			
		default:
			break;
		}
		return value;
	}
	
	public String columnAttr(String columnName){
		String value = null;
		if(columnName==null){
			return null;
		}
		columnName=columnName.toLowerCase();
		String[] strs = columnName.split("_");
		value = strs[0];
		for(int i=1; i<strs.length; i++){
			value = value+this.upperCaseStart(strs[i]);
		}
		
		return value;
	}
	
	public String upperCaseStart(String str){
		if(str==null||str.length()<2){
			return str;
		}
		else{			
			str = str.substring(0,1).toUpperCase()+str.substring(1);
			return str;
		}
	}
	
	/**
	 * 执行sql语法的操作
	 * @param querySql
	 */
	public List executeQuery(String querySql, int column_count){
		int[] types = new int[column_count];
		for(int i=0; i<column_count; i++){
			types[i]=java.sql.Types.VARCHAR;
		}
		List list = executeQuery(querySql, types);
		return list;
	}
	
	/**
	 * 执行sql语法的操作int, string, float
	 * @param querySql
	 */
	public List executeQuery(String querySql, int[] types){
		String sql = this.getSqlSelect(querySql);
		List glist = new ArrayList();
		try {			
			Statement ps = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);			
			java.sql.ResultSet rs = ps.executeQuery(sql);
			
			rs.last();					
			if(rs.getRow()>maxTotalRow){
				log4.info("Out of max total row limit:"+rs.getRow());				
				rs.close();
				ps.close();
				return glist;
			}
			rs.first();
			rs.beforeFirst();
			Object[] objs = null;
			while(rs.next()){
				try{
					objs = this.getResultObject(rs, types);
					glist.add(objs);
				}catch(Exception e){
					log4.info(e.getMessage());
				}
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {			
			log4.error(e.getMessage());
			log4.error("error:Invalid sql="+sql);
			if(this.debug){
				e.printStackTrace();
			}
		}		
		return glist;
	}
	
	private Object[] getResultObject(ResultSet rs, int[] types) throws Exception{
		Object[] objs = new Object[types.length];
		for(int i=0; i<types.length; i++){				
			if(types[i]==java.sql.Types.VARCHAR){
				objs[i]=rs.getString(i+1);	
			}
			if(types[i]==java.sql.Types.INTEGER){
				objs[i]=rs.getInt(i+1);	
			}
			if(types[i]==java.sql.Types.BIGINT){
//				objs[i]=rs.getBigDecimal(i+1);
				objs[i] = rs.getLong(i+1);
			}
			if(types[i]==java.sql.Types.TIME){
				objs[i]=rs.getTime(i+1);	
			}
			if(types[i]==java.sql.Types.DOUBLE){
				objs[i]=rs.getDouble(i+1);	
			}
			if(types[i]==java.sql.Types.DATE){
				objs[i]=rs.getDate(i+1);	
			}
			if(types[i]==java.sql.Types.FLOAT){
				objs[i]=rs.getFloat(i+1);	
			}
		}
		return objs;
	}
	
	/**
	 * url处理，替换host,port
	 * @param url
	 * @param host
	 * @param port
	 * @return
	 */
	private String getNewUrl(String url, String host, String port){
		String temp = null;
		if(url.indexOf("localhost")>0){
			temp = url.replaceFirst("localhost", "-host-");
		}
		else{
			temp = url.replaceFirst("\\d+.\\d+.\\d+.\\d+", "-host-");
		}
		String temp2 = null;
		if(port!=null){
			temp2 = temp.replaceFirst(":\\d+", ":-port-");
			if(temp2.indexOf("-port-")>0){
				temp = temp2;
			}
		}
//		System.out.println("url="+temp+" host="+host);
		url = temp.replace("-host-", host);
//		System.out.println("url="+url);
		if(port!=null && temp2.indexOf("-port-")>0){
			url = url.replace("-port-", port);
		}
		return url;
		
	}
	
	private String driver;
	private String url;
	private String host;
	private String user;
	private String pwd;
	private String port;
	private String dsPath;
	
	private boolean showDataCount=false;
	private int maxTotalRow=MAX_TOTAL_ROW;
	public void setMaxTotalRow(int totalRow){
		maxTotalRow=totalRow;
	}
	
	public boolean getShowDataCount(){
		return this.showDataCount;
	}
	
	public int getMaxTotalRow(){
		return this.maxTotalRow;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setShowDataCount(boolean showDataCount){
		this.showDataCount=showDataCount;
	}
	
	public void setDebug(boolean debug){
		this.debug=debug;
	}

//	public static void main(String[] args) {
//		DBTool dbTool = new DBTool();
//		System.out.println(dbTool.columnAttr("test_name_code"));
//		System.out.println(dbTool.columnAttr("test_code"));
//		System.out.println(dbTool.columnAttr("test"));
//		System.out.println(dbTool.columnAttr("tt"));
//	}
}
