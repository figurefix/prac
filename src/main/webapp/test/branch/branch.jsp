<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="figurefix.prac.test.branch.Node" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib uri="http://taglib.prac.figurefix" prefix="ff" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>test branch edit</title>
<ff:import src="/web/js/branch.js"/>
<script type="text/javascript">
function addnode() {
	if(document.getElementById("pnodex").value!='') {
		return;
	}
	document.getElementById("op").value = "add";
	var name = document.getElementById("nodename").value;
	if(name=="") {
		alert("input node name");
		return;
	}
	document.forms[0].submit();
}
function delnode() {
	if(document.getElementById("pnodex").value!='') {
		return;
	}
	document.getElementById("op").value = "del";
	if(document.getElementById("pnode").value=="") {
		alert("choose node");
		return;
	}
	document.getElementById("op").value = "del";
	document.forms[0].submit();
}
function movnode() {
	if(document.getElementById("pnodex").value!='') {
		return;
	}
	if(current==null) {
		alert("choose node");
		return;
	}
	current.remove();
	document.getElementById("pnodex").value = document.getElementById("pnode").value;
	document.getElementById("pnode").value = "";
	document.getElementById("movediv").style.display = "block";
}
function moveroot() {
	document.getElementById("op").value = "mov";
	document.getElementById("pnode").value = "";
	document.forms[0].submit();
}
function refresh() {
	document.getElementById("op").value = "";
	document.forms[0].submit();
}
</script>
</head>
<body>

<form method="post" action="<%=request.getContextPath()%>/BranchEdit.do">
<input type="hidden" id="op" name="op" value=""/>
<input type="hidden" id="pnode" name="pnode" value=""/>
<input type="hidden" id="pnodex" name="pnodex" value=""/>
<table>
<tr>
	<td>
		<input type="text" id="nodename" name="nodename" value=""/>
		<button type="button" onclick="addnode()">add node</button>	
	</td>
	<td>
		<button type="button" onclick="delnode()">del node</button>	
	</td>
	<td>
		<button type="button" onclick="movnode()">mov node</button>
	</td>
	<td>
		<div id="movediv" style="display:none;">
		choose target location 
		<button type="button" onclick="moveroot()">set as root</button> /
		<button type="button" onclick="refresh()">cancel</button>
		</div>
	</td>
</tr>
</table>

</form>
<div id="bchdiv"></div>
</body>
</html>
<script type="text/javascript">
var current = null;
function clkbch(xbch, data) {
	current = xbch;
	document.getElementById("pnode").value = data;
	if(document.getElementById("pnodex").value!='') {
		document.getElementById("op").value = "mov";
		document.forms[0].submit();
	}
}
var bch = new prac.Branch("bchdiv", clkbch);
<%
Node root = (Node)(request.getAttribute("root"));
ArrayList<Node> child = root!=null ? root.child : null;
StringBuffer js = new StringBuffer();
if(child!=null) {
	js.append("bch");
	for(Node sub : child) {
		sub.tojs(js);
	}
	js.append(";");
}
%>
<%=js.toString()%>
bch.expandAll();
</script>
