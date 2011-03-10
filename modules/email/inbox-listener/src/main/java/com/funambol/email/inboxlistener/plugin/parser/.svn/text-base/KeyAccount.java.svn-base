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
package com.funambol.email.inboxlistener.plugin.parser;


import org.apache.commons.lang.builder.ToStringBuilder;
import com.funambol.pushlistener.service.registry.RegistryEntry;

/**
 *
 * @version $Id: KeyAccount.java,v 1.2 2008-05-13 10:09:53 gbmiglia Exp $
 */
public class KeyAccount {


    /**    */
    private long   id            = -1;
    /**    */
    private String username      = null;
    /**    */
    private String msAddress     = null;
    /**    */
    private String msMailboxname = null;

    //------------------------------------------------------------- Constructors

    /**
     *
     */
    public KeyAccount(){
    }
    
    //----------------------------------------------------------- Public Methods
    
    /**
     *
     */
    public long getId() {
        return id;
    }
    /**
     *
     */
    public String getUsername() {
        return username;
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
    public void setId(long _id) {
        id = _id;
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
    public void setMsAddress(String _msAddress) {
        msAddress = _msAddress;
    }
    /**
     *
     */
    public void setMsMailboxname(String _msMailboxname) {
        msMailboxname = _msMailboxname;
    }    

    

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" [")
          .append("id: ").append(getId()).append(", ")
          .append("userName: ").append(getUsername()).append(", ")
          .append("MSAddress: ").append(getMsAddress()).append(", ")
          .append("MSMailboxName: ").append(getMsMailboxname()).append("]");

        return sb.toString();
    }

}
