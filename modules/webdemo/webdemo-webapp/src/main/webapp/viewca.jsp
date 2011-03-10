<%-- $Id: viewca.jsp,v 1.18 2007-11-20 17:35:25 luigiafassina Exp $ --%>

<%@taglib prefix="c" uri="c_uri" %>
<%@taglib prefix="fmt" uri="fmt_uri" %>

<%@page import="javax.servlet.jsp.jstl.core.Config" %>

<fmt:setBundle basename="calendar"/>

<script src ="<%= request.getContextPath() %>/js/util.js"
        type="text/javascript"></script>

<SCRIPT language="JavaScript1.2">
function setOperation(operation) {

 if(operation=="1") {
     document.info.operation.value = "RETURN";
 } else if(operation=="2") {

     datestart = 0;
     dateend   = 0;

     // Start Date
     dds   = document.info.ddstart.value;
     mms   = document.info.mmstart.value;
     yyyys = document.info.yyyystart.value;

     // End Date
     dde   = document.info.ddend.value;
     mme   = document.info.mmend.value;
     yyyye = document.info.yyyyend.value;

     // Summary is mandatory
     summarycal = document.info.summary.value;

     if (summarycal.length == 0) {
         alert('<fmt:message key="msg.errorsummary"/>');
         return false;
     }

     //
     // Check for event calendar
     // 1) Start date is mandatory
     // 2) End date is mandatory
     //
     if (dds.length == 0 || mms.length == 0 || yyyys.length == 0) {
         alert('<fmt:message key="msg.erroremptystart"/>');
         return false;
     }

     if (dde.length == 0 || mme.length == 0 || yyyye.length == 0) {
         alert('<fmt:message key="msg.erroremptyend"/>');
         return false;
     }

     if (yyyys.length > 0 && yyyys.length != 4) {
         alert('<fmt:message key="msg.errorformatstart"/>');
         return false;
     }

     if (yyyys == '0000') {
         alert('<fmt:message key="msg.errorzeros"/>');
         return false;
     }
     
     if (!checkYearRange(yyyys)) {
         alert('<fmt:message key="msg.erroryears"/>');
         return false;
     }

     if (yyyye.length > 0 && yyyye.length != 4) {
         alert('<fmt:message key="msg.errorformatendCal"/>');
         return false;
     }

     if (yyyye == '0000') {
         alert('<fmt:message key="msg.errorzeroeCal"/>');
         return false;
     }

     if (!checkYearRange(yyyye)) {
         alert('<fmt:message key="msg.erroryeareCal"/>');
         return false;
     }
     
     if (dds.length != 0 || mms.length != 0 || yyyys.length != 0) {
         if (!checkdate(dds,mms,yyyys)) {
             alert('<fmt:message key="msg.errorstart"/>');
             return false;
         }

         datestart = dds + mms+ yyyys;
     }

     hhs  = document.info.houstart.value;
     mins = document.info.minstart.value;
     if (hhs.length != 0 || mins.length != 0) {
         if (!checkTime(hhs, mins)) {
             alert('<fmt:message key="msg.errorstarttime"/>');
             return false;
         }
     } else {
         hhs  = 0;
         mins = 0;
     }

     if (dde.length != 0 || mme.length != 0 || yyyye.length != 0) {
         if (!checkdate(dde,mme,yyyye)) {
             alert('<fmt:message key="msg.errorendCal"/>');
             return false;
         }
         dateend = dde + mme + yyyye;
     }

     hhe  = document.info.houend.value;
     mine = document.info.minend.value;
     if (hhe.length != 0 || mine.length != 0) {
         if (!checkTime(hhe, mine)) {
             alert('<fmt:message key="msg.errorendtime"/>');
             return false;
         }
     } else {
         hhe  = 0;
         mine = 0;
     }

     //The end of the event must occur after the start
     if (datestart.length != 0 && dateend != 0) {
         if (!compareDate(yyyys, mms, dds, hhs, mins, yyyye, mme, dde, hhe, mine)) {
             alert('<fmt:message key="msg.errorcompareCal"/>');
             return false;
         }
     }

     document.info.operation.value = "UPDATE";
 }

 if (operation=="3") {
  if (!confirm("<fmt:message key="msg.confirmDeleteCal"/>")) {
   return;
  }

  document.info.operation.value = "DELETE";
 }

 document.info.submit();
}

function handleAllDayEventCheckbox() {

 if (document.info.allday.checked) {
     document.info.houstart.disabled = true;
     document.info.minstart.disabled = true;
     document.info.houstart.value = "";
     document.info.minstart.value = "";

     document.info.houend.disabled = true;
     document.info.minend.disabled = true;
     document.info.houend.value = "";
     document.info.minend.value = "";

 } else {
     document.info.houstart.disabled = false;
     document.info.minstart.disabled = false;
     document.info.houstart.value = "<c:out value="${item.houstart}"/>";
     document.info.minstart.value = "<c:out value="${item.minstart}"/>";

     document.info.houend.disabled = false;
     document.info.minend.disabled = false;
     document.info.houend.value = "<c:out value="${item.houend}"/>";
     document.info.minend.value = "<c:out value="${item.minend}"/>";
 }
}

