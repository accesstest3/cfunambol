/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */
package com.funambol.email.model;


import org.apache.commons.lang.builder.ToStringBuilder;
import com.funambol.pushlistener.service.registry.RegistryEntry;

/**
 *
 * @version $Id: MailServerAccount.java,v 1.1 2008-03-25 11:25:32 gbmiglia Exp $
 */
public class MailServerAccount extends RegistryEntry
        implements java.io.Serializable {


    /**    */
    private String     username ;
    /**    */
    private String     msLogin ;
    /**    */
    private String     msPassword ;
    /**    */
    private String     outLogin ;
    /**    */
    private String     outPassword ;
    /**    */
    private String     msAddress ;
    /**    */
    private String     msMailboxname;

    /**    */
    private boolean    push;

    /**    */
    private int        maxEmailNumber ;
    /**    */
    private int        maxImapEmail ;

    /**    */
    private MailServer mailServer;

    //------------------------------------------------------------- Constructors

    /**
     *
     */
    public MailServerAccount(){
    }
    
    //----------------------------------------------------------- Public Methods
    
    /**
     *
     */
    public String getUsername() {
        return username;
    }
    /**
     *
     */
    public String getMsLogin() {
        return msLogin;
    }
    /**
     *
     */
    public String getOutLogin() {
        return outLogin;
    }
    /**
     *
     */
    public String getMsPassword() {
        return msPassword;
    }
    /**
     *
     */
    public String getOutPassword() {
        return outPassword;
    }
    /**
     *
     */
    public String getMsAddress() {
        return msAddress;
    }
    /**
     *
     */
    public String getMsMailboxname() {
        return msMailboxname;
    }
    /**
     *
     */
    public boolean getPush() {
        return push;
    }
    /**
     *
     */
    public int getMaxEmailNumber() {
        return maxEmailNumber;
    }
    /**
     *
     */
    public int getMaxImapEmail() {
        return maxImapEmail;
    }
    /**
     *
     */
    public MailServer getMailServer() {
        return mailServer;
    }

    /**
     *
     */
    public void setUsername(String _username) {
        username = _username;
    }
    /**
     *
     */
    public void setMsLogin(String _msLogin) {
        msLogin = _msLogin;
    }
    /**
     *
     */
    public void setOutLogin(String outLogin) {
        this.outLogin = outLogin;
    }
    /**
     *
     */
    public void setMsPassword(String _msPassword) {
        msPassword = _msPassword;
    }
    /**
     *
     */
    public void setOutPassword(String outPassword) {
        this.outPassword = outPassword;
    }
    /**
     *
     */
    public void setMsAddress(String _msAddress) {
        msAddress = _msAddress;
    }
    /**
     *
     */
    public void setMsMailboxname(String _msMailboxname) {
        msMailboxname = _msMailboxname;
    }    
    /**
     *
     */
    public void setPush(boolean _push) {
        push = _push;
    }
    /**
     *
     */
    public void setMaxEmailNumber(int _maxEmailNumber) {
        maxEmailNumber = _maxEmailNumber;
    }
    /**
     *
     */
    public void setMaxImapEmail(int _maxImapEmail) {
        maxImapEmail = _maxImapEmail;
    }
    /**
     *
     */
    public void setMailServer(MailServer _mailServer) {
        mailServer = _mailServer;
    }


    /**
     *
     */
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("\nid"            , getId());
        sb.append("\ntaskBeanFile"  , getTaskBeanFile());
        sb.append("\nactive"        , getActive());
        sb.append("\nstatus"        , getStatus());
        sb.append("\nperiod"        , getPeriod());
        sb.append("\nusername"      , getUsername());
        sb.append("\nMSLogin"       , getMsLogin());
        sb.append("\nMSPassword"    , "********");
        sb.append("\nOutLogin"      , getOutLogin());
        sb.append("\nOutPassword"   , "********");
        sb.append("\nMSAddress"     , getMsAddress());
        sb.append("\nMSMailboxname" , getMsMailboxname());
        sb.append("\nPush"          , getPush());
        sb.append("\nMax Email"     , getMaxEmailNumber());
        sb.append("\nMax IMAP Email", getMaxImapEmail());
        if (this.mailServer != null){
            sb.append("\nMailServer", this.mailServer.toString());
        }
        return sb.toString();
    }


    public String toStringForCommandLine() {
        StringBuilder sb = new StringBuilder();

        sb.append("id: ").append(getId())
          .append(" [")
          .append("task bean file: ").append(getTaskBeanFile()).append(", ")
          .append("active: ").append(getActive()).append(", ")
          .append("status: ").append(getStatus()).append(", ")
          .append("refresh time: ").append(getPeriod()).append(" min., ")
          .append("userName: ").append(getUsername()).append(", ")
          .append("MSLogin: ").append(getMsLogin()).append(", ")
          .append("MSAddress: ").append(getMsAddress()).append(", ")
          .append("MSMailboxname: ").append(getMsMailboxname()).append(", ")
          .append("push: ").append(getPush()).append(", ")
          .append("Max Email: ").append(getMaxEmailNumber());

        if (this.mailServer != null){
            sb.append(this.mailServer.toStringForCommandLine());
        } else {
            sb.append("]");
        }

        return sb.toString();
    }

}
