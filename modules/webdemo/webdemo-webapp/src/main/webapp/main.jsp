<%-- $Id: main.jsp,v 1.8 2007-11-20 16:22:46 nichele Exp $ --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html; charset=UTF-8" %>

<% 
   String main = request.getParameter("main"); 
   if (main == null || main.equals("")) {
%>
       <jsp:forward page='index.html'/>
<%
   }
%>

<html>
<head>
  <title>Web Demo Client</title>
  <link rel="stylesheet" type="text/css" href="/css/font-classes.css" />
  <link rel="stylesheet" type="text/css" href="css/standard.css">
  
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  
</head>

<body>

<table border="0" cellspacing="0" cellpadding="0">
<tbody>
<tr>
<td valign="top" width="165" height="400">
  <jsp:include page="menu.jsp"/>
</td>
<td valign="top" width="50"><br></td>
<td valign="top" align="center" width="450">
<div align="center"><a href="http://www.funambol.org" border="0"><img src="imgs/funambol.png" alt="Funambol" border="0"></a><br>
<p>

<div align="left">
<jsp:include page="<%= main %>"/>
</div>
</td>
</tr>
</tbody>
</table>
</body>
</html>
