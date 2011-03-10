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

package com.funambol.framework.engine.source;

import java.sql.Timestamp;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * Implements the basic functionalities of a <i>SyncSource</i> like naming.
 *
 * @version $Id: AbstractSyncSource.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public abstract class AbstractSyncSource implements SyncSource, java.io.Serializable {

    // --------------------------------------------------------------- Constants
    public static final String LOG_NAME = "engine.source";

    // ---------------------------------------------------------- Protected data

    protected String name         = null;
    protected String type         = null;
    protected String sourceURI    = null;
    protected String sourceQuery  = null;
    protected SyncSourceInfo info = null;

    /**
     * How many items were added ?
     */
    protected long howManyAdded;

    /**
     * How many items were deleted ?
     */
    protected long howManyDeleted;

    /**
     * How many items were updated ?
     */
    protected long howManyUpdated;

    // ------------------------------------------------------------ Private data

    private transient FunambolLogger logger =
                          FunambolLoggerFactory.getLogger(LOG_NAME);

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of AbstractSyncSource */
    protected AbstractSyncSource() {
    }

    /**
     * @deprecated the type should not be set at SyncSource level but at SyncItem
     *             level. As alternative, if the SyncSource handles just a type,
     *             it can be set as preferred type in the SyncSourceInfo
     */
    public AbstractSyncSource(String name, String type, String sourceURI) {
        this.name      = name;
        this.type      = (type == null) ? "unknown" : type;
        this.sourceURI = sourceURI;
    }

    public AbstractSyncSource(String name, String sourceURI) {
        this.name      = name;
        this.sourceURI = sourceURI;
    }

    public AbstractSyncSource(String name) {
        this(name, null, null);
    }

    // ---------------------------------------------------------- Public methods

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @deprecated the type should not be set at SyncSource level but at SyncItem
     *             level. As alternative, if the SyncSource handles just a type,
     *             it can be set as preferred type in the SyncSourceInfo
     */
    public String getType() {
        return this.type;
    }

    /**
     * @deprecated the type should not be set at SyncSource level but at SyncItem
     *             level. As alternative, if the SyncSource handles just a type,
     *             it can be set as preferred type in the SyncSourceInfo
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for property query.
     * @return Value of property query.
     */
    public String getSourceQuery() {
        return sourceQuery;
    }

    /**
     * Getter for property uri.
     * @return Value of property uri.
     */
    public String getSourceURI() {
        return sourceURI;
    }

    /**
     * Setter for property uri.
     * @param sourceURI New value of property uri.
     */
    public void setSourceURI(String sourceURI) {
        this.sourceURI = sourceURI;
    }

    /**
     * Returns the type info of the content handled by this source
     *
     * @return the type info of the content handled by this source
     */
    public SyncSourceInfo getInfo() {
        return this.info;
    }

    /**
     * Setter for the property <i>info</i>
     */
    public void setInfo(SyncSourceInfo info) {
        this.info = info;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());

        sb.append(" - {name: ").append(getName()     );
        sb.append(", uri: "    ).append(getSourceURI());
        sb.append("}"         );

        return sb.toString();
    }

    /**
     * This resets the howMany counters. It should then be called by extending
     * classes before anything else.
     *
     * @param syncContext the syncContext to use
     *
     * @throws SyncSourceException in case of any error
     *
     * @see SyncSource
     */
    public void beginSync(SyncContext syncContext) throws SyncSourceException {

        howManyAdded = howManyDeleted = howManyUpdated = 0;
    }

    /**
     * It logs the howMany counters. It should then be called by extending
     * classes before anything else.
     *
     * @param principal user/device
     *
     * @throws SyncSourceException in case of any error
     *
     * @see SyncSource
     */
    public void endSync() throws SyncSourceException {

        if (logger.isInfoEnabled()) {
            logger.info(getSourceURI()
                        + ": "
                        + howManyAdded
                        + " new items added, "
                        + howManyUpdated
                        + " existing items updated, "
                        + howManyDeleted
                        + " items deleted."
                        );
        }
    }

    /**
     * @see SyncSource
     */
    public void commitSync() throws SyncSourceException {
    }

    // -------------------------------------------------------- Abstract methods

    public abstract SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException;

    public abstract SyncItemKey[] getDeletedSyncItemKeys(Timestamp sinceTs,
                                                         Timestamp untilTs)
        throws SyncSourceException;

    public abstract SyncItemKey[] getNewSyncItemKeys(Timestamp sinceTs,
                                                     Timestamp untilTs)
        throws SyncSourceException;


    public abstract SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
        throws SyncSourceException;

    public abstract void removeSyncItem(SyncItemKey syncItemKey,
                                        Timestamp   time,
                                        boolean     softDelete) throws SyncSourceException;

    public abstract SyncItem updateSyncItem(SyncItem syncInstance)
        throws SyncSourceException;

    public abstract SyncItem addSyncItem(SyncItem syncInstance)
        throws SyncSourceException;

    public abstract SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
        throws SyncSourceException;

    public abstract void setOperationStatus(String operationName,
                                            int status,
                                            SyncItemKey[] keys);

}
