package test.jview.jtool;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.jview.jtool.ITask;
import org.jview.jtool.manager.TaskManager;
import org.jview.jtool.ta_dbs.DbConstClass;
import org.jview.jtool.ta_dbs.DbData;
import org.jview.jtool.ta_dbs.DbDesc;
import org.jview.jtool.ta_dbs.DbEnum;
import org.jview.jtool.ta_dbs.DbFData;
import org.jview.jtool.ta_dbs.DbInsert;
import org.jview.jtool.ta_dbs.DbJson;
import org.jview.jtool.ta_dbs.DbMyInsert;
import org.jview.jtool.ta_dbs.DbMySelect;
import org.jview.jtool.ta_dbs.DbMyUpdate;
import org.jview.jtool.ta_dbs.DbSelect;
import org.jview.jtool.ta_dbs.DbShow;
import org.jview.jtool.tools.DBTool;
import org.jview.jtool.util.CommMethod;

public class testDbsClass {
	@Test
	public void testDbShow_fail(){
		ITask dir = new DbShow();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool("localhost", "test", "test", "5432");
		List<String> list=dir.doExecute("");
		CommMethod.print(list);
		TestCase.assertTrue("msg count=1", list.size()==1);
	
	}
	
	@Test
	public void testDbShow_config(){
		ITask dir = new DbShow();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("");
		CommMethod.print(list);
		TestCase.assertTrue("table count>2", list.size()>2);
	
	}
	
	@Test
	public void testData_config(){
		ITask dir = new DbData();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("tg_role");
		CommMethod.print(list);
		TestCase.assertTrue("tg_role data count>2", list.size()>2);
	
	}
	
	@Test
	public void testData2_config(){
		ITask dir = new DbData();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("select * from tg_role");
		CommMethod.print(list);
		TestCase.assertTrue("tg_role data count>2", list.size()>2);
	
	}
	
	@Test
	public void testFData_config(){
		ITask dir = new DbFData();
		TaskManager dbInfo = new TaskManager();
		String[] args={"para", "tools", "dir", "."};
		dbInfo.main(args);
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("tg_role");
		CommMethod.print(list);
		TestCase.assertTrue("tg_role fdata count>2", list.size()>2);
	
	}
	
	@Test
	public void testFData2_config(){
		ITask dir = new DbFData();
		TaskManager dbInfo = new TaskManager();
		String[] args={"para", "tools", "dir", "."};
		dbInfo.main(args);
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("select * from tg_role");
		CommMethod.print(list);
		TestCase.assertTrue("tg_role fdata count>2", list.size()>2);
	
	}
	
	@Test
	public void testSelect_config(){
		ITask dir = new DbSelect();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("tg_role");
		CommMethod.print(list);
		TestCase.assertTrue("startWidth select", list.get(0).startsWith("select"));
	
	}
	
	@Test
	public void testInsert_config(){
		ITask dir = new DbInsert();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("tg_role");
		CommMethod.print(list);
		TestCase.assertTrue("tg_role count>2", list.size()>2);
	
	}

	
	@Test
	public void testMyInsert_config(){
		ITask dir = new DbMyInsert();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("tg_role");
		CommMethod.print(list);
//		TestCase.assertTrue("tg_role count>2", list.size()>2);
	
	}
	
	@Test
	public void testMyUpdate_config(){
		ITask dir = new DbMyUpdate();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("tg_role&ID");
		CommMethod.print(list);
//		TestCase.assertTrue("tg_role count>2", list.size()>2);
	
	}
	
	@Test
	public void testMySelect_config(){
		ITask dir = new DbMySelect();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("tg_role");
		CommMethod.print(list);
//		TestCase.assertTrue("tg_role count>2", list.size()>2);
	
	}
	
	
	@Test
	public void testDesc_config(){
		ITask dir = new DbDesc();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("tg_role");
		CommMethod.print(list);
		TestCase.assertTrue("create table", list.get(0).indexOf("create table")>0);
		
	
	}
	
	@Test
	public void testEnum_config(){
		ITask dir = new DbEnum();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("select code, name from ts_sysconfig");
		CommMethod.print(list);
		TestCase.assertTrue("create enum code", list.get(0).indexOf("(")>0);
		
	
	}
	
	@Test
	public void testJson_config(){
		ITask dir = new DbJson();
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		List<String> list=dir.doExecute("select code, name from ts_sysconfig");
		CommMethod.print(list);
		TestCase.assertTrue("create json code size>2", list.size()>2);
		
	
	}
	
	@Test
	public void testConstClass(){
		TaskManager dbInfo = new TaskManager();
		dbInfo.dbTool = new DBTool();
		ITask tool = new DbConstClass();
		List<String> list= tool.doExecute("test.gen select code, value from ts_parameter");
		CommMethod.print(list);
	}
}
