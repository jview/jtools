package org.jview.jtool.model;

/**
 * 
 * @author chenjh
 *
 */
public class RepVO {
	private int type;
	private int count;
	private String fileName;
	private String filter;
	private String src;
	private String dest;
	public RepVO(){
		
	}
	public RepVO(String line){
		String[] strs=line.split("\\s+");
		if(strs.length==6){
			this.type=Integer.parseInt(strs[0]);
			this.count=Integer.parseInt(strs[1]);
			this.fileName=strs[2];			
			this.filter=strs[3];
			this.src=strs[4];
			this.dest=strs[5];
		}
		else{
			System.out.println("Invalid line:"+line);
		}
	}
	/**
	 * 替换类型，0为全部替换，1为指定次数替换
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * 替换次数
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * 替换文件名或tableName|con1,con2
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * 过滤条件，支持多个，以“，”隔开，如:str1,str2,str3(不能出现空格)
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}
	/**
	 * 替换条件
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}
	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	/**
	 * 替换结果
	 * @return the dest
	 */
	public String getDest() {
		return dest;
	}
	/**
	 * @param dest the dest to set
	 */
	public void setDest(String dest) {
		this.dest = dest;
	}
	
	
}
