/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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

import com.funambol.framework.server.Sync4jSourceType;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents a connector.
 *
 *
 *
 * @version $Id: Sync4jConnector.java,v 1.2 2007-11-28 11:15:38 nichele Exp $
 *
 */
public class Sync4jConnector implements Serializable {

	private String connectorId;
    private String connectorName;
    private String description;
    private String adminClass;
    private Sync4jSourceType[] syncSourceType;

    /** Creates a new instance of SyncConnector */
    public Sync4jConnector() {
        this(null,null,null,null);
    }

    public Sync4jConnector(String connectorId  ,
                           String connectorName,
                           String description  ,
                           String adminClass   ) {
        this.connectorId   = connectorId;
        this.connectorName = connectorName;
        this.description   = description;
        this.adminClass    = adminClass;
    }

    /** Getter for property connectorId.
     * @return Value of property connectorId.
     *
     */
    public String getConnectorId() {
        return connectorId;
    }

    /** Setter for property connectorId.
     * @param connectorId New value of property connectorId.
     *
     */
    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    /** Getter for property connectorName.
     * @return Value of property connectorName.
     *
     */
    public String getConnectorName() {
        return connectorName;
    }

    /** Setter for property connectorName.
     * @param connectorName New value of property connectorName.
     *
     */
    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    /** Getter for property desciption.
     * @return Value of property description.
     *
     */
    public String getDescription() {
        return description;
    }

    /** Setter for property description.
     * @param description New value of property description.
     *
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Getter for property adminClass.
     * @return Value of property adminClass.
     *
     */
    public String getAdminClass() {
        return adminClass;
    }

    /** Setter for property adminClass.
     * @param adminClass New value of property adminClass.
     *
     */
    public void setAdminClass(String adminClass) {
        this.adminClass = adminClass;
    }

    public void setSourceTypes(Sync4jSourceType[] syncSourceType) {
        this.syncSourceType = syncSourceType;
    }
    public Sync4jSourceType[] getSourceTypes() {
        return this.syncSourceType;
    }

    public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("IdConnector  :" + this.connectorId);
		sb.append("ConnectorName:" + this.connectorName);
		sb.append("Description  :" + this.description);
		sb.append("AdminClass   :" + this.adminClass);
		return sb.toString();
    }

}
