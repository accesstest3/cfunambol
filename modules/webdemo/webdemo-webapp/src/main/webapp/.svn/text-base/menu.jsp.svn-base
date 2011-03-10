<!-- $Id: menu.jsp,v 1.6 2007-11-29 10:53:45 nichele Exp $ -->

<%@page import="com.funambol.framework.server.Sync4jUser"%>

<font class="text">
<table border="0" cellspacing="0" cellpadding="0" class="menu-table">
  <tbody>
    <tr>
      <td valign="top" width="165" height="400">
        <p>
        <table cellpadding="0" cellspacing="0" border="0" width="165" class="menu-table">
          <tbody>
            <tr height="4">
              <td align="right" colspan="1" bgcolor="#003399" height="4"><img src="imgs/blank.gif" width="165" height="4" alt=""></td>
            </tr>
            <tr>
              <td colspan="1" bgcolor="#dddddd" align="centre"><b>Web Demo Client</td>
            </tr>
            <tr>
              <td colspan="1" align="left" bgcolor="#eeeeee">&nbsp;</td>
            </tr>
            <tr>
              <td colspan="1" align="left" bgcolor="#eeeeee">
                <b>&nbsp;&nbsp;Welcome,
                <%= ((Sync4jUser)session.getAttribute("user")).getUsername() %>
                </b>
              </td>
            </tr>
            <tr>
              <td colspan="1" align="left" bgcolor="#eeeeee">
                &nbsp;&middot;<a href="login.jsp?logout=true">Logout</a><br>
              </td>
            </tr>
            <tr>
              <td colspan="1" align="left" bgcolor="#eeeeee">&nbsp;<hr/></td>
            </tr>
            <tr>
              <td colspan="1" align="left" bgcolor="#eeeeee">
                &nbsp;&middot;<a href="main.jsp?main=main&type=contact">Contacts</a><br>
                &nbsp;&middot;<a href="main.jsp?main=main&type=contact&operation=reset">Reset Contact demo</a><br>
              </td>
            </tr>
            <tr>
              <td colspan="1" align="left" bgcolor="#eeeeee">&nbsp;<hr/></td>
            </tr>
            <tr>
              <td colspan="1" align="left" bgcolor="#eeeeee">
                &nbsp;&middot;<a href="main.jsp?main=main&type=event">Calendars</a><br>
                &nbsp;&middot;<a href="main.jsp?main=main&type=event&operation=reset">Reset Calendar demo</a><br>
              </td>
            </tr>
            <tr>
              <td colspan="1" align="left" bgcolor="#eeeeee">&nbsp;<hr/></td>
            </tr>
            <tr>
              <td colspan="1" align="left" bgcolor="#eeeeee">
                &nbsp;&middot;<a href="termsandconditions.txt">Terms &amp; Conditions</a><br>
              </td>
            </tr>
            <tr>
              <td colspan="1" align="left" bgcolor="#eeeeee">&nbsp;</td>
            </tr>
          </tbody>
        </table>
      </td>
    </tr>
  </tbody>
</table>
</font>