</SCRIPT>

<p>&nbsp;<p>
<form name="info" method="POST" action="main.jsp" accept-charset="utf-8">
 <input type="hidden" name="operation">
 <input type="hidden" name="main" value="main">
 <input type="hidden" name="item" value="<c:out value="${item.id}"/>">
 <input type="hidden" name="type" value="<c:out value="${param.type}"/>">

 <c:if test="${not(empty error_item)}">
  <TABLE cellspacing="0" cellpadding="4" width="80%" align="center">
   <TR class="teta">
    <TD class="teta" colspan="2" align="center">
     <FONT class="teta"><b><fmt:message key="msg.notfoundCal"/></b></FONT>
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

 <TABLE cellspacing="0" cellpadding="4" width="80%" align="center">
   <TR class="teta">
    <TD class="teta" colspan="2">
     <FONT class="teta"><b><fmt:message key="msg.detailsCal"/></b></FONT>
    </TD>
   </TR>
   <TR><TD colspan="2">&nbsp;</TD></TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.summary"/>&nbsp;*</FONT>
    </TD>
    <TD class="teta">
        <input type="text" style="width:100%" name="summary"
               value="<c:out value="${item.summary}"/>">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.dtstart"/>&nbsp;*</FONT>
    </TD>
    <TD class="teta">
        <input type="text" maxlength="2" size="2" name="ddstart"
               value="<c:out value="${item.ddstart}"/>">
        /
        <input type="text" maxlength="2" size="2" name="mmstart"
               value="<c:out value="${item.mmstart}"/>">
        /
        <input type="text" maxlength="4" size="4" name="yyyystart"
               value="<c:out value="${item.yyyystart}"/>">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.timestart"/></FONT>
    </TD>
    <TD class="teta">
        <c:choose>
            <c:when test="${item.allday}">
                <input type="text" maxlength="2" disabled size="2"
                       name="houstart" value=""/>
                :
                <input type="text" maxlength="2" disabled size="2"
                       name="minstart" value=""/>
            </c:when>
            <c:otherwise>
                <input type="text" maxlength="2" size="2" name="houstart"
                       value="<c:out value="${item.houstart}"/>">
                :
                <input type="text" maxlength="2" size="2" name="minstart"
                       value="<c:out value="${item.minstart}"/>">
            </c:otherwise>
        </c:choose>
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.dtendCal"/>&nbsp*</FONT>
    </TD>
    <TD class="teta">
        <input type="text" maxlength="2" size="2" name="ddend"
               value="<c:out value="${item.ddend}"/>">
        /
        <input type="text" maxlength="2" size="2" name="mmend"
               value="<c:out value="${item.mmend}"/>">
        /
        <input type="text" maxlength="4" size="4" name="yyyyend"
               value="<c:out value="${item.yyyyend}"/>">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.timeend"/></FONT>
    </TD>
    <TD class="teta">
        <c:choose>
            <c:when test="${item.allday}">
                <input type="text" maxlength="2" disabled size="2"
                       name="houend" value=""/>
                :
                <input type="text" maxlength="2" disabled size="2"
                       name="minend" value=""/>
            </c:when>
            <c:otherwise>
                <input type="text" maxlength="2" size="2" name="houend"
                       value="<c:out value="${item.houend}"/>">
                :
                <input type="text" maxlength="2" size="2" name="minend"
                       value="<c:out value="${item.minend}"/>">
            </c:otherwise>
        </c:choose>
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.alldayCal"/></FONT>
    </TD>
    <TD class="teta">
        <c:choose>
            <c:when test="${item.allday}">
                <input type="checkbox" name="allday"
                       onclick="handleAllDayEventCheckbox()" checked="checked"/>
            </c:when>
            <c:otherwise>
                <input type="checkbox" name="allday"
                       onclick="handleAllDayEventCheckbox()"/>
            </c:otherwise>
        </c:choose>
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.typeCal"/></FONT>
    </TD>
    <TD class="teta">
        <input type="text" style="width:100%" name="typeev"
               value="<c:out value="${item.typeev}"/>">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.location"/></FONT>
    </TD>
    <TD class="teta">
        <input type="text" style="width:100%" name="location"
               value="<c:out value="${item.location}"/>">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.desc"/></FONT>
    </TD>
    <TD class="teta">
     <textarea name="desc" rows="5" class="teta" style="width:100%" WRAP=OFF><c:out value="${item.desc}"/></textarea>
    </TD>
   </TR>

   <TR class="teta"><TD class="teta" colspan="2">&nbsp;</TD></TR>

   <TR class="teta">
    <TD class="teta" colspan="2" align="center">
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

   <TR class="teta">
    <TD class="teta" colspan="2">
        <FONT class="teta"><fmt:message key="msg.attention"/></FONT>
    </TD>
   </TR>

 </TABLE>
</form>