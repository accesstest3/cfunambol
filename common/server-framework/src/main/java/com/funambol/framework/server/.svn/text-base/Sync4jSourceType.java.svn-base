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
import org.apache.commons.lang.builder.ToStringBuilder;

import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceErrorDescriptor;

/**
 * This class represents a Funambol source type with a list of SyncSource
 * with the same type.
 *
 * @version $Id: Sync4jSourceType.java,v 1.2 2007-11-28 11:15:38 nichele Exp $
 */
public class Sync4jSourceType implements Serializable {

    private String sourceTypeId;
    private String description;
    private String className;
    private String adminClass;
    private SyncSource[] syncSource;
    private SyncSourceErrorDescriptor[] syncSourceFailed;

    /** Creates a new instance of SyncSourceType */
    public Sync4jSourceType() {
        this(null, null, null,null);
    }

    public Sync4jSourceType(String sourceTypeId, 
                            String description , 
                            String className   ,
                            String adminClass  ) {
        this.sourceTypeId = sourceTypeId;
        this.description  = description ;
        this.className    = className   ;
        this.adminClass   = adminClass  ;
    }

    /** Getter for property sourceTypeId.
     * @return Value of property sourceTypeId.
     *
     */
    public String getSourceTypeId() {
        return sourceTypeId;
    }

    /** Setter for property sourceTypeId.
     * @param sourceTypeId New value of property sourceTypeId.
     *
     */
    public void setSourceTypeId(String sourceTypeId) {
        this.sourceTypeId = sourceTypeId;
    }

    /** Getter for property description.
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

    /** Getter for property className.
     * @return Value of property className.
     *
     */
    public String getClassName() {
        return className;
    }

    /** Setter for property className.
     * @param className New value of property className.
     *
     */
    public void setClassName(String className) {
        this.className = className;
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

    /**
     * Set an array of instances of SyncSource
     */
    public void setSyncSources(SyncSource[] syncSource) {
        this.syncSource = syncSource;
    }
    public SyncSource[] getSyncSources() {
        return this.syncSource;
    }

    /**
     * Set an array of SyncSourceException with the uri of SyncSource with
     * creations failed
     */
    public void setSyncSourcesFailed(SyncSourceErrorDescriptor[] syncSourceFailed) {
        this.syncSourceFailed = syncSourceFailed;
    }
    public SyncSourceErrorDescriptor[] getSyncSourcesFailed() {
        return this.syncSourceFailed;
    }
    
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);

         sb.append("sourceTypeId", sourceTypeId);
         sb.append("description" , description);
         sb.append("className"   , className);
         sb.append("adminClass"  , adminClass);

         return sb.toString();
    }

}
