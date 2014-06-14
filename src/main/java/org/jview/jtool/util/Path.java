package org.jview.jtool.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CZipInputStream;
import java.util.zip.CZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author chenjh
 *
 */
public class Path {
	public static String FILE_WEB_INF="WEB-INF";
	private static final Logger log4 = LoggerFactory.getLogger(Path.class);
	public static String sourcePathStart=null;//用于暂存初始sourcePath,以免在递归也跟着变化
	public static List<String> updateList=null; 
	
	public static String checkFile(String path){
		File file = new File(path);
		if (!file.exists()) {
			return (path + "目录不存在");			
		}
		if (!file.isFile()) {
			return (path + "文件不是文件");
			
		}
		return null;
	}
	
	public static String checkDir(String path){
		File file = new File(path);
		if (!file.exists()) {
			return (path + "目录不存在");			
		}
		if (!file.isDirectory()) {
			return (path + "文件不是目录");
			
		}
		return null;
	}
	
	/**
	 * 比较文件是否有更新或变化(以修改时间或新增文件为依据)
	 * @param sourcePath
	 * @param destPath
	 * @return
	 */
	public static List<String> checkFileModifys(String sourcePath, String destPath){
		sourcePath=sourcePath.replaceAll("\\\\", "/");
		destPath=destPath.replaceAll("\\\\", "/");
//		updatePath=updatePath.replaceAll("\\\\", "/");
//		int count=0;
//		System.out.println("checking--sourcePath="+sourcePath+"  destPath="+destPath);
		if(sourcePathStart==null){
			sourcePathStart=sourcePath;
			updateList=new ArrayList<String>();
		}
		
		File sourceFile = new File(sourcePath);		
		
		if(sourceFile.isDirectory()){
			for(File sub:sourceFile.listFiles()){
				if(sub.isFile()){
					File destFile = new File(destPath+"/"+sub.getName());
//					System.out.println("subPath="+sub.getAbsolutePath().replaceAll("\\\\", "/")
//							+"   destFilePath="+destPath+"/"+sub.getName()+" exists="+destFile.exists());
					if(!destFile.exists()||destFile.lastModified()!=sub.lastModified()){
						String tmpPath = sub.getAbsolutePath();
						
						tmpPath=tmpPath.replaceAll("\\\\", "/");
//						String path = updatePath+tmpPath.substring(sourcePathStart.length());
////						System.out.println("--subPath="+tmpPath+" path="+path+"   "+destFile.getAbsolutePath());
////						System.out.println("--subTime="+sub.lastModified()+" path="+path+"   "+destFile.lastModified());
//						Path.initPath(path.substring(0, path.lastIndexOf("/")+1));
//						Path.copyFile(sub, path);
//						updateCount++;
						updateList.add(tmpPath.substring(sourcePathStart.length()));
					}
				}
				else{
					checkFileModifys(sourcePath+"/"+sub.getName(), destPath+"/"+sub.getName());
				}
			}
		}
		return updateList;
		
	}
	
	/**
	 * 检查文件是否已存在
	 * @param sourcePath
	 * @param destPath
	 * @return
	 */
	public static List<String> checkFileExists(String sourcePath, String destPath){
		sourcePath=sourcePath.replaceAll("\\\\", "/");
		destPath=destPath.replaceAll("\\\\", "/");
//		updatePath=updatePath.replaceAll("\\\\", "/");
//		System.out.println("checking--sourcePath="+sourcePath+"  destPath="+destPath);
		if(sourcePathStart==null){
			sourcePathStart=sourcePath;
			updateList=new ArrayList<String>();
		}
		
		File sourceFile = new File(sourcePath);		
		
		if(sourceFile.isDirectory()){
			for(File sub:sourceFile.listFiles()){
				if(sub.isFile()){
					File destFile = new File(destPath+"/"+sub.getName());
					if(!destFile.exists()){
						String tmpPath = sub.getAbsolutePath();						
						tmpPath=tmpPath.replaceAll("\\\\", "/");
						updateList.add(tmpPath.substring(sourcePathStart.length()));
					}
				}
				else{
					checkFileModifys(sourcePath+"/"+sub.getName(), destPath+"/"+sub.getName());
				}
			}
		}
		return updateList;
		
	}
	
