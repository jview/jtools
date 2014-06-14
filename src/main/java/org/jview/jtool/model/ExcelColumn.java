package org.jview.jtool.model;

public class ExcelColumn {
	private String code;
	private String column;
	private int cols;
	private int rowIndex;
	public static final String PROP_CODE="code";
	public static final String PROP_COLUMN="column";
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	
	
	public int getCols() {
		return cols;
	}
	public void setCols(int cols) {
		this.cols = cols;
	}
	public int getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	
}
