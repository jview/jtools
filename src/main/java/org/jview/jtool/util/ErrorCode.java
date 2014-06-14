package org.jview.jtool.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author chenjh
 *
 */
public class ErrorCode {
	/**
     * 判断数据是否为空
     * @param inputStr
     * @return
     */
    public static boolean isEmpty(String inputStr) {
    	
        if (null == inputStr || inputStr.trim().equals("")||inputStr.equals("null")) {
            return true;
        }
        return false;
    }

    
    /**
     * 判断数据是否为指定的长度
     * @param inputStr
     * @param minLength
     * @param maxLength
     * @return
     */
    public static boolean isInLength(String inputStr, int minLength, int maxLength) {

        int fieldLength = inputStr.trim().length();
        if (fieldLength > maxLength || fieldLength < minLength) {
            return false;
        }
        return true;
    }

    
   /**
    * 判断数据是否为指定的格式ָ
    * @param inputStr
    * @param expstr
    * @return
    */
    public static boolean isMatcher(String inputStr,String expstr) {
        Pattern pattern = Pattern.compile(expstr);
        Matcher m = pattern.matcher(inputStr);
        boolean b = m.matches();
        return b;
    }


    /**
     * 检查in是否在start与end之间
     * @param in
     * @param start
     * @param end
     * @return
     */
    public static boolean isInRange(double in, double start, double end) {
    	if(in < start || in >= end) {
    		return false;
    	} else {
    		return true;
    	}
    }
}
