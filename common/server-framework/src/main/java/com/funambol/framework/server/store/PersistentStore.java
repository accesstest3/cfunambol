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

package com.funambol.framework.server.store;

import java.util.Map;

import com.funambol.framework.filter.Clause;

import com.funambol.framework.server.store.ConfigPersistentStoreException;
import com.funambol.framework.server.store.PersistentStoreException;

/**
 * A <i>PersistentStore</i> is a class that stores objects in a persistent media
 * such as a database. The work of saving and reading data to and from the store
 * is delegated to the store() and read() methods that can take the appropriate
 * actions based on the type of the object that has to be written or read.
 * <p>
 * To store an object just call <i>store(obj)</i>.<br>
 * To read an object call read(obj).
 * <p>
 * Note that those two methods return true if they know how to deal with the
 * given object; that return value is not intended to be a success indicator. It
 * just tells the caller that the <i>PersistentStore</i> knew how to process the
 * given object.
 * <p>
 * A <i>PersistentStore</i> can be configured calling <i>configure()</i> with a
 * <i>java.util.Map</i> parameter containing configuration information. The
 * content of the map is implementation specific.
 *
 *
 *
 * @version $Id: PersistentStore.java,v 1.2 2007-11-28 11:15:59 nichele Exp $
 */
public interface PersistentStore {

    // ---------------------------------------------------------- Public methods

    /**
     * Configure the persistent store
     *
     * @param config an <i>Map</i> containing configuration parameters.
     *
     * @throws ConfigPersistentStoreException
     */
    public void configure(Map config) throws ConfigPersistentStoreException;

    /**
     * Store the given object to the persistent media.
     *
     * @param o object to be stored
     *
     * @return true if this <i>PersistentStore</i> processed the given object,
     *         false otherwise
     *
     * @throws PersistentStoreException
     */
    public boolean store(Object o) throws PersistentStoreException;

    /**
     * Read from the persistent media the given object.
     *
     * @param o object to be read
     *
     * @return true if this <i>PersistentStore</i> processed the given object,
     *         false otherwise
     *
     * @throws PersistentStoreException
     */
    public boolean read(Object o) throws PersistentStoreException;

    /**
     * Read all objects stored the persistent media.
     *
     * @param objClass the object class handled by the persistent store
     *
     * @return an array containing the objects read. If no objects are found an
     *         empty array is returned. If the persistent store has not
     *         processed the quest, null is returned.
     *
     * @throws PersistentStoreException
     */
    public Object[] read(Class objClass) throws PersistentStoreException;

    /**
     * Delete the given object to the persistent media
     *
     * @param o object to be deleted
     *
     * @return true if this <i>PersistentStore</i> processed the given object,
     *         false otherwise
     *
     * @throws PersistentStoreException
     */
    public boolean delete(Object o) throws PersistentStoreException;

    /**
     * Read all objects stored the persistent media.
     *
     * @param o the object class handled by the persistent store
     * @param clause the array of select conditions
     *
     * @return an array containing the objects read. If no objects are found an
     *         empty array is returned. If the persistent store has not
     *         processed the quest, null is returned.
     *
     * @throws PersistentStoreException
     */
    public Object[] read(Object o, Clause clause) throws PersistentStoreException;

    /**
     * Counts all objects stored the persistent media.
     *
     * @param o the object class handled by the persistent store
     * @param clause the array of select conditions
     *
     * @return An integer containing the number in the persistent
     *         store for the Object-type.
     *
     * @throws PersistentStoreException
     */
    public int count(Object o, Clause clause) throws PersistentStoreException;


}
