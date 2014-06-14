package org.jview.jtool.manager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.model.LineVO;
import org.jview.jtool.model.RepVO;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;


/**
 * 替换工具
 * @author chenjh
 *
 */
public class Replace {
	private static Logger log4 = Logger.getLogger(Replace.class);
	public static String REPLACE_PROP="replace.properties";
	public static String REPLACE_FILE="replace_file.conf";
	public static String REPLACE_SQL="replace_sql.conf";
	private Properties config = new Properties(); 
	private List<RepVO> repFileList=new ArrayList<RepVO>();
	private List<RepVO> repSqlList=new ArrayList<RepVO>();
	private List<String> dataList = new ArrayList<String>();
	private String path=null;
	private String run_path=null;
	private String task_key = "dbs";
	private void loadConfig(){
		String pconf = System.getProperty("pconf");
		
		if(!ErrorCode.isEmpty(pconf)){
			path=pconf.replaceAll("\\\\", "/");
			if(!path.endsWith("/")){
				path = path+"/";
			}
		}		
		InputStream in = null;
		
		try {
			//载入replace.properties
			Properties p = new Properties();
			if(path==null){
				in =  ClassLoader.getSystemResourceAsStream(REPLACE_PROP);
			}
			else{
				File file = new File(path+REPLACE_PROP);
				if(file.exists()){
					in = new FileInputStream(file);
				}
				else{
					in =  ClassLoader.getSystemResourceAsStream(path+REPLACE_PROP);
				}				
			}		
			try{
				p.load(in);
			}catch(Exception e){
				log4.info(path+REPLACE_PROP+" file not exist!");
				if(debug)
					e.printStackTrace();
			}
			this.config=p;
			in.close();
			//载入replace.properties end
			
			//载入replace_file.conf
			File file = new File(path+REPLACE_FILE);
			
			List<String> lineList = CommMethod.readLineFile(file);
			List<RepVO> repList=new ArrayList<RepVO>();			
			RepVO rep = null;
			
			if(file.exists()){				
				for(String line:lineList){
//					log4.info(line);
					if(ErrorCode.isEmpty(line)||line.trim().startsWith("#")){
						continue;
					}	
					line = line.trim();
					rep = new RepVO(line);
					rep.setFilter(this.getConvert(rep.getFilter()));
					rep.setSrc(this.getConvert(rep.getSrc()));
					rep.setDest(this.getConvert(rep.getDest()));
					repList.add(rep);							
				}
				this.repFileList=repList;
				this.print(this.repFileList);
			}
			else{
				log4.info("File:"+path+REPLACE_FILE+" not found!");
				return;
			}
			
			
			
			
			//载入replace_sq.conf			
			file = new File(path+REPLACE_SQL);			
			lineList = CommMethod.readLineFile(file);
			repList=new ArrayList<RepVO>();			
			rep = null;			
			if(file.exists()){				
				for(String line:lineList){
					if(ErrorCode.isEmpty(line)||line.startsWith("#")){
						continue;
					}	
					line = line.trim();
					rep = new RepVO(line);
					rep.setFilter(this.getConvert(rep.getFilter()));
					rep.setSrc(this.getConvert(rep.getSrc()));
					rep.setDest(this.getConvert(rep.getDest()));
					repList.add(rep);												
				}
				this.repSqlList=repList;
			}
			else{
				log4.info("File:"+path+REPLACE_FILE+" not found!");
				return;
			}
			
			
		} catch (IOException e) {
			log4.error(e.getMessage());
			if(debug)
				e.printStackTrace();
		} catch (Exception e) {
			log4.error(e.getMessage());
			if(debug)
				e.printStackTrace();
		}
		
//		log4.info(this.config.get("dest_host"));
		
		
		
	}
	
