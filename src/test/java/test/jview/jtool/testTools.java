package test.jview.jtool;

import org.junit.Test;
import org.jview.jtool.manager.TaskManager;

public class testTools {
	
	@Test
	public void testDir(){
		String[] args={"para", "tools", "dir"};
		TaskManager.main(args);
	}
	@Test
	public void testHelp(){
		String[] args={"para", "tools", "help"};
		TaskManager.main(args);
	}
	@Test
	public void testDirAll(){
		String[] args={"para", "tools", "dir", "*"};
		TaskManager.main(args);
	}
	
	@Test
	public void testDirAll_time(){
		String[] args={"para", "tools", "dir", "-time", "*"};
		TaskManager.main(args);
	}
	
	@Test
	public void testDirCurrent(){
		String[] args={"para", "tools", "dir", "."};
		TaskManager.main(args);
	}
	
	
	@Test
	public void testDbShow(){
//		DbShow show=new DbShow();
//		show.doExecute("show");
		String[] args={"para", "dbs", "show"};
		TaskManager.main(args);
	}
	
	@Test
	public void testFileModifyTime_time(){
		String[] args={"para", "tools", "fileModifyTime", "bin/temp.txt", "-time", "01:00:00"};
		TaskManager.main(args);
	}
	
	@Test
	public void testFileModifyTime_date(){
		String[] args={"para", "tools", "fileModifyTime", "bin/temp.txt", "-date", "2011-01-01"};
		TaskManager.main(args);
	}
	
	@Test
	public void testFileModifyTime_datetime(){
		String[] args={"para", "tools", "fileModifyTime", "bin/temp.txt", "-datetime", "2011-01-02 02:02:02"};
		TaskManager.main(args);
	}

	@Test
	public void testCd(){
//		String arg="para tools cd";
//		String[] args=arg.split(" ");
		String[] args={"para", "tools", "cd"};
		TaskManager.main(args);
	}
	
	@Test
	public void testCd_bin(){
//		String arg="para tools cd";
//		String[] args=arg.split(" ");
		String[] args={"para", "tools", "cd", "bin"};
		TaskManager.main(args);
	}
	
	@Test
	public void testMd5(){
		String[] args={"para", "tools", "md5", "123456"};
		TaskManager.main(args);
	}
	
	@Test
	public void testUpper(){
		String[] args={"para", "tools", "upper", "abcd1S"};
		TaskManager.main(args);
	}
	
	@Test
	public void testLower(){
		String[] args={"para", "tools", "lower", "abcd1S"};
		TaskManager.main(args);
	}
}
