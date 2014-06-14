package org.jview.jtool.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jview.jtool.ITask;
import org.jview.jtool.model.StringListComparator;
import org.jview.jtool.model.TaskVO;
import org.jview.jtool.ta_dbs.IDb;
import org.jview.jtool.ta_replace.IRep;
import org.jview.jtool.ta_tools.ITool;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.tools.Formula;
import org.jview.jtool.tools.MD5;
import org.jview.jtool.util.ClassUtil;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * 主程序
 * @author chenjh
 * qq:80012995
 * @email:jview@139.com
 * @version 1.0
 *
 */
public class TaskManager {
	private static Logger log4 = Logger.getLogger(TaskManager.class);
	public static DBTool dbTool=null;
	public static boolean debug=true;
	private boolean sort=false;
	private boolean disp=true;//是否显示数据库命令执行结果，同时多条命令时可以不显示前面的命令结果
	private List<String> dataList=new ArrayList<String>();
	
	public static String TASK_PACKAGE=ITask.class.getPackage().getName()+".ta_";
	private static Map<String, ITask> taskMap;
	private static Map taskListMap;
//	private static Map<String, ITool> toolMap;
//	private static Map<String, IDb> dbOperMap;
//	private List<TaskVO> toolList;
//	private List<OperVO> dbOperList;
	private String operCode;//最近的操作代码
	
	public static void main(String[] args) {
		TaskManager dbInfo = new TaskManager();
		
		boolean isParaMode = false;
		String cmd = null;
		
//		for(String arg:args){
//			System.out.println("arg-"+arg);
//		}
		
		if(args!=null&&args.length>1){			
			if(args[0].equals("para")){
				isParaMode =true;
			}
			String host=null, user=null, pwd=null;
			String port = null;
			if(args.length>1){
				if(CommMethod.hasIpAddress(args[1])){//有ip地址
					if(args.length>=4){				
						host = args[1];
						user = args[2];
						pwd = args[3];
					}
					cmd = "";
					if(args.length>=5){
						if(args[4].matches("\\d+")){
							port = args[4];
							for(int i=5; i<args.length; i++){
								cmd = cmd+" "+args[i];
							}
						}
						else{
							for(int i=4; i<args.length; i++){
								cmd = cmd+" "+args[i];
							}
						}				
					}
					dbInfo.dbTool = new DBTool(host, user, pwd, port);
				}
				else{//没有IP地址，直接指令
					cmd="";
					for(int i=1; i<args.length; i++){
						cmd = cmd+" "+args[i];
					}
							
				}
//				System.out.println("cmd="+cmd);
				
			}
		}
		
		dbInfo.isParaMode=isParaMode;
		
		//读取参数
		dbInfo.initConfig();
		if(dbInfo.dbTool==null){
			dbInfo.dbTool= new DBTool();
		}
		if(isParaMode){
			dbInfo.paraMode(cmd);
		}
		else{
			try{				
				dbInfo.commandMode(cmd);
			}catch(Exception e){
				log4.error("Cmd error");				
				e.printStackTrace();
				
			}
		}
		
	}
	public void initTask(){
		if(dbTool==null){
			dbTool = new DBTool();
		}
//		System.out.println("------initTask----"+this.taskMap);
		if(this.taskMap==null){
			this.taskMap=new HashMap();
			this.taskListMap = new HashMap();
//			Set<Class<?>> clzSet=null;
//			Set<Package> packs = ClassUtil.getPackages(ITool.class.getPackage());
//			Set<Class<?>> clzSet2 = ClassUtil.getClasses(ITool.class.getPackage());
//			System.out.println("------packs="+packs+" "+clzSet2);
//			Class clz= null;
//			Iterator iters=null;
//			Class tmp;
//			List list = null;
//			String task_key = null;
//			for(Package pack:packs){
////				System.out.println("--initTask--"+pack.getName());
//				if(pack.getName().startsWith(TASK_PACKAGE)){	
//					list = new ArrayList();					
//					this.initTools(pack);
//					this.initDbs(pack);					
//					System.out.println("--initTask--"+pack.getName());
//				}				
//			}
			this.initTools(ITool.class.getPackage());
			this.initDbs(IDb.class.getPackage());
			this.initReplace(IRep.class.getPackage());	
		}
	}
	
	private void initTools(Package pack){
		String task_key = cmd_type.TOOLS.cmd;
		this.initPackage(pack, task_key, ITool.class.getName());
	}
	
	private void initDbs(Package pack){
		String task_key = cmd_type.DBS.cmd;
		this.initPackage(pack, task_key, IDb.class.getName());
	}	
		
	private void initReplace(Package pack){
		String task_key = cmd_type.REPLACE.cmd;
		this.initPackage(pack, task_key, IRep.class.getName());
	}
	
