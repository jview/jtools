/**
 * TODO
 */
package org.jview.jtool.thread;

import java.io.BufferedReader;
import java.io.File;

import org.apache.log4j.Logger;
import org.jview.jtool.tools.AlertMessage;
import org.jview.jtool.util.ErrorCode;




/**
 * @author jview
 * @created  2007-11-23
 *
 */
public class ReaderThread implements Runnable{
	private Logger logT = Logger.getLogger(ReaderThread.class);
	private AlertMessage aMessage;
	BufferedReader br = null;
	private Logger log4 = null;
	public ReaderThread(BufferedReader br, Logger log4){
		this.br = br;
		this.log4 = log4;
	}
	public ReaderThread(BufferedReader br, Logger log4, AlertMessage aMessage){
		this.br = br;
		this.log4 = log4;
		this.aMessage = aMessage;
	}

	public void run(){
		
//		logT.debug("===============");
		if(br!=null){
			if(log4==null){
				logT.debug("error:"+log4);
				log4 = logT;
//				return;
			}
			try{
				
				String line_r = br.readLine();			
				while (line_r != null) {
					if(this.aMessage!=null&&!ErrorCode.isEmpty(line_r)){						
						if(File.separator.equals("\\")){
							this.aMessage.addMessage(line_r.trim().replaceAll("\\\\", "/"));
						}
						else{
							this.aMessage.addMessage(line_r.trim());
						}
						log4.debug(line_r);			
					}	
					else{
						log4.debug("error aMessage is null:"+line_r);
					}
					line_r = br.readLine();
							
				}
				br.close();		
//				logT.info("exit");
			}catch(Exception e){
				log4.error(e.toString());
				e.printStackTrace();
			}
		}
	}

}
