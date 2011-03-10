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

/**
 * This class represents a Sync4j module.
 *
 *
 *
 * @version $Id: Sync4jModule.java,v 1.2 2007-11-28 11:15:38 nichele Exp $
 *
 */
public class Sync4jModule implements Serializable {

    private String            moduleId     ;
    private String            moduleName   ;
    private String            description  ;
    private Sync4jConnector[] connectors   ;

    /** Creates a new instance of Sync4jModule */
    public Sync4jModule() {
        this(null,null,null);
    }

    public Sync4jModule(String moduleId   ,
                        String moduleName ,
                        String description) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.description = description;
    }

    /** Getter for property moduleId.
     * @return Value of property moduleId.
     *
     */
    public String getModuleId() {
        return moduleId;
    }

    /** Setter for property moduleId.
     * @param moduleId New value of property moduleId.
     *
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /** Getter for property moduleName.
     * @return Value of property moduleName.
     *
     */
    public String getModuleName() {
        return moduleName;
    }

    /** Setter for property moduleName.
     * @param moduleName New value of property moduleName.
     *
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
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

    public void setConnectors(Sync4jConnector[] connectors) {
        this.connectors = connectors;
    }

    public Sync4jConnector[] getConnectors() {
        return connectors;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("ModuleId:    ").append(moduleId).append(", ")
          .append("ModuleName:  ").append(moduleName).append(", ")
	  .append("Description: ").append(description);
	return sb.toString();
    }

}
