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
package com.funambol.foundation.items.manager ;

import java.sql.Timestamp;
import java.util.List;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.dao.EntityDAO;
import com.funambol.foundation.util.Def;

/**
 *
 *
 * @version $Id: PIMEntityManager.java,v 1.1.1.1 2008-03-20 21:38:41 stefano_fornari Exp $
 */
public abstract class PIMEntityManager {
    
    // ------------------------------------------------------------ Private data
    
    protected static final FunambolLogger log =
            FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);
    
    /** Is the manager initialized ? */
    protected boolean initialized = false;

    /**
     * Array with the lists of the changed items:
     * - first element = new items
     * - second element = updated items
     * - third element = deleted items
     */
    protected List<String>[] changedItems = null;
    
    // ---------------------------------------------------------- Public methods

    public char getItemState(String id, Timestamp t) throws EntityException {
        try {
            return getEntityDAO().getItemState(id, t);
        } catch (DAOException dbae) {
            throw new EntityException("Error while getting item state.", dbae);
        }
    }
    
    public List getAllItems()
    throws EntityException {
        
        try {
            return getEntityDAO().getAllItems();
        } catch (DAOException dbae) {
            throw new EntityException("Error while getting all items.", dbae);
        }
    }
    
    public List getNewItems(Timestamp since, Timestamp to)
    throws EntityException {
        
        try {
            initChangedItems(since, to);            
            return changedItems[0]; // new items
        } catch (DAOException dbae) {
            throw new EntityException("Error while getting new items.", dbae);
        }
    }
    
    public List getUpdatedItems(Timestamp since, Timestamp to)
    throws EntityException {
        
        try {
            initChangedItems(since, to);
            return changedItems[1]; // updated items
        } catch (DAOException dbae) {
            throw new EntityException("Error while getting updated items.",
                    dbae);
        }
    }
    
    public List getDeletedItems(Timestamp since, Timestamp to)
    throws EntityException {
        
        try {
            initChangedItems(since, to);
            return changedItems[2]; // deleted items
        } catch (DAOException dbae) {
            throw new EntityException("Error while getting deleted items.",
                    dbae);
        }
    }
    
    public void removeItem(String uid, Timestamp t)
    throws EntityException {
        
        try {
            getEntityDAO().removeItem(uid, t);
        } catch (DAOException dbae) {
            throw new EntityException("Error while deleting item.", dbae);
        }
    }
    
    public void removeAllItems(Timestamp t)
    throws EntityException {
        
        try {
            getEntityDAO().removeAllItems(t);
        } catch (DAOException dbae) {
            throw new EntityException("Error while removing all items.", dbae);
        }
    }

    // ------------------------------------------------------- Protected methods

    /**
     * 
     * returns the EntityDAO specific for every subclass
     * @return
     */
    abstract protected EntityDAO getEntityDAO();

    /**
     * Initializes changedItems array
     * @param since the earliest allowed last-update Timestamp
     * @param to the latest allowed last-update Timestamp
     * @throws DAOException if an error occurs
     */
    protected void initChangedItems(Timestamp since, Timestamp to)
    throws DAOException {

        if (!initialized) {
            changedItems = getEntityDAO().getChangedItemsByLastUpdate(since, to);
            initialized = true;
        }
    }
}
