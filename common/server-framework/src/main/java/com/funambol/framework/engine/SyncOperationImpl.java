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

import java.security.Principal;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncOperation;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * This class is a concrete implementation of <i>SyncOperation</i>
 * <p>
 * The property <i>operation</i> contains the type of the operation to perform
 * and the not null syncItemX selects on what source the operation has to be
 * applied.
 *
 *
 *
 * @version $Id: SyncOperationImpl.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class SyncOperationImpl implements SyncOperation {

    // -------------------------------------------------------------- Properties

    private SyncItem syncItemA = null;
    public SyncItem getSyncItemA() {
        return this.syncItemA;
    }

    public void setSyncItemA(SyncItem syncItemA) {
        this.syncItemA = syncItemA;
    }

    private SyncItem syncItemB = null;
    public SyncItem getSyncItemB() {
        return this.syncItemB;
    }

    public void setSyncItemB(SyncItem syncItemB) {
        this.syncItemB = syncItemB;
    }

    private char operation = 0;
    public char getOperation() {
        return this.operation;
    }

    public void setOperation(char operation) {
        this.operation = operation;
    }

    private Principal owner = null;

    /**
     * The principal that requested this operation.
     *
     * @return the principal that requested this operation. It may be null.
     */
    public Principal getOwner() {
        return this.owner;
    }

    /**
     * Set the owner of this operation
     */
    public void setOwner(Principal owner) {
        this.owner = owner;
    }

    private boolean aOperation = false;

    /**
     * Getter for property aOperation.
     * @return Value of property aOperation.
     */
    public boolean isAOperation() {
        return aOperation;
    }

    /**
     * Setter for property aOperation.
     * @param aOperation New value of property aOperation.
     */
    public void setAOperation(boolean aOperation) {
        this.aOperation = aOperation;
    }

    private boolean bOperation = false;

    /**
     * Getter for property bOperation.
     * @return Value of property bOperation.
     */
    public boolean isBOperation() {
        return bOperation;
    }

    /**
     * Setter for property bOperation.
     * @param bOperation New value of property aOperation.
     */
    public void setBOperation(boolean bOperation) {
        this.bOperation = bOperation;
    }

    /**
     * Has the client item to be deleted ?
     */
    private boolean deleteForced = false;

    public boolean isDeleteForced() {
        return deleteForced;
    }

    public void setDeleteForced(boolean delete) {
        this.deleteForced = delete;
    }

    // ------------------------------------------------------------ Constructors

    public SyncOperationImpl() {
    }

    /**
     * Create a new SyncOperationImpl
     *
     * @param syncItemA the A item
     * @param syncItemB the B item
     * @param operation the operation
     * @param aOperation is an operation to be performed on source A?
     * @param bOperation is an operation to be performed on source B?
     */
    public SyncOperationImpl(Principal owner     ,
                             SyncItem  syncItemA ,
                             SyncItem  syncItemB ,
                             char      operation ,
                             boolean   aOperation,
                             boolean   bOperation) {
        this.owner      = owner    ;
        this.syncItemA  = syncItemA;
        this.syncItemB  = syncItemB;
        this.operation  = operation;
        this.aOperation = aOperation;
        this.bOperation = bOperation;
    }

    public SyncOperationImpl(SyncItem syncItemA,
                             SyncItem syncItemB,
                             char     operation) {
        //
        // owner can be set later...
        //
        this(null, syncItemA, syncItemB, operation, false, false);
    }

    // ---------------------------------------------------------- Public methods

    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this);

        b.append("A", "" + syncItemA);
        b.append("B", "" + syncItemB);
        String o = "";
        switch (operation) {
            case NEW :          o = "NEW"         ; break;
            case DELETE:        o = "DELETE"      ; break;
            case UPDATE:        o = "UPDATE"      ; break;
            case CONFLICT:      o = "CONFLICT"    ; break;
            case NOP:           o = "NOP"         ; break;
            case ACCEPT_CHUNK:  o = "ACCEPT_CHUNK"; break;
        }
        b.append("operation", o);
        b.append("isAOperation",   String.valueOf(aOperation));
        b.append("isBOperation",   String.valueOf(bOperation));
        b.append("isDeleteForced", String.valueOf(deleteForced));

        return b.toString();
    }
}
