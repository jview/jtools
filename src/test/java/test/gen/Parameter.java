package test.gen;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jview.paras.util.CommMethod;
import com.jview.paras.service.IPara;
import com.jview.paras.service.impl.ParaPropImpl;

/**
 *Copyright: Copyright (c) 2010
 *@author chenjh 
 *Auto create by ParaConstant 2012-01-02
 */
public class Parameter{
	private static Logger log4 = Logger.getLogger(Parameter.class);
	public static final String REL_PATH="Parameter";
	public static final String EMAIL_SEND_USER="email_send_user";//monitor
	public static final String SYSTEM_CODE="system_code";//sza_monito..
	public static final String SYSTEM_TYPE="system_type";//monitor
	public static final String EMAIL_SEND_HOST="email_send_host";//mail.kpifa..
	public static final String SMS_SEND_ID="sms_send_id";//51
	public static final String SYSTEM_NAME="system_name";//????????
	public static final String SMS_SEND_STATUS="sms_send_status";//false
	public static final String EMAIL_SEND_PASS="email_send_pass";//kpifa_3214..
	public static final String SMS_SEND_USER="sms_send_user";//K00084
	public static final String EMAIL_SEND_ADDR="email_send_addr";//monitor@kp..
	public static final String SMS_SEND_DEPT="sms_send_dept";//D016
	private static Map propMap;
	private static IPara propRead;
	static{
		propRead = new ParaPropImpl(REL_PATH);
		propMap = new HashMap();
		propMap.put(EMAIL_SEND_USER,"monitor");
		propMap.put(SYSTEM_CODE,"sza_monitor");
		propMap.put(SYSTEM_TYPE,"monitor");
		propMap.put(EMAIL_SEND_HOST,"mail.kpifa.cn");
		propMap.put(SMS_SEND_ID,"51");
		propMap.put(SYSTEM_NAME,"????????");
		propMap.put(SMS_SEND_STATUS,"false");
		propMap.put(EMAIL_SEND_PASS,"kpifa_32147");
		propMap.put(SMS_SEND_USER,"K00084");
		propMap.put(EMAIL_SEND_ADDR,"monitor@kpifa.cn");
		propMap.put(SMS_SEND_DEPT,"D016");
	}
	public static String getValue(String key){
		String value  = null;
		if(propRead==null){
			value = ""+propMap.get(key);
		}
		else{
			value =  propRead.getValue(key);
			if(value==null){
				
				value = ""+propMap.get(key);
				log4.error("PARA_ERR:"+REL_PATH+" key:"+key+" value is invalid use "+value+" as default!");
			}
		}
		return value;
	}

	public static int getIntValue(String key){
		int value  = 0;
		try{
			if(propRead==null){
				value =Integer.parseInt(""+propMap.get(key));
			}
			else{
				try{
					value =  propRead.getIntValue(key);
				}catch(Exception e){
//					e.printStackTrace();
					value =Integer.parseInt(""+propMap.get(key));
					log4.error("PARA_ERR:"+e.getMessage());
					log4.error("PARA_ERR:"+REL_PATH+" key:"+key+" value is invalid use "+value+" as default!");
				}
				
			}
		}catch(Exception e){
//			e.printStackTrace();
			log4.error("PARA_ERR:"+e.getMessage());
			log4.fatal("PARA_FAT:"+REL_PATH+" key:"+key+" value is invalid and default value is invalid too!");
		}
		return value;
	}

	public static boolean getBoolValue(String key){
		boolean value = false;
		
		try{
			if(propRead==null){
				value =CommMethod.parseBool(""+propMap.get(key));
			}
			else{
				try{
					value =  propRead.getBoolValue(key);
				}catch(Exception e){
					value =CommMethod.parseBool(""+propMap.get(key));
					log4.error("PARA_ERR:"+e.getMessage());
					log4.error("PARA_ERR:"+REL_PATH+" key:"+key+" value is invalid use "+value+" as default!");
				}
				
			}
		}catch(Exception e){
//			e.printStackTrace();
			log4.error("PARA_ERR:"+e.getMessage());
			log4.fatal("PARA_FAT:"+REL_PATH+" key:"+key+" value is invalid and default value is invalid too!");
		}
		return value;
	}

	public static double getDoubleValue(String key){
		double value = 0;
		
		try{
			if(propRead==null){
				value =Double.parseDouble(""+propMap.get(key));
			}
			else{
				try{
					value =  propRead.getDoubleValue(key);
				}catch(Exception e){
					value =Double.parseDouble(""+propMap.get(key));
					log4.error("PARA_ERR:"+e.getMessage());
					log4.error("PARA_ERR:"+REL_PATH+" key:"+key+" value is invalid use "+value+" as default!");
				}
				
			}
		}catch(Exception e){
//			e.printStackTrace();
			log4.error("PARA_ERR:"+e.getMessage());
			log4.fatal("PARA_FAT:"+REL_PATH+" key:"+key+" value is invalid and default value is invalid too!");
		}
		return value;
	}
}
