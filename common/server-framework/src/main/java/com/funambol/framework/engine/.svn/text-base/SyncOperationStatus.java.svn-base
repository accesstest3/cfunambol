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

package com.funambol.framework.engine;

import com.funambol.framework.engine.SyncOperation;
import com.funambol.framework.engine.source.SyncSource;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents a <i>SyncOperation</i> status.
 *
 *
 *
 * @version $Id: SyncOperationStatus.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 *
 */
public class SyncOperationStatus {

    // -------------------------------------------------------------- Properties

    /**
     * The operation this object represents the execution for
     */
    private SyncOperation operation;

    /** Getter for property operation.
     * @return Value of property operation.
     *
     */
    public SyncOperation getOperation() {
        return operation;
    }

    /**
     * The source the operation was executed on
     */
    private SyncSource syncSource;

    /** Getter for property syncSource.
     * @return Value of property syncSource.
     *
     */
    public SyncSource getSyncSource() {
        return syncSource;
    }

    /**
     * The status code
     */
    private int statusCode;

    /**
     * Getter for property statusCode.
     * @return Value of property syncSource.
     *
     */
    public int getStatusCode() {
        return statusCode;
    }


    // ------------------------------------------------------------- Contructors

    /**
     * Creates a new instance of SyncOperationStatus
     *
     * @param operation the operation - NOT NULL
     * @param syncSource the source - NOT NULL
     * @param statusCode the status code
     *
     * @throws IllegalArgumentException in case operation is null
     */
    public SyncOperationStatus( SyncOperation operation  ,
                                SyncSource    syncSource ,
                                int           statusCode ) {
        if (operation == null) {
            throw new IllegalArgumentException("operation cannnot be null");
        }
        if (syncSource == null) {
            throw new IllegalArgumentException("syncSource cannnot be null");
        }

        this.operation  = operation ;
        this.syncSource = syncSource;
        this.statusCode = statusCode;
    }

    /**
     * Creates a new instance of SyncOperationStatus
     *
     * @param operation the operation - NOT NULL
     * @param syncSource the source - NOT NULL
     *
     * @throws IllegalArgumentException in case operation is null
     */
    public SyncOperationStatus( SyncOperation operation  ,
                                SyncSource    syncSource ) {
        this(operation, syncSource, -1);
    }

    // ---------------------------------------------------------- Public methods

    public String toString() {
        return new ToStringBuilder(this).
            append("operation",  operation.toString() ).
            append("syncSource", syncSource.toString()).
            append("statusCode", getStatusCode()).
            toString();
    }

}