	/**
	 * 包名中必须有.ta_才行
	 * @param pack
	 * @param task_key
	 * @param ignoreClassName
	 */
	private void initPackage(Package pack, String task_key, String ignoreClassName){
//		String task_key = cmd_type.REPLACE.cmd;
		Set<Class<?>> clzSet=null;
		Iterator iters=null;
		Class clz;
		List list = new ArrayList();
		if(pack.getName().indexOf(".ta_"+task_key)>0){
			clzSet =ClassUtil.getClasses(pack);
			iters = clzSet.iterator();	
			ITask taskBean = null;
			TaskVO tInfo=null;
			while(iters.hasNext()){
				clz = (Class)iters.next();
				try {					
					if(!clz.getName().endsWith(ignoreClassName)){									
						taskBean = (ITask)ClassUtil.newInstance(clz.getName(), new Object[]{});
						tInfo = new TaskVO();
						tInfo.setCode(taskBean.getCode());
						tInfo.setHelpInfo(taskBean.getHelpInfo());
						tInfo.setTaskId(taskBean.getTaskId());
						list.add(tInfo);								
						this.taskMap.put(task_key+"_"+taskBean.getCode(), taskBean);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}							
			}
			this.taskListMap.put(task_key, list);
		}
	}
	
	/**
	 * 初始参数处理
	 */
	private void initConfig(){		
		this.initTask();
		String rowtotal = System.getProperty("rowtotal");
		String rowpage = System.getProperty("rowpage");
		String rowcount = System.getProperty("rowcount");
		String filemode = System.getProperty("filemode");
		String lowcase = System.getProperty("lowcase");
		String debug = System.getProperty("debug");
		if(!ErrorCode.isEmpty(rowtotal)){
			this.config("rowtotal "+rowtotal);
		}
		if(!ErrorCode.isEmpty(rowcount)){
			this.config("rowcount "+rowcount);
		}
		if(!ErrorCode.isEmpty(filemode)){
			this.config("filemode "+filemode);
		}
		if(!ErrorCode.isEmpty(rowpage)){
			this.config("rowpage "+rowpage);
		}
		if(!ErrorCode.isEmpty(lowcase)){
			this.config("lowcase "+lowcase);
		}
		if(!ErrorCode.isEmpty(debug)){
			this.config("debug "+debug);
		}
		
	}
	
	/**
	 * 参数模式
	 * @param cmd
	 */
	private void paraMode(String cmd){
		if(ErrorCode.isEmpty(cmd)){
			log4.info("No cmd of para.");
			return;
		}
		else{
			cmd = cmd.trim();
			try{
				if(cmd.startsWith(cmd_type.TOOLS.getCmd()+" ")){
					this.cmdToolOper(cmd.substring(cmd_type.TOOLS.getCmd().length()+1));
				}
				else if(cmd.startsWith(cmd_type.DBS.getCmd()+" ")){
					this.cmdDbsOper(cmd.substring(cmd_type.DBS.getCmd().length()+1));
				}
				else{
					if(cmd.indexOf(" ")>0){
						this.cmdTask(cmd.substring(0, cmd.indexOf(" ")), cmd.substring(cmd.indexOf(" ")));
					}				
					else{
						this.cmdTask(cmd, null);
					}
				}
			}catch(Exception e){
				log4.error("Invalid para cmd:"+cmd);
//				e.printStackTrace();
				if(debug){
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 检查是否是登入模式
	 * @param cmd
	 * @param sql
	 * @return
	 */
	private boolean loginMode(String cmd, String sql){
		boolean status=false;
		if(ErrorCode.isEmpty(sql)){						
			this.loginMode(cmd);
			status = true;
			
		}
		return status;
	}
	
	private List<String> loginCmd(String cmdMode, String cmd){
		List<String> resutList=new ArrayList();
		try{
			cmd = cmd.trim();
//			System.out.println("-------loginCmd--cmdMode="+cmdMode+" cmd="+cmd);
			
			//按条件查找行，多个条件，采用二次匹配方式
			if(cmd.startsWith(cmd_login.F.getCmd()+" ")){
				String src_value = cmd.substring(2);		
//				System.out.println("======1==="+src_value+"  cmdMode="+cmdMode+" disp="+this.disp+"  "+this.dataList.size());
				List<String> filterList =CommMethod.filterContent(dataList, src_value, ",");
				//保留行头处理
				StringBuffer rSb = new StringBuffer();
				if(this.isDataHead(cmdMode)&&dataList.size()>0){
					rSb.append(dataList.get(0).trim()+"\n");
				}
//				System.out.println("======2==="+rSb.toString()+" filterList size="+filterList.size()+" showDataCount="+showDataCount+" dbTool="+dbTool);
				String rValue = this.dbTool.getListContent(filterList, this.showDataCount, true);					
				rSb.append(rValue);
//				System.out.println("======21==="+rSb.toString());
				System.out.println(rSb.toString());
				log4.info(rSb.toString());
				resutList.add(dataList.get(0));
				resutList.addAll(filterList);
				
			}
			else if(cmd.startsWith(cmd_login.FF.getCmd()+" ")){
				if(last_cmd!=null){
					this.disp=false;
					this.cmdTask(last_cmd, last_sql);
					this.disp=true;
				}
				String src_value = cmd.substring(2);		
//				System.out.println("======1==="+src_value+" disp="+this.disp+"  "+this.dataList.size());
				List<String> filterList =CommMethod.filterContent(dataList, src_value, ",");
				//保留行头处理
				StringBuffer rSb = new StringBuffer();
				if(this.isDataHead(cmdMode)&&dataList.size()>0){
					rSb.append(dataList.get(0).trim()+"\n");
				}
//				System.out.println("======2==="+rSb.toString()+" filterList size="+filterList.size()+" showDataCount="+showDataCount+" dbTool="+dbTool);
				String rValue = this.dbTool.getListContent(filterList, this.showDataCount, true);					
				rSb.append(rValue);
//				System.out.println("======21==="+rSb.toString());
				System.out.println(rSb.toString());
				log4.info(rSb.toString());
				resutList.add(dataList.get(0));
				resutList.addAll(filterList);
			}
			else if(cmd.equalsIgnoreCase(cmd_login.FN.getCmd())||cmd.startsWith(cmd_login.FN.getCmd()+" ")){
				String src_value = cmd.substring(cmd_login.FN.getCmd().length());
				int pageLimit=this.dataList.size()/this.pageRow+1;
				if(ErrorCode.isEmpty(src_value)){
					if(this.pageNum<pageLimit){
						this.pageNum++;
					}
				}
				else{
//					try {
						src_value = src_value.trim();
						int num =Integer.parseInt(src_value);
						if(num>=0&&num<=pageLimit){
							this.pageNum=num;								
						}
						else if(num>pageLimit){
							String rValue="Out of max";
							System.out.println(rValue);
							log4.info(rValue);
							resutList.add(rValue);
							return resutList;
						}
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						log4.error(e.getMessage());
//						if(debug)
//							e.printStackTrace();
//					}
				}
				
				//保留行头处理
				StringBuffer rSb = new StringBuffer();
				String rValue ="";
				if(this.isDataHead(cmdMode)&&dataList.size()>0){
					rSb.append(dataList.get(0).trim()+"\n");
					rValue = this.dbTool.getListContent(this.dataList.subList(1, this.dataList.size()), this.showDataCount, true, this.pageRow, this.pageNum);
					resutList.add(dataList.get(0));
					resutList.addAll(this.dataList.subList(1, this.dataList.size()));
				}
				else{
					rValue = this.dbTool.getListContent(this.dataList, this.showDataCount, true, this.pageRow, this.pageNum);
					resutList.addAll(dataList);
				}
				
				rSb.append(rValue);
				System.out.println(rSb.toString());
				log4.info(rSb.toString());					
			}
			else if(cmd.startsWith(cmd_login.ORDER.getCmd())){
				String[] filters = cmd.substring(cmd_login.ORDER.getCmd().length()+1).split(" ");
				int col = 0;
				boolean status = false;
				try {
					col = Integer.parseInt(filters[0]);
					if(cmdMode.equals("data")||cmdMode.equals("fdata")){
						status = this.sort(dataList, col, true);
					}
					else{
						status = this.sort(dataList, col, false);
					}
				} catch (Exception e) {
					if(debug)
						e.printStackTrace();
					status = false;
					log4.error("Invalid parameter");
					resutList.add("Invalid parameter");
				}
				if(!status){
					return resutList;
				}
				String rValue = this.dbTool.getListContent(dataList, this.showDataCount, true);
				System.out.println(rValue);
				log4.info(rValue);		
				resutList.addAll(dataList);
			}
			else if(cmd.equalsIgnoreCase(cmd_login.HELP.getCmd())){
				String rValue = "";
				String helpInfo=null;
				if(cmdMode.equals("tools")){
					for(cmd_tool cType:cmd_tool.values()){
						helpInfo=cType.getCmd()+", "+cType.getHelpInfo();
						rValue = rValue+helpInfo+"\n";
						resutList.add(helpInfo);
					}
				}
				List<TaskVO> dbsList = (List) this.taskListMap.get(cmdMode);
				if(dbsList!=null){
					for(TaskVO tInfo:dbsList){
						helpInfo=tInfo.getCode()+", "+tInfo.getHelpInfo();
						rValue = rValue+helpInfo+"\n";
						resutList.add(helpInfo);
					}
				}
				rValue +="\n";
				resutList.add(" ");
				for(cmd_login cType:cmd_login.values()){
					helpInfo=cType.getCmd()+", "+cType.getHelpInfo();
					rValue = rValue+helpInfo+"\n";
					resutList.add(helpInfo);
				}
				System.out.println(rValue);
				log4.info(rValue);
			}
			else{
//				System.out.println("-------loginCmd--cmdMode2="+cmdMode+" cmd="+cmd);
				this.cmdTask(cmdMode, cmd);				
			}
		}
		catch(Exception e){
			log4.error("error:"+e.getMessage());
//			e.printStackTrace();
			resutList.add(cmd+","+e.getMessage());
			if(debug){
				e.printStackTrace();
			}
		}
		return resutList;
	}
	
	/**
	 * 二级登入模式
	 * @param cmd
	 */
	private void loginMode(String cmd){
		String cmdMode = cmd;
		String cmdOper=cmd;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		do {
			System.out.print(this.dbTool.getUser()+"|"+cmdMode+">");
			try{
				cmd = br.readLine();
				if(ErrorCode.isEmpty(cmd)){
					continue;
				}
				cmd = cmd.trim();
				if(cmd.equalsIgnoreCase(cmd_login.EXIT.getCmd())){
					System.out.println(cmdMode+" exit");
					log4.info(cmdMode+" exit");
					return;
				}
				else if(cmd.equalsIgnoreCase(cmd_login.QUIT.getCmd())){
					System.out.println(this.dbTool.getUser()+"|"+cmdMode+" quit");
					log4.info(this.dbTool.getUser()+"|"+cmdMode+" quit");
					System.exit(0);
				}
				
				cmd_login[] cmdTypes=cmd_login.values();
				boolean isExist=false;
				for(cmd_login cmdType:cmdTypes){
					if(cmd.startsWith(cmdType.cmd+" ")){
						cmdOper=operCode;
						isExist=true;
						break;
					}
				}
//				System.out.println("----------loginMode--cmd="+cmd+" cmdMode="+cmdMode+" cmdOper="+cmdOper);
				if(isExist){
					this.loginCmd(cmdOper, cmd);
				}
				else{
					this.cmdTask(cmdMode, cmd);
				}
			}catch(Exception e){
				log4.error("error:"+e.getMessage());
			}
		} while (true);
	}
	
//	/**
//	 * 二级tool模式
//	 * @param cmd
//	 */
//	private void toolMode(String cmd){
//		String cmdMode = cmd;
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		do {
//			try{
//				System.out.print(this.dbTool.getUser()+"|"+cmdMode+">");
//				cmd = br.readLine();
//				if(ErrorCode.isEmpty(cmd)){
//					continue;
//				}
//				cmd = cmd.trim();
//				if(cmd.equalsIgnoreCase(cmd_tool.EXIT.getCmd())){
//					log4.info(cmdMode+" exit");
//					return;
//				}
//				else if(cmd.equalsIgnoreCase(cmd_tool.QUIT.getCmd())){
//					log4.info(this.dbTool.getUser()+"|"+cmdMode+" quit");
//					System.exit(0);
//				}	
////				System.out.println("------0---cmd="+cmd+" cmdMode="+cmdMode);
//				if(cmdMode.equals(cmd_type.DBS.getCmd())){
//					this.cmdDbsOper(cmd);
//				}
//				else if(cmdMode.equals(cmd_type.TOOLS.getCmd())){					
//					this.cmdToolOper(cmd);
//				}
//			}
//			catch(Exception e){
//				log4.error(e.getMessage());
////				e.printStackTrace();
//				if(debug){
//					e.printStackTrace();
//				}
//			}
//		} while (true);
//	}
	
	
	/**
	 * 按指定的列进行排序
	 * @param dataList
	 * @param col
	 * @param isFirst是否从首行开始
	 * @return
	 */
	public boolean sort(List<String> dataList, int col, boolean isFirst){		
		boolean status = false;
		if(dataList==null||dataList.size()<1){
			return status;
		}
		if(this.sort){
			this.sort=false;
		}
		else{
			this.sort=true;
		}
//		System.out.println("sort="+this.sort);
		
		String first = dataList.get(0);
		if(col>=first.split("\t").length){
			log4.info("column count out of range");
			return status;
		}
		if(isFirst){
			List<String> sortedList = new ArrayList<String>();			
			sortedList.add(first);
			Collections.sort(dataList.subList(1, dataList.size()), new StringListComparator(this.sort, col));
			sortedList.addAll(dataList);
			dataList =sortedList;
		}		
		
		else{
			Collections.sort(dataList, new StringListComparator(this.sort, col));
		}
		status = true;
		return status;
	}
	
	
	public boolean isDataHead(String cmdMode){
		if("data".equals(cmdMode)||"fdata".equals(cmdMode)){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 命令模式
	 */
	private void commandMode(String cmd){
//		System.out.println("---"+cmd);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		String cmd = null;
		//首次打开，也可执行命令，如登入到dbs,tools
		if(cmd!=null){
			cmd = cmd.trim();
			try {
				this.doCmd(cmd);
			} catch (Exception e) {
				log4.error(e.getMessage());				
				if(debug){
					e.printStackTrace();
				}
			}
		}
		do {
			System.out.print(this.dbTool.getUser()+">");
			try{
				cmd = br.readLine();
				if(ErrorCode.isEmpty(cmd)){
					continue;
				}
				else{					
					cmd=cmd.trim();
					if(cmd.startsWith("quit")||cmd.equalsIgnoreCase("exit")){
						log4.info("Quit");
						System.exit(0);
					}
				}
				this.disp=true;
				this.doCmd(cmd);
				
//				if(cmd.indexOf("|")>0){
//					String[] as = cmd.split("\\|");
//					String cmd_cur = null;
//					
//					for(int i=0; i<as.length; i++){
//						cmd_cur = as[i];
//						cmd_cur = cmd_cur.trim();
//						if(i<as.length-1){
//							this.disp=false;
//						}
//						else{
//							this.disp=true;
//						}
//						this.doCmd(cmd_cur);
//					}
//				}
//				else{
//					this.disp=true;
//					this.doCmd(cmd);
////					if(cmd.indexOf(" ")>0){
////						this.cmdDbOper(cmd.substring(0, cmd.indexOf(" ")), cmd.substring(cmd.indexOf(" ")));
////					}
////					else{
////						this.cmdDbOper(cmd, null);
////					}
//				}
			}catch(Exception e){
				log4.error(e.getMessage());				
				if(debug){
					e.printStackTrace();
				}
			}
		} while (true);
	}
	
	private void doCmd(String cmd) throws Exception{
		if(cmd.indexOf(" ")>0){
			String cmdMode = cmd.substring(0, cmd.indexOf(" "));
			String cmdEnd =  cmd.substring(cmd.indexOf(" ")+1);
			cmdMode = cmdMode.trim();
			cmdEnd = cmdEnd.trim();
//			System.out.println("===========doCmd() cmdMode="+cmdMode+" cmdEnd="+cmdEnd);
			if(cmdEnd.startsWith("f")||cmdEnd.startsWith("fn")||cmdEnd.startsWith("order")){
				this.loginCmd(cmdMode, cmdEnd);
			}
			else{
				this.cmdTask(cmdMode, cmdEnd);
			}
		}
		else{
			this.cmdTask(cmd, null);
		}
	}
	
	private String getListContent(List<String> sList){
		String rValue = null;
		if(this.isParaMode){
			rValue = this.dbTool.getListContent(sList, this.showDataCount, true);
		}
		else{
			rValue = this.dbTool.getListContent(sList, this.showDataCount, true, this.pageRow, this.pageNum);
		}
		return rValue;
	}
	
	private String last_cmd;
	private String last_sql;
	/**
	 * cmd操作处理
	 * @param cmd
	 * @param sql
	 * @throws Exception
	 */
	private void cmdTask(String cmd, String sql) throws Exception{
//		System.out.println("-------cmdTask--cmd="+cmd+" sql="+sql);
//		if(!this.dbTool.isInit){
//			this.dbTool.init();
//		}
//		String task_key="dbs";
		last_cmd = cmd;
		last_sql=sql;
		String rValue = sql;
		if(sql!=null){
			sql = sql.trim();
		}	
		if(!ErrorCode.isEmpty(sql)){
			this.pageNum=1;
			if(this.fileMode){
				File file = new File(sql);
				if(file.exists()){
					List<String> lineList=CommMethod.readLineFile(file);
					sql ="";
					for(String line:lineList){
						sql = sql+line;
					}
				}
				else{
					rValue=sql+" file not found!";
					System.out.println(rValue);
					log4.info(rValue);
					return;
				}
			}
		}
		IDb dboper = null;
		String cmd_code = cmd;
		
		if(cmd_type.CAL.getCmd().equals(cmd)){
			Formula f = new Formula();
//			rValue = cmd.substring()
			if(!ErrorCode.isEmpty(sql))
				rValue = sql+"="+f.formulaParser(sql);
		
			if(disp&&rValue!=null){
				System.out.println(rValue);
				log4.info(rValue);
			}
		}	
		else if(cmd_type.CONFIG.getCmd().equals(cmd)){
			if(ErrorCode.isEmpty(sql)){
				List<String> configList=new ArrayList();
				configList.add("config rowcount "+this.dbTool.getShowDataCount()+" (false|true)");
				configList.add("config filemode "+this.fileMode+" (false|true)");
				configList.add("config rowtotal "+this.dbTool.getMaxTotalRow()+" (num)");
				configList.add("config rowpage "+this.pageRow+" (num)");
				configList.add("config lowercase "+CommMethod.LOWER_CASE+" (true|false)");
				CommMethod.print(configList);
				return;
			}
			config(sql);
		} 
		else if(cmd.startsWith(cmd_type.TOOLS.getCmd())){
			if(ErrorCode.isEmpty(sql)){
				loginMode(cmd);
			}
			else{
				this.cmdToolOper(sql);
			}		
		}	
		else if(cmd.startsWith(cmd_type.DBS.getCmd())){
			if(ErrorCode.isEmpty(sql)){
				loginMode(cmd);
			}
			else{
				this.cmdDbsOper(sql);
			}		
		}	
		else if(cmd.startsWith(cmd_type.REPLACE.getCmd())){
			if(ErrorCode.isEmpty(sql)){
				loginMode(cmd);
			}
			else{
				this.cmdReplaceOper(sql);
			}		
		}	
		else if(cmd_type.HELP.getCmd().equals(cmd)){
			rValue = "";	
			for(cmd_type cType:cmd_type.values()){
				rValue = rValue+cType.getCmd()+", "+cType.getHelpInfo()+"\n";
			}
			System.out.println(rValue);
			log4.info(rValue);
		} 
		else{
			rValue="Invalid oper cmd:"+cmd+ ", Please input help for help";
			System.out.println(rValue);
			log4.info(rValue);
		}
	}
	
	/**
	 * dbs操作处理
	 * @param cmd
	 */
	public List<String> cmdDbsOper(String cmd){
//		String sql = cmd;
		String rValue = null;
		List<String> resultList = new ArrayList<String>();
		ITask task = null;
		String task_key =cmd_type.DBS.getCmd();
		String cmd_code = cmd;
		String sql = "";
		if(cmd.indexOf(" ")>0){
			cmd_code=cmd.substring(0, cmd.indexOf(" "));
			sql = cmd.substring(cmd.indexOf(" ")+1);
		}
		
		String next =null;
		int index = cmd.indexOf("|");
		if(index<0){
			index = cmd.indexOf("~");
		}
//		System.out.println("========"+index+" "+cmd.length()+" cmd="+cmd);
		if(index>0){
			next = cmd.substring(index+1);
			cmd = cmd.substring(0, index);
		}
		
		
		
		
//		System.out.println("task_key="+task_key+" cmd_code="+cmd_code+" rValue="+rValue);
		operCode=cmd_code;
		if(this.taskMap.containsKey(task_key+"_"+cmd_code)){
//			System.out.println("==============cmd_code================="+cmd_code);
			rValue = "";
			if(cmd.indexOf(" ")>0){
				rValue = cmd.substring(cmd_code.length()+1).trim();
			}
			task = this.taskMap.get(task_key+"_"+cmd_code);
						
//			System.out.println("==cmd_code="+cmd_code+" rValue="+rValue+" next="+next+" taskName="+task.getClass().getName());
			this.dataList=task.doExecute(rValue);
			resultList=this.dataList;
			if(next!=null){
				next=next.trim();
				return this.loginCmd(cmd_code, next);
//				return dataList;
			}
//			System.out.println("=======cmd_code==="+cmd_code+" dataList="+dataList.size()+" dataList2="+this.dataList.size());
			if(task.getCode().equalsIgnoreCase("data")||task.getCode().equalsIgnoreCase("fdata")){
				//保留行头处理
				StringBuffer rSb = new StringBuffer();
				if(this.dataList.size()>0){
					rSb.append(this.dataList.get(0).trim()+"\n");
					rValue = this.getListContent(this.dataList.subList(1, this.dataList.size()));
					rSb.append(rValue);	
				}
				
				if(disp){
					System.out.println(rSb.toString());
					log4.info(rSb.toString());
				}
			}
			else{
				if(this.dataList.size()>1){
					CommMethod.print(this.dataList);
				}
				else if(this.dataList.size()==1){
					rValue = this.dataList.get(0);
					System.out.println(rValue);
					log4.info(rValue);	
//					this.dataList.clear();
				}
				else{
					rValue=task.getCode()+" "+task.getHelpInfo();
					System.out.println(rValue);
					log4.info(rValue);
					resultList.add(rValue);
				}
			}
			return resultList;
			
		}	
//		else if(cmd.equalsIgnoreCase(cmd_tool.HELP.getCmd())){
//			String rValue = "";
//			List<TaskVO> toolList = (List)this.taskListMap.get(task_key);
//			for(TaskVO tInfo:toolList){
//				rValue = rValue+tInfo.getCode()+", "+tInfo.getHelpInfo()+"\n";
//			}			
//			log4.info(rValue);
//		}
		else{
			log4.info("Invalid "+task_key+" cmd:"+cmd);
			rValue = "";			
			rValue +="Invalid "+task_key+" cmd:"+cmd+"\n";
					
			List<TaskVO> toolList = (List)this.taskListMap.get(task_key);
			for(TaskVO tInfo:toolList){
				rValue = rValue+tInfo.getCode()+", "+tInfo.getHelpInfo()+"\n";
			}
			System.out.println(rValue);
			log4.info(rValue);
		}
		resultList.add(rValue);
		return resultList;
	}
	
	
	/**
	 * tools操作处理
	 * @param cmd
	 */
	public List<String> cmdToolOper(String cmd){
		String rValue=null;
		List<String> resultList = new ArrayList<String>();
		ITask task = null;
		String task_key =cmd_type.TOOLS.getCmd();
		String next =null;
		int index = cmd.indexOf("|");
		if(index<0){
			index = cmd.indexOf("~");
		}
//		System.out.println("========"+index+" "+cmd.length()+" cmd="+cmd);
		if(index>0){
			next = cmd.substring(index+1);
			cmd = cmd.substring(0, index);
			
//			System.out.println(next);
		}
		
		String cmd_code = cmd;
		if(cmd.indexOf(" ")>0){
			cmd_code=cmd.substring(0, cmd.indexOf(" "));
		}
		
		operCode=cmd_code;
		if(this.taskMap.containsKey(task_key+"_"+cmd_code)){
			rValue = cmd;
			if(cmd.indexOf(" ")>0){
				rValue = cmd.substring(cmd_code.length()+1).trim();
			}
			task = this.taskMap.get(task_key+"_"+cmd_code);
			
//			System.out.println("----------tools---"+rValue);
			this.dataList=task.doExecute(rValue);
			resultList=this.dataList;
//			System.out.println("----rValue="+rValue+" dataList size="+this.dataList.size());
			if(next!=null){
				next=next.trim();
				return this.loginCmd(cmd_code, next);
//				return dataList;
			}
			if(this.dataList.size()>1){
				CommMethod.print(this.dataList);
			}
			else if(this.dataList.size()==1){				
				rValue = this.dataList.get(0);
				System.out.println(rValue);
				log4.info(rValue);
			}
			else{
				rValue=task.getCode()+" "+task.getHelpInfo();
				System.out.println(rValue);
				log4.info(rValue);
				resultList.add(rValue);
			}
			return resultList;
		}
		else if(cmd.startsWith(cmd_tool.MD5.getCmd()+" ")){
			MD5 md5 = new MD5();					
			rValue = md5.getMD5ofStr(cmd.substring(cmd_tool.MD5.getCmd().length()+1));
			System.out.println(rValue);
			log4.info(rValue);					
		}
		else if(cmd.startsWith(cmd_tool.B64.getCmd()+" ")){
			BASE64Encoder b64 = new BASE64Encoder();	
			rValue = b64.encode(cmd.substring(cmd_tool.B64.getCmd().length()+1).trim().getBytes());
			System.out.println(rValue);
			log4.info(rValue);					
		}
		else if(cmd.startsWith(cmd_tool.B64D.getCmd()+" ")){
			BASE64Decoder b64 = new BASE64Decoder();						
			try {
				rValue = new String(b64.decodeBuffer(cmd.substring(cmd_tool.B64D.getCmd().length()+1).trim()));
				System.out.println(rValue);
				log4.info(rValue);
			} catch (IOException e) {
				log4.error("error:"+e.getMessage());
				e.printStackTrace();
			}					
		}	
		else if(cmd.startsWith(cmd_tool.LOWER.getCmd()+" ")){					
			rValue = cmd.substring(cmd_tool.LOWER.getCmd().length()+1).trim().toLowerCase();
			System.out.println(rValue);
			log4.info(rValue);					
		}
		else if(cmd.startsWith(cmd_tool.UPPER.getCmd()+" ")){					
			rValue = cmd.substring(cmd_tool.UPPER.getCmd().length()+1).trim().toUpperCase();
			System.out.println(rValue);
			log4.info(rValue);					
		}
		else if(cmd.startsWith(cmd_tool.ZIP.getCmd()+" ")){				
			rValue = cmd.substring(cmd_tool.ZIP.getCmd().length()+1).trim();
			try {
				rValue=""+Path.zipFile(rValue);
			} catch (Exception e) {
				log4.error("error:"+e.getMessage());
				e.printStackTrace();
			}
			System.out.println(rValue);
			log4.info(rValue);					
		}
		else if(cmd.startsWith(cmd_tool.UNZIP.getCmd()+" ")){				
			rValue = cmd.substring(cmd_tool.UNZIP.getCmd().length()+1).trim();
			try {
				rValue=""+Path.unzipFile(rValue);
			} catch (Exception e) {
				log4.error("error:"+e.getMessage());
				e.printStackTrace();
			}
			System.out.println(rValue);
			log4.info(rValue);					
		}
//		else if(cmd.startsWith(cmd_tool.CHECK_EXIST.getCmd()+" ")){				
//			String rValue = cmd.substring(cmd_tool.CHECK_EXIST.getCmd().length()+1).trim();
//			try {
//				if(rValue.indexOf(" ")<0){
//					log4.info("Invalid check_exist cmd:"+cmd);
//					log4.info(cmd_tool.CHECK_EXIST.getCmd()+" "+cmd_tool.CHECK_EXIST.getHelpInfo());
//				}
//				String sourcePath = rValue.substring(0, rValue.indexOf(" ")).trim();
//				String destPath = rValue.substring(rValue.indexOf(" ")).trim();
//				Path.sourcePathStart=null;				
//				this.dataList=Path.checkFileExists(sourcePath, destPath);
////				System.out.println("=========="+this.dataList.size());
//				CommMethod.print(dataList);
//			} catch (Exception e) {
//				log4.error("error:"+e.getMessage());
//				e.printStackTrace();
//			}
//			log4.info(rValue);					
//		}
//		else if(cmd.startsWith(cmd_tool.CHECK_MODIFY.getCmd()+" ")){				
//			String rValue = cmd.substring(cmd_tool.CHECK_MODIFY.getCmd().length()+1).trim();
//			try {
//				if(rValue.indexOf(" ")<0){
//					log4.info("Invalid check_modify cmd:"+cmd);
//					log4.info(cmd_tool.CHECK_MODIFY.getCmd()+" "+cmd_tool.CHECK_MODIFY.getHelpInfo());
//				}
//				String sourcePath = rValue.substring(0, rValue.indexOf(" ")).trim();
//				String destPath = rValue.substring(rValue.indexOf(" ")).trim();
//				Path.sourcePathStart=null;				
//				this.dataList=Path.checkFileModifys(sourcePath, destPath);
////				System.out.println("=========="+this.dataList.size());
//				CommMethod.print(dataList);
//			} catch (Exception e) {
//				log4.info(cmd);
//				log4.info("error:"+e.getMessage());
////				e.printStackTrace();
//			}
//			log4.info(rValue);					
//		}
//		else if(cmd.equalsIgnoreCase(cmd_tool.HELP.getCmd())){
//			String rValue = "";
//					
//			List<TaskVO> toolList = (List)this.taskListMap.get(task_key);
//			System.out.println("==========="+toolList.size());
//			for(TaskVO tInfo:toolList){
//				rValue = rValue+tInfo.getCode()+", "+tInfo.getHelpInfo()+"\n";
//			}
//			for(cmd_tool cType:cmd_tool.values()){
//				rValue = rValue+cType.getCmd()+", "+cType.getHelpInfo()+"\n";
//			}
//			log4.info(rValue);
//		}
		else{
			log4.info("Invalid "+task_key+" cmd:"+cmd);
			rValue = "";	
			rValue +="Invalid "+task_key+" cmd:"+cmd+"\n";
			
		
			for(cmd_tool cType:cmd_tool.values()){
				rValue = rValue+cType.getCmd()+", "+cType.getHelpInfo()+"\n";
			}
			List<TaskVO> toolList = (List)this.taskListMap.get(task_key);
			for(TaskVO tInfo:toolList){
				rValue = rValue+tInfo.getCode()+", "+tInfo.getHelpInfo()+"\n";
			}	
			System.out.println(rValue);
			log4.info(rValue);
		}
		resultList.add(rValue);
		return resultList;
	}
	
	/**
	 * tools操作处理
	 * @param cmd
	 */
	public List<String> cmdReplaceOper(String cmd){
		ITask task = null;
		List<String> resultList = new ArrayList<String>();
		String task_key =cmd_type.REPLACE.getCmd();
		String next =null;
		int index = cmd.indexOf("|");
		if(index<0){
			index = cmd.indexOf("~");
		}
//		System.out.println("========"+index+" "+cmd.length()+" cmd="+cmd);
		if(index>0){
			next = cmd.substring(index+1);
			cmd = cmd.substring(0, index);
			
//			System.out.println(next);
		}
		
		String cmd_code = cmd;
		if(cmd.indexOf(" ")>0){
			cmd_code=cmd.substring(0, cmd.indexOf(" "));
		}
		
		
		String rValue=null;
		operCode=cmd_code;
		if(this.taskMap.containsKey(task_key+"_"+cmd_code)){
			rValue = cmd;
			if(cmd.indexOf(" ")>0){
				rValue = cmd.substring(cmd_code.length()+1).trim();
			}
			task = this.taskMap.get(task_key+"_"+cmd_code);
			
//			System.out.println("----------tools---"+rValue);
			this.dataList=task.doExecute(rValue);
			resultList=this.dataList;
			if(next!=null){
				next=next.trim();
				return this.loginCmd(cmd_code, next);
				
			}
			if(this.dataList.size()>1){
				CommMethod.print(this.dataList);
			}
			else if(this.dataList.size()==1){
				rValue = this.dataList.get(0);
				System.out.println(rValue);
				log4.info(rValue);		
			}
			return resultList;
		}
		else if(cmd.startsWith(cmd_tool.MD5.getCmd()+" ")){
			MD5 md5 = new MD5();					
			rValue = md5.getMD5ofStr(cmd.substring(cmd_tool.MD5.getCmd().length()+1));
			System.out.println(rValue);
			log4.info(rValue);					
		}		
		else{
			log4.info("Invalid replace cmd:"+cmd);
			rValue = "";			
			rValue +="Invalid replace cmd:"+cmd+"\n";
		
			for(cmd_tool cType:cmd_tool.values()){
				rValue = rValue+cType.getCmd()+", "+cType.getHelpInfo()+"\n";
			}
			System.out.println(rValue);
			log4.info(rValue);
		}
		resultList.add(rValue);
		return resultList;
	}
	
	public static DBTool getDBTool(){		
		return dbTool;
	}
	
	public static Map getTaskMap(){
		return taskMap;
	}	
	
	public List<String> getDataList() {
		return dataList;
	}
	/**
	 * 设置系统变量
	 * @param conf_str
	 */
	private void config(String conf_str){
		String rValue = null;
		if(conf_str.startsWith("rowcount")){
			String temp = conf_str.substring("rowcount".length()+1);
			if(temp.equalsIgnoreCase("true")){				
				this.setShowDataCount(true);				
			}
			else if(temp.equalsIgnoreCase("false")){
				this.setShowDataCount(false);
			}
			else{
				rValue="Invalid para,only for [true|false]";
				System.out.println(rValue);
				log4.info(rValue);
				return;
			}
			rValue = "Change show row count status to "+temp;
		}
		else if(conf_str.startsWith("filemode")){
			String temp = conf_str.substring("filemode".length()+1);
			if(temp.equalsIgnoreCase("true")){				
				this.fileMode=true;			
			}
			else if(temp.equalsIgnoreCase("false")){
				this.fileMode=false;
			}
			else{
				rValue="Invalid para,only for [true|false]";
				System.out.println(rValue);
				log4.info(rValue);
				return;
			}
			rValue = "Change filemode status to "+temp;
		}
		else if(conf_str.startsWith("lowercase")){
			String temp = conf_str.substring("lowercase".length()+1);
			if(temp.equalsIgnoreCase("true")){				
				CommMethod.LOWER_CASE=true;			
			}
			else if(temp.equalsIgnoreCase("false")){
				CommMethod.LOWER_CASE=false;
			}
			else{
				rValue="Invalid para,only for [true|false]";
				System.out.println(rValue);
				log4.info(rValue);
				return;
			}
			rValue = "Change filemode status to "+temp;
		}
		else if(conf_str.startsWith("debug")){
			String temp = conf_str.substring("debug".length()+1);
			if(temp.equalsIgnoreCase("true")){				
				this.debug=true;
				if(this.dbTool!=null)
					this.dbTool.setDebug(debug);
			}
			else if(temp.equalsIgnoreCase("false")){
				this.debug=false;
				if(this.dbTool!=null)
					this.dbTool.setDebug(debug);
			}
			else{
				rValue="Invalid para,only for [true|false]";
				System.out.println(rValue);
				log4.info(rValue);
				return;
			}
			if(this.dbTool!=null)
				rValue = "Change filemode status to "+temp;
		}
		else if(conf_str.startsWith("rowtotal")){
			String temp = conf_str.substring("rowtotal".length()+1);
			Integer totalRow = 0;
			try {
				totalRow = Integer.parseInt(temp);
			} catch (Exception e) {
				log4.error("error:"+e.getMessage());
//				e.printStackTrace();
			}
			if(this.dbTool!=null){
				this.dbTool.setMaxTotalRow(totalRow);
				rValue = "Change max total row to "+totalRow;
			}
		}		
		else if(conf_str.startsWith("rowpage")){
			String temp = conf_str.substring("rowpage".length()+1);
			Integer pageRow = 0;
			try {
				pageRow = Integer.parseInt(temp);
				this.pageRow=pageRow;
			} catch (Exception e) {
				log4.error("error:"+e.getMessage());
//				e.printStackTrace();
			}
			
			rValue = "Change page row to "+pageRow;
		}
		
		if(rValue!=null){
			System.out.println(rValue);
			log4.info(rValue);
		}
	}
	private boolean isParaMode=false;
	private boolean showDataCount=false;
	private int pageRow=20;
	private int pageNum=1;
	private boolean fileMode=false;
	public void setShowDataCount(boolean showDataCount){
		this.showDataCount=showDataCount;
		this.dbTool.setShowDataCount(showDataCount);
	}
	
	


	/**
	 * 命令登入模式后的功能，帮助信息
	 * @author chenjh
	 *
	 */
	private enum cmd_login{
		F("f", "find", "key [key2,key3]查找内容"),
		FF("ff", "find ", "key [key2,key3]重复找查内容，再次执行上次命令，再查结果"),
		FN("fn", "find next page", "fn [num]翻到指定页"),
		ORDER("order", "order content", "column count对指定的例进行排序"),
		HELP("help", "Help", "帮助"),
		EXIT("exit", "Exit to return up level", "返回上一层"),
		QUIT("quit", "Quit", "退出");
		cmd_login(String cmd, String remark, String helpInfo){
			this.cmd=cmd;
			this.remark=remark;
			this.helpInfo = helpInfo;
		}
		private String cmd;
		private String remark;
		private String helpInfo;
		public String getCmd() {
			return cmd;
		}
		public String getRemark() {
			return remark;
		}
		public String getHelpInfo() {
			return helpInfo;
		}
	}
	
	/**
	 * 主功能enum，命令，帮助信息
	 * @author chenjh
	 *
	 */
	private enum cmd_type{		
		CONFIG("config", "config", " para true|false|count是否显示行号"),
		REPLACE("replace", "replace", "替换或检查工具"),
		TOOLS("tools", "common tool", "常用工具"),
		DBS("dbs", "common dbs", "常用数据库工具"),
		CAL("cal", "cal 1+1", "计算器"),
		HELP("help", "Help", "帮助"),
		QUIT("quit", "Quit", "退出");
		cmd_type(String cmd, String remark, String helpInfo){
			this.cmd=cmd;
			this.remark=remark;
			this.helpInfo = helpInfo;
		}
		private String cmd;
		private String remark;
		private String helpInfo;
		public String getCmd() {
			return cmd;
		}
		public String getRemark() {
			return remark;
		}
		public String getHelpInfo() {
			return helpInfo;
		}
		
	}
	
	/**
	 * tools,常用功能enum
	 * @author chenjh
	 *
	 */
	private enum cmd_tool{
		MD5("md5", "encrypt value", "value,用MD5加密"),
		B64("b64", "Encryption value", "value,用Base64加密"),
		B64D("b64d", "Decryption value", "value,用Base64解密"),
		LOWER("lower", "lowercase", "value,转小写"),		
		UPPER("upper", "uppercase", "value,转大写"),
		ZIP("zip", "zip", "file"),		
		UNZIP("unzip", "unzip", "file");
//		CHECK_EXIST("exists", "exists", "source dest ,检查文件已存在"),
//		CHECK_MODIFY("modifys", "modifys", "source dest 检查文件更新情况");
//		HELP("help", "Help", "帮助"),
//		EXIT("exit", "Exit to return up level", "返回上一层"),
//		QUIT("quit", "Quit", "退出");
		cmd_tool(String cmd, String remark, String helpInfo){
			this.cmd=cmd;
			this.remark=remark;
			this.helpInfo = helpInfo;
		}
		private String cmd;
		private String remark;
		private String helpInfo;
		public String getCmd() {
			return cmd;
		}
		public String getRemark() {
			return remark;
		}
		public String getHelpInfo() {
			return helpInfo;
		}
	}
}


