package org.jview.jtool.biz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.CellView;
import jxl.LabelCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.format.Font;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.log4j.Logger;
import org.jview.jtool.model.ExcelColumn;
import org.jview.jtool.model.ExcelGroup;
import org.jview.jtool.model.ExcelHead;
import org.jview.jtool.util.CommMethod;
import org.jview.jtool.util.ErrorCode;
import org.jview.jtool.util.Path;










/**
 * excel
 * @author chenjh
 *
 */
public class ExcelBizImpl extends BaseExcel implements IExcelBiz {
	
	private Logger log4 = Logger.getLogger(ExcelBizImpl.class);

	private Map modelMap;
	private static Map keysCountMap=null;
	
	
	private int count = 0;
	private Sheet sheet;
	private jxl.Cell[] ceHeads = null;
	private jxl.Cell[] ceGroups = null;
	private int[] column_sizes=null;
	private ExcelModel getExcelModel(String model_keys){
		return (ExcelModel)this.getModelMap().get(model_keys);
	}
	private Map getKeysCountMap(){
		if(keysCountMap==null){
			keysCountMap = new HashMap();
		}
		
		return keysCountMap;
	}
	
	public Map getModelMap(){
		if(this.modelMap==null){
			this.modelMap = new HashMap();
		}
		return this.modelMap;
	}
	
	private void initFieldColsMap(String keys, List egcList, List ecList, String[] fields){
		ExcelModel eModel = this.getExcelModel(keys);		
		//如果已经初始化过，可以不用初始化了fieldMap,columnMap
		if(eModel.getFieldMap()!=null){
			return;
		}
		
		eModel.setFieldMap(new HashMap());
		
		
		int k=0;
		for(String field:fields){
			eModel.getFieldMap().put(field, k);
			k++;
		}
		
//		eModel.setGroupMap(new HashMap());
//		ExcelColumn ec = null;
//		for(int i=0; i<egcList.size(); i++){
//			ec = (ExcelColumn)egcList.get(i);
//			for(String field:fields){
//				if(field.equals(ec.getCode())){
//					eModel.getGroupMap().put(field, ec.getCols());
//				}
//			}
//		}
//		System.out.println("----------ecList size="+ecList.size());
		eModel.setColumnMap(new HashMap());
		ExcelColumn ec = null;
		for(int i=0; i<ecList.size(); i++){
			ec = (ExcelColumn)ecList.get(i);
			for(String field:fields){
				if(field.equalsIgnoreCase(ec.getCode())){
					eModel.getColumnMap().put(field, ec.getCols());
//					System.out.println("-----------field="+field+" --"+ec.getCols());
				}
			}
		}
		this.getModelMap().put(keys, eModel);
	}
	
//	/**
//	 * 在path后加流水号
//	 * 在eModel中缓存上次文件号
//	 * @param path
//	 * @return
//	 */
//	private String getPathNew(ExcelModel eModel, String path){
//		String fileName = path.substring(path.lastIndexOf("/")+1);
//		path = path.substring(0, path.lastIndexOf("/"));
//		String fileEnd = fileName.substring(fileName.lastIndexOf(".")+1);
//		fileName = fileName.substring(0, fileName.lastIndexOf("."));
//		int num = 0;
//		if(fileName.indexOf("_")>0){
//			String nums = fileName.substring(fileName.lastIndexOf("_")+1);
//			if(CommMethod.matchNumberstr(nums)){
//				fileName = fileName.substring(0, fileName.lastIndexOf("_"));
//			}
//		}
//		String newPath = path+"/"+fileName+"_"+eModel.getCount()+"."+fileEnd;
//		File file = new File(newPath);
//		if(file.exists()){			
//			eModel.setCount(eModel.getCount()+1);
//			return getPathNew(eModel, newPath);
//		}
//		file=null;
//		return newPath;
//		
//	}
	
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
	 * 从excel模板载入行头信息
	 */
	public String getExcelHeadString(String keys, String formatXls, int startLine, String lang){
		if(ErrorCode.isEmpty(formatXls)){
			return null;
		}
		List ecList = this.loadFormat(keys, formatXls, startLine, lang);
		StringBuffer ecSb = new StringBuffer();
		ExcelColumn ec = null;
		for(int j=0; j<ecList.size(); j++){
			ec = (ExcelColumn)ecList.get(j);
			ecSb.append(ec.getColumn()+"	");
		}
		return ecSb.toString();
	}
	
