package test.jview.jtool;

import java.util.List;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.util.CommMethod;

public class testDbs {
	public static TaskManager taskManager;
	@BeforeClass
	public static void init(){
		taskManager = new TaskManager();
		String[] args={"para", "dbs", "desc", "tg_user"};
//		taskManager.main(args);
		taskManager.initTask();
	}
	
	@Test
	public void testShow(){
		String[] args={"para", "dbs", "show"};
		TaskManager.main(args);
	}
	
	@Test
	public void testShowDb(){
		String[] args={"para", "dbs", "showDb"};
		TaskManager.main(args);
	}
	
	@Test
	public void testAttr(){
		String[] args={"para", "dbs", "attr ts_user"};
		TaskManager.main(args);
	}
	
	@Test
	public void testMyInsert(){
		String[] args={"para", "dbs", "myinsert ts_user"};
		TaskManager.main(args);
	}
	
	@Test
	public void testUpdate(){
		String[] args={"para", "dbs", "update ts_user&user_id,!password,!user_id"};
		TaskManager.main(args);
	}
	
	@Test
	public void testShow2(){
		String cmd="para dbs show |f user";
		String[] args=cmd.split(" ");
		TaskManager.main(args);
	}
	
	@Test
	public void testShow3(){
		List<String> dataList=taskManager.cmdDbsOper("show");
		CommMethod.print(dataList);
		TestCase.assertTrue("show count>4", dataList.size()>4);
		
		System.out.println("--------------------");
		dataList=taskManager.cmdDbsOper("show |f user");
		CommMethod.print(dataList);
		TestCase.assertTrue("show count>4", dataList.size()>4);
	}
	
	@Test
	public void testData(){
		List<String> dataList=taskManager.cmdDbsOper("data tg_user");
		System.out.println("--------------------");
		TestCase.assertEquals("data tg_user count=4", dataList.size(), 4);
		dataList=taskManager.cmdDbsOper("data tg_user |f test");
		CommMethod.print(dataList);
		TestCase.assertEquals("data tg_user count=2", dataList.size(), 2);
//		String[] args2={"para", "dbs", "f", "test"};
//		TaskManager.main(args2);
	}
	
	@Test
	public void testFData(){
//		TaskManager tm = new TaskManager();
//		String[] args={"para", "dbs", "desc", "ts_user"};
//		tm.main(args);
		List<String> dataList=this.taskManager.cmdDbsOper("fdata tg_user|f test");
		TestCase.assertEquals("data tg_user count=2", dataList.size(), 2);
	}
	
	
	
	@Test
	public void testFData_test(){
		String[] args={"para", "dbs", "fdata", "ts_user|f 338444"};
		TaskManager.main(args);
	}
	
	@Test
	public void testFData_test2(){
		String cmd="para dbs fdata ts_user |f 338444";
		String[] args=cmd.split(" ");
		TaskManager.main(args);
	}
}
