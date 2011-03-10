/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.framework.engine.source;

import java.sql.Timestamp;

import com.funambol.framework.filter.FilterClause;
import com.funambol.framework.security.Sync4jPrincipal;

/**
 * Contains the context used in the synchronization process.
 * @version $Id: SyncContext.java,v 1.4 2008-05-15 05:32:14 nichele Exp $
 */
public class SyncContext implements java.io.Serializable {

    // --------------------------------------------------------------- Constants
    public static final int CONFLICT_RESOLUTION_SERVER_WINS = 0;
    public static final int CONFLICT_RESOLUTION_CLIENT_WINS = 1;
    public static final int CONFLICT_RESOLUTION_MERGE_DATA  = 2;

    // -------------------------------------------------------------- Properties

    private Sync4jPrincipal principal          = null;
    private int             syncMode           = -1;
    private FilterClause    filterClause       = null;
    private String          sourceQuery        = null;
    private int             conflictResolution = 0;
    private Timestamp       since              = null;
    private Timestamp       to                 = null; 

    /**
     * Creates a new SyncContext
     * @param principal the principal
     * @param syncMode the mode
     * @param filter the filter
     */
    public SyncContext(Sync4jPrincipal principal  ,
                       int             syncMode   ,
                       FilterClause    filter     ,
                       String          sourceQuery,
                       int             conflictResolution) {

        this.principal          = principal;
        this.syncMode           = syncMode;
        this.filterClause       = filter;
        this.sourceQuery        = sourceQuery;
        this.conflictResolution = conflictResolution;
    }

    /**
     * Creates a new SyncContext
     * 
     * @param principal
     * @param syncMode
     * @param filter
     * @param sourceQuery
     * @param conflictResolution
     */
    public SyncContext(Sync4jPrincipal principal  ,
                       int             syncMode   ,
                       FilterClause    filter     ,
                       String          sourceQuery,
                       int             conflictResolution,
                       Timestamp       since,
                       Timestamp       to) {

        this.principal          = principal;
        this.syncMode           = syncMode;
        this.filterClause       = filter;
        this.sourceQuery        = sourceQuery;
        this.conflictResolution = conflictResolution;
        this.since              = since;
        this.to                 = to;
    }
    
    /**
     * Returns the principal
     * @return the principal
     */
    public Sync4jPrincipal getPrincipal() {
        return principal;
    }

    /**
     * Returns the syncMode
     * @return the syncMode
     */
    public int getSyncMode() {
        return syncMode;
    }

    /**
     * Returns the filterClause
     * @return the filterClause
     */
    public FilterClause getFilterClause() {
        return filterClause;
    }

    /**
     * Returns the sourceQuery
     * @return the sourceQuery
     */
    public String getSourceQuery() {
        return sourceQuery;
    }

    /**
     * Returns the conflictResolution
     * @return the conflictResolution
     */
    public int getConflictResolution() {
        return conflictResolution;
    }
    
    /**
     * Returns the since
     * @return the since
     */
    public Timestamp getSince() {
        return since;
    }

    /**
     * Returns the to
     * @return the to
     */
    public Timestamp getTo() {
        return to;
    }
    
}
