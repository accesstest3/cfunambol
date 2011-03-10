/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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

package com.funambol.admin.mo;

import com.funambol.framework.engine.source.SyncSource;

/**
 * This class represents a server side SyncSource configuration server bean.
 *
 * @version $Id: ConnectorManagementObject.java,v 1.5 2007-11-28 10:28:18 nichele Exp $
 */
public class ConnectorManagementObject 
extends ManagementObject {
    
    // ------------------------------------------------------------ Private data
    
    private String moduleId;
    private String connectorId;
    
    // ------------------------------------------------------------ Constructors
    
    /**
     * Creates a new SyncSourceManagementObject.
     *
     * @param obj the management object
     * @param moduleId the module id of the module the source belongs to
     * @param connectorId the connector id of the connector the source belongs to
     */
    public ConnectorManagementObject( Object obj           , 
                                      String moduleId      , 
                                      String connectorId   ,
                                      String connectorName ) {
        //
        // We do not know the source path yet...
        //
        super(obj, null);
        
        this.moduleId     = moduleId    ;
        this.connectorId  = connectorId ;
        
        createPath(connectorName);
    }
    
    // ---------------------------------------------------------- Public methods

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }
    
    // --------------------------------------------------------- Private methods
    
    /**
     * Builds the path from the instance moduleId, connectorId and the given
     * connectorName
     *
     * @param connectorName the connector name
     */
    private void createPath(String connectorName) {
         setPath(moduleId + '/' + connectorId + '/' + connectorName + ".xml");
    }

}
