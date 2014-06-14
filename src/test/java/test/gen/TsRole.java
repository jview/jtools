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
public class TsRole{
	private static Logger log4 = Logger.getLogger(TsRole.class);
	public static final String REL_PATH="TsRole";
	public static final String ADMIN="admin";//1
	public static final String USER="user";//2
	private static Map propMap;
	private static IPara propRead;
	static{
		propRead = new ParaPropImpl(REL_PATH);
		propMap = new HashMap();
		propMap.put(ADMIN,"1");
		propMap.put(USER,"2");
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