	public void importExcel(String keys, String importFiles, int startLine, String lang, String code, String name, String fields){
		
	}
	
	@Override	
	public String exportExcel(String keys, List list, String formatXls, int startLine, String lang, String model_code, String model_name, String[] fields, String outFile, boolean renameOutFileOnExist, String columnsSize){
		log4.info("========exportExcel==keys="+keys+" formatXls="+formatXls +" startLine="+startLine+" lang="+lang+" model_code="+model_code+" model_name="+model_name);
		// TODO Auto-generated method stub
		File file = null;
		if(ErrorCode.isEmpty(formatXls)){
			file = this.creatExcelFile(keys, list, startLine, outFile, null);
			if(file!=null){
				return file.getAbsolutePath();
			}
			return null;
		}
		ExcelModel eModel = this.getExcelModel(keys);
//		System.out.println("===========keys1="+keys+" eModel="+eModel+" count="+eModel.getCount());
		//如果未初始化格式信息，这里初始化
		if(eModel==null&&formatXls!=null){
			this.getExcelHeadString(keys, formatXls, startLine, lang);
			eModel = this.getExcelModel(keys);
		}
//		log4.debug("===========keys2="+keys+" eModel="+eModel);
		if(eModel==null){
			log4.error("Invalid keys:"+keys);
			return null;
		}
		
		if(!ErrorCode.isEmpty(columnsSize)){
			String[] sizes = columnsSize.split(",");
			this.column_sizes=new int[sizes.length];
			int i=0;
			for(String s:sizes){
				this.column_sizes[i]=CommMethod.getIntegerValue(s);
				i++;
			}
		}
		
		
		
		file = this.creatExcelFile(eModel, keys, list, startLine, model_code, model_name, fields, outFile, null, renameOutFileOnExist);
		if(file!=null){
			return file.getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * 
	 * @param eModel
	 * @param keys
	 * @param list
	 * @param startLine
	 * @param model_code
	 * @param model_name
	 * @param fields
	 * @param outFile
	 * @param file_name
	 * @return
	 */
	private File creatExcelFile(Object eModelObj, String keys, List list,  int startLine,String model_code, String model_name, String[] fields, String outFile, String file_name, boolean renameOutFileOnExist){
		ExcelModel eModel =(ExcelModel)eModelObj;
		
		String path = null; 
		WritableWorkbook wwb;
		
	
		
		if(ErrorCode.isEmpty(outFile)){
			path = Path.getWebPath();
			path = path +"upload"+File.separator+this.getPerson_seq()+File.separator;			
			Path.initPath(path); 
			if(ErrorCode.isEmpty(file_name)){
				file_name = this.fileName;
			}
			path = path +File.separator+ file_name;
		}
		else{
			path = outFile;
		}

		path = path.replaceAll("\\\\", "/");

		if(renameOutFileOnExist&&eModel!=null){
			path = this.getPathNew(keys, path);
			this.getModelMap().put(keys, eModel);
			eModel = this.getExcelModel(keys);
		}
//		System.out.println("===========keys3="+keys+" eModel="+eModel+" count="+eModel.getCount());
		java.io.File file = new java.io.File(path);
		
		if(file.exists()){
			
		}
	    try {
	    	log4.info("-------------path="+path+" file_path = "+file.getCanonicalPath());
	    	if(fields!=null){
	    		this.initFieldColsMap(keys, eModel.getEgcList(), eModel.getEcList(), fields);
	    		eModel = this.getExcelModel(keys);
	    	}
	    	int field_size = 0;
	    	if(fields!=null){
	    		field_size=fields.length;
	    	}
	    	
	    	if(ErrorCode.isEmpty(model_code)){
	    		model_code = file_name;
	    	}
	    	if(ErrorCode.isEmpty(model_name)){
	    		model_name = file_name;
	    	}

			wwb= Workbook.createWorkbook(file);
			ExcelHead eh = null;
			WritableSheet ws = wwb.createSheet(model_code, 0);
			
			
			
			this.createSheets(eModel, ws, eModel.getEgcList(), eModel.getEcList(), model_code, model_name, field_size, list, startLine);//Excel文件生成
			 
			wwb.write();			
			wwb.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log4.error(e.getMessage());
			e.printStackTrace();
			
		}
		catch(Exception e){
			log4.error(e.getMessage());
			e.printStackTrace();
		}
		return file;
	}
	
	public File creatExcelFile(String keys, List list,  int startLine, String outFile, String file_name){		
		String path = null; 
		WritableWorkbook wwb;
		
	
		if(ErrorCode.isEmpty(outFile)){
			path = Path.getWebPath();
			path = path +"upload"+File.separator+this.getPerson_seq()+File.separator;			
			Path.initPath(path); 
			if(ErrorCode.isEmpty(file_name)){
				file_name = this.fileName;
			}
			path = path +File.separator+ file_name;
		}
		else{
			path = outFile;
		}
		
		
		

		path = path.replaceAll("\\\\", "/");
		path = this.getPathNew(keys, path);//如果文件已存在，则在文件号+1

		int rowscount=startLine;
//		System.out.println("===========keys3="+keys+" eModel="+eModel+" count="+eModel.getCount());
		java.io.File file = new java.io.File(path);
		
		if(file.exists()){
			
		}
	    try {
	    	log4.info("-------------path="+path+" file_path = "+file.getCanonicalPath());
	    	this.getFormat();
	    	this.getFormatCenter();
	    	this.getFormatRed();
	    	    	
			wwb= Workbook.createWorkbook(file);
			ExcelHead eh = null;
			WritableSheet ws = wwb.createSheet(keys, 0);
			
			Object[] objs = null;
			
			for (int i = 0; i < list.size(); i++) {
				objs = (Object[])list.get(i);
				rowscount++;				
				this.addCell(ws, format, objs, rowscount);
			}
//			this.createSheets(eModel, ws, eModel.getEcList(), model_code, model_name, field_size, list, startLine);//Excel文件生成
			 
			wwb.write();			
			wwb.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log4.error(e.getMessage());
			e.printStackTrace();
			
		}
		catch(Exception e){
			log4.error(e.getMessage());
			e.printStackTrace();
		}
		return file;
	}
	
	
	/**
	 * 导出的Excel文件生成( 在父类中实现文件下载
	 * field_size=0时表示不生成表头文件
	 */
	private void createSheets(ExcelModel eModel, WritableSheet ws, List egcList, List ecList, String model_code, String model_name, int field_size, List tList, int rowscount) throws Exception {
		WritableCellFormat format = this.getFormat();
		this.getFormatCenter();
		this.getFormatRed();		
		if(field_size>0){
			this.createExcelHead(eModel, ws, 0, egcList, ecList, model_code, model_name, field_size);
		}
		this.createExcelContent(eModel, ws, rowscount, tList);

	}
	
	/**
	 * Excel标题与行头处理
	 * 
	 * @param ws
	 * @param format
	 * @param rowscount
	 * @throws Exception
	 */
	private void createExcelHead(ExcelModel eModel, WritableSheet ws, int rowscount, List egcList, List ecList, String model_code, String model_name, int field_size)
			throws Exception {
		
		
		int cols = 0;

		
		
		// 第一行标题
		String disp = model_name;
		
		if (cols < 0) {
			return;
		}
		
//		cols = field_size+1;
		cols = ecList.size(); 
//		System.out.println("===========cols="+cols+" rowscount="+rowscount+" field_size="+field_size);
		ws.mergeCells(1, rowscount, cols - 1, rowscount);		
		ws.addCell(new Label(0, rowscount, model_code, this.getFormatCenter()));
		ws.addCell(new Label(1, rowscount, disp + "("+ CommMethod.format(new Date(), null) + ")", this.getFormatCenter()));
		rowscount++;
		
		// Object[] objs = e_group.values();

		// WritableCellFormat format2= this.getFormatRed();
		// WritableCellFormat formatCenter= this.getFormatCenter();
		// 第二行头内容,取column,不处理即可
//		if(this.getHeadGroup()==ConstParas.type_head_group.GROUP.getType()||
//				this.getHeadGroup()==ConstParas.type_head_group.TITLE_GROUP.getType()){
//			cols = 0;				
//			for (int i = 0; i < egList.size(); i++) {
//				eg = (ExGroup) egList.get(i);
//				
//				// e_group e_g = (e_group)objs[i];
//				list = this.getExGroupCodeColsByGroup(ecList, eg.getCols());
//				ws.mergeCells(cols, rowscount, cols + list.size() - 1, rowscount);
//				ws.addCell(new Label(cols, rowscount, ""+this.groupMap.get(eg.getCols()), formatCenter));
//				cols = cols + list.size();
//	
//			}
//			rowscount++;
//		}		


//		 第二行头内容,取column,不处理即可	group	
		cols = 0;		
		int cols_cur=0;
		ExcelColumn eg = null;
		String curValue = null;
		ExcelColumn curEg=null;	
		WritableCellFormat ceFormat = null;
		if(egcList!=null&&egcList.size()>0){
//			检查空行，准备用于合并
			for (int i = 0; i < egcList.size(); i++) {
				eg = (ExcelColumn) egcList.get(i);	
				if(!ErrorCode.isEmpty(eg.getColumn())){
					if(curEg!=null)
						curEg.setCols(cols_cur);
					curEg = eg;
					cols_cur=cols;
				}
				cols++;
				cols_cur++;
				
			}
			curEg.setCols(curEg.getCols()+1);
			cols_cur=0;
			cols=0;
			for (int i = 0; i < egcList.size(); i++) {
				eg = (ExcelColumn) egcList.get(i);				
				if(!ErrorCode.isEmpty(eg.getColumn())){
	//				System.out.println("----------code="+eg.getCode()+"---cols="+cols+" cols_cur="+(eg.getCols())+" column="+eg.getColumn());				
					ws.mergeCells(cols, rowscount, eg.getCols()-1 , rowscount);
					ws.addCell(new Label(cols, rowscount, eg.getColumn(), this.formatCenter));
					cols = eg.getCols();
				}
			}				
			rowscount++;
			
//			
//			for (int i = 0; i < egcList.size(); i++) {
//				eg = (ExcelColumn) egcList.get(i);								
//				ceFormat = this.getFormatCenter();
//				ws.addCell(new Label(cols, rowscount, eg.getColumn(), ceFormat));
//				cols = eg.getCols();
//				
//			}				
//			rowscount++;
		}
//		System.out.println("------------ceHead="+this.ceHeads);
		// 第三行头内容
		cols = 0;	
		ExcelColumn ec = null;
		WritableCellFeatures ceFeature;
		
		CellView cellview = null;
		for (int i = 0; i < ecList.size(); i++) {
			ec = (ExcelColumn) ecList.get(i);
			cellview = this.sheet.getColumnView(i);		
			if(this.column_sizes==null||this.column_sizes.length<=1){
				 cellview.setAutosize(true);
			 }
			 else{
				 if(i<this.column_sizes.length){
					 cellview.setSize(this.column_sizes[i]);
				 }
			 }
			ws.setColumnView(i, cellview);
//			System.out.println("----------code="+ec.getCode()+"---cols="+cols+" egCols="+ec.getCols()+" rowIndex="+ec.getRowIndex()+" column="+ec.getColumn());
			ceFormat = this.getFormatCenter();
			Label label = new Label(cols, rowscount, ec.getColumn());
			if(this.ceHeads!=null){				
				ceFormat = this.getCellFormat(this.ceHeads[i]);
				label.setCellFormat(ceFormat);
//				ceFeature = new WritableCellFeatures();				
//				label.setCellFeatures(new WritableCellFeatures(this.ceHeads[i].getCellFeatures()));
			}			
			ws.addCell(label);			
			cols++;
		}
		

	}
	
	
	private WritableCellFormat getCellFormat(Cell cell){
		WritableCellFormat ceFormat=null;
		try{
			ceFormat = new WritableCellFormat();
			LabelCell l_c = (LabelCell) cell;
			CellFormat c_c_format = l_c.getCellFormat();
			Font c_c_font = c_c_format.getFont();
			WritableFont w_font = new WritableFont(c_c_font);
			WritableCellFormat w_c_format = ceFormat;
			w_c_format.setAlignment(c_c_format.getAlignment());
			w_c_format.setBackground(c_c_format.getBackgroundColour());
			w_c_format.setBackground(c_c_format.getBackgroundColour(), c_c_format.getPattern());
			w_c_format.setFont(w_font);
			w_c_format.setIndentation(c_c_format.getIndentation());
			w_c_format.setOrientation(c_c_format.getOrientation());
			w_c_format.setWrap(c_c_format.getWrap());
			w_c_format.setVerticalAlignment(c_c_format.getVerticalAlignment());				
//			l.setCellFormat(w_c_format);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ceFormat;
	}
	
	
	/**
	 * Excel内容填充
	 * 
	 * @param ws
	 * @param format
	 * @param colscount
	 * @param rowscount
	 * @throws Exception
	 */
	private void createExcelContent(ExcelModel eModel, WritableSheet ws, int rowscount, List tList)
			throws Exception {
		log4.info("----------createExcelContent size="+tList.size());
		Object obj = null;
		for (int i = 0; i < tList.size(); i++) {
			obj = tList.get(i);
			rowscount++;
			this.addCell(eModel, obj, ws, format, rowscount);
		}

	}

	/**
	 * 载入格式信息及返回lang对应的ExcelColumn list
	 * @param keys
	 * @param formatXls
	 * @param startLine
	 * @param lang
	 * @return ExcelColumn list
	 */
	private List loadFormat(String keys, String formatXls, int startLine, String lang) {
		// TODO Auto-generated method stub
		ExcelModel eModel = this.getExcelModel(keys);
		startLine=1;
		log4.info("=========loadFormat---formatXls="+formatXls+" keys="+keys+" startLine="+startLine);
		//如果未载入过，则载入，否则不用载入
//		if(eModel==null){
			eModel = new ExcelModel();			
//		}
//		else{
//			System.out.println("------2------------");
//			return eModel.getEcList();
//		}
		String rconf = System.getProperty("rconf");
		eModel.setHeadMap(null);
		List list = null;
		String path = null;
		if(ErrorCode.isEmpty(rconf)){
			path = Path.getClassesPath(this.getClass());
			path = path + formatXls;
			log4.info("------path="+path);
//			path = path.replaceAll("\\\\", "/");
		}
		else{
			path = rconf+"/"+formatXls;
		}
		
		File file = new File(path);
		log4.info("----------xltFile="+path+" exist="+file.exists());
		if(file.exists()){
			list = this.loadFormatSheet(file, startLine);
		}
		else{
			log4.info("Unexit file of path:"+path);
			return list;
		}
		
		List egList = new ArrayList();
		List ecList = new ArrayList();
		for(Object obj: list){
			if(obj instanceof ExcelGroup){
				egList.add(obj);
			}
			else if(obj instanceof ExcelHead){
				ecList.add(obj);
			}
		}
		
//		System.out.println("------------egList="+egList.size());
		ExcelGroup eg = null;
		boolean isExist=false;
		List keyList = null;
		List valueList = null;
		for(int i=0; i<egList.size(); i++){
			eg = (ExcelGroup)egList.get(i);
			if(eg.getLang().trim().equals("code")){
				keyList=eg.getColumnList();
			}
//			System.out.println("------------lang="+eg.getLang());
			if(eg.getLang().trim().equals(lang)){
				eModel.setGroupMap(eg.getHeadMap());
				eModel.setEgcList(eg.getColumnList());	
				valueList = eg.getColumnList();
			}
		}
		if(keyList!=null&&valueList!=null){
			ExcelColumn ecKey;
			ExcelColumn ecValue;
			for(int i=0; i<keyList.size(); i++){
				ecKey = (ExcelColumn)keyList.get(i);
				ecValue = (ExcelColumn)valueList.get(i);
				eModel.getGroupMap().put(ecKey.getColumn(), ecValue.getColumn());
//				System.out.println("-------code="+ecKey.getColumn()+"  column="+ecValue.getColumn()+" cols="+ecKey.getCols());
			}
		}
		
//		System.out.println("------------ecList="+ecList.size());
		ExcelHead eh = null;
		isExist=false;
		for(int i=0; i<ecList.size(); i++){
			eh = (ExcelHead)ecList.get(i);
//			System.out.println("------------lang="+eh.getLang());
			if(eh.getLang().trim().equals(lang)){
				eModel.setHeadMap(eh.getHeadMap());				
				eModel.setEcList(eh.getColumnList());				
				isExist=true;
			}
		}
		if(!isExist){
			log4.info("Unexist head type of lang:"+lang);
		}
		
		if(eModel.getHeadMap()==null){
			log4.info("Unexist head type of lang:"+lang);
		}
		this.getModelMap().put(keys, eModel);
//		System.out.println("----keys="+keys+" eModel="+eModel+" list size="+list.size());
		return eModel.getEcList();
	}	

	/**
	 * 对excel文件中的数据进行采集
	 * 
	 * @return
	 */
	private List loadFormatSheet(File file, int startLine) {		
		if(file!=null){
			log4.info("=====loadFormatSheet===startLine="+startLine+" fileName="+file.getName());
		}
		else{
			log4.info("=====loadFormatSheet===startLine="+startLine+" file=null");
			return null;
		}
		
		int rowscount = startLine;
		jxl.Cell[] ceHead = null;
		jxl.Cell[] ce = null;
		List ehList = new LinkedList();
		List egList = new LinkedList();
		try {
			// java.io.File file = com.itlt.upload.UploadRenderer.upload_file;

			// InputStream is = new FileInputStream(file)
			// log4.info("file="+file);
			Workbook wbFile = Workbook.getWorkbook(file);

//			log4.info("wbFile=" + wbFile + " wbFile="+ wbFile.getNumberOfSheets());

			Sheet sh = wbFile.getSheet(0);
			this.sheet=sh;
			int rowCount = sh.getRows();



			Object entity = null;
			int rowHead=-1;
			String h_value = null, values= null;
			
			for (int i = rowscount; i < rowCount; i++) {
				ce = sh.getRow(i);				
				values = ce[0].getContents().toString();
				values = values.trim();
				if(i>0&&values.equals("code")){
					rowHead = i;
					this.ceHeads=ce;
				}
				else if(values.equals("group_code")){
					this.ceGroups=ce;
				}
			}
			ceHead = sh.getRow(rowHead);
			rowscount++;
			
			
			String group_head = "group_";
			ExcelGroup eg = null;
			ExcelHead eh = null;
			ExcelColumn ec = null;
			
			List egcList = null;
			List ecList = null;
			
			
			for (int i = rowscount; i < rowCount; i++) {
				ce = sh.getRow(i);				
				values = ce[0].getContents().toString();
				values = values.trim();
				//group
				if(values.indexOf(group_head)>=0){
					eg = new ExcelGroup();
					egcList = new ArrayList();
					for (int j = 0; j < ce.length; j++) {
						h_value = ceHead[j].getContents().toString();
						values = ce[j].getContents().toString();							
						if(j==0){
							values = values.trim();						
							eg.setLang(values.substring(group_head.length()));
							
						}else{
							ec = new ExcelColumn();
							ec.setCode(h_value.trim());
							ec.setColumn(values.trim());
							ec.setCols(j);
							egcList.add(ec);
						}
//						System.out.print("	"+values);
					}
					eg.setColumnList(egcList);
					egList.add(eg);
				}
				//head
				else{
					eh = new ExcelHead();				
					ecList = new LinkedList();
					for (int j = 0; j < ce.length; j++) {
						h_value = ceHead[j].getContents().toString();
						values = ce[j].getContents().toString();					
						if(j==0){
							values = values.trim();		
//							System.out.println("---------11---lang="+values);
							eh.setLang(values);
							
						}else{
							ec = new ExcelColumn();
							ec.setCode(h_value.trim());
							ec.setColumn(values.trim());
							ec.setCols(j);
							ecList.add(ec);
						}
//						System.out.print("	"+values);
					}
					eh.setColumnList(ecList);
					ehList.add(eh);
				}
				
				
				
//				System.out.println();
				// entity.setRowIndex(i+1);
			}
			
//			this.print(ehList);
			ehList.addAll(egList);
			wbFile.close();

		} catch (Exception e) {
			e.printStackTrace();
			log4.error(e.getLocalizedMessage());
	
			return null;
		}

		return ehList;
	}
	
	
	
	private void print(List ehList){
		ExcelHead eh = null;
		ExcelColumn ec = null;
		List ecList = null;
		for(int i=0;i<ehList.size(); i++){
			eh = (ExcelHead)ehList.get(i);
			System.out.println("lang="+eh.getLang());
			ecList = eh.getColumnList();
			for(int j=0; j<ecList.size(); j++){
				ec = (ExcelColumn)ecList.get(j);
				System.out.print("	"+ec.getCode());
			}
			System.out.println();
			for(int j=0; j<ecList.size(); j++){
				ec = (ExcelColumn)ecList.get(j);
				System.out.print("	"+ec.getColumn());
			}
			System.out.println();
		}
	}
	/**
	 * (导出)Excel内容填充(从第三行开始)
	 * 
	 * @param Info
	 * @param ws
	 * @param format
	 * @param rowscount
	 * @throws Exception
	 */
	private void addCell(ExcelModel eModel, Object obj, WritableSheet ws,
			WritableCellFormat format, int rowscount) throws Exception {
	
		if (obj != null) {
			int cols = 0;
			ExcelColumn ec = null;
			// List ecList =
			// HibDb.queryHQL("from ExGroupCode ec where ec.status='A' and ec.exGroup.excel.code='"+code+"'");

			int count = 0;
			for (int i = 0; i < eModel.getEcList().size(); i++) {
				ec = (ExcelColumn) eModel.getEcList().get(i);
//				if(ec.getCols()>=0){
					ws.addCell(new Label(i - count, rowscount, this.getValue(eModel, obj, ec.getCols(), ec.getCode()), format));
					cols++;
//				}
			}

		}
	}
	
	private void addCell(WritableSheet ws,
			WritableCellFormat format, Object[] objs, int rowscount) throws Exception {
		
			int cols = 0;
			
			// List ecList =
			// HibDb.queryHQL("from ExGroupCode ec where ec.status='A' and ec.exGroup.excel.code='"+code+"'");

			int count = 0;
			int i=0;
			for(Object obj: objs)
			{				
				ws.addCell(new Label(i, rowscount, ""+obj, format));
				i++;
				cols++;
			}

		
	}
	
	
	private String getValue(ExcelModel eModel, Object obj, int cols, String code) {
		Object[] entity = (Object[])obj;
		// TODO Auto-generated method stub
		int field_cols = CommMethod.getIntegerValue(""+eModel.getColumnMap().get(code));
		int field_sub =  CommMethod.getIntegerValue(""+eModel.getFieldMap().get(code));
		if(cols<0){
			return "";
		}
//		String value = "";
		Object value = null;
		
		if(cols==field_cols){
			value = entity[field_sub];
			if(value instanceof Date){
				value =CommMethod.format((Date)value, null);
			}
		}			
		
		return ""+value;
	}

	
	/**
	 * (导入)Excel信息读取，从每行中取所需数据
	 * 
	 * @param ce
	 * @return
	 */
//	private Object getCell(Object obj, jxl.Cell[] ce, int[] arrays) {
//		int array = 0;
//		boolean isError = false;
//
//		String ce_value = null;
//		String errors = "";
//		if (ce != null) {
//			int cols = 0;
//			for (int i = 0; i < ce.length; i++) {
//				if (i < arrays.length) {
//					array = arrays[i];
//					if (array >= 0) {
//						if (ce[i].getType() == CellType.DATE) {
//							DateCell datec00 = (DateCell) ce[i];
//							Date dt = datec00.getDate();
//							ce_value = CommMethod.formatDate(dt, null);
//						} else {
//							ce_value = ce[i].getContents().toString();
//						}
//						String err = this.setValue(obj, array, ce_value);
//						if (!ErrorCode.isEmpty(err)) {
//							if (err.equals("false")) {
//							} else {
//								errors = errors + "||" + err;
//							}
//						}
//
//					}
//					// 忽略列名不符的数据
//					else {
//
//					}
//
//				}
//				cols++;
//			}
//
//			if (errors.length() > 0) {
//				String info = this.getRowInfo(obj) + " 错误:";
//
//				this.aMessage.addMessage(info + errors);
//
//				return null;
//			}
//		}
//
//		return obj;
//	}
	
	private String getRowInfo(Object obj){
		ExcelColumn entity = (ExcelColumn)obj;
		return "行号:"+entity.getRowIndex()+" 工号为:"+entity.getCode();
	}

//	public String setValue(Object obj, int cols, String value){
//		ExcelColumn entity = (ExcelColumn)obj;
//		boolean isError =false;
//		if(cols<0)
//			return "false";		
//		String times = null;
//
//		String info = ""+this.dataMap.get(cols)+":";
//		String error = null;
//		
//		//学员资料
//		if(cols==getCols(ExcelColumn.PROP_CODE)){			
//			value = value.trim();						
//			try{
//				entity.setCode(value);	
//				if(ErrorCode.isEmpty(entity.getCode())){
//					error="code不能为空";
//					isError=true;
//				}
//			}catch(Exception e){
//				error = info+value+e.getMessage();
//				isError = true;			
//			}			
//		}
//		else if(cols==getCols(ExcelColumn.PROP_COLUMN)){			
//			entity.setColumn(value);
//			if(ErrorCode.isEmpty(entity.getColumn())){
//				error="column不能为空";
//				isError=true;
//			}
//		}
//		
//		
//		if(isError){
//			return error;
//		}
//		return null;
//	}
	/**
	 * 返回列数号{1,2},不带<>()的检查
	 * 
	 * @param ce
	 *            {name, code}
	 * @return
	 */
	private int[] getCols(ExcelModel eModel, jxl.Cell[] ce) {
		int[] arrays = new int[ce.length];
		int cols = -1;
		String values = null;
		for (int i = 0; i < ce.length; i++) {
			values = ce[i].getContents().toString();
			if (!ErrorCode.isEmpty(values)) {
				cols = this.getColsByName(eModel, values.trim());
				if (cols < 0) {
					log4.error(values+ "-列名与系统不匹配，忽此列所有数据的导入，不予处理!");
//					this.aMessage.addMessage(values+ "-列名与系统不匹配，忽此列所有数据的导入，不予处理!");
				}
				arrays[i] = cols;

			}

		}
		return arrays;

	}
	
//	private void getColsHead(jxl.Cell[] ce){
//		String values = null;
//		for (int i = 0; i < ce.length; i++) {
//			values = ce[i].getContents().toString();
//			if (!ErrorCode.isEmpty(values)) {
//				System.out.println(values);
//			}
//		}
//	}
	
//	public  int getCols(String code){
//		code = code.trim();
//		if(exGroupCodeList==null){
//			getExGroupCodeList();
//		}
//		int cols = -1;
//		ExGroupCode ec = null;
//		
//		for(int i=0; i<exGroupCodeList.size(); i++){
//			ec = (ExGroupCode)exGroupCodeList.get(i);
//			if(ec.getCode().trim().equals(code)){
//				cols = ec.getCols();
//			}
//		}
//		return cols;
//	}
	
	/**
	 * 根据名称得cols
	 * 
	 * @param status
	 * @return
	 */
	private int getColsByName(ExcelModel eModel, String cnName) {
		// e_Info csObj = null;
		int cols = -1;
		if (cnName == null) {
			return cols;
		}
		Iterator it = eModel.getHeadMap().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			 System.out.println("----------getColsByName key="+key+" value="+value);
			if (value != null && value.toString().trim().equals(cnName.trim())) {
				cols = (Integer) key;
			}
		}

		return cols;
	}
	
//	public static void main(String[] args){
//		ExcelBOImpl t = new ExcelBOImpl();
//		String path = "d:/format_mail_send_1.xls";
////		File file = new File("d:/format_mail_send.xls");
////		t.loadFormatSheet(file, 2);
//		String path2 = t.getPathNew(null, path, 0);
//		System.out.println("path2="+path2);
//		
//		
//	}
	
	class ExcelModel{
		private List ecList = null;//excel column
		private List egcList = null; //excel group column
		private Map groupMap;		
		private Map headMap;//用于处理表头，code:name
		private Map columnMap;//用于暂存字段对应例,code:cols
		private Map fieldMap;
		private int count;//导入文件重复记录器
		public List getEcList() {
			return ecList;
		}
		public void setEcList(List ecList) {
			this.ecList = ecList;
		}
		
		public List getEgcList() {
			return egcList;
		}
		public void setEgcList(List egcList) {
			this.egcList = egcList;
		}
		public Map getHeadMap() {
			return headMap;
		}
		public void setHeadMap(Map headMap) {
			this.headMap = headMap;
		}
		public Map getColumnMap() {
			return columnMap;
		}
		public void setColumnMap(Map columnMap) {
			this.columnMap = columnMap;
		}
		public Map getFieldMap() {
			return fieldMap;
		}
		public void setFieldMap(Map fieldMap) {
			this.fieldMap = fieldMap;
		}
		
		
		public Map getGroupMap() {
			return groupMap;
		}
		public void setGroupMap(Map groupMap) {
			this.groupMap = groupMap;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		
		
		
		
	}
}
