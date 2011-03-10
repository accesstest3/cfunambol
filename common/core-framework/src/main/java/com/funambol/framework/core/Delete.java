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
package com.funambol.framework.core;

/**
 * Corresponds to the &lt;Delete&gt; element in the SyncML represent DTD
 *
 * @version $Id: Delete.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class Delete
extends ModificationCommand
implements java.io.Serializable {

    // --------------------------------------------------------------- Constants
    /** Command name */
    public static final String COMMAND_NAME = "Delete";

    // ------------------------------------------------------------ Private data
    private Boolean archive;
    private Boolean sftDel;

    // ------------------------------------------------------------ Constructors
    /** For serialization purposes */
    protected Delete() {}

    /**
     * Creates a new Delete object with the given command identifier,
     * noResponse, archiveData, softDelete, credential, meta and array of item
     *
     * @param cmdID the command identifier - NOT NULL
     * @param noResp true if no response is required
     * @param archive true if the deleted data should be archived
     * @param sftDel true if this is a "soft delete". If set to false, then
     *                   this delete command is a "hard delete"
     * @param cred the authentication credential
     * @param meta the meta data
     * @param items the array of item - NOT NULL
     *
     */
    public Delete(final CmdID cmdID,
                  final boolean noResp,
                  final boolean archive,
                  final boolean sftDel,
                  final Cred cred,
                  final Meta meta,
                  final Item[] items) {
        super(cmdID, meta, items);

        setCred(cred);
        this.noResp  = (noResp)  ? new Boolean(noResp)  : null;
        this.archive = (archive) ? new Boolean(archive) : null;
        this.sftDel  = (sftDel)  ? new Boolean(sftDel)  : null;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets the command name property
     *
     * @return the command name property
     */
    public String getName() {
        return Delete.COMMAND_NAME;
    }

    /**
     * Gets the Archive property
     *
     * @return true if the deleted data should be archived
     */
    public boolean isArchive() {
        return (archive != null);
    }

    /**
     * Gets the Boolean archive property
     *
     * @return archive the Boolean archive property
     */
    public Boolean getArchive() {
        if (archive == null || !archive.booleanValue()) {
            return null;
        }
        return archive;
    }

    /**
     * Sets the archive property
     *
     * @param archive the Boolean archive object
     */
    public void setArchive(Boolean archive) {
        this.archive = (archive.booleanValue()) ? archive : null;
    }

    /**
     * Gets the SftDel property
     *
     * @return <b>true</b>  if this is a "Soft delete"
     *         <b>false</b> if this is a "hard delete"
     */
    public boolean isSftDel() {
        return (sftDel != null);
    }

    public Boolean getSftDel() {
        if (sftDel == null || !sftDel.booleanValue()) {
            return null;
        }
        return sftDel;
    }

    public void setSftDel(Boolean sftDel) {
        this.sftDel = (sftDel.booleanValue()) ? sftDel : null;
    }
}
