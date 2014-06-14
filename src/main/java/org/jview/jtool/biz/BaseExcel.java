package org.jview.jtool.biz;



import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

/**
 * excel基本
 * @author chenjh
 *
 */
public class BaseExcel {
	protected WritableCellFormat format;
	protected WritableCellFormat formatRed;
	protected WritableCellFormat formatCenter;
	protected String fileName = "fileName.xls";
	protected int person_seq;
	
	/**
	 * @return the format1
	 */
	public WritableCellFormat getFormat() throws Exception{
//		if(format==null){
		    format=new WritableCellFormat();
		    format.setAlignment(jxl.format.Alignment.LEFT);
		    format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		    format.setBorder(Border.ALL, BorderLineStyle.THIN);
		 
//		}
		return format;
	}

	/**
	 * @param format1 the format1 to set
	 */
	public void setFormat(WritableCellFormat format) {
		this.format = format;
	}

	/**
	 * @return the formatRed
	 */ 
	public WritableCellFormat getFormatRed() throws Exception{
//		if(formatRed == null){			
			formatRed=new WritableCellFormat();
			formatRed.setAlignment(jxl.format.Alignment.CENTRE);
			formatRed.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			WritableFont wf_color = new WritableFont(WritableFont.ARIAL,10,WritableFont.NO_BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.RED);
			WritableCellFormat wff_color = new WritableCellFormat(wf_color);	
			formatRed.setBorder(Border.ALL, BorderLineStyle.THIN);
			formatRed.setFont(wf_color);				
			
//		}
		return formatRed;
	}

	/**
	 * @param formatRed the formatRed to set
	 */
	public void setFormatRed(WritableCellFormat formatRed) {
		this.formatRed = formatRed;
	}

	/**
	 * @return the formatCenter
	 */
	public WritableCellFormat getFormatCenter() throws Exception{
//		if(formatCenter==null){
			formatCenter=new WritableCellFormat();
			formatCenter.setAlignment(jxl.format.Alignment.CENTRE);
			formatCenter.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			formatCenter.setBorder(Border.ALL, BorderLineStyle.THIN);
		 
//		}
		return formatCenter;
	}

	/**
	 * @param formatCenter the formatCenter to set
	 */
	public void setFormatCenter(WritableCellFormat formatCenter) {
		this.formatCenter = formatCenter;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getPerson_seq() {
		return person_seq;
	}

	public void setPerson_seq(int personSeq) {
		person_seq = personSeq;
	}
	
	
}
