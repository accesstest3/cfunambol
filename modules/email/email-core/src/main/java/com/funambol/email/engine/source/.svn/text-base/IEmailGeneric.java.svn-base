/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.email.engine.source;

import com.funambol.email.exception.EntityException;
import com.funambol.email.model.DefaultFolder;
import com.funambol.email.model.EmailFilter;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncSourceException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *
 * <p>Interface for Email SyncSource Wrapper </p>
 *
 * @version $Id: IEmailGeneric.java,v 1.2 2008-05-18 15:18:54 nichele Exp $
 */
public interface IEmailGeneric {

    /**
     *
     * @return SyncItemKey[]
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public SyncItemKey[] getAllSyncItemKeys()
      throws SyncSourceException ;
    /**
     *
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return HashMap
     */
    public Map getAllSyncItemInfo()
      throws SyncSourceException ;
    /**
     *
     * @param syncItemKey
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return SyncItem
     */
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
      throws SyncSourceException ;
    /**
     *
     * @param syncItem
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return SyncItemKey[]
     */
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
      throws SyncSourceException ;
    /**
     *
     * @param syncItem
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return boolean
     */
    public boolean isSyncItemInFilterClause(SyncItem syncItem)
      throws SyncSourceException ;
    /**
     *
     * @param syncItemKey
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return boolean
     */
    public boolean isSyncItemInFilterClause(SyncItemKey syncItemKey)
      throws SyncSourceException ;
    /**
     *
     * @param syncItemKey
     * @param s
     * @param engineSoftDelete
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public void removeSyncItem(SyncItemKey syncItemKey, Timestamp s, boolean engineSoftDelete)
      throws SyncSourceException ;
    /**
     *
     * @param syncItem
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return SyncItem
     */
    public SyncItem updateSyncItem(SyncItem syncItem)
      throws SyncSourceException ;
    /**
     *
     * @param syncItem
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return SyncItem
     */
    public SyncItem addSyncItem(SyncItem syncItem)
      throws SyncSourceException ;
    /**
     *
     * @param since
     * @param to
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return SyncItemKey[]
     */
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp to)
      throws SyncSourceException ;
    /**
     *
     * @param since
     * @param to
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return SyncItemKey[]
     */
    public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp to)
      throws SyncSourceException ;
    /**
     *
     * @param since
     * @param to
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return SyncItemKey[]
     */
    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp to)
      throws SyncSourceException ;
    /**
     *
     * @param syncItemKey
     * @throws com.funambol.framework.engine.source.SyncSourceException
     * @return char
     */
    public char getSyncItemStateFromId(SyncItemKey syncItemKey)
      throws SyncSourceException ;
    /**
     *
     * @param operation
     * @param statusCode
     * @param keys
     */
    public void setOperationStatus(String operation, int statusCode, SyncItemKey[] keys) ;


    /**
     *
     * @param sURI
     * @param pID
     * @throws com.funambol.email.exception.EntityException
     */
    public void checkMessageIDforDrafts(String sURI, long pID, String username)
      throws EntityException;

    /**
     *
     * @param df defaulFolder object
     * @param protocol
     * @param sURI sourceURI
     * @param pID principal ID
     * @throws com.funambol.email.exception.EntityException
     */
    public void insertDefaultFolder(DefaultFolder df, EmailFilter filter,
                                    String protocol, String sURI,  long pID, String username)
      throws EntityException;
    
    
    /**
     * Verify if the SyncItem is an email or a folder
     * @param syncItem The SyncItem to verify
     * @return True if the SyncItem is an email, false otherwise
     */
    public boolean isEmail(SyncItem syncItem);

    
}