	/**
	 * 文件复制
	 * @param file 源文件
	 * @param path 目标文件
	 * @return
	 */
	public static boolean copyFile(File file, String filePath){
//		 log4.info("============copy path1="+filePath);
		try {
	         
	          if (file!=null&&file.isFile() == true) {
	                 int c;
	                 FileInputStream in1 = new FileInputStream(file);
	                 File x = new File(filePath);// 新文件
//	                 log4.info("============copy path2="+filePath);
	                 FileOutputStream out1 = new FileOutputStream(x);
	                 c = (int) file.length();
	                 byte[] b = new byte[c];
	                 /** 以下4行是“全部读入内存进行复制”方式 */
	                 in1.read(b);
	                 in1.close();
	                 out1.write(b);
	                 out1.close();
	                 x.setLastModified(file.lastModified());
	                 /*
						 * 
						 * //单字节复制 for (int i = 0; i < f.length(); i++) { b[i] =
						 * (byte)
						 * in1.read(); } for (int i = 0; i < f.length(); i++) {
						 * out1.write(b[i]); }
						 * 
						 */
	          } else {
	                 log4.info(file.getAbsolutePath() + " : 文件不存在或不能读取！");
	                 return false;
	          }
	          
	   } catch (Exception e1) {
	          log4.info(file.getAbsolutePath() + " : 复制过程出现异常！");
	          e1.printStackTrace();
	          return false;
	
	   }
		return true;
	}
	
	/**
	 * 文件复制
	 * @param path1源文件
	 * @param parth2目标文件
	 * @return
	 */
	public static boolean copyFile(String filePath1, String filePath2){
		  try {
	          File f = new File(filePath1);
	          if (f!=null&&f.isFile() == true) {
	                 int c;
	                 FileInputStream in1 = new FileInputStream(f);
	                 File x = new File(filePath2);// 新文件
	                 FileOutputStream out1 = new FileOutputStream(x);
	                 c = (int) f.length();
	                 byte[] b = new byte[c];
	                 /** 以下4行是“全部读入内存进行复制”方式 */
	                 in1.read(b);
	                 in1.close();
	                 out1.write(b);
	                 out1.close();
	                 /*
						 * 
						 * //单字节复制 for (int i = 0; i < f.length(); i++) { b[i] =
						 * (byte)
						 * in1.read(); } for (int i = 0; i < f.length(); i++) {
						 * out1.write(b[i]); }
						 * 
						 */
	                 x = null;
	          } else {
	                 log4.info(filePath1 + " : 文件不存在或不能读取！");
	                 return false;
	          }
	          f = null;
	   } catch (Exception e1) {
	          log4.info(filePath1 + " : 复制过程出现异常！");
	          e1.printStackTrace();
	          return false;
	
	   }
	   return true;
	
	}
	
