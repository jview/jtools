package test.jview.jtool;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.jview.jtool.manager.TaskManager;
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
		List<String> list = dbInfo.cmdToolOper("d:");
		CommMethod.print(list);
	}
}
