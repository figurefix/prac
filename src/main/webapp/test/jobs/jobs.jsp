<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://taglib.prac.figurefix" prefix="ff" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>test jobs</title>
<%
	String ctxpath = request.getContextPath();
%>
<ff:import src="/web/css/prac.css"/>
<style type="text/css">
input,button {
	display: block;
	margin:10px;
	font-size: 1.5em;
}
</style>
<script type="text/javascript">

</script>
</head>
<body>

<%
	String msg = "";
	Object msgobj = request.getAttribute("message");
	if(msgobj!=null) {
		msg = msgobj.toString();
	}
%>
<%=msg%>

<form action="<%=ctxpath%>/MyTestJob.do" method="post" accept-charset="UTF-8" >
	<input type="submit" name="action" value="connect database" />
	<input type="submit" name="action" value="make sql error" />
	<input type="submit" name="action" value="write file log" />
</form>
<form action="<%=ctxpath%>/DispatchBgJob.do" method="post" accept-charset="UTF-8" >
	<input type="submit" name="action" value="dispatch background job" />
	<input type="submit" name="action" value="dispatch defered background job" />
</form>
<form action="<%=ctxpath%>/CountJob.do" method="post" accept-charset="UTF-8" >
	<input type="submit" name="action" value="count jobs" />
	<input type="submit" name="action" value="count bg jobs" />
</form>
</body>
</html>
