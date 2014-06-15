package test.jview.jtool;

import java.io.File;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.ta_tools.ToolCd;
import org.jview.jtool.ta_tools.ToolDate;
import org.jview.jtool.ta_tools.ToolDir;
import org.jview.jtool.ta_tools.ToolExport;
import org.jview.jtool.ta_tools.ToolFileModifyTime;
import org.jview.jtool.ta_tools.ToolPwd;
import org.jview.jtool.ta_tools.ToolSort;
import org.jview.jtool.ta_tools.ToolTime;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.CommMethod;

public class testToolClass {
	
	@Test
	public void testDir_current(){
		ITask dir = new ToolDir();
		List<String> list=dir.doExecute(".");
		CommMethod.print(list);
		TestCase.assertEquals("D:/system/mydev/jtool", list.get(0));
	}
	
	@Test
	public void testDir_list(){
		ITask task = new ToolCd();
		List<String> list=task.doExecute("cd test_file");
		CommMethod.print(list);
		list=task.doExecute("cd ..");
		CommMethod.print(list);
		ITask dir = new ToolDir();
		list=dir.doExecute("*");
		CommMethod.print(list);
		list=CommMethod.filterContent(list, "bin");
		TestCase.assertEquals("find fileName bin", list.size()>0, true);
		System.out.println("---------");
		CommMethod.print(list);
	}
	

	@Test
	public void testDir_list_bin(){
		ITask dir = new ToolDir();
		List<String> list=dir.doExecute("bin");
		CommMethod.print(list);
		TestCase.assertEquals(list.size()>0, true);
		list=CommMethod.filterContent(list, ".conf");
//		TestCase.assertEquals(list.size()>0, true);
		System.out.println("---------");
		CommMethod.print(list);
	}
	
	
	@Test
	public void testFileModifyTime_time(){
		ITask dir = new ToolFileModifyTime();
		String modifyTime="02:03:02";
		List<String> list=dir.doExecute("test_file/file/temp.txt -time "+modifyTime);
		CommMethod.print(list);
		File file = new File("test_file/file/temp.txt");
		System.out.println(file.getAbsolutePath()+" exist="+file.exists());
		TestCase.assertTrue("file exist", file.exists());
		Date date = new Date(file.lastModified());
		TestCase.assertEquals(CommMethod.format(date, "HH:mm:ss"), modifyTime);
	}
	
	@Test
	public void testFileModifyTime_date(){
		ITask dir = new ToolFileModifyTime();
		String modifyDate="2011-01-01";
		List<String> list=dir.doExecute("test_file/file/temp.txt -date "+modifyDate);
		CommMethod.print(list);
		File file = new File("test_file/file/temp.txt");
		System.out.println(file.getAbsolutePath()+" exist="+file.exists());
		TestCase.assertTrue("file exist", file.exists());
		Date date = new Date(file.lastModified());
		TestCase.assertEquals(CommMethod.format(date, "yyyy-MM-dd"), modifyDate);
		
	}
	
	@Test
	public void testFileModifyTime_datetime(){
		ITask dir = new ToolFileModifyTime();
		String modifyDate="2011-01-02 02:02:02";
		List<String> list=dir.doExecute("test_file/file/temp.txt -datetime "+modifyDate);
		CommMethod.print(list);
		File file = new File("test_file/file/temp.txt");
		System.out.println(file.getAbsolutePath()+" exist="+file.exists());
		TestCase.assertTrue("file exist", file.exists());
		Date date = new Date(file.lastModified());
		TestCase.assertEquals("check datetime modify",CommMethod.format(date, "yyyy-MM-dd HH:mm:ss"), modifyDate);
	}
	
	@Test
	public void testDate_calDay(){
		ITask task = new ToolDate();
		List<String> list=task.doExecute("2013-01-05 - 2013-01-02");
		CommMethod.print(list);
		TestCase.assertTrue("end with cal day", list.get(0).endsWith("=3"));
		list=task.doExecute("01-05 - 01-02");
		CommMethod.print(list);
		TestCase.assertTrue("end with cal day", list.get(0).endsWith("=3"));
	}
	
	@Test
	public void testTime_cal(){
		ITask task = new ToolTime();
		List<String> list=task.doExecute("05:03:02 - 04:02:01");
		CommMethod.print(list);
		TestCase.assertTrue("end with cal day", list.get(0).endsWith("=61"));
		list=task.doExecute("-h 05:03:02 - 04:02:01");
		CommMethod.print(list);
		TestCase.assertTrue("end with cal day", list.get(0).endsWith("=1"));
	}
	
	@Test
	public void testCd_bin(){
		ITask task = new ToolCd();
		List<String> list=task.doExecute("bin");
		CommMethod.print(list);
		TestCase.assertEquals("cd bin", "D:/system/mydev/jtool/bin", list.get(0));
		
	}
	
	@Test
	public void testCd_bin2(){
		ITask task = new ToolCd();
		List<String> list=task.doExecute("cd bin");
		CommMethod.print(list);
		TestCase.assertEquals("cd bin", "D:/system/mydev/jtool/bin", list.get(0));
		
	}
	
	@Test
	public void testCd(){
		ITask task = new ToolCd();
		List<String> list=task.doExecute("");
		CommMethod.print(list);
		TestCase.assertEquals("cd bin", "D:/system/mydev/jtool", list.get(0));
		
	}
	
	@Test
	public void testCd2(){
		ITask task = new ToolCd();
		List<String> list=task.doExecute("cd");
		CommMethod.print(list);
		TestCase.assertEquals("cd bin", "D:/system/mydev/jtool", list.get(0));
		
	}
	
	public void testExport(){
		ITask task = new ToolExport();
		List<String> list = task.doExecute("export -xlt format_mail_send.xlt select * from mail_send");
		CommMethod.print(list);
	}
	
	@Test
	public void testPwd(){
		ITask task = new ToolPwd();
		List<String> list=task.doExecute("");
		CommMethod.print(list);
		String path = System.getProperty("user.dir");
		TestCase.assertEquals(path, list.get(0));
//		task = new ToolDecode();
//		File file = new File("user2.jsp");
//		file.delete();
//		list=task.doExecute("user2.fe 123456");
//		CommMethod.print(list);
//		file = new File("test_file/decode/user2.jsp");
//		TestCase.assertTrue("decode file exist", file.exists());
		
	}
	
	
	@Test
	public void testExport1(){
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		ITask tool = new ToolExport();
		List<String> dataList= tool.doExecute("export -xlt format_mail_send1.xls select * from tg_user");
		
	}
	
	@Test
	public void testExportHelp(){
		ITask tool = new ToolExport();
		List<String> list = tool.doExecute("export");
		CommMethod.print(list);
		
	}
	
	@Test
	public void testSortStr() {
		ITask tool = new ToolSort();
		List<String> list = tool.doExecute("a,d,c,b,x");
		
		CommMethod.print(list);
		TestCase.assertNotNull("sort result", list);
		TestCase.assertEquals("sort result for ,", "a,b,c,d,x", list.get(0));
		list = tool.doExecute("a,d,c, b, x");
		CommMethod.print(list);
		TestCase.assertEquals("sort result for ,", " b, x,a,c,d", list.get(0));
		list = tool.doExecute("a#d#c#b#x");
		CommMethod.print(list);
		TestCase.assertEquals("sort result for #", "a#b#c#d#x", list.get(0));
		list = tool.doExecute("a|d|c|b|x");
		CommMethod.print(list);
		TestCase.assertEquals("sort result for |", "a|b|c|d|x", list.get(0));
		list = tool.doExecute("a;d;c;b;x");
		CommMethod.print(list);
		TestCase.assertEquals("sort result for ;", "a;b;c;d;x", list.get(0));
		list = tool.doExecute("a/d/c/b/x");
		CommMethod.print(list);
		TestCase.assertEquals("sort result for /", "a/b/c/d/x", list.get(0));

	}
	
	@Test
	public void testSortFile() {
		ITask tool = new ToolSort();
		List<String> list = tool.doExecute("-f d:/data2.txt");
		CommMethod.print(list);
		TestCase.assertEquals("file sort success", "d:/data2.txt文件排序成功!", list.get(0));
	}
	
	@Test
	public void testSortFile2() {
		ITask tool = new ToolSort();
		List<String> list = tool.doExecute("-f d:/data*.txt");
//		System.out.println(list.size());
		CommMethod.print(list);
		TestCase.assertEquals("file sort data file2", 2, list.size());
		
//		TestCase.assertEquals("file sort success", "d:/data2.txt文件排序成功!", list.get(0));
	}

}
