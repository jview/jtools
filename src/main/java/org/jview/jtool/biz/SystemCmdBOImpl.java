package org.jview.jtool.biz;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;
import org.jview.jtool.thread.ReaderThread;
import org.jview.jtool.tools.AlertMessage;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;


/**
 * 执行dos命令
 * @author chenjh
 *
 */
public class SystemCmdBOImpl implements ISystemCmdBO {
	private Logger log4 = Logger.getLogger(SystemCmdBOImpl.class); // 系统日志处理，用于显示异常操作
	/* (non-Javadoc)
	 * @see com.szair.monitor.util_i.impl.Test#doCmd(java.lang.String, java.lang.String)
	 */
	public void doCmd(String command, String prepare) {
		if (File.separator.equals("\\")) {
			this.doCmdWin(command, prepare, null);
		} else {
			this.doCmdLinux(command, prepare, null);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.szair.monitor.util_i.impl.Test#doCmd(java.lang.String, java.lang.String, com.szair.monitor.util.AlertMessage)
	 */
	public void doCmd(String command, String prepare, AlertMessage aMessage) {
		if (File.separator.equals("\\")) {
			this.doCmdWin(command, prepare, aMessage);
		} else {
			if(prepare!=null&&prepare.equals("cmd.exe")){
				prepare = "/bin/sh";
				prepare = "/usr/bin/ksh";
			}
			this.doCmdLinux(command, prepare, aMessage);
		}
	}

	/* (non-Javadoc)
	 * @see com.szair.monitor.util_i.impl.Test#doCmdWin(java.lang.String, java.lang.String)
	 */
	public void doCmdWin(String command, String prepare){
		this.doCmdWin(command, prepare, null);
	}
	
	/* (non-Javadoc)
	 * @see com.szair.monitor.util_i.impl.Test#doCmdWin(java.lang.String, java.lang.String, com.szair.monitor.util.AlertMessage)
	 */
	public void doCmdWin(String command, String prepare, AlertMessage aMessage) {
		boolean isPrepare = true;
		try {
			if(prepare==null){
				isPrepare = false;
				prepare = command;
			}
			Process p = Runtime.getRuntime().exec(prepare);
								
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader be = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			if(isPrepare){
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
				log4.info(command);
				bw.write(command + " \r\n");
				bw.flush();
				bw.close();
			}
						
			Thread t1 = new Thread(new ReaderThread(br, null, aMessage));
			t1.start();
			Thread t2 = new Thread(new ReaderThread(be, this.log4, aMessage));
			t2.start();
			//多线程是否结束
			try{
				t1.join();
				t2.join();
			}
			catch(InterruptedException e){
				log4.error("command="+command);
				e.printStackTrace();
			}
//			//多线程是否结束
//			while(t1.isAlive())   {   
//		        try   {         
//		              t1.sleep(10);   
//		          }   catch   (InterruptedException   e)   {   
//		            }   
//			}   
//			while(t2.isAlive())   {   
//		        try   {         
//		              t2.sleep(10);   
//		          }   catch   (InterruptedException   e)   {   
//		            }   
//			}   

			int value = p.waitFor();				
			if(p.exitValue()==0){ 
				log4.info("运行成功!");       
	        } 

	
		} catch (IOException e) {
			log4.error("command="+command);
			e.printStackTrace();
		}
		catch(Exception e){
			log4.error("command="+command);
			e.printStackTrace();
		}
	}
	

	/**
	 * 执行shell命令
	 * 
	 * @param command
	 *            命令 exp:cd path
	 */
	private void doCmdLinux(String command, String prepare) {
		this.doCmdLinux(command, prepare, null);
	}
	/* (non-Javadoc)
	 * @see com.szair.monitor.util_i.impl.Test#doCmdLinux(java.lang.String, java.lang.String, com.szair.monitor.util.AlertMessage)
	 */
	public void doCmdLinux(String command, String prepare,  AlertMessage aMessage) {
		boolean isPrepare = true;
		try {
			if(prepare==null){
				isPrepare = false;
				prepare = command;
			}
			String path = Path.getWebPath();

//			String[] cmd = { "/bin/sh", "-c", command };
			log4.debug("prepare="+prepare+" command="+command);
			Process p = Runtime.getRuntime().exec(prepare);

			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader be = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			if(isPrepare){
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
				bw.write(command);
				bw.flush();
				bw.close();
			}

			
			Thread t1 = new Thread(new ReaderThread(br, null, aMessage));
			t1.start();
			Thread t2 = new Thread(new ReaderThread(be, null, aMessage));
			t2.start();
			//多线程是否结束
			try{
				t1.join();
				t2.join();
			}
			catch(InterruptedException e){
				log4.error("command="+command);
				e.printStackTrace();
				
			}
			try{
				int value = p.waitFor();				
				if(p.exitValue()==0){ 
					log4.info("运行成功!");       
		        } 
			}catch(Exception e){
				log4.error("command="+command);
				e.printStackTrace();
			}

		} catch (IOException e) {
			log4.error("command="+command);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("=======SystemCmd=1======"+args);
		System.out.println("=======SystemCmd=2======"+args.length);
		if(args.length>0){
			for(String arg: args){
				System.out.println("========"+arg);
			}
		}
			
		String prepare=null,command=null;
		if(args.length==1){
			command = args[0];
		}
		else if(args.length>1){
			if(!ErrorCode.isEmpty(args[0])){
				prepare=args[0];
			}
			command = args[1];
		}
		int count=0;
		for(String arg: args){ 
			count++;
			if(count>2){
				command = command+" "+arg;
			}
		}
		
		AlertMessage aMessage = new AlertMessage();
		SystemCmdBOImpl systemBo = new SystemCmdBOImpl(); 
		systemBo.doCmd(command, prepare, aMessage);
		
		
	}
}