	public void replaceFile(){
		for(RepVO rep:repFileList){
			try {
				if(rep.getFileName().indexOf("|")>0){
					this.replaceSql(rep);
				}
				else{
					this.replaceFile(rep);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	

	public void checkFile(){
		for(RepVO rep:repFileList){
			try {
				if(rep.getFileName().indexOf("|")>0){
					this.checkSql(rep);
				}
				else {
					this.checkFile(rep);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 备份File
	 */
	public void backupFile(){
		
		
		ZipOutputStream zos = null;
//		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path
//				+ ConstSystem.UPDATE_LOCAL_SYSTEM_FILE));
		try{
			
			
			ZipEntry ze = null;
			byte[] buf = new byte[1024];
			int readLen = 0;
//			String n_name = null;
//			boolean isExist = false;
			int count = 0;
			int cur_count=0;
			File f = null;
			String path = this.run_path;
			
			String file_path = null;
			if(!path.endsWith("/")){
				path = path+"/";
			}
			
			log4.info(path+"system.zip");
			zos = new ZipOutputStream(new FileOutputStream(path+"system.zip"));
			File sqlFile = new File(path+"backup.sql");
			List<String> sqlList = new ArrayList<String>();
			List<String> fileNameList = new ArrayList<String>();
			String ze_path = null;
			boolean check=false;
			for(RepVO rep:repFileList){				
				if(rep.getFileName().indexOf("|")>0){//sql
//					List<String> updateList = this.dbTool.getUpdateSql(rep.getFileName());
					ITask dbOper = (ITask)TaskManager.getTaskMap().get(task_key+"_"+"update");
					List<String> updateList = dbOper.doExecute(rep.getFileName());
					List<String> filterList = CommMethod.filterContent(updateList, rep.getFilter());
					cur_count=0;
					String sql = null;
					for(String filter:filterList){
						check = this.checkUpdateSql(filter, !direct?rep.getSrc():rep.getDest());
						if(check){
//							log4.info("    checkLine ok "+sql);
							sql = filter;
							cur_count++;							
						}
				
					}
//					String result = null;
					if(cur_count==rep.getCount()||replace){		
						log4.info("    check sql ok "+sql);
						sqlList.add(sql);
					}
					else{
						log4.info("    check sql fail "+rep.getFileName());
					}
					
				}
				else{//file
					file_path = rep.getFileName();
					if(!file_path.startsWith("/")){
						file_path = this.run_path+rep.getFileName();
					}					
					f = new File(file_path);
					if(!f.exists()){
						log4.info(file_path);
						continue;
					}
					List<String> readList = CommMethod.readLineFile(f);
					List<LineVO> lineList = CommMethod.getLine(readList);
					lineList = CommMethod.filterLine(lineList, rep.getFilter(), ",");
						
					
					List<LineVO> repList = new ArrayList<LineVO>();
					for(LineVO line:lineList){
						if(ErrorCode.isEmpty(line.getLine())||line.getLine().startsWith("#")){
							continue;
						}
						cur_count=0;
						if(rep.getType()==0){
							cur_count=CommMethod.countContent(line.getLine(), direct?rep.getDest():rep.getSrc());							
						}
						else if(rep.getType()==1){
							for(int i=0; i<rep.getCount(); i++){
								if(line.getLine().indexOf(direct?rep.getDest():rep.getSrc())>=0){
									cur_count++;													
								}						
							}
						}					
						if(cur_count>0||replace){
							repList.add(line);
							ze_path = getAbsFileName(path, f);
							if(fileNameList.contains(ze_path)){
								continue;
							}
							fileNameList.add(ze_path);
							
							
//								log4.info("    checkingLine ok "+rep.getType()+" "+cur_count+" find:"+(direct?rep.getDest():rep.getSrc())+" "+line.getLine());
							ze = new ZipEntry(ze_path);							
							ze.setSize(f.length());
							ze.setTime(f.lastModified());
							count++;							
							// 将ZipEntry加到zos中，再写入实际的文件内容
							zos.putNextEntry(ze);
							InputStream is = new BufferedInputStream(new FileInputStream(f));
							while ((readLen = is.read(buf, 0, 1024)) != -1) {
								zos.write(buf, 0, readLen);
							}
							is.close();
						}
						else{
							log4.info("checking fail "+count+"/"+rep.getCount());
						}
						count = count+cur_count;
					}
				}
				
			}
			if(count>0){
				zos.close();
			}
			
			if(sqlList.size()>0){
				log4.info(sqlFile.getAbsolutePath());
				CommMethod.writeLineFile(sqlFile, sqlList);
			}
			log4.info("备份成功，共计"+count+"个文件");
		}catch(Exception e){
			log4.info("备份失败!");
			e.printStackTrace();
		}
		
	}
		
	/**
	 * 给定根目录，返回另一个文件名的相对路径，用于zip文件中的路径.
	 * 
	 * @param baseDir
	 *            java.lang.String 根目录
	 * @param realFileName
	 *            java.io.File 实际的文件名
	 * @return 相对文件名
	 */
	private String getAbsFileName(String baseDir, File realFileName) {
		File real = realFileName;
		File base = new File(baseDir);
		String ret = real.getName();
		while (true) {
			real = real.getParentFile();
			if (real == null)
				break;
			if (real.equals(base))
				break;
			else {
				ret = real.getName() + "/" + ret;
			}
		}
		return ret;
	}

	
	
	
	private void replaceFile(RepVO rep)throws Exception{
		String path = rep.getFileName();
		if(!path.startsWith("/")){
			path = this.run_path+rep.getFileName();
		}
		File file = new File(path);
		if(file.exists()){
			List<String> readList = CommMethod.readLineFile(file);
			List<LineVO> lineList = CommMethod.getLine(readList);
			lineList = CommMethod.filterLine(lineList, rep.getFilter(), ",");
			int count=0;
//			boolean isReplace=false;
			int cur_count=0;
			List<LineVO> repList = new ArrayList<LineVO>();
			for(LineVO line:lineList){
				if(ErrorCode.isEmpty(line.getLine())||line.getLine().startsWith("#")){
					continue;
				}
				cur_count=0;
				if(rep.getType()==0){
					cur_count=CommMethod.countContent(line.getLine(), direct?rep.getSrc():rep.getDest());
					log4.info(" direct="+direct+" cur_count="+cur_count+" "+(direct?rep.getSrc():rep.getDest()));
					if(cur_count>0){
						if(direct){
							line.setLine(line.getLine().replaceAll(rep.getSrc(), rep.getDest()));
						}
						else{
							line.setLine(line.getLine().replaceAll(rep.getDest(), rep.getSrc()));
						}
					}					
				}
				else if(rep.getType()==1){
					for(int i=0; i<rep.getCount(); i++){
						if(line.getLine().indexOf(direct?rep.getSrc():rep.getDest())>=0){
							cur_count++;
							if(direct){							
								line.setLine(line.getLine().replace(rep.getSrc(), rep.getDest()));
							}
							else{
								line.setLine(line.getLine().replace(rep.getDest(), rep.getSrc()));
							}							
						}						
					}
				}
				if(cur_count>0){
					repList.add(line);
					log4.info("    replaceLine ok "+rep.getType()+" "+cur_count+" replaced:"+(!direct?rep.getSrc():rep.getDest())+" "+line.getLine());
					
				}
				count = count+cur_count;
			}
			if(count>0){
				for(LineVO line:repList){
					readList.set(line.getIndex(), line.getLine());
				}
			}
			if(count>0&&(count==rep.getCount()||replace)){
				CommMethod.writeLineFile(file, readList);
				log4.info("replace ok "+count+"/"+rep.getCount());
			}			
			else{
				log4.info("replace fail "+count+"/"+rep.getCount());
			}
		}
		else{
			log4.info("replace fail:"+path+" not exist!");
		}
	}
	
	private void replaceSql(RepVO rep){
		DBTool dbTool = TaskManager.getDBTool();
		if(!dbTool.isInit){
			dbTool.init();
		}
		ITask dbOper = (ITask)TaskManager.getTaskMap().get(task_key+"_"+"update");
//		List<String> updateList = this.dbTool.getUpdateSql(rep.getFileName());
		List<String> updateList = dbOper.doExecute(rep.getFileName());
		List<String> filterList = CommMethod.filterContent(updateList, rep.getFilter());
		String updateSql="";
		int count=0;
		String sql = null;
		for(String filter:filterList){
			sql = this.replaceUpdate(filter, direct?rep.getSrc():rep.getDest(), direct?rep.getDest():rep.getSrc());
			if(!sql.equals(filter)){
				log4.info("    replaceLine ok "+sql);
				count++;
			}
			updateSql = updateSql+sql;
		}

		String result = null;
		if(count>0&&(count==rep.getCount()||replace)){
//			result = this.dbTool.executeSql(updateSql);
			dbOper = (ITask)TaskManager.getTaskMap().get(task_key+"_"+"sql");
			dataList = dbOper.doExecute(updateSql);
			if(dataList.size()>0){
				result = dataList.get(0);
			}
			result = result+"\n" 
				+"replace ok "+count+"/"+rep.getCount();
		}
		else{
			result="replace fail "+count+"/"+rep.getCount();
		}
		
		log4.info(result);
		
		
	}
	
	private void checkFile(RepVO rep) throws Exception{
		String path = rep.getFileName();
		if(!path.startsWith("/")){
			path = this.run_path+rep.getFileName();
		}
		File file = new File(path);
		if(file.exists()){
			List<String> readList = CommMethod.readLineFile(file);
			List<LineVO> lineList = CommMethod.getLine(readList);
			lineList = CommMethod.filterLine(lineList, rep.getFilter(), ",");
			int count=0;
		
			int cur_count=0;
			List<LineVO> repList = new ArrayList<LineVO>();
			for(LineVO line:lineList){
				if(ErrorCode.isEmpty(line.getLine())||line.getLine().startsWith("#")){
					continue;
				}
				cur_count=0;
				if(rep.getType()==0){
					cur_count=CommMethod.countContent(line.getLine(), direct?rep.getDest():rep.getSrc());							
				}
				else if(rep.getType()==1){
					for(int i=0; i<rep.getCount(); i++){
						if(line.getLine().indexOf(direct?rep.getDest():rep.getSrc())>=0){
							cur_count++;													
						}						
					}
				}
				if(cur_count>0){
					repList.add(line);					
					log4.info("    checkingLine ok "+rep.getType()+" "+cur_count+" find:"+(direct?rep.getDest():rep.getSrc())+" "+line.getLine());
					
				}
				count = count+cur_count;
			}
			
			
			if(count==rep.getCount()){
				log4.info("checking file ok "+count+"/"+rep.getCount());
			}
			else if(count!=rep.getCount()){
				log4.info("checking file fail "+count+"/"+rep.getCount());
			}
			else{
				log4.info("checking file fail "+count+"/"+rep.getCount());
			}
		}
		else{
			log4.info("checking file fail:"+path+" not exist!");
		}
		
	}
	
	private void checkSql(RepVO rep){
		ITask dbOper = (ITask)TaskManager.getTaskMap().get(task_key+"_"+"update");
		List<String> updateList = dbOper.doExecute(rep.getFileName());		
		List<String> filterList = CommMethod.filterContent(updateList, rep.getFilter());
		
		boolean check=false;
		int count=0;
		String sql = null;
		for(String filter:filterList){
			check = this.checkUpdateSql(filter, !direct?rep.getSrc():rep.getDest());
			if(check){
				sql = filter;
//				log4.info("    checkLine ok "+sql);
				count++;							
			}
			
		}
		String result = null;
		if(count==rep.getCount()){			
			result = "checking sql ok "+count+"/"+rep.getCount();
		}
		else{
			result="checking sql fail "+count+"/"+rep.getCount();
		}
		
		log4.info(result);
	}
	
	/**
	 * 将#paramter#转成paramter,即去掉前后#
	 * @param value
	 * @return
	 */
	private String getConvert(String value){
		String rValue = null;
		if(ErrorCode.isEmpty(value)){
			return value;
		}
		String split_str="#";
		if(value.indexOf(split_str)>=0){
			rValue = this.getConvert(value, split_str);
			if(value.indexOf(split_str)>0){
				rValue = this.getConvert(rValue, split_str);
			}
			if(rValue.indexOf(split_str)>0){
				rValue = this.getConvert(rValue, split_str);
			}
			if(rValue.indexOf(split_str)>0){
				rValue = this.getConvert(rValue, split_str);
			}
		}
		else{
			rValue = value;
		}	
		return rValue;
	}
	
	/**
	 * 在update语句中进行替换,只替换值，不替换表或字段
	 * @param line(updateSql)
	 * @param src
	 * @param dest
	 * @return
	 */
	private String replaceUpdate(String line, String src, String dest){
		StringBuffer sb = new StringBuffer();		
		String lineStart = line.substring(0, line.indexOf("set")+"set".length());
		String lineMid=line.substring(line.indexOf("set")+"set".length(), line.indexOf("where"));
		String lineEnd = line.substring(line.indexOf("where"));
		
		for(String str:lineMid.split(", ")){
			if(str.indexOf("=")>0){
				str = str.substring(0, str.indexOf("=")+1)
					+str.substring(str.indexOf("=")+1).replaceAll(src, dest);
				sb.append(str).append(", ");
			}
		}
		String rLine = sb.toString().trim();
		if(rLine.endsWith(",")){
			rLine = rLine.substring(0, rLine.length()-1);
		}
		rLine = lineStart+ " "+rLine+lineEnd;
		return rLine;
	}
	
	private boolean checkUpdateSql(String line, String src){
//		StringBuffer sb = new StringBuffer();		
//		String lineStart = line.substring(0, line.indexOf("set")+"set".length());
		String lineMid=line.substring(line.indexOf("set")+"set".length(), line.indexOf("where"));
//		String lineEnd = line.substring(line.indexOf("where"));
		int count=0;
		for(String str:lineMid.split(", ")){
			if(str.indexOf("=")>0){
				str = str.substring(0, str.indexOf("=")+1)
					+str.substring(str.indexOf("=")+1);
				
				if(str.indexOf(src)>=0){
					count++;
				}
//				log4.info(str+"========"+src+count);
//				sb.append(str).append(", ");
			}
		}
		
//		if(rLine.endsWith(",")){
//			rLine = rLine.substring(0, rLine.length()-1);
//		}
//		rLine = lineStart+ " "+rLine+lineEnd;
		
		boolean status =  count>0?true:false;
//		log4.info(status);
		return status;
	}
	
	/**
	 * 值转换，具有##包含的key用值替换
	 * @param value
	 * @param split_str
	 * @return
	 */
	private String getConvert(String value, String split_str){
		String rValue = null;
		String head=null;
		String key =null;
		String keyValue = null;
		
		if(value.indexOf(split_str)==0){
			rValue = value.substring(split_str.length());
			head="";
		}
		else if(value.indexOf(split_str)>0){
			head=value.substring(0, value.indexOf(split_str));
			rValue = value.substring(value.indexOf(split_str)+split_str.length());
		}
		else{
			return value;
		}
		if(rValue.indexOf(split_str)>0){
			key = rValue.substring(0, rValue.indexOf(split_str));
			keyValue = config.getProperty(key);
			if(null==keyValue){
				log4.info("Param:"+key+" not found in "+REPLACE_PROP);
				return value;
			}
			value = head+keyValue+rValue.substring(rValue.indexOf(split_str)+split_str.length());
			return value;
		}
		else{
			return value;
		}
	}
	
	private void print(List<RepVO> repList){
		for(RepVO rep:repList){
			log4.info(rep.getType()+"	"+rep.getCount()+"	"+rep.getFileName()+"	"+rep.getFilter()+"	"+rep.getSrc()+"	"+rep.getDest());
		}
	}
	
	private void run(String[] args){
//		String cmd = null;
//		String runPath =null;
		String mode="replace";
		if(args!=null&&args.length>0){			
			
			if(args[0].equalsIgnoreCase("check")){
				mode="check";
			}	
			else if(args[0].equalsIgnoreCase("replace")){
				mode="replace";
			}
			else if(args[0].equalsIgnoreCase("backup")){
				mode="backup";
			}
			else{
				log4.info("[mode(replace|check|backup) [runPath] [direct(true|false) [replace(false|true) [debug(false|true)]]]]");
				return;
			}
			
			
			
			if(args.length>1){
				String path_direct = args[1];
				String directStr = null;
				String replaceStr = null;
				String debugStr = null;
				if(!(path_direct.equalsIgnoreCase("true")||path_direct.equalsIgnoreCase("false"))){
					this.run_path=path_direct;
					if(args.length>2){
						directStr=args[2];
					}
					if(args.length>3){
						replaceStr=args[3];
					}
					if(args.length>4){
						debugStr=args[4];
					}
				}
				else{
					directStr = path_direct;
					if(args.length>2){
						replaceStr=args[2];
					}
					if(args.length>3){
						debugStr=args[3];
					}
				}
		
				this.direct=Boolean.parseBoolean(directStr);
				this.replace=Boolean.parseBoolean(replaceStr);
				this.debug=Boolean.parseBoolean(debugStr);
			}

		}
		
		if(ErrorCode.isEmpty(run_path)){
//			log4.info("RunPath can not empty!");
			if(ErrorCode.isEmpty(run_path)){
				File file = new File("temp.txt");
				run_path = file.getAbsolutePath().replaceAll("\\\\", "/");
				run_path = run_path.substring(0, run_path.lastIndexOf("/")+1);
				log4.info(run_path);
//				return;
			}
		}
		
		
		this.loadConfig();
		if(mode.equals("replace")){
			this.replaceFile();
		}
		else if(mode.equals("check")){
			this.checkFile();
		}
		else if(mode.equals("backup")){
			this.backupFile();
		}
		
	}
	private boolean debug=true;
	private boolean replace;
	private boolean direct;
	
	public static void main(String[] args) {
		Replace p = new Replace();
		p.run(args);
	}
	
	
}