	 /**  
     *  复制整个文件夹内容  
     *  @param  oldPath  String  原文件路径  如：c:/fqf  
     *  @param  newPath  String  复制后路径  如：f:/fqf/ff  
     *  @return  boolean  
     */  
   public static void  copyFolder(String  oldPath,  String  newPath)  {  
 
       try  {  
           (new  File(newPath)).mkdirs();  //如果文件夹不存在  则建立新文件夹  
           File  a=new  File(oldPath);  
           String[]  file=a.list();  
           File  temp=null;  
           for  (int  i  =  0;  i  <  file.length;  i++)  {  
               if(oldPath.endsWith(File.separator)){  
                   temp=new  File(oldPath+file[i]);  
               }  
               else{  
                   temp=new  File(oldPath+File.separator+file[i]);  
               }  
 
               if(temp.isFile()){  
                   FileInputStream  input  =  new  FileInputStream(temp);  
                   FileOutputStream  output  =  new  FileOutputStream(newPath  +  "/"  +  
                           (temp.getName()).toString());  
                   byte[]  b  =  new  byte[1024  *  5];  
                   int  len;  
                   while  (  (len  =  input.read(b))  !=  -1)  {  
                       output.write(b,  0,  len);  
                   }  
                   output.flush();  
                   output.close();  
                   input.close();  
               }  
               if(temp.isDirectory()){//如果是子文件夹  
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);  
               }  
           }  
       }  
       catch  (Exception  e)  {  
//           System.out.println("复制整个文件夹内容操作出错");  
    	   log4.info("copy full fold error "+e.getMessage());
           e.printStackTrace();  
 
       }  
 
   } 
   /**  
    *  移动文件到指定目录  
    *  @param  oldPath  String  如：c:/fqf.txt  
    *  @param  newPath  String  如：d:/fqf.txt  
    */  
  public static void  moveFile(String  oldPath,  String  newPath)  {  
      copyFile(oldPath,  newPath);  
      delFile(oldPath);  

  } 
  
  /**  
   *  删除文件  
   *  @param  filePathAndName  String  文件路径及名称  如c:/fqf.txt  
   *  @param  fileContent  String  
   *  @return  boolean  
   */  
 public static void  delFile(String  filePathAndName)  {  
     try  {  
         String  filePath  =  filePathAndName;  
         filePath  =  filePath.toString();  
         java.io.File  myDelFile  =  new  java.io.File(filePath);  
         myDelFile.delete();  

     }  
     catch  (Exception  e)  {  
//         System.out.println("删除文件操作出错");
         log4.info("del file error :"+e.getMessage());
         e.printStackTrace();  

     }  

 }  
 
 

	/**
	 * 解压zip程序
	 * 
	 */
	public static boolean unzipFile(String filenames) {
		String info = null;
//		log4.info("=======readZipFile=解压文件=======");
//		if(true)//temp change
//			return;
		boolean status = false;
		int count = 0;
		String paths2 = null;		
		File infile = new File(filenames);
		if(infile.isDirectory()){
			return false;
		}
		try {
			//不解压文件
			List<String> nList = new LinkedList<String>();
			nList.add(".dll");

			// 建立与目标文件的输入连接
//			ZipInputStream in = new ZipInputStream(new FileInputStream(infile));
			CZipInputStream in = new CZipInputStream(new FileInputStream(infile), "GBK");
			ZipEntry file = in.getNextEntry();
			
			int i = infile.getAbsolutePath().replaceAll("\\\\", "/").lastIndexOf("/");		
			String dirname;
			if (i != -1)
				dirname = infile.getAbsolutePath().substring(0, i);
			else
				dirname = infile.getAbsolutePath();
//			dirname = dirname + "\\test\\files\\";
			
			
			File newdir = new File(dirname);
			newdir.mkdir();

			byte[] c = new byte[1024];
//			int len;
			int slen;
			
			boolean isExist = false;
			while (file != null) {
				isExist = false;
//				System.out.println("path="+file.getName());
				for(int j=0; j<nList.size(); j++){
					if(file.getName().endsWith(""+nList.get(j))){
						isExist = true;
					}						
				}
				if(isExist){
					file = in.getNextEntry();
					continue;
				
				}
				
				i = file.getName().lastIndexOf(File.separatorChar);
				
				if (i != -1) {
					File dirs = new File(dirname +File.separator +  (file.getName()).substring(0, i));					
					dirs.mkdirs();
					dirs = null;
				}
				count++;
				info = ("Extract " + (file.getName())+ " ........  ");

				if (file.isDirectory()) {
					File dirs = new File((file.getName()));
					dirs.mkdir();
					dirs = null;
				} else {
					paths2 = dirname+File.separator+  (file.getName());
					if(File.separator.equals("\\")){
						paths2 = paths2.replaceAll("/", "\\\\");
					}
					
					initPath(paths2);
//					System.out.println("==============paths2="+paths2); 
					FileOutputStream out = new FileOutputStream(paths2);
					while ((slen = in.read(c, 0, c.length)) != -1)
						out.write(c, 0, slen);
					out.close();
				}
				System.out.println(info+"O.K");
				file = in.getNextEntry();
			}
			in.close();
			status=true;
		} catch (ZipException zipe) {
			// MessageBox(0,infile.getName()+"不是一个ZIP文件！","文件格式错误",16);
			info = infile.getName()+"不是一个ZIP文件！文件格式错误";
			log4.error(info);
//			this.msgs.add(info);
			zipe.printStackTrace();
		} catch (IOException ioe) {
			info = infile.getName()+"文件读取错误";
			log4.error(info);
//			this.msgs.add(info);
			ioe.printStackTrace();
			// MessageBox(0,"读取"+"时错误！","文件读取错误",16);
		} catch (Exception e) {
			e.printStackTrace();
			info = "程序问题";
//			this.msgs.add(info);
			log4.error(info);
//			System.out.println("over");
		}
		info = infile.getName()+" 解压出"+count+"个文件";
//		this.msgs.add(info);
		log4.info(info);
		return status;
	}
	
	
	/**
	 * 备份主程序为zip
	 * 
	 * @return
	 */
	public static boolean zipFile(String path) throws Exception {
				
		boolean status = false;
		List<String> nList = new ArrayList<String>();
//		nList.add("");
		File file = new File(path);
		String basePath = path;
		String pathName = path;
		if(file.isFile()){
			if(basePath.indexOf("/")>0){
				basePath = basePath.substring(0, basePath.lastIndexOf("/"));
			}
			if(pathName.indexOf(".")>0){
				pathName = pathName.substring(0, pathName.lastIndexOf("."))+".zip";
			}
		}

		List<File> fileList = getSubFiles(file, file.isDirectory());
//		log4.info("file count="+fileList.size());

		// 压缩文件名
//		CZipOutputStream zos = new CZipOutputStream(new FileOutputStream(path
//				+ ConstSystem.UPDATE_LOCAL_SYSTEM_FILE), "utf-8");
		CZipOutputStream zos = new CZipOutputStream(new FileOutputStream(pathName), "GBK");
//		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path
//				+ ConstSystem.UPDATE_LOCAL_SYSTEM_FILE));
		try{
			ZipEntry ze = null;
			byte[] buf = new byte[1024*5];
			int readLen = 0;
			String n_name = null;
			boolean isExist = false;
			int count = 0;
			for (int i = 0; i < fileList.size(); i++) {
				File f = (File) fileList.get(i);
				isExist = false;
//				System.out.print("Adding: " + f.getPath() + f.getName());
				for(int j=0; j<nList.size(); j++){
					n_name = ""+nList.get(j);
					if(f.getName().endsWith(n_name)){
						isExist = true;
					}
				}
				if(isExist||f.isDirectory()){
					continue;
				}
				
	
				
				// 创建一个ZipEntry，并设置Name和其它的一些属性
				n_name=getAbsFileName(basePath, f);
				ze = new ZipEntry(n_name);
				ze.setSize(f.length());
				ze.setTime(f.lastModified());
				count++;
				
				// 将ZipEntry加到zos中，再写入实际的文件内容
				log4.info("zip add:"+n_name);
				zos.putNextEntry(ze);
				InputStream is = new BufferedInputStream(new FileInputStream(f));
				while ((readLen = is.read(buf, 0, 1024*5)) != -1) {
					zos.write(buf, 0, readLen);
				}
				is.close();
	//			System.out.println(" done...");
			}
			log4.info("压缩成功，共计"+count+"个文件");
			status = true;
		}catch(Exception e){
			log4.error("压缩失败!");
			e.printStackTrace();
		}
		zos.close();
		

		return status;
	}
	
	/**
	 * 取得指定目录下的所有文件列表，包括子目录.
	 * 
	 * @param baseDir
	 *            File 指定的目录
	 * @return 包含java.io.File的List
	 */
	public static List<File> getSubFiles(File baseDir, boolean showSub) {		
		
		List<File> ret = new ArrayList<File>();
		if(!showSub&&baseDir.isFile()){
			ret.add(baseDir);
			return ret;
		}
		File[] tmp = baseDir.listFiles();
//		log4.info("tmp size="+tmp.length);
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].isFile()) {
				ret.add(tmp[i]);
//				log4.info("dir="+tmp[i].getName());
				
			}
			else if (tmp[i].isDirectory()) {
//				log4.info("dir="+tmp[i].getName());
				ret.add(tmp[i]);
				if(showSub){
					ret.addAll(getSubFiles(tmp[i], showSub));
				}
			}

		}
		return ret;
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
	public static String getAbsFileName(String baseDir, File realFileName) {
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
	/**
	  * 获取类的class文件位置的URL。这个方法是本类最基础的方法，供其它方法调用。
	  */
	private static URL getClassLocationURL(final Class cls) {
		if (cls == null)
			throw new IllegalArgumentException("null input: cls");
		URL result = null;
		final String clsAsResource = cls.getName().replace('.', '/').concat(
				".class");
		final ProtectionDomain pd = cls.getProtectionDomain();
		// java.lang.Class contract does not specify
		// if 'pd' can ever be null;
		// it is not the case for Sun's implementations,
		// but guard against null
		// just in case:
		if (pd != null) {
			final CodeSource cs = pd.getCodeSource();
			// 'cs' can be null depending on
			// the classloader behavior:
			if (cs != null)
				result = cs.getLocation();

			if (result != null) {
				// Convert a code source location into
				// a full class file location
				// for some common cases:
				if ("file".equals(result.getProtocol())) {
					try {
						if (result.toExternalForm().endsWith(".jar")
								|| result.toExternalForm().endsWith(".zip"))
							result = new URL("jar:".concat(
									result.toExternalForm()).concat("!/")
									.concat(clsAsResource));
						else if (new File(result.getFile()).isDirectory())
							result = new URL(result, clsAsResource);
					} catch (MalformedURLException ignore) {
					}
				}
			}
		}

		if (result == null) {
			// Try to find 'cls' definition as a resource;
			// this is not
			// document��d to be legal, but Sun's
			// implementations seem to //allow this:
			final ClassLoader clsLoader = cls.getClassLoader();
			result = clsLoader != null ? clsLoader.getResource(clsAsResource)
					: ClassLoader.getSystemResource(clsAsResource);
		}
		return result;
	}
	
	public static String getClassesPath(Class clazz)
    {
        String paths = null;
        String tempPath = null;
        String packageName = null;
        try
        {
            tempPath = getPathFromClass(clazz);
            packageName = clazz.getPackage().getName();
            packageName = packageName.replaceAll("\\.", "/");
            tempPath = tempPath.replaceAll("\\\\", "/");
            
           
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        String value = null;
//        if(tempPath.indexOf(packageName) > 0)
//            value = (new StringBuilder(packageName)).append("/").append("classes").toString();
//        else
//            value = "classes";
        int index = tempPath.indexOf(packageName);
        if(index>0){
        	paths = tempPath.substring(0, index);
        }
        else{
        	if(tempPath.endsWith(".jar")){
        		value = "/lib/";
        		if(tempPath.indexOf("/WEB-INF/")>0){
        			value="/WEB-INF/lib/";
        		}
        	}
        	else if(tempPath.indexOf("WEB-INF") > 0)
                value = value="/WEB-INF/classes/";
            else
                value = "classes";
            paths = tempPath.substring(0, tempPath.indexOf(value) + value.length());
        	log4.debug("------tempPath="+tempPath+"---package"+clazz.getPackage().getName());
        	paths = tempPath.substring(0, tempPath.indexOf(value)+value.length());
        }
        return paths;
    }
	
	/**
	  * 获取一个类的class文件所在的绝对路径。 这个类可以是JDK自身的类，也可以是用户自定义的类，或者是第三方开发包里的类。
	  * 只要是在本程序中可以被加载的类，都可以定位到它的class文件的绝对路径。
	  * 
	  * @param cls
	  *            一个对象的Class属性
	  * @return 这个类的class文件位置的绝对路径。 如果没有这个类的定义，则返回null。
	  */
	public static String getPathFromClass(Class cls) throws IOException {
		String path = null;
		if (cls == null) {
			throw new NullPointerException();
		}
		URL url = getClassLocationURL(cls);
		if (url != null) {
			path = url.getPath();
			if ("jar".equalsIgnoreCase(url.getProtocol())) {
				try {
					path = new URL(path).getPath();
				} catch (MalformedURLException e) {
				}
				int location = path.indexOf("!/");
				if (location != -1) {
					path = path.substring(0, location);
				}
			}
			File file = new File(path);
			path = file.getCanonicalPath();
		}
		return path;
	}
	
	/**
	 * web路径处理,如D:/services/tomcat/webapps/ess/
	 * @return绝对路径
	 */
	public static String getWebPath() {
		String paths = null;
		String tempPath = null;
		try {
			tempPath = Path.getPathFromClass(Path.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String value = null;
		if(tempPath.indexOf(FILE_WEB_INF)>0){
			if(tempPath.indexOf("classes")>0){
				value = FILE_WEB_INF+File.separator+"classes";
			}
			else{
				value = FILE_WEB_INF+File.separator+"lib";
			}
		}		
		else{
			value = "classes";
		}
		int index = tempPath.indexOf(value);

		if(index>0)
			paths = tempPath.substring(0, index);
		return paths;
	}
   
	/**
	 * 如果文件不存在自动创建
	 * @param paths=D:\\test\\test1\\test2\\test3
	 */
	public static void initPath(String paths){
		
		paths = paths.replaceAll("\\\\", "/");
//		paths = paths.replaceAll("//", "/");
		log4.info("initPath="+paths);
//		File f = null;
//		String temp = "";
//		try{		
//			while(paths.indexOf(File.separator)>0){
//
//				temp = temp +File.separator+ paths.substring(0, paths.indexOf(File.separator));
//				paths = paths.substring(paths.indexOf(File.separator)+1);
////				log4.info("temp="+temp.substring(1)+"  path="+paths);
//				f = new File(temp.substring(1));
//				if(!f.exists()){					
//					f.mkdirs();					
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		log4.info("init photo paths="+paths+" separator="+File.separator+" index="+paths.indexOf(File.separator));
		File f = null;
		String temp = "";
		try{		
			boolean status = false;
			while(paths.indexOf("/")>=0){
				status = false;
				temp = temp +"/"+ paths.substring(0, paths.indexOf("/"));
				paths = paths.substring(paths.indexOf("/")+1);
//				
				f = new File(temp.substring(1));
				if(!f.exists()){					
					status = f.mkdirs();					
				}
				
//				System.out.println("temp="+temp.substring(1)+"  path="+paths);
				if(status)
					log4.debug(" path="+f.getAbsolutePath()+" status="+status);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
//		f.exists();
//		f.createNewFile();
//		while (paths.)
		
	}
}
