package org.jview.jtool.biz;

import java.io.File;
import java.util.List;

/**
 * excel
 * @author chenjh
 *
 */
public interface IExcelBiz {
	/**
	 * 从formatXls格式文件中初始化格式信息，并返回lang对应的表头行
	 * @param keys
	 * @param formatXls
	 * @param startLine
	 * @param lang
	 * @return
	 */
	public  String getExcelHeadString(String keys, String formatXls, int startLine, String lang);
	
	/**
	 * 导出Excel
	 * @param keys<model key>唯一性
	 * @param list<Object[]>
	 * @param startLine开始行号
	 * @param lang语言
	 * @param code<model code>
	 * @param name<model name>
	 * @param fields,从dataSql取到的字段信息数组
	 * @param fileName 要导出的文件名
	 */
	public String exportExcel(String keys, List list, String formatXls, int startLine, String lang, String code, String name, String[] fields, String fileName, boolean renameOutFileOnExist, String columnsSize);
	
	
	/**
	 * 导入
	 * @param keys
	 * @param importFiles
	 * @param startLine
	 * @param lang
	 * @param code
	 * @param name
	 * @param fields
	 */
	public void importExcel(String keys, String importFiles, int startLine, String lang, String code, String name, String fields);
	
	/**
	 * 直接导出内容，不含头文件，没有格式
	 * @param keys
	 * @param list
	 * @param startLine
	 * @param file_name
	 * @return
	 */
	public File creatExcelFile(String keys, List list,  int startLine, String outFile, String file_name);
}
