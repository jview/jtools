package org.jview.jtool.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.ListFile;
import org.jview.jtool.util.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 编码解码，加密解密
 * @author chenjh
 *
 */
public class FileEncode {
	 private static final Logger log4 = LoggerFactory.getLogger(FileEncode.class);   
	   
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		FileEncode fe = new FileEncode();
//		String value = "123chenjh";
		System.out.println("====start======");
		fe.keyMd5=true;
		fe.encodeFile("d:/var/ttt.rar", null);
		System.out.println("========"+(System.currentTimeMillis()-time));
	}
	
	public static final int FILE_BYTE_SIZE=1024*5;
	public static final int DISP_INFO_LINE_COUNT=1000;
	public static final int HEX_START=432;
	private static final String KEY_DEFAULT="cs123456";
	private int lineSize=80;
	private int keyValue = 100;
	private int keyLen=0;
	private boolean saveOld=true;
	private boolean zip=false;
	private boolean keyMd5=false;
	
	private String keys;
	private int[][][][] arrays;
	private Ragion array_rag;
	private void init(){
		MD5 md5 = new MD5();
		String value = keys;
		if(value==null){
			value = KEY_DEFAULT;
		}
		String pass = md5.getMD5ofStr(value);
//		String pass="abcd";
		int rad = 9;
		int len = pass.length();
		byte[] bytes = pass.getBytes();
		array_rag=new Ragion();
		array_rag.i=rad;
		array_rag.j=len;
		array_rag.k=len;
		array_rag.m=len;
		arrays = new int[rad][len][len][len];
		for(int i=0; i<rad; i++){
			for(int j=0; j<len; j++){
				for(int k=0; k<len; k++){
					for(int m=0; m<len; m++){
						arrays[i][j][k][m]=2*i+j*3+k*4-2*m+bytes[m];
//						System.out.print(arrays[i][j][k][m]+" ");
					}
//					System.out.println();
				}
//				System.out.println();
				
			}
//			System.out.println();
		}
//		Ragion rag = new Ragion();
//		for(int i=0; i<40; i++){
//			
//			System.out.print(getArraysCurValue(arrays, rag)+" ");
//		}
	}
	
	private int getArraysCurValue(int[][][][] arrays, Ragion rag){				
		int value =arrays[rag.i][rag.j][rag.k][rag.m];
		if(rag.m<array_rag.m){
			rag.m++;
		}
		if(rag.m==array_rag.m){
			rag.k++;
			rag.m=0;
		}
		if(rag.k==array_rag.k){
			rag.j++;
			rag.k=0;
		}
		if(rag.j==array_rag.j){
			rag.i++;
			rag.j=0;
		}
								
		if(rag.i==array_rag.i){
			rag.i=0;
			rag.j=0;
			rag.k=0;
			rag.m=0;	
		}
		return value;
	}
	
	/**
	 * 十六进制转十进制
	 * @param hex
	 * @return
	 */
	private int hexToInteger(String hex){		
		return Integer.parseInt(hex, 16);
	}
	/**
	 * key转成数值
	 * @param key
	 * @return
	 */
	private int getKeyValue(String key){
		key = key.trim();
		byte[] bytes = key.getBytes();
		int bb = 0;
		for(byte b:bytes){
			bb = bb+b;
		}
		this.keyLen=100+bytes.length;
		return this.getKeyLimit(bb+bytes.length);
	}
	/**
	 * 控制数值范围
	 * @param keyValue
	 * @return
	 */
	private int getKeyLimit(int keyValue){
		if(keyValue>3000){
			return keyValue/5;
		}
		else if(keyValue<0){
			return keyValue+100;
		}
		return keyValue;
	}
	
	/**
	 * 文件编码(加上文件名，放在行头，格式，类型,日期)
	 * @param path1源文件
	 * @param parth2目标文件
	 * @return
	 */
	public  boolean encodeFile(String filePath1, String filePath2) {		
		if(keys!=null){
			this.keyValue=this.getKeyValue(keys);
		}
		if(this.keyMd5&&this.arrays==null){
			this.init();
		}
		try {
			long time=System.currentTimeMillis();
			File f = new File(filePath1);
			System.out.println(f.getAbsolutePath());
			if (f != null && f.isFile() == true) {
//				System.out.println("==============1================" + keyValue	+ " " + keys);
				FileInputStream in1 = new FileInputStream(f);				
				ListFile lFile = new ListFile();
				lFile.setClass(String.class);
				if(ErrorCode.isEmpty(filePath2)){
					if(filePath1.lastIndexOf(".")>0){
						filePath2=filePath1.substring(0, filePath1.lastIndexOf("."))+".fe";
					}
				}
				lFile.setOpFile(new File(filePath2));
				int len, count = 0, count_var=0;
				int k_value = HEX_START+keyValue;
				byte[] bytes = new byte[FILE_BYTE_SIZE];

				StringBuffer sb = new StringBuffer();
				String fInfo = f.getName()+"|"+f.lastModified();
//				System.out.println("fInfo="+fInfo);
				//生成文件头信息
				for(byte b: fInfo.getBytes()){
					count_var++;
					sb.append(Integer.toHexString(b+k_value+count_var));
				}				
				lFile.add(sb.toString());
				//end生成
				count_var=0;
				
				int var = 0;
				int lastCount=0;
				Ragion rag = new Ragion();
				
				sb = new StringBuffer();
				while ((len = in1.read(bytes)) != -1) {
					for (byte b : bytes) {
						
						count_var++;
						if(this.keyMd5){	
							var = this.getArraysCurValue(arrays, rag);
							var = b+var+k_value;
//							System.out.println(count_var+" "+Integer.toHexString(var)+" arrays--"+arrays.length+" "+arrays[0].length+" "+arrays[0][0].length+" "+arrays[0][0][0].length+"  arrays["+rag.i+"]["+rag.j+"]["+rag.k+"]["+rag.m+"]="+var+"  "+"   "+b);
						}
						else{
							var = b+k_value+count_var;
						}
						sb.append(Integer.toHexString(var));
						if(count_var>=this.keyLen){
							count_var=0;
						}
						if (sb.length() > lineSize) {
							count++;
							lFile.add(sb.toString());
							sb = new StringBuffer();
							if (count % DISP_INFO_LINE_COUNT == 0) {
								log4.info(""+count);
							}
						}	
						//最后一组字节数不足bytes的个数时的处理，读完最后一个退出(即不读空字附)
						if(len<bytes.length){
							lastCount++;
							if(lastCount==len){
								break;
							}
						}
					}
				}
				if (sb.length() > 0) {
					count++;
					lFile.add(sb.toString());
					lFile.addEnd();
				}
				log4.info(""+count);
				in1.close();
				
				if(zip){
//					System.out.println("filePath2="+filePath2);
					boolean status = Path.zipFile(filePath2);		
					File file = new File(filePath2);
					file.delete();
				}
				if(!this.saveOld){
					f.delete();
				}
				log4.info((System.currentTimeMillis()-time)+"s");
//				System.out.println("==============2================" + keyValue	+ " " + keys);
			} else {
				log4.info(filePath1 + " : 文件不存在或不能读取！");
				return false;
			}

		} catch (Exception e1) {
			log4.info(filePath1 + " : 复制过程出现异常！");
			e1.printStackTrace();
			return false;

		}
		return true;

	}
	
	/**
	 * 文件解码,支持*号处理
	 * @param path1源文件
	 * @param parth2目标文件
	 * @return
	 */
	public  boolean decodeFile(String filePath1, String filePath2) {
		filePath1 = filePath1.replaceAll("\\\\", "/");
		if(keys!=null){
			this.keyValue=this.getKeyValue(keys);
		}
		if(this.keyMd5&&this.arrays==null){
			this.init();
		}
		int  value;

		try {
			long time = System.currentTimeMillis();
			if(zip){
				boolean status = Path.unzipFile(filePath1);
				File file = new File(filePath1);
				if(!saveOld){
					file.delete();
				}
				filePath1=filePath1.substring(0, filePath1.lastIndexOf("."))+".fe";
			}
			File f = new File(filePath1);
			if (f != null && f.isFile() == true) {				
				ListFile lFile = new ListFile();
				lFile.setOpFile(f);
				lFile.setClass(String.class);
//				System.out.println("==============1================"+keyValue+" "+keys);				
				// 单字节复制				
				String line = null;
				int size=lFile.size();
				lFile.getEnd();
				int k_value = HEX_START+this.keyValue;
				int j=0, k=0, count_var=0;
				byte[] bytes = new byte[FILE_BYTE_SIZE];
				
				//解析第一行文件头信息，取文件名
				line = (String)lFile.get(0);	
				byte[] fInfo_bytes = new byte[line.length()/3];
				for(j=0; j<line.length(); j=j+3){
					count_var++;
					value = this.hexToInteger(line.substring(j, j+3))-k_value-count_var;					
					fInfo_bytes[k]=(byte) value;
					k++;
				}	
				
				String fInfo = new String(fInfo_bytes);
				String fName = fInfo.substring(0, fInfo.indexOf("|"));
				//end解析第一行
				
				count_var=0;				
				k=0;
				
				//如果没有目标文件则取原文件名
				if(ErrorCode.isEmpty(filePath2)){
					filePath2=filePath1.substring(0, filePath1.lastIndexOf("/")+1)+fName;
				}
//				System.out.println("fInfo="+fInfo+" filePath2="+filePath2);
				File x = new File(filePath2);// 新文件
				FileOutputStream out1 = new FileOutputStream(x);
				Ragion rag = new Ragion();
				int var = 0;
				for(int i=1; i<size; i++){
					line = (String)lFile.get(i);										
					for(j=0; j<line.length(); j=j+3){
						count_var++;
						value = this.hexToInteger(line.substring(j, j+3));						
						if(this.keyMd5){			
							var = this.getArraysCurValue(arrays, rag);
							var = value-k_value-var;
//							System.out.println(count_var+" "+line.substring(j, j+3)+" arrays--"+arrays.length+" "+arrays[0].length+" "+arrays[0][0].length+" "+arrays[0][0][0].length+"  arrays["+rag.i+"]["+rag.j+"]["+rag.k+"]["+rag.m+"]="+var+"   "+value);
						}
						else{
							var = value-k_value-count_var;
						}
						if(count_var>=this.keyLen){
							count_var=0;
						}
						bytes[k]=(byte) var;
						k++;
						if(k==bytes.length){
							out1.write(bytes, 0, k);
							k=0;
						}
					}					
					if (i % DISP_INFO_LINE_COUNT == 0) {
						log4.info(""+i);
					}				
				}	
				if(k>0){
					out1.write(bytes, 0, k);
					k=0;
				}
				log4.info(""+size);
				lFile.getEnd();
				out1.close();
				if(!this.saveOld){
					f.delete();
				}
//				System.out.println("==============2================"+keyValue+" "+keys);
				log4.info((System.currentTimeMillis()-time)+"s");
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
	 * @return the keyValue
	 */
	public int getKeyValue() {
		return keyValue;
	}

	/**
	 * @param keyValue the keyValue to set
	 */
	public void setKeyValue(int keyValue) {
		this.keyValue = keyValue;
	}
	/**
	 * @return the keys
	 */
	public String getKeys() {
		return keys;
	}
	/**
	 * @param keys the keys to set
	 */
	public void setKeys(String keys) {
		this.keys = keys;
	}
	
	public void setSaveOld(boolean saveOld){
		this.saveOld=saveOld;
	}
	
	public void setZip(boolean zip){
		this.zip=zip;
	}
	
	
}

class Ragion{
	int i;
	int j;
	int k;
	int m;	
}
