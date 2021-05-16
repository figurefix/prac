<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://taglib.prac.figurefix" prefix="ff" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>test</title>
<ff:import src="/web/css/prac.css"/>
<ff:import src="/web/js/formgrid.js"/>
<ff:import src="/web/js/datagrid.js"/>
<script type="text/javascript">
function aller() {
	document.forms[0].submit();
}
function fghref(line) {
	alert("line:"+line);
}
function addrow() {
	myformgrid.add();
}
function selchg(r) {
	alert("select in row "+r);
}
function dglink(sno, nam) {
	alert("sno="+sno+", nam="+nam);
}
</script>
</head>
<body>

<ff:message />

<ff:text name="normaltxt" value="默认值"/>

<ff:case>
	<ff:if exists="matchif" logic="and" within="aaxx">
		it's if
	</ff:if>
	 hehe 
	<ff:else>
		it's else
	</ff:else>
</ff:case>

<hr>

<form action="<%=request.getContextPath()%>/testTagLibJob.do" method="post" accept-charset="UTF-8">
	
<input type="submit" name="query" value="submit 提交"/>

<hr>

	<ff:input type="text" name="xx|gt" value="haha"></ff:input>
	<ff:input name="yy|gt" value="hehe" />
	
	<ff:input type="checkbox" name="myckb"></ff:input>
	<ff:input type="checkbox" name="myckb" value="cch2"></ff:input>
	
	<ff:input type="radio" name="myradio" value="radio1"></ff:input>
	<ff:input type="radio" name="myradio" value="radio2"></ff:input>
	<ff:input type="radio" name="myradio" value="radio3"></ff:input>

	<ff:if exists="showselect">
		<ff:select name="mysel.sel" impl="ff.prac.test.taglib.MySelection">
			<ff:option value="x">xxx</ff:option>
			<ff:option value="y">yyy</ff:option>
		</ff:select>
	</ff:if>

<hr>

<button type="button" onclick="addrow()">add row</button>

<br>

<ff:formgrid id="myformgrid">
	<ff:column type="text" name="mytxt" title="TEXT" align="right" wrap="true"></ff:column>
	<ff:column type="input" name="myipt" title="INPUT" maxlength="5" style="width:100px;"></ff:column>
	<ff:column type="hidden" name="myhid"></ff:column>
	<ff:column type="select" name="mysel1" title="选择1" options="a=aa,b=bb"></ff:column>
	<ff:column type="select" name="mysel2" title="选择2" onchange="selchg">
		<optgroup label="GROUP1">
			<option value="a">opt a</option>
			<option value="b">opt b</option>
		</optgroup>
		<optgroup label="GROUP2">
			<option value="c">opt c</option>
		</optgroup>
	</ff:column>
	<ff:column type="href" name="myhrf" title="LINK" href="fghref">
	</ff:column>
</ff:formgrid>
<hr>

<ff:if exists="mydatagrid">
	<input type="submit" name="action" value="download"/>
</ff:if>

</form>

<ff:if exists="mydatagrid">
	<marquee id="mymarquee" style="display:block; width:200px;"> datagrid loading </marquee>
</ff:if>
<ff:datagrid id="mydatagrid"></ff:datagrid>
<script type="text/javascript">
document.getElementById("mymarquee").style.display = "none";
</script>

<ff:error name="crash" />

</body>
</html>
