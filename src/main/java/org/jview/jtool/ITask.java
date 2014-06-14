package org.jview.jtool;

import java.util.List;

public interface ITask {	
	public String getCode();
	public int getTaskId();
	public String getHelpInfo();
	public List<String> doExecute(String value);
}
