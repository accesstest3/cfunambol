<%-- $Id: listca.jsp,v 1.5 2007-03-01 15:11:43 luigiafassina Exp $ --%>

<%@taglib prefix="c" uri="c_uri" %>
<%@taglib prefix="fmt" uri="fmt_uri" %>

<%@page import="javax.servlet.jsp.jstl.core.Config" %>

<fmt:setBundle basename="calendar"/>
<SCRIPT language="JavaScript1.2">
function showCalendar(id) {
  window.location.href = 'main.jsp?main=main&operation=VIEW&type=event&item=' + id;
}
</script>

<p>&nbsp;<p>
<TABLE cellspacing="0" cellpadding="4" width="80%" align="center">
  <TR class="teta">
      <TD class="teta">
          <FONT class="teta"><b><fmt:message key="msg.titleCal"/></b></FONT>
      </TD>
  </TR>
  <TR><TD>&nbsp;</TD></TR>

  <TR class="teta">
      <TD class="teta">
          <FONT class="teta"><fmt:message key="msg.subtitleCal"><fmt:param value="main.jsp?main=main&operation=ADD&type=event"/></fmt:message></FONT>
      </TD>
  </TR>
  <TR class="teta"><TD class="teta" colspan="2">&nbsp;</TD></TR>

  <c:if test="${numItems != 0}">
   <TR class="teta"><TD class="teta">

   <SELECT name="files" size="20" style="width:100%"
           onclick="javascript:showCalendar(this.options[this.selectedIndex].value)">
    <c:forEach items="${items}" var="item">
     <option value="<c:out value="${item.id}"/>">

      <c:choose>
        <c:when test="${empty item.summary}">
          <c:out value="${item.desc}"/>
        </c:when>
        <c:otherwise>
          <c:out value="${item.summary}"/>
        </c:otherwise>
      </c:choose>

     </option>
    </c:forEach>
   </SELECT>
   </TD></TR>
  </c:if>

</TABLE>
