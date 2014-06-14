<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.jview.jtool.manager.TaskManager" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript">
	function resetInput(){
		document.getElementById('paras').focus();
	}
</script>
</head>
<body onload="resetInput();">

<%
String[] args = new String[0];

TaskManager manager = null;
Object obj = session.getAttribute("taskManager");
if(obj==null){
	manager = new TaskManager();
	manager.initTask();
	session.setAttribute("taskManager", manager);
}
else{
	manager = (TaskManager)obj;
}
String paras = request.getParameter("paras");
String types = request.getParameter("types");
System.out.println("types="+types);
List<String> dataList = new ArrayList<String>();
if(paras!=null && paras.length()!=0){
	if(types==null){
		types="tools";
	}
	paras = paras.trim();
	if(types.startsWith("dbs")){
		dataList = manager.cmdDbsOper(paras);
	}
	else if(types.startsWith("tools")){
		dataList = manager.cmdToolOper(paras);
	}
	else{		
		dataList.add("Not start by dbs/tools");
	}
}
else{
	paras="";
	if(types==null){
		types="tools";
	}
}

%>
<form action="index.jsp">
	<select name="types">
		<option value="dbs" <%if(types.equals("dbs")){ %>selected<%} %>>dbs</option>
		<option value="tools" <%if(types.equals("tools")){ %>selected<%} %>>tools</option>
	</select>
	<input type="text" id="paras" name="paras" value="<%=paras%>" size="40">
	<input type="submit"  value="ok">
	
</form>
<textarea rows="20" cols="100">
<%
for(String info:dataList){
	out.print(info+"\n");
} 
%>
</textarea>
</body>
</html>