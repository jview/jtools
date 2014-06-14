package org.jview.jtool.model;

/**
 * 
 * @author chenjh
 *
 */
public class LineVO {
	private int index;
	private boolean ignore;
	private String line;
	
	/**
	 * 行号
	 * @return
	 */
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * 行
	 * @return
	 */
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	/**
	 * @return the ignore
	 */
	public boolean isIgnore() {
		return ignore;
	}
	/**
	 * @param ignore the ignore to set
	 */
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	
}
