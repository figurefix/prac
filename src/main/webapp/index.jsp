<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>test</title>
<%
	String ctxpath = request.getContextPath();
%>
<style type="text/css">
a {
	display:block;
	padding:8px;
	font-size:20pt;
}
a:hover {
	color:#fff;
	background-color:#069;
}
</style>
<script type="text/javascript">

</script>
</head>
<body>
<a href="<%=ctxpath%>/test/jobs/index.jsp">test jobs</a>
<a href="<%=ctxpath%>/test/report/index.jsp">test report</a>
<a href="<%=ctxpath%>/test/taglib/index.jsp">test taglib</a>
<a href="<%=ctxpath%>/BranchEdit.do">test branch</a>
</body>
</html>
