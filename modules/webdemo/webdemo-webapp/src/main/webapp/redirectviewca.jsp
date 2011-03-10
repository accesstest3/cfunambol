<%-- $Id: redirectviewca.jsp,v 1.3 2007-03-01 15:13:11 luigiafassina Exp $ --%>

<%
    String itemId = request.getParameter("item");
%>
<script language="javascript">
    window.location.href = "main.jsp?main=main&operation=VIEW&db&type=event&item=<%= itemId%>";
</script>
