package org.jview.jtool.tools;

import sun.net.ftp.*;


import sun.net.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.Path;






/**
 * FTP远程命令列表<br>
 * USER PORT RETR ALLO DELE SITE XMKD CDUP FEAT<br>
 * PASS PASV STOR REST CWD STAT RMD XCUP OPTS<br>
 * ACCT TYPE APPE RNFR XCWD HELP XRMD STOU AUTH<br>
 * REIN STRU SMNT RNTO LIST NOOP PWD SIZE PBSZ<br>
 * QUIT MODE SYST ABOR NLST MKD XPWD MDTM PROT<br>
 * 在服务器上执行命令,如果用sendServer来执行远程命令(不能执行本地FTP命令)的话，所有FTP命令都要加上\r\n<br>
 * ftpclient.sendServer("XMKD /test/bb\r\n"); //执行服务器上的FTP命令<br>
 * ftpclient.readServerResponse一定要在sendServer后调用<br>
 * nameList("/test")获取指目录下的文件列表<br>
 * XMKD建立目录，当目录存在的情况下再次创建目录时报错<br>
 * XRMD删除目录<br>
 * DELE删除文件<br>
 * <p>
 * Title: 使用JAVA操作FTP服务器(FTP客户端)
 * </p>
 * <p>
 * Description: 上传文件的类型及文件大小都放到调用此类的方法中去检测，比如放到前台JAVASCRIPT中去检测等
 * 针对FTP中的所有调用使用到文件名的地方请使用完整的路径名（绝对路径开始）。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: 静靖工作室
 * </p>
 * 
 * @author 欧朝敬 13873195792
 * @version 1.0
 */ 
@SuppressWarnings("unchecked") 
public class FtpUtil {
	private static Logger log4 = Logger.getLogger(FtpUtil.class);
	private FtpClient ftpclient;
	private String ipAddress; 
	private int ipPort;
	private String userName;
	private String PassWord;

	/**
	 * 构造函数
	 * 
	 * @param ip
	 *            String 机器IP
	 * @param port
	 *            String 机器FTP端口号
	 * @param username
	 *            String FTP用户名
	 * @param password
	 *            String FTP密码
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked") 
	public FtpUtil(String ip, int port, String username, String password)
			throws Exception {
		ipAddress = new String(ip);
		ipPort = port;
		ftpclient = new FtpClient(ipAddress, ipPort);
		// ftpclient = new FtpClient(ipAddress);
		userName = new String(username);
		PassWord = new String(password);
	}

	/**
	 * 构造函数
	 * 
	 * @param ip
	 *            String 机器IP，默认端口为21
	 * @param username
	 *            String FTP用户名
	 * @param password
	 *            String FTP密码
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked") 
	public FtpUtil(String ip, String username, String password)
			throws Exception {
		ipAddress = new String(ip);
		ipPort = 21;
		ftpclient = new FtpClient(ipAddress, ipPort);
		// ftpclient = new FtpClient(ipAddress);
		userName = new String(username);
		PassWord = new String(password);
	}

	/**
	 * 登录FTP服务器
	 * 
	 * @throws Exception
	 */
	public void login() throws Exception {
		ftpclient.login(userName, PassWord);		
	}

