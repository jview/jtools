package test.jview.jtool;

import java.util.List;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.ta_tools.ITool;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.CommMethod;

public class testToolOper {
	private static TaskManager dbInfo;
	@BeforeClass
	public static void setUp() throws Exception {	
		dbInfo = new TaskManager();
		dbInfo.dbTool =  new DBTool();	
		dbInfo.initTask();
	}
	
	
	@Test
	public void testExport(){	
		List<String> list = dbInfo.cmdToolOper("export -xlt format_mail_send.xlt select * from mail_send");
		CommMethod.print(list);
	}
	
	@Test
	public void testExport1(){
		List<String> list = dbInfo.cmdToolOper("export -xlt format_mail_send1.xls select * from mail_send");
		CommMethod.print(list);
	}
	
	@Test
	public void testExportHelp(){		
		List<String> list = dbInfo.cmdToolOper("export");
		CommMethod.print(list);
		
	}
	
	@Test
	public void testHelp(){		
		List<String> list = dbInfo.cmdToolOper("help");
		CommMethod.print(list);
		
	}
	
	@Test
	public void testPwd(){
		List<String> list = dbInfo.cmdToolOper("d:");
		CommMethod.print(list);
	}
	
	@Test
	public void testDir(){
		List<String> list = dbInfo.cmdToolOper("dir /home/jview/temp");
		CommMethod.print(list);
	}
	
	
	@Test
	public void testSort(){
		List<String> list = dbInfo.cmdToolOper("sort 1,3,2,5,4");
		TestCase.assertEquals("sort success", "1,2,3,4,5", list.get(0));
//		CommMethod.print(list);
	}
	
	@Test
	public void testSortFile(){
		List<String> list = dbInfo.cmdToolOper("sort -f /home/jview/temp/jtools/sort*.txt");
		CommMethod.print(list);
	}
	
	@Test
	public void testDate(){
		List<String> list = dbInfo.cmdToolOper("date 2013-03-01 - 2012-05-20");
		CommMethod.print(list);
	}
	
	@Test
	public void testDateFile(){
		List<String> list = dbInfo.cmdToolOper("date -f /home/jview/temp/jtools/date.txt");
		CommMethod.print(list);
	}
	
	@Test
	public void testDateFiles(){
		List<String> list = dbInfo.cmdToolOper("date -f /home/jview/temp/jtools/*.txt");
		CommMethod.print(list);
	}
	
	@Test
	public void testLoadFile(){
		ITool tool = new ITool();
		try {
			List<String> list = tool.loadFilePath("-f /home/jview/temp/jtools/date.*");
			CommMethod.print(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
