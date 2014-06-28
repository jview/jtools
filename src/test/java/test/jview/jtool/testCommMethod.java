package test.jview.jtool;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.jview.jtool.util.CommMethod;

public class testCommMethod {
	private static List<String> lineList=new ArrayList<String>(); 
	static {
		lineList.add("this is a test");
		lineList.add("myTest");
		lineList.add("hello jview");
	}
	@Test
	public void testFilterUnexist() {
		List<String> rList=CommMethod.filterContentUnexist(lineList, "test");
		CommMethod.print(rList);
		
	}
}
