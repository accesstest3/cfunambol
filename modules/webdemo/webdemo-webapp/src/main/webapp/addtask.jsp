<%-- $Id: addtask.jsp,v 1.5 2007-10-26 08:12:09 luigiafassina Exp $ --%>

<%@taglib prefix="c" uri="c_uri" %>
<%@taglib prefix="fmt" uri="fmt_uri" %>

<%@page import="javax.servlet.jsp.jstl.core.Config" %>

<fmt:setBundle basename="calendar"/>

<script src="<%= request.getContextPath() %>/js/util.js"
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
         alert('<fmt:message key="msg.errorformatendTask"/>');
         return false;
     }

     if (yyyye == '0000') {
         alert('<fmt:message key="msg.errorzeroeTask"/>');
         return false;
     }

     if (!checkYearRange(yyyye)) {
         alert('<fmt:message key="msg.erroryeareTask"/>');
         return false;
     }
     
     if (dds.length != 0 || mms.length != 0 || yyyys.length != 0) {
         if (!checkdate(dds,mms,yyyys)) {
             alert('<fmt:message key="msg.errorstart"/>');
             return false;
         }

         datestart = dds + mms+ yyyys;
     }

     if (dde.length != 0 || mme.length != 0 || yyyye.length != 0) {
         if (!checkdate(dde,mme,yyyye)) {
             alert('<fmt:message key="msg.errorendTask"/>');
             return false;
         }
         dateend = dde + mme + yyyye;
     }

     //The end of the task must occur after the start
     if (datestart.length != 0 && dateend != 0) {
         if (!compareDateWithoutTime(yyyys, mms, dds, yyyye, mme, dde)) {
             alert('<fmt:message key="msg.errorcompareTask"/>');
             return false;
         }
     }

     //
     // Check for task calendar
     // 1) Percent must be a valid number between 0 and 100
     // 2) Date completed must be a valid date even if it is not mandatory
     //
     perccomp = document.info.percentcomplete.value;

     if (!isNumber(perccomp) || perccomp<0 || perccomp>100) {
         alert('<fmt:message key="msg.errorperccomp"/>');
         return false;
     }

     ddc   = document.info.ddcompleted.value;
     mmc   = document.info.mmcompleted.value;
     yyyyc = document.info.yyyycompleted.value;
     if (ddc.length != 0 || mmc.length != 0 || yyyyc.length != 0) {
         if (yyyyc.length != 4) {
             alert('<fmt:message key="msg.errorformatcompleted"/>');
             return false;
         }

         if (yyyyc == '0000') {
             alert('<fmt:message key="msg.errorzeroc"/>');
             return false;
         }

         if (!checkdate(ddc, mmc, yyyyc)) {
             alert('<fmt:message key="msg.errorcompleted"/>');
             return false;
         }
     }

     document.info.operation.value = "INSERT";
 }
 document.info.submit();
}

</SCRIPT>

<p>&nbsp;<p>

<form name="info" method="POST" action="main.jsp" accept-charset="utf-8">
 <TABLE cellspacing="0" cellpadding="4" width="80%" align="center">

   <TR class="teta">
    <TD class="teta" colspan="2">
     <FONT class="teta"><b><fmt:message key="msg.detailsTask"/></b></FONT>
    </TD>
   </TR>
   <TR><TD colspan="2">&nbsp;</TD></TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.summary"/>&nbsp;*</FONT>
    </TD>
    <TD class="teta"><input type="text" style="width:100%" name="summary" value=""></TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.dtstart"/>&nbsp;</FONT>
    </TD>
    <TD class="teta">
        <input type="text" maxlength="2" size="2" name="ddstart" value="">
        /
        <input type="text" maxlength="2" size="2" name="mmstart" value="">
        /
        <input type="text" maxlength="4" size="4" name="yyyystart" value="">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.dtendTask"/>&nbsp;</FONT>
    </TD>
    <TD class="teta">
        <input type="text" maxlength="2" size="2" name="ddend" value="">
        /
        <input type="text" maxlength="2" size="2" name="mmend" value="">
        /
        <input type="text" maxlength="4" size="4" name="yyyyend" value="">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.percentcomplete"/></FONT>
    </TD>
    <TD class="teta">
     <input type="text" style="width:100%" name="percentcomplete" value="">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.dtcompleted"/></FONT>
    </TD>
    <TD class="teta">
     <input type="text" maxlength="2" size="2" name="ddcompleted" value="">
     /
     <input type="text" maxlength="2" size="2" name="mmcompleted" value="">
     /
     <input type="text" maxlength="4" size="4" name="yyyycompleted" value="">
    </TD>
   </TR>

   <TR class="teta">
    <TD class="teta">
        <FONT class="teta"><fmt:message key="msg.desc"/></FONT>
    </TD>
    <TD class="teta">
     <textarea name="desc" rows="5" class="teta" style="width:100%" WRAP=OFF></textarea>
    </TD>
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

   <TR class="teta">
    <TD class="teta" colspan="2">
        <FONT class="teta"><fmt:message key="msg.attention"/></FONT>
    </TD>
   </TR>

 </TABLE>

 <input type="hidden" name="operation">
 <input type="hidden" name="nextView">
 <input type="hidden" name="main" value="main">
 <input type="hidden" name="type" value="<c:out value="${param.type}"/>">

</form>
