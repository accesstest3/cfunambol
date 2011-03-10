<%-- $Id: add.jsp,v 1.9 2007-10-19 13:49:55 nichele Exp $ --%>

<%@taglib prefix="c" uri="c_uri" %>
<%@taglib prefix="fmt" uri="fmt_uri" %>

<%@page import="javax.servlet.jsp.jstl.core.Config" %>

<fmt:setBundle basename="contact"/>

<script src ="<%= request.getContextPath() %>/js/util.js"
        type="text/javascript"></script>

<SCRIPT language="JavaScript1.2">
function setOperation(operation) {

 if(operation=="1") {
  document.info.operation.value = "RETURN";
 } else if(operation=="2") {
 
  // First name or Last name must be set
  name    = document.info.name.value;
  surname = document.info.surname.value;
  if (name.length == 0 && surname.length == 0) {
     alert('<fmt:message key="msg.errorcontact"/>');
     return false;
  }
  emailAdr = document.info.email.value;
  if (emailAdr.length != 0) {
      if(!checkEmailAdr(emailAdr)) {

          msg = '<fmt:message key="msg.erroremail"/>\n'
              + '<fmt:message key="msg.continue"/>  ';
          if (confirm(msg)) {
          } else{
            return false;
          };
      }
  }
  
  document.info.operation.value = "INSERT";
 }

 document.info.submit();
}

</SCRIPT>

<p>&nbsp;<p>

<form name="info" method="POST" action="main.jsp" accept-charset="utf-8">
 <TABLE cellspacing="0" cellpadding="4" width="360" align="center">

   <TR class="teta">
       <TD class="teta" colspan="2">
           <FONT class="teta"><b><fmt:message key="msg.details"/></b></FONT>
       </TD>
   </TR>
   <TR><TD colspan="2">&nbsp;</TD></TR>

   <TR class="teta">
    <TD width="100" class="teta" >
        <FONT class="teta"><fmt:message key="msg.name"/></FONT>
    </TD>
    <TD class="teta"><input style="width:100%" type="text" name="name" value=""></TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.surname"/></FONT>
    </TD>
    <TD class="teta"><input style="width:100%" type="text" name="surname" value=""></TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.fileas"/></FONT>
    </TD>
    <TD class="teta"><input style="width:100%" type="text" name="fileas" value=""></TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.email"/></FONT>
    </TD>
    <TD class="teta"><input style="width:100%" type="text" name="email" value=""></TD>
   </TR>
   
   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.generaltel"/></FONT>
    </TD>
    <TD class="teta"><input style="width:100%" type="text" name="generaltel" value=""></TD>
   </TR>
   
   <TR class="teta">
    <TD class="teta"><FONT class="teta"><fmt:message key="msg.org"/></FONT></TD>
    <TD class="teta"><input style="width:100%" type="text" name="org" value=""></TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.jobtitle"/></FONT>
    </TD>
    <TD class="teta"><input style="width:100%" type="text" name="title" value=""></TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.hometel"/></FONT>
    </TD>
    <TD class="teta"><input style="width:100%" type="text" name="hometel" value=""></TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.businesstel"/></FONT>
    </TD>
    <TD class="teta"><input style="width:100%" type="text" name="businesstel" value=""></TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.cell"/></FONT>
    </TD>
    <TD class="teta"><input style="width:100%" type="text" name="cell" value=""></TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.fax"/></FONT>
    </TD>
    <TD class="teta"><input style="width:100%" type="text" name="fax" value=""></TD>
   </TR>

   <TR class="teta"><TD class="teta" colspan="2">&nbsp;</TD></TR>

   <TR class="teta">
    <TD class="teta" colspan="2" align="center">
     <input type="button" onClick="setOperation('1')"
            value="<fmt:message key="lbl.back"/>">
     &nbsp;&nbsp;
     <input type="button" onClick="setOperation('2')"
            value="<fmt:message key="lbl.add"/>">
    </TD>
   </TR>
   
 </TABLE>

 <input type="hidden" name="operation">
 <input type="hidden" name="main" value="main">
 <input type="hidden" name="type" value="<c:out value="${param.type}"/>">

</form>
