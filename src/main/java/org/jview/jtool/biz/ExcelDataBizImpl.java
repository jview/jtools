package org.jview.jtool.biz;

import java.util.List;
import org.apache.log4j.Logger;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.util.ErrorCode;






public class ExcelDataBizImpl implements IExcelDataBiz {
	private static Logger log4 = Logger.getLogger(ExcelDataBizImpl.class);
	private  IExcelBiz excelBo;		
	
	/**
	 * 得到查询数据的相关字段
	 * @param dataSql
	 * @return
	 */
	private String getDataField(String dataSql){
		String str = "select";
		String dataField = dataSql.substring(dataSql.indexOf(str)+str.length());
		str = "from";
		dataField = dataField.substring(0, dataField.lastIndexOf(str));		
		return dataField;
	}
	
	public String[] getDataFields(String dataField){
		String[] fields = dataField.split(",");
		String str=null;
		for(int i=0; i<fields.length; i++){
			str = fields[i].trim();
			if(str.indexOf(" as ")>0){
				str = str.substring(str.lastIndexOf(" as ")+" as ".length());
			}
			else if(str.indexOf(" ")>0){
				str = str.substring(str.lastIndexOf(" ")+1);
			}	
			fields[i]=str;			
		}
		return fields;
	}
	
	/**
	 * 
	 * @param keys
	 * @param dataFields
	 * @param list
	 * @param formatXls
	 * @param lang
	 * @param code
	 * @param name
	 * @param startLine
	 * @param outFile,可以为空,当outFile为空时取beans.xml中的值
	 */
	public void export(String keys, String dataFields, List list,String formatXlt, String headLang, String excelCode, String excelName,  int startLine, String outFile){
		String dataField = dataFields;
		if(dataField.indexOf("*")>0){
			log4.error("Invalid dataFields:"+dataFields);
		}
//		System.out.println("-------logDbDao-"+this.logDbDao+"  excelBo="+this.excelBo);
		String[] fields = getDataFields(dataField);
		
		
		
		log4.info("======export formatXls="+formatXlt+" lang="+headLang+" excelCode="+excelCode+" excelName="+excelName);
			
		String ecSb = this.getExcelBo().getExcelHeadString(keys, formatXlt, startLine, headLang);
		if(ErrorCode.isEmpty(outFile)){					
			//将内容转成文本格式，用于保存到附件中
			StringBuffer attachSb= new StringBuffer();
			String str = null;
			if(ecSb!=null)
				attachSb.append(ecSb.toString()+"\n");
			for(int j=0; j<list.size(); j++){
				Object[] objs = (Object[])list.get(j);
				StringBuffer lineSb = new StringBuffer();
				for(int k=0; k<objs.length; k++){
					str = ""+objs[k];
					lineSb.append(str.replaceAll("	", "||")+"	");
				}							
				attachSb.append(lineSb.toString().replaceAll("\\n", "##")+"\n");
			}
//						System.out.println(attachSb.toString());
			
			
		}
		else{//outFile不为空，则直接保存到本地指定的路径下
			this.getExcelBo().exportExcel(keys, list, formatXlt,  startLine, headLang, excelCode, excelName, fields, outFile, this.renameOutFileOnExist, ""+fields.length);
		}	
	}
	
	/**
	 * 
	 * @param countSql
	 * @param dataSql
	 * @param result
	 * @param startLine
	 * @param format
	 * @param outFile
	 */
	@Override
	public void export(String keys, String dataSql, String formatXls, String lang, String code, String name,  int startLine, String outFile){
		String dataField = this.getDataField(dataSql);
//		if(dataField.indexOf("*")>0){
//			log4.error("Invalid dataSql:"+dataSql);
//		}
//		System.out.println("-------logDbDao-"+this.logDbDao+"  excelBo="+this.excelBo);
		String[] fields = getDataFields(dataField);
		
		log4.debug("dataSql:"+dataSql);
		
		List<Object[]> list = TaskManager.dbTool.executeQuery(dataSql, fields.length);
		

		log4.info("------------1---"+list.size());
		log4.debug("------------2---"+list.size());
		String ecSb = this.getExcelBo().getExcelHeadString(keys, formatXls, startLine, lang);
		if(ErrorCode.isEmpty(outFile)){					
			//将内容转成文本格式，用于保存到附件中
			StringBuffer attachSb= new StringBuffer();
			String str = null;
			if(ecSb!=null)
				attachSb.append(ecSb.toString()+"\n");
			for(int j=0; j<list.size(); j++){
				Object[] objs = list.get(j);
				StringBuffer lineSb = new StringBuffer();
				for(int k=0; k<objs.length; k++){
					str = ""+objs[k];
					lineSb.append(str.replaceAll("	", "||")+"	");
				}							
				attachSb.append(lineSb.toString().replaceAll("\\n", "##")+"\n");
			}
//						System.out.println(attachSb.toString());
			
			
		}
		else{//outFile不为空，则直接保存到本地指定的路径下
			this.getExcelBo().exportExcel(keys, list, formatXls,  startLine, lang, code, name, fields, outFile, this.renameOutFileOnExist, ""+fields.length);
		}	
	}
	
	private String columnsSize;
	private boolean renameOutFileOnExist;
	
	public IExcelBiz getExcelBo() {		
		return excelBo;
	}

	public void setExcelBo(IExcelBiz excelBo) {
		this.excelBo = excelBo;
	}

	public String getColumnsSize() {
		return columnsSize;
	}

	public void setColumnsSize(String columnsSize) {
		this.columnsSize = columnsSize;
	}

	public boolean isRenameOutFileOnExist() {
		return renameOutFileOnExist;
	}

	public void setRenameOutFileOnExist(boolean renameOutFileOnExist) {
		this.renameOutFileOnExist = renameOutFileOnExist;
	}
	
	
	
	
}
