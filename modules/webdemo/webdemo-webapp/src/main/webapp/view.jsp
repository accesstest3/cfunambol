<%-- $Id: view.jsp,v 1.12 2007-11-16 11:16:02 nichele Exp $ --%>

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
 }
 if(operation=="2") {

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
          if (!confirm(msg)) {
            return false;
          };
      }
  }
  
   document.info.operation.value = "UPDATE";
 }

 if (operation=="3") {
  if (!confirm("<fmt:message key="msg.confirmDelete"/>")) {
   return false;
  }

  document.info.operation.value = "DELETE";
 }

 document.info.submit();
}

function resizePhoto() {
    photo = document.getElementById("photo");
    x = photo.width;
    y = photo.height;
    
    ratio = x/y;
    
    maxY = 53;
    
    photo.width = parseInt(ratio * maxY);
    photo.height = maxY;

}

</SCRIPT>

<p>&nbsp;<p>
<form name="info" method="POST" action="main.jsp" accept-charset="utf-8">

 <input type="hidden" name="operation">
 <input type="hidden" name="main" value="main">
 <input type="hidden" name="item" value="<c:out value="${item.id}"/>">

 <c:if test="${not(empty error_item)}">
  <TABLE cellspacing="0" cellpadding="4" width="360" align="center">
    <TR class="teta">
      <TD class="teta" colspan="2" align="center">
        <FONT class="teta">
          <b><fmt:message key="msg.notfounditem"/></b>
        </FONT>
      </TD>
    </TR>

    <TR class="teta"><TD class="teta" colspan="2">&nbsp;</TD></TR>

    <TR class="teta">
      <TD class="teta" colspan="2" align="center">
        <input type="button" onClick="setOperation('1')"
               value="<fmt:message key="lbl.back"/>">
      </TD>
    </TR>
  </TABLE>

  <%-- Don't remove or change this line because --%>
  <%-- without the if, the return does not work --%>
  <% if (1==1) return;%>

 </c:if>

 <TABLE cellspacing="0" cellpadding="4" width="360" align="center" border="0" bordercolor="red">

   <TR class="teta">
       <TD class="teta" colspan="4">
           <FONT class="teta"><b><fmt:message key="msg.details"/></b></FONT>
       </TD>
   </TR>

   <TR><TD colspan="4">&nbsp;</TD></TR>
   
    <c:choose>
        <c:when test="${item.hasphoto == 'Y'}">
        
            <TR class="teta">
                <TD width="100" class="teta">
                    <FONT class="teta"><fmt:message key="msg.name"/></FONT>
                </TD>
                <TD width="210" class="teta">
                    <input type="text" style="width:100%" name="name"
                           value="<c:out value="${item.name}"/>">
                </TD>
                <TD align="center" width="50" rowspan="2" class="teta">
                    <img align="absmiddle" onLoad="resizePhoto()" id="photo" margin="0" border="0" src="main/img?id=${item.id}" alt="<c:out value="${item.name}"/> <c:out value="${item.surname}"/>" />
                </TD>
                <TD width="0" rowspan="2"/>
            </TR>

            <TR class="teta">
                <TD class="teta">
                    <FONT class="teta"><fmt:message key="msg.surname"/></FONT>
                </TD>
                <TD class="teta">
                    <input type="text" style="width:100%" name="surname"
                           value="<c:out value="${item.surname}"/>">
                </TD>
           </TR>

        </c:when>
        <c:otherwise>
            <TR class="teta">
                <TD class="teta">
                    <FONT class="teta"><fmt:message key="msg.name"/></FONT>
                </TD>
                <TD class="teta" colspan="3">
                    <input type="text" style="width:100%" name="name"
                           value="<c:out value="${item.name}"/>">
                </TD>
            </TR>

            <TR class="teta">
                <TD class="teta">
                    <FONT class="teta"><fmt:message key="msg.surname"/></FONT>
                </TD>
                <TD class="teta" colspan="3">
                    <input type="text" style="width:100%" name="surname"
                           value="<c:out value="${item.surname}"/>">
                </TD>
           </TR>
        </c:otherwise>
    </c:choose>
    
   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.fileas"/></FONT>
    </TD>
    <TD class="teta" colspan="3">
        <input type="text" style="width:100%" name="fileas"
               value="<c:out value="${item.fileas}"/>">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.email"/></FONT>
    </TD>
    <TD class="teta" colspan="3">
        <input type="text" style="width:100%" name="email"
               value="<c:out value="${item.email}"/>">
    </TD>
   </TR>
   

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.generaltel"/></FONT>
    </TD>
    <TD class="teta" colspan="3">
        <input type="text" style="width:100%" name="generaltel"
               value="<c:out value="${item.generaltel}"/>" >
    </TD>
   </TR>
   
   <TR class="teta">
    <TD class="teta"><FONT class="teta"><fmt:message key="msg.org"/></FONT></TD>
    <TD class="teta" colspan="3">
        <input type="text" style="width:100%" name="org"
               value="<c:out value="${item.org}"/>">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.jobtitle"/></FONT>
    </TD>
    <TD class="teta" colspan="3">
        <input type="text" style="width:100%" name="title"
               value="<c:out value="${item.title}"/>" >
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.hometel"/></FONT>
    </TD>
    <TD class="teta" colspan="3">
        <input type="text" style="width:100%" name="hometel"
               value="<c:out value="${item.hometel}"/>" >
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.businesstel"/></FONT>
    </TD>
    <TD class="teta" colspan="3">
        <input type="text" style="width:100%" name="businesstel"
               value="<c:out value="${item.businesstel}"/>" >
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.cell"/></FONT>
    </TD>
    <TD class="teta" colspan="3">
        <input type="text" style="width:100%" name="cell"
               value="<c:out value="${item.cell}"/>" >
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.fax"/></FONT>
    </TD>
    <TD class="teta" colspan="3">
        <input type="text" style="width:100%" name="fax"
               value="<c:out value="${item.fax}"/>" >
    </TD>
   </TR>

   <TR class="teta"><TD class="teta" colspan="4">&nbsp;</TD></TR>

   <TR class="teta">
    <TD class="teta" colspan="4" align="center">
     <input type="button" onClick="setOperation('1')"
            value="<fmt:message key="lbl.back"/>">
     &nbsp;&nbsp;
     <input type="button" onClick="setOperation('2')"
            value="<fmt:message key="lbl.update"/>">
     &nbsp;&nbsp;
     <input type="button" onClick="setOperation('3')"
            value="<fmt:message key="lbl.delete"/>">
    </TD>
   </TR>

 </TABLE>

</form>