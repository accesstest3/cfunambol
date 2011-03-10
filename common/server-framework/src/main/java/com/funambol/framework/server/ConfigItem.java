/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.framework.server;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.funambol.framework.security.Sync4jPrincipal;

/**
 * This class represents a device configuration property.
 *
 * @version $Id: ConfigItem.java,v 1.2 2008-05-22 13:19:30 nichele Exp $
 */
public class ConfigItem implements Serializable {

    //--------------------------------------------------------------- Properties

    private String          username   = null;
    private Sync4jPrincipal principal  = null;
    private String          nodeURI    = null;
    private String          value      = null;
    private Timestamp       lastUpdate = null;
    private char            status     = 'N';
    private boolean         encrypted  = false;

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getNodeURI() {
        return nodeURI;
    }

    public void setNodeURI(String nodeURI) {
        this.nodeURI = nodeURI;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Sync4jPrincipal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Sync4jPrincipal principal) {
        this.principal = principal;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //------------------------------------------------------------- Constructors

    /**
     * Creates a new instance of Config.
     * @param username the username
     * @param principal the principal
     * @param nodeURI the node URI
     */
    public ConfigItem(String username, Sync4jPrincipal principal, String nodeURI) {
        this(username, principal, nodeURI, null, null, 'N', false);
    }

    /**
     * Creates a new instance of Config.
     * @param username the username
     * @param principal the principal
     * @param nodeURI the node URI
     * @param value the value
     */
    public ConfigItem(String username, Sync4jPrincipal principal, String nodeURI, String value) {
        this(username, principal, nodeURI, value, null, 'N', false);
    }

    /**
     * Creates a new instance of Config.
     * @param username the username
     * @param principal the principal
     * @param nodeURI the node URI
     * @param value the value
     * @param lastUpdate the last update timestamp
     */
    public ConfigItem(String username,
                      Sync4jPrincipal principal,
                      String nodeURI,
                      String value,
                      Timestamp lastUpdate) {

        this(username, principal, nodeURI, value, lastUpdate, 'N', false);
    }

    /**
     * Creates a new instance of Config.
     * @param username the username
     * @param principal the principal
     * @param nodeURI the node URI
     * @param value the value
     * @param status the status to set
     */
    public ConfigItem(String username,
                      Sync4jPrincipal principal,
                      String nodeURI,
                      String value,
                      Timestamp lastUpdate,
                      char status) {

        this(username, principal, nodeURI, value, lastUpdate, status, false);
    }

    /**
     * Creates a new instance of Config. In this case the principal id is -1.
     *
     * @param username the username
     * @param nodeURI the node URI
     * @param value the value
     * @param lastUpdate the last update timestamp
     * @param status the status to set
     * @param encrypted true if the value is encrypted, false otherwise
     */
    public ConfigItem(String username,
                      String nodeURI,
                      String value,
                      Timestamp lastUpdate,
                      char status,
                      boolean encrypted) {
        this(username, new Sync4jPrincipal(-1), nodeURI, value, lastUpdate, status, encrypted);
    }

    /**
     * Creates a new instance of Config.
     *
     * @param username the username
     * @param principal the principal
     * @param nodeURI the node URI
     * @param value the value
     * @param lastUpdate the last update timestamp
     * @param status the status to set
     * @param encrypted true if the value is encrypted
     */
    public ConfigItem(String username,
                      Sync4jPrincipal principal,
                      String nodeURI,
                      String value,
                      Timestamp lastUpdate,
                      char status,
                      boolean encrypted) {

        this.username   = username;
        this.principal  = principal;
        this.nodeURI    = nodeURI;
        this.value      = value;
        this.lastUpdate = lastUpdate;
        this.status     = status;
        this.encrypted  = encrypted;
    }
    
    // ---------------------------------------------------------- Public methods
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("username"  , username  );
        sb.append("principal" , principal );
        sb.append("nodeURI"   , nodeURI   );
        sb.append("value"     , value     );
        sb.append("status"    , status    );
        sb.append("lastUpdate", lastUpdate);
        sb.append("encrypted" , encrypted );
        return sb.toString();
    }
}
