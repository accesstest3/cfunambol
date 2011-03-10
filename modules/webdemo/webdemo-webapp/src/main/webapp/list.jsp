<%-- $Id: list.jsp,v 1.6 2007-03-01 14:44:57 luigiafassina Exp $ --%>

<%@taglib prefix="c" uri="c_uri" %>
<%@taglib prefix="fmt" uri="fmt_uri" %>

<%@page import="javax.servlet.jsp.jstl.core.Config" %>

<fmt:setBundle basename="contact"/>

<SCRIPT language="JavaScript1.2">
function showContact(id) {
  window.location.href = 'main.jsp?main=main&operation=VIEW&type=contact&item=' + id;
}
</script>

<p>&nbsp;<p>
<TABLE cellspacing="0" cellpadding="4" width="80%" align="center">
  <TR class="teta">
      <TD class="teta">
          <FONT class="teta"><b><fmt:message key="msg.title"/></b></FONT>
      </TD>
  </TR>
  <TR><TD>&nbsp;</TD></TR>

  <TR class="teta">
      <TD class="teta">
          <FONT class="teta"><fmt:message key="msg.subtitle"><fmt:param value="main.jsp?main=main&operation=ADD&type=contact"/></fmt:message> </FONT>
      </TD>
  </TR>
  <TR class="teta"><TD class="teta" colspan="2">&nbsp;</TD></TR>

  <c:if test="${numItems != 0}">
   <TR class="teta"><TD class="teta">

   <SELECT name="files" size="20" style="width:100%"
           onclick="javascript:showContact(this.options[this.selectedIndex].value)">
    <c:forEach items="${items}" var="item">
     <option value="<c:out value="${item.id}"/>">
      <c:out value="${item.displayname}"/>
     </option>
    </c:forEach>
   </SELECT>

   </TD></TR>
  </c:if>

</TABLE>
