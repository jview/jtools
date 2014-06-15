package org.jview.jtool.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.jview.jtool.model.LineVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





/**
 * 
 * @author chenjh
 *
 */
public class CommMethod {
	private static final Logger log = LoggerFactory.getLogger(CommMethod.class);
	public static boolean LOWER_CASE=true;
	public static boolean LOWER_CASE_LIST_CACHE=true;
	private static List<String> lowerList = new ArrayList<String>();	
	private static List<LineVO> lowerLineList = new ArrayList<LineVO>();
	private static int dataList_HashCode=0;
	private static int lineList_HashCode=0;
	
	/**
	 * 取当前时间
	 * 
	 * @return
	 */
	public static Time getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return Time.valueOf(sdf.format(new Date()));
	}
	/**
	 * String转Time
	 * 
	 * @param datestr
	 * @param expstr
	 * @return
	 */
	public static Time parseTime(String timestr) {
		Time time = null;
//		System.out.println("timestr="+timestr);
		
		if (ErrorCode.isEmpty(timestr))
			return null;

		if(timestr.length()<"00:00:00".length()){						
			timestr = timestr+":00";
		}
		try {
			time = Time.valueOf(timestr);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
//		System.out.println("time="+time);
		return time;
	}
	
	/**
	 * String转Date 依据expstr的格式将字符串datestr转成Date
	 * 
	 * @param datestr
	 * @param expstr日期正则表达式如
	 *            :MM-dd HH:mm\yyyy-MM-dd, 如果expstr为空，默认为yyyy-MM-dd
	 * @return
	 */
	public static Date parseDate(String datestr, String expstr) {
		Date date = null;

		if (ErrorCode.isEmpty(expstr)) {
			expstr = "yyyy-MM-dd";
		}

		if (datestr == null || datestr.equals(""))
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat(expstr);

		try {
			date = sdf.parse(datestr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String formatEDate(Date date) {
//		Date date = new Date();
		  SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd HH:mm yyyy",Locale.US);
		  String times = sdf.format(date);
		  return times;
		}

	public static Date parseEDate(String date, String expStr) {
		SimpleDateFormat dateFormats = new SimpleDateFormat(expStr,Locale.US);
		Date result = null;	
		try {
			result = dateFormats.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
		
	}
	
	/**
	 * date格式化，转成String
	 * 
	 * @param d
	 * @param expstr
	 *            ,日期正则表达式,如:MM-dd HH:mm\yyyy-MM-dd, 如果expstr为空，默认为yyyy-MM-dd
	 * @return
	 */
	public static String format(Date date, String expstr) {
		if (date == null) {
			return "";
		}
		if (ErrorCode.isEmpty(expstr)) {
			expstr = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(expstr);
		return sdf.format(date);
	}
	
	/**
	 * 匹配数字输入
	 * 
	 * @param numberstr是否为数字型字符串
	 * @return
	 */
	public static boolean matchNumberstr(String numberstr) {
		numberstr=numberstr.trim();
		String regEx = "^\\d+$";
		boolean result = Pattern.compile(regEx).matcher(numberstr).matches();
		return result;
	}
	
	/**
	 * 转换
	 * @param objs
	 * @param split_str
	 * @return
	 */
	public static String getArrayContent(Object[] objs, String split_str){
		if(split_str==null){
			split_str=" ";
		}
		String value="";
		for(Object obj:objs){
			value =value+ obj +split_str;
		}
		if(value.endsWith(split_str)) {
			value=value.substring(0, value.length()-split_str.length());
		}
		return value;
	}
	
	/**
	 * 转成时间
	 * @param timeValue
	 * @param type type;//1秒,2分钟,3小时
	 * @return
	 */
	public static String getTime(long timeValue){
		String value = "";
		long second = timeValue;
		long minute;
		long hour;
		minute = second/60;
		second = second%60;
		hour = minute/60;
		minute = minute%60;
		if(hour>=10){
			value = value+hour;
		}
		else{
			value = value+"0"+hour;
		}
		value = value+":";
		if(minute>=10){
			value = value+minute;
		}
		else{
			value = value+"0"+minute;
		}
		value = value+":";
		if(second>=10){
			value = value+second;
		}
		else{
			value = value+"0"+second;
		}
		return value;
	}
	
	/**
	 * 对像为空时不显示null而显示成0.0
	 * 
	 * @param obj
	 * @return
	 */
	public static String getNotNullNumber(Object obj) {

		String value = null;
		if (obj == null)
			value = "0";
		else if (matchNumberstr("" + obj)) {
			return "" + obj;
		} else {
			value = "0";
		}
		return value;

	}
	
	public static int getIntegerValue(String strs){
		return Integer.parseInt(getNotNullNumber(strs));
	}
	
	/**
	 * 字符数字数组转成整型数组,遇到空字符,默认为0
	 * @param arrays
	 * @return
	 */
	public static Integer[] getIntArray(String[] arrays){
		Integer[] intArrays = new Integer[arrays.length];
		for(int i=0; i<arrays.length; i++){
			try {
				intArrays[i] = Integer.parseInt(arrays[i].trim());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				intArrays[i]=0;
//				e.printStackTrace();
			}
		}
		return intArrays;
	}
	
	/**
	 * 转换
	 * @param arrays
	 * @return
	 */
	public static Float[] getFloatArray(String[] arrays){
		Float[] floatArrays = new Float[arrays.length];
		for(int i=0; i<arrays.length; i++){
			try {
				floatArrays[i] = Float.parseFloat(arrays[i].trim());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				floatArrays[i]=0f;
//				e.printStackTrace();
			}
		}
		return floatArrays;
	}
	/**
	 * 内容过滤,用于从记录中按查询内容查找相应的行
	 * @param dataList
	 * @param filter
	 * @return
	 */
	public static List<String> filterContent(List<String> dataList, String filter){
		
		if(ErrorCode.isEmpty(filter)){
			return dataList;
		}
		else if(filter.equals("%")){
			return dataList;
		}
		else{
			filter = filter.trim();
		}
//		System.out.println("filter"+dataList.get(0));		
		if(LOWER_CASE){
			if(filter.matches("\\w+")){
				return filterContentLower(dataList, filter);
			}
		}
		
		List<String> filterList = new ArrayList<String>();				
		for(String data:dataList){				
			if(data.indexOf(filter)>=0){
				filterList.add(data);
			}				
		}			
		
		return filterList;
	}
	
	/**
	 * 内容过滤,用于从记录中按查询内容查找相应的行(忽略大小写)
	 * @param dataList
	 * @param filter
	 * @return
	 */
	public static List<String> filterContentLower(List<String> dataList, String filter){
		filter = filter.toLowerCase();
//		System.out.println("filter"+dataList.get(0));		
		List<String> filterList = new ArrayList<String>();		
		
		if(LOWER_CASE_LIST_CACHE){
			int data_list_HashCode=dataList.hashCode();
			if(dataList.size()>0&&(dataList.size()!=lowerList.size()||data_list_HashCode!=dataList_HashCode)){
				dataList_HashCode=data_list_HashCode;
				
				lowerList.clear();
				for(String data:dataList){
					lowerList.add(data.toLowerCase());
				}
//				System.out.println("=======filter lower cache size="+lowerList.size());
			}
		}
		
		int i=0;
		for(String data:lowerList){				
			if(data.indexOf(filter)>=0){
				filterList.add(dataList.get(i));
			}	
			i++;
		}			
		
		return filterList;
	}
	
	/**
	 * 内容过滤,用于从记录中按查询内容查找相应的行,支持多个条件，以split_str隔开
	 * @param dataList
	 * @param filter
	 * @param split_str
	 * @return
	 */
	public static List<String> filterContent(List<String> dataList, String filter, String split_str){
//		System.out.println(filter+" split="+split_str);
		if(filter.indexOf(split_str)>0){
			for(String fstr:filter.split(split_str)){
//				System.out.println("fstr="+fstr);
				dataList=filterContent(dataList, fstr.trim());
			}
		}
		else{
			dataList=filterContent(dataList, filter);
		}
		
		return dataList;
	}
	
	/**
	 * 内容过滤,用于从记录中按查询内容查找相应的行,支持多个条件，以split_str隔开
	 * @param dataList
	 * @param filter
	 * @param split_str
	 * @return
	 */
	public static List<String> filterContentLower(List<String> dataList, String filter, String split_str){
//		System.out.println(filter+" split="+split_str);
		if(filter.indexOf(split_str)>0){
			for(String fstr:filter.split(split_str)){
//				System.out.println("fstr="+fstr);
				dataList=filterContentLower(dataList, fstr.trim());
			}
		}
		else{
			dataList=filterContentLower(dataList, filter);
		}
		
		return dataList;
	}
	
	/**
	 * 内容过滤,用于从记录中按查询内容查找相应的行
	 * @param dataList
	 * @param filter
	 * @return
	 */
	public static List<LineVO> filterLine(List<LineVO> dataList, String filter){
		if(ErrorCode.isEmpty(filter)){
			return dataList;
		}
		else{
			filter = filter.trim();
		}
		
		if(LOWER_CASE){
			if(filter.matches("\\w+")){
				return filterLineLower(dataList, filter);
			}
		}
//		System.out.println("filter"+dataList.get(0));		
		List<LineVO> filterList = new ArrayList<LineVO>();				
		for(LineVO data:dataList){				
			if(data.getLine().indexOf(filter)>=0){
				filterList.add(data);
			}				
		}			
		
		return filterList;
	}
	
	/**
	 * 内容过滤,用于从记录中按查询内容查找相应的行(忽略大小写)
	 * @param dataList
	 * @param filter
	 * @return
	 */
	public static List<LineVO> filterLineLower(List<LineVO> dataList, String filter){
//		System.out.println("filter"+dataList.get(0));		
		List<LineVO> filterList = new ArrayList<LineVO>();		
		
		filter = filter.toLowerCase();
		
		if(LOWER_CASE_LIST_CACHE){
			int line_list_HashCode=dataList.hashCode();
			if(dataList.size()>0&&(dataList.size()!=lowerLineList.size()||line_list_HashCode!=lineList_HashCode)){
				lineList_HashCode=line_list_HashCode;
				lowerLineList.clear();
				for(LineVO data:dataList){
					data.setLine(data.getLine().toLowerCase());
					lowerLineList.add(data);
				}
//				System.out.println("=======filter lower line cache size="+lowerLineList.size());
			}
		}
		
		int i=0;		
		for(LineVO data:lowerLineList){				
			if(data.getLine().indexOf(filter)>=0){
				filterList.add(dataList.get(i));
			}				
			i++;
		}			
		
		return filterList;
	}
	
	/**
	 * 内容过滤,用于从记录中按查询内容查找相应的行,支持多个条件，以split_str隔开
	 * @param dataList
	 * @param filter
	 * @param split_str
	 * @return
	 */
	public static List<LineVO> filterLine(List<LineVO> dataList, String filter, String split_str){
		if(filter.indexOf(split_str)>0){
			for(String fstr:filter.split(split_str)){
				dataList=filterLine(dataList, fstr);
			}
		}
		else{
			dataList=filterLine(dataList, filter);
		}
		
		return dataList;
	}
	
	/**
	 * 过滤器
	 * @param msgs
	 * @param filter
	 * @return
	 */
	public static List filterValue(List<String> msgList, String filter){
		List fList = new ArrayList();
		boolean isExist=false;
		boolean isBreak=false;
		if(filter==null){
			return msgList;
		}
		for(String msg: msgList){
//			System.out.println(msg);
			if(filter.indexOf(",")<0&&msg.indexOf(filter)>0){//filter不带","号，只过滤一次
				fList.add(msg);					
			}else{//filter带","号，过滤所有的filter,只要一个不成功就失败
				String[] filters = filter.split(",");
				isBreak=false;
				for(String fstr: filters){
					isExist=false;
					if(msg.indexOf(fstr)>=0){
						isExist=true;
					}			
					if(!isExist){
						isBreak=true;
					}					
				}
				if(!isBreak){
					fList.add(msg);	
				}
			}								
		}
		return fList;
	}
	
	public static void print(List<String> dataList){
		for(String data:dataList){
			System.out.println(data);
			log.info(data);
		}
	}
	
	/**
	 * 转换成带行号的行对象
	 * @param dataList
	 * @return
	 */
	public static List<LineVO> getLine(List<String> dataList){
		List<LineVO> lineList = new ArrayList<LineVO>();		
		if(dataList==null){
			return lineList;
		}
		
		LineVO line=null;
		int count=0;
		for(String data:dataList){			
			line = new LineVO();
			line.setIndex(count);
			line.setLine(data);
			lineList.add(line);
			count++;
		}
		return lineList;
	}
	
	/**
	 * 是否包含ip地址
	 * @param host
	 * @return
	 */
	public static boolean hasIpAddress(String host){
		String temp = null;
		if(host.indexOf("localhost")>0){
			temp = host.replaceFirst("localhost", "-host-");
		}
		else{
			temp = host.replaceFirst("\\d+.\\d+.\\d+.\\d+", "-host-");
		}
		
		return temp.indexOf("-host-")>=0;
//		
		
	}
	
	public static List<String> readLineFile(File file) throws Exception {
		List<String> list = new LinkedList<String>();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while((line = br.readLine())!=null){
//			System.out.println(line);
			list.add(line);
		}
		br.close();
		fr.close();
		return list;
	}
	
	public static void writeFile(File file, String content)throws Exception{
		FileWriter fw = new FileWriter(file);
		fw.write(content);
		fw.close();
	}
	
	
	public static void writeLineFile(File file, List<String> lineList)throws Exception{
		FileWriter fw = new FileWriter(file);
		for(String line:lineList){
			fw.write(line+"\n");
		}
		fw.close();
	}
	
//	private static void initPath(String paths){
//		
//		paths = paths.replaceAll("\\\\", "/");
////		log4.info("initPath="+paths);
//		File f = null;
//		String temp = "";
//		try{		
//			while(paths.indexOf("/")>=0){
//				temp = temp +"/"+ paths.substring(0, paths.indexOf("/"));
//				paths = paths.substring(paths.indexOf("/")+1);
////				System.out.println("temp="+temp.substring(1)+"  path="+paths);
//				f = new File(temp.substring(1));
//				if(!f.exists()){					
//					f.mkdirs();					
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//	}
//	
	
	public static int countContent(String content, String str){		
		int sum = 0;
//		System.out.println("content="+content+" str="+str+" countContent="+content.indexOf(str));
		while (content.indexOf(str) != -1) {			
			sum++;
			content = content.substring(content.indexOf(str)+str.length());
		}
		return sum;
	}
	
	public static String gbk2Utf8(String s) {
		String fullStr = null;
		try {
//			byte[] fullByte = gbk2utf8(s);
			
			char[] c = s.toCharArray();
			byte[] fullByte = new byte[3 * c.length];
			for (int i = 0; i < c.length; i++) {
				String binary = Integer.toBinaryString(c[i]);
				StringBuffer sb = new StringBuffer();
				int len = 16 - binary.length();
				// 前面补零
				for (int j = 0; j < len; j++) {
					sb.append("0");
				}
				sb.append(binary);
				// 增加位，达到到24位3个字节
				sb.insert(0, "1110");
				sb.insert(8, "10");
				sb.insert(16, "10");
				fullByte[i * 3] = Integer.valueOf(sb.substring(0, 8), 2).byteValue();// 二进制字符串创建整型
				fullByte[i * 3 + 1] = Integer.valueOf(sb.substring(8, 16), 2).byteValue();
				fullByte[i * 3 + 2] = Integer.valueOf(sb.substring(16, 24), 2).byteValue();
			}
			fullStr = new String(fullByte, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fullStr;
	}
	
	/**
	 * gbk转utf8
	 * @param s
	 * @return
	 */
	public static String utf8ToStr(String s) {
        String ret = "null";
        try {
            ret = java.net.URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException ex) {
        	ex.printStackTrace();
        }
        return ret;
    }

	/**
	 * 编码是否有效
	 * 
	 * @param text
	 * @return
	 */
	private static boolean Utf8codeCheck(String text) {
		String sign = "";
		if (text.startsWith("%e"))
			for (int i = 0, p = 0; p != -1; i++) {
				p = text.indexOf("%", p);
				if (p != -1)
					p++;
				sign += p;
			}
		return sign.equals("147-1");
	}

	/**
	 * 是否Utf8Url编码
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isUtf8Url(String text) {
		text = text.toLowerCase();
		int p = text.indexOf("%");
		if (p != -1 && text.length() - p > 9) {
			text = text.substring(p, p + 9);
		}
		return Utf8codeCheck(text);
	}
	
	/**
	 * 合并空格
	 * @param value
	 * @return
	 */
	public static String merchBlank(String value){
		if(value==null){
			return "";
		}
		else {
			return value.replaceAll("\\ +", " ");
		}		
		
	}
	
	public static String getParameterByKey(String value, String key){
		int index = value.indexOf(key);		
		if(index>=0){
			value = value.substring(index+key.length()+1);
			if(value.indexOf(" ")>0){
				value = value.substring(0, value.indexOf(" "));
			}			
			return value;
		}
		return null;
		
	}
}