	/**
	 * 退出FTP服务器
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked") 
	public void logout() throws Exception {
		// 用ftpclient.closeServer()断开FTP出错时用下更语句退出
		ftpclient.sendServer("QUIT\r\n");
		int reply = ftpclient.readServerResponse(); // 取得服务器的返回信息
	}

	/**
	 * 在FTP服务器上建立指定的目录,当目录已经存在的情下不会影响目录下的文件,这样用以判断FTP
	 * 上传文件时保证目录的存在目录格式必须以"/"根目录开头
	 * 
	 * @param pathList
	 *            String
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked") 
	public void buildList(String pathList) throws Exception {
		ftpclient.ascii();
		StringTokenizer s = new StringTokenizer(pathList, "/"); // sign
		int count = s.countTokens();
		String pathName = "";
		while (s.hasMoreElements()) {
			pathName = pathName + "/" + (String) s.nextElement();
			try {
				ftpclient.sendServer("XMKD " + pathName + "\r\n");
			} catch (Exception e) {
				e = null;
			}
			int reply = ftpclient.readServerResponse();
		}
		ftpclient.binary();
	}

	/**
	 * 取得指定目录下的所有文件名，不包括目录名称 分析nameList得到的输入流中的数，得到指定目录下的所有文件名
	 * 
	 * @param fullPath
	 *            String
	 * @return ArrayList
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked") 
	public ArrayList fileNames(String fullPath) throws Exception {
		ftpclient.ascii(); // 注意，使用字符模式
		TelnetInputStream list = ftpclient.nameList(fullPath);
		byte[] names = new byte[2048];
		int bufsize = 0;
		bufsize = list.read(names, 0, names.length); // 从流中读取
		list.close();
		ArrayList namesList = new ArrayList();
		int i = 0;
		int j = 0;
		while (i < bufsize /* names.length */) {
			// char bc = (char) names;
			// System.out.println(i + " " + bc + " : " + (int) names);
			// i = i + 1;
			if (names.length == 10) { // 字符模式为10，二进制模式为13
			// 文件名在数据中开始下标为j,i-j为文件名的长度,文件名在数据中的结束下标为i-1
			// System.out.write(names, j, i - j);
			// System.out.println(j + " " + i + " " + (i - j));
				String tempName = new String(names, j, i - j);
				namesList.add(tempName);
				// System.out.println(temp);
				// 处理代码处
				// j = i + 2; //上一次位置二进制模式
				j = i + 1; // 上一次位置字符模式
			}
			i = i + 1;
		}
		return namesList;
	}
	
	/**
	  * 判断一行文件信息是否为目录
	  * 
	  * @param line
	  * @return
	  */
	public boolean isDir(String line) {
	  return ((String) parseLine(line).get(0)).indexOf("d") != -1;
	}
	public boolean isFile(String line) {
//		System.out.println(line);
	  return !isDir(line);
	}
	/**
	  * 处理getFileList取得的行信息
	  * 
	  * @param line
	  * @return
	  */
	private ArrayList parseLine(String line) {
	  ArrayList s1 = new ArrayList();
	  StringTokenizer st = new StringTokenizer(line, " ");
	  while (st.hasMoreTokens()) {
	   s1.add(st.nextToken());
	  }
	  return s1;
	}
	
	private String getModifyTime(List lineList){
		return lineList.get(5)+"/"+lineList.get(6)+" "+lineList.get(7);
	}
	
	/**
	  * 返回文件夹或者文件的名称
	  * 
	  * @param line
	  * @return
	  */
	public String getFileName(String line) {
	  String filename = (String) parseLine(line).get(8);
	  return filename;
	}
	
	/**
	  * 返回当前目录的文件名称
	  * 
	  * @return
	  * @throws IOException
	  */
	public ArrayList getNameList(String remotePath) throws IOException {
	  BufferedReader dr = new BufferedReader(new InputStreamReader(ftpclient
	    .nameList(remotePath)));
	  ArrayList al = new ArrayList();
	  String s = "";
	  while ((s = dr.readLine()) != null) {
//	   System.out.println("filename:" + s);
	   al.add(s);
	  }
	  return al;
	}
	
	
	
	/**
	  * 返回当前目录的所有文件及文件夹
	  * 
	  * @return
	  * @throws IOException
	  */
	public ArrayList getFileList(String remotePath) throws IOException {
	  ftpclient.cd(remotePath);
	  BufferedReader dr = new BufferedReader(new InputStreamReader(ftpclient
	    .list()));
	 
	  ArrayList al = new ArrayList();
	  String s = "";
	  while ((s = dr.readLine()) != null) {
//	   System.out.println("readLine:" + s);
	   if ((!((String) parseLine(s).get(8)).equals("."))
	     && (!((String) parseLine(s).get(8)).equals(".."))) {
	    al.add(s);
//	    System.out.println("s:" + s);
	   }
	  }
	  return al;
	}

	/**
	 * 上传文件到FTP服务器,destination路径以FTP服务器的"/"开始，带文件名、 上传文件只能使用二进制模式，当文件存在时再次上传则会覆盖
	 * 
	 * @param source
	 *            String
	 * @param destination
	 *            String
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked") 
	public void upFile(String source, String destination) throws Exception {
		destination = destination.replaceAll("\\\\", "/");
		buildList(destination.substring(0, destination.lastIndexOf("/")));
		ftpclient.binary(); // 此行代码必须放在buildList之后
		TelnetOutputStream ftpOut = ftpclient.put(destination);
		TelnetInputStream ftpIn = new TelnetInputStream(new FileInputStream(
				source), true);
		byte[] buf = new byte[204800];
		int bufsize = 0;
		while ((bufsize = ftpIn.read(buf, 0, buf.length)) != -1) {
			ftpOut.write(buf, 0, bufsize);
		}
		ftpIn.close();
		ftpOut.close();

	}

	/**
	 * JSP中的流上传到FTP服务器, 上传文件只能使用二进制模式，当文件存在时再次上传则会覆盖 字节数组做为文件的输入流,此方法适用于JSP中通过
	 * request输入流来直接上传文件在RequestUpload类中调用了此方法， destination路径以FTP服务器的"/"开始，带文件名
	 * 
	 * @param sourceData
	 *            byte[]
	 * @param destination
	 *            String
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked") 
	public void upFile(byte[] sourceData, String destination) throws Exception {
		buildList(destination.substring(0, destination.lastIndexOf("/")));
		ftpclient.binary(); // 此行代码必须放在buildList之后
		TelnetOutputStream ftpOut = ftpclient.put(destination);
		ftpOut.write(sourceData, 0, sourceData.length);
		// ftpOut.flush();
		ftpOut.close();
	}
	
	public int downCount=0;
	@SuppressWarnings("unchecked") 
	
	private static Map keysCountMap=null;
	private String filenameEncode;
	public String pathStart;
	private Map getKeysCountMap(){
		if(keysCountMap==null){
			keysCountMap = new HashMap();
		}
		
		return keysCountMap;
	}
	
	
	public String getFilenameEncode() {
		return filenameEncode;
	}

	public void setFilenameEncode(String filenameEncode) {
		this.filenameEncode = filenameEncode;
	}

	/**
	 * 如果path已存在，则path_num+1
	 * @param keys
	 * @param path
	 * @return
	 */
	private String getPathNew(String keys, String path){
		int count = 0;
		Object countObj = getKeysCountMap().get(keys);
		if(countObj!=null){
			count = (Integer)countObj;
		}
		count++;
		String fileName = path.substring(path.lastIndexOf("/")+1);
		path = path.substring(0, path.lastIndexOf("/"));
		String fileEnd = fileName.substring(fileName.lastIndexOf(".")+1);
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		int num = 0;
		if(fileName.indexOf("_")>0){
			String nums = fileName.substring(fileName.lastIndexOf("_")+1);
			if(CommMethod.matchNumberstr(nums)){
				fileName = fileName.substring(0, fileName.lastIndexOf("_"));
			}
		}
		String newPath = path+"/"+fileName+"_"+count+"."+fileEnd;
		File file = new File(newPath);
		if(file.exists()){			
			getKeysCountMap().put(keys, count);
//			log4.info("keys="+keys+" count="+count+" countObj="+countObj+" newPath="+newPath+" count="+getKeysCountMap().get(keys));
			return getPathNew(keys, newPath);
		}
		file=null;
		return newPath;
	}
	/**
	 * 递归ftp文件下载，可根据文件时间识别是否文件有变更，如果有变更才下载
	 */
	public int downDir(String localPath, String remotePath, String backupPath)
			throws Exception {
		if(pathStart==null){
			pathStart=localPath;
		}
		FileOutputStream outStream = null;
		ArrayList list = null;		
		
		remotePath = remotePath.replaceAll("\\\\", "/");
		list = getFileList(remotePath);
		ftpclient.binary();
		File temp = null;
		List lineList = null;
		String ftpFileTime = null; 
		Date fDate = null;
		Date ftpFileDate = null;
		String ftpFileName = null;
		for (int i = 0; i < list.size(); i++) {
			// 如果是文件，则直接执行下载
			lineList = this.parseLine(list.get(i).toString());
			ftpFileTime=this.getModifyTime(lineList);
			if (isFile(list.get(i).toString())) {
				ftpclient.cd(remotePath);
				ArrayList listfileName = getNameList(remotePath);
				for (int j = 0; j < listfileName.size(); j++) {
					ftpFileName = listfileName.get(j).toString();
					temp = new File(localPath + File.separator + ftpFileName);
//					System.out.println("---0"+CommMethod.gbk2Utf8(ftpFileName));
//					System.out.println("---1"+CommMethod.utf8ToStr(ftpFileName));
//					System.out.println("---11"+new String(ftpFileName.getBytes("UTF-8"),"ISO-8859-1"));
//					System.out.println("---12"+new String(new String(ftpFileName.getBytes("UTF-8"),"ISO-8859-1").getBytes("ISO-8859-1"),"UTF-8"));   
//					System.out.println("---2"+new String(ftpFileName.getBytes("GBK"),"UTF-8"));
//					System.out.println("---3"+new String(ftpFileName.getBytes("utf8"),"gbk"));
				
					if(this.filenameEncode!=null&&this.filenameEncode.equalsIgnoreCase("gbk")){
//						ftpFileName=new String(ftpFileName.getBytes("GBK"),"UTF-8");
						ftpFileName = CommMethod.gbk2Utf8(ftpFileName);
					}					
					
					log4.info("------localEncode="+this.filenameEncode+"--fileName="+listfileName.get(j).toString()+"----"+ftpFileName);
					if(lineList.get(lineList.size()-1).equals(temp.getName())){
						downCount++;
						//时间处理
						fDate = new Date();
						fDate.setTime(temp.lastModified());
						ftpFileDate = CommMethod.parseEDate(ftpFileTime+" "+CommMethod.format(fDate, "yyyy"), "MMM/dd HH:mm yyyy");
						//如果文件时间一样，则不下载
						if(temp.lastModified()==ftpFileDate.getTime()){
							log4.info("Ignore file for the same time:"+ftpFileDate+" on "+temp.getName());
							continue;
						}
						//end时间处理
						
						
						
//							log4.info(list.get(i)+"----"+remotePath+ File.separator+ listfileName.get(j).toString()+"----- "+lineList.get(lineList.size()-1)+" tempName="+temp.getName()+"--------"+temp.lastModified()+"----------"+ftpFileDate.getTime());
						
						
						outStream = new FileOutputStream(temp);
						log4.info("-------fileName"+temp.getName()+"----"+ftpFileName);
						TelnetInputStream is = ftpclient.get(ftpFileName);
						byte[] bytes = new byte[1024];
						int c;
						// 暂未考虑中途终止的情况
						while ((c = is.read(bytes)) != -1) {
							outStream.write(bytes, 0, c);
						}
						is.close();
						outStream.close();
						temp.setLastModified(ftpFileDate.getTime());
						String tmpPath = temp.getAbsolutePath();
						tmpPath=tmpPath.replaceAll("\\\\", "/");
//						System.out.println(tmpPath);
						String path = this.getPathNew(tmpPath, backupPath+tmpPath.substring(pathStart.length()));
//						System.out.println("-------------"+path.substring(0, path.lastIndexOf("/")+1));
						Path.initPath(path.substring(0, path.lastIndexOf("/")+1));
						Path.copyFile(temp, path);
						log4.info("Success download file on：" + remotePath+ "/"+ ftpFileName);
					}
				}
			} else if (isDir(list.get(i).toString()))// 是目录
			{
				temp = new File(localPath + File.separator+ getFileName(list.get(i).toString()));
				if(!temp.exists()){
					temp.mkdirs();
				}
				
				String newRemote = remotePath + File.separator
						+ getFileName(list.get(i).toString());
				downDir(localPath + File.separator
						+ getFileName(list.get(i).toString()), newRemote, backupPath);
			}
		}
		
		return downCount;
	}

	/**
	 * 从FTP文件服务器上下载文件SourceFileName，到本地destinationFileName 所有的文件名中都要求包括完整的路径名在内
	 * 
	 * @param SourceFileName
	 *            String
	 * @param destinationFileName
	 *            String
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked") 
	public String downFile(String sourceFileName, String destinationFileName)
			throws Exception {
		ftpclient.binary(); // 一定要使用二进制模式
		TelnetInputStream ftpIn = ftpclient.get(sourceFileName);
		byte[] buf = new byte[204800];
		int bufsize = 0;
		File file = new File(destinationFileName);
		if(file.isDirectory()){
			destinationFileName=destinationFileName.replaceAll("\\\\", "/");
			if(!destinationFileName.endsWith("/")){
				destinationFileName=destinationFileName+"/";
			}
			destinationFileName=destinationFileName+sourceFileName.substring(sourceFileName.lastIndexOf("/")+1);
		}
		FileOutputStream ftpOut = new FileOutputStream(destinationFileName);
		while ((bufsize = ftpIn.read(buf, 0, buf.length)) != -1) {
			ftpOut.write(buf, 0, bufsize);
		}
		ftpOut.close();
		ftpIn.close();
		return "downFile success:"+destinationFileName;
	}

	/**
	 *从FTP文件服务器上下载文件，输出到字节数组中
	 * 
	 * @param SourceFileName
	 *            String
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] downFile(String sourceFileName) throws Exception {
		ftpclient.binary(); // 一定要使用二进制模式
		TelnetInputStream ftpIn = ftpclient.get(sourceFileName);
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byte[] buf = new byte[204800];
		int bufsize = 0;

		while ((bufsize = ftpIn.read(buf, 0, buf.length)) != -1) {
			byteOut.write(buf, 0, bufsize);
		}
		byte[] return_arraybyte = byteOut.toByteArray();
		byteOut.close();
		ftpIn.close();
		return return_arraybyte;
	}

	/**
	 * 调用示例 FtpUtil fUp = new FtpUtil("192.150.189.22", 21, "admin", "admin");
	 * fUp.login(); fUp.buildList("/adfadsg/sfsdfd/cc"); String destination =
	 * "/test.zip"; fUp.upFile(
	 * "C:\\Documents and Settings\\Administrator\\My Documents\\sample.zip"
	 * ,destination); ArrayList filename = fUp.fileNames("/"); for (int i = 0; i
	 * < filename.size(); i++) { System.out.println(filename.get(i).toString());
	 * } fUp.logout();
	 * 
	 * @param args
	 *            String[]
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked") 
	public static void main(String[] args) throws Exception {
		FtpUtil fUp = new FtpUtil("10.104.10.10", 21, "scan", "scan");
		fUp.login();
		
//		  fUp.buildList("/adfadsg/sfsdfd/cc");
//		  String destination =  "/test/SetupDJ.rar"; 
//		  fUp.upFile("C:\\Documents and Settings\\Administrator\\My Documents\\SetupDJ.rar"
//		  , destination); ArrayList filename = fUp.fileNames("/"); 
//		  for (int i =0; i < filename.size(); i++) {
//			  System.out.println(filename.get(i).toString()); 
//		  }
		  
		  fUp.downFile("/temp3/readme.txt", "e:\\readme.txt");
		 
//		FileInputStream fin = new FileInputStream("e:\\uuid.txt");
//		byte[] data = new byte[20480000];
//		fin.read(data, 0, data.length);
//		fUp.upFile(data, "tempPush.txt");
//		fUp.upFile("E:/uuid.txt", "/temp3/uuid3.txt");	
//		fUp.logout();
//		System.out.println("程序运行完成！");
		/*
		 * FTP远程命令列表 USER PORT RETR ALLO DELE SITE XMKD CDUP FEAT PASS PASV STOR
		 * REST CWD STAT RMD XCUP OPTS ACCT TYPE APPE RNFR XCWD HELP XRMD STOU
		 * AUTH REIN STRU SMNT RNTO LIST NOOP PWD SIZE PBSZ QUIT MODE SYST ABOR
		 * NLST MKD XPWD MDTM PROT
		 */
		/*
		 * 在服务器上执行命令,如果用sendServer来执行远程命令(不能执行本地FTP命令)的话，所有FTP命令都要加上\r\n
		 * ftpclient.sendServer("XMKD /test/bb\r\n"); //执行服务器上的FTP命令
		 * ftpclient.readServerResponse一定要在sendServer后调用
		 * nameList("/test")获取指目录下的文件列表 XMKD建立目录，当目录存在的情况下再次创建目录时报错 XRMD删除目录
		 * DELE删除文件
		 */
	}
}