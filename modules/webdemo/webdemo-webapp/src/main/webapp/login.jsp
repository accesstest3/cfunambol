<%-- $Id: login.jsp,v 1.6 2007-11-19 13:40:57 nichele Exp $ --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html; charset=UTF-8" %>

<%@taglib prefix="c" uri="c_uri" %>
<%@taglib prefix="fmt" uri="fmt_uri" %>

<fmt:setBundle basename="login"/>

<html>
<head>
  <title><fmt:message key="msg.title"/></title>
  <link rel="stylesheet" type="text/css" href="/css/font-classes.css" />
  <link rel="stylesheet" type="text/css" href="css/standard.css">

  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta http-equiv="cache-control" content="no-cache">
  <meta http-equiv="pragma" content="no-cache">

</head>

<body>
<form name="login" method="POST"
      action="main.jsp?main=main&type=contact" accept-charset="utf-8">

<table cellspacing="0" cellpadding="0" width="100%">
  <tbody>

   <tr>
<td valign="top" align="center">
<div align="center"><a href="http://www.funambol.org" border="0"><img src="imgs/funambol.png" alt="Funambol" border="0"></a><br>
<p>

    <br>
    <TABLE cellspacing="0" cellpadding="8" align="center">

      <c:if test="${not(empty error_noauth)}">
        <TR class="teta">
         <TD class="teta" colspan="2" align="center">
          <FONT class="teta"><b><fmt:message key="msg.noauth"/></b></FONT>
         </TD>
        </TR>
      </c:if>

       <TR class="teta">
        <TD class="teta" width="30%"><FONT class="teta"><fmt:message key="msg.username"/></FONT></TD>
        <TD class="teta" width="70%"><input type="text" name="username" value="" style="width:100%"></TD>
       </TR>

       <TR class="teta">
        <TD class="teta" width="30%"><FONT class="teta"><fmt:message key="msg.password"/></FONT></TD>
        <TD class="teta" width="70%"><input type="password" name="password" value="" style="width:100%"></TD>
       </TR>

       <TR class="teta">
        <TD class="teta" colspan="2" align="center" valign="bottom">
         <input type="submit" value="<fmt:message key="lbl.login"/>">
        </TD>
       </TR>
       
       <TR class="teta">
        <TD class="teta" colspan="2" align="center" nowrap="nowrap">
         <FONT class="darkgrey"><B><fmt:message key="msg.note"/></B> <fmt:message key="msg.notetextup"/></BR></FONT>
         <FONT class="darkgrey"><fmt:message key="msg.notetextdown"/></FONT>
        </TD>
       </TR>

    </TABLE>
   </tr>
  </tbody>
 </table>
</form>
</body>
</html>
