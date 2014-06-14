package org.jview.jtool.biz;

/**
 * excel导出数据
 * @author chenjh
 *
 */
public interface IExcelDataBiz {

	/**
	 * 
	 * @param countSql
	 * @param dataSql
	 * @param result
	 * @param startLine
	 * @param format
	 * @param outFile
	 */
	public abstract void export(String keys, String dataSql, String formatXls,
			String lang, String code, String name, int startLine, String outFile);

}