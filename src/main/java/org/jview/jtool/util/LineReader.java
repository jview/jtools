package org.jview.jtool.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * 
 * @author chenjh
 *
 */
public class LineReader extends LineNumberReader {
	public LineReader(Reader in) {
		super(in);
	}

	private int lineNumber = 0;

	public String readLine() throws IOException {
		int count = 0;
		while (super.getLineNumber() < lineNumber) {
			count++;
			if (count > lineNumber) {// 防读死处理
				break;
			}
			super.readLine();
		}
		lineNumber++;
		return super.readLine();
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
}