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
package com.funambol.server.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funambol.framework.filter.Clause;

import com.funambol.framework.server.store.ConfigPersistentStoreException;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;

import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;

import com.funambol.server.config.Configuration;

/**
 * This class represents the main persistent store of the Funambol server. It
 * handles everything related to saving and reading information to and from the
 * database, delegating the work to other <i>PersistentStore</i>s if necessary.
 * <p>
 * <i>PersistentStoreManager</i> can be configured with a list of <i>PersistetStore</i>s
 * that are called in sequence until one of them can process the given object.<br>
 * This list is expressed in the form of the string array <i>stores</i>; each
 * string is the name of a bean (or a class) and is loaded by
 * <i>com.funambol.framework.tools.beans.BeanFramework</i>.
 * <p>
 *
 * @version $Id: PersistentStoreManager.java,v 1.1.1.1 2008-02-21 23:36:01 stefano_fornari Exp $
 */
public class PersistentStoreManager
implements PersistentStore, Serializable, LazyInitBean {

    // --------------------------------------------------------------- Constants

    public static final String
    CONFIG_JNDI_DATA_SOURCE_NAME = "jndi-data-source-name";

    public static final String
    CONFIG_USERNAME              = "username";

    public static final String
    CONFIG_PASSWORD              = "password";

    // ------------------------------------------------------------ Private data

    private PersistentStore persistentStores[] = null;

    // ------------------------------------------------------------ Constructors

    // -------------------------------------------------------------- Properties

    /**
     * The persistent stores handled by this manager
     */
    private String[] stores = null;

    /** Getter for property stores.
     * @return Value of property stores.
     *
     */
    public String[] getStores() {
        return this.stores;
    }

    /** Setter for property stores.
     * @param stores New value of property stores.
     *
     */
    public void setStores(String[] stores) {
        this.stores = stores;
    }

    /**
     * The JNDI name of the datasource to be used
     */
    private String jndiDataSourceName = null;

    public String getJndiDataSourceName() {
        return this.jndiDataSourceName;
    }

    public void setJndiDataSourceName(String jndiDataSourceName) {
        this.jndiDataSourceName = jndiDataSourceName;
    }

    /**
     * The database user
     */
    private String username = null;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * The database password
     */
    private String password = null;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ---------------------------------------------------------- Public methods

    public boolean store(Object o)
    throws PersistentStoreException {
        for (int i=0; ((persistentStores != null) && (i<persistentStores.length)); ++i) {
            if (persistentStores[i].store(o)) {
                return true;
            }
        }

        return false;
    }

    public boolean read(Object o)
    throws PersistentStoreException {
        for (int i=0; ((persistentStores != null) && (i<persistentStores.length)); ++i) {
            if (persistentStores[i].read(o)) {
                return true;
            }
        }

        return false;
    }

    /** Read all objects stored the persistent media.
     *
     * @return an array containing the objects read. If no objects are found an
     *         empty array is returned. If the persistent store has not
     *         processed the quest, null is returned.
     *
     * @throws PersistentStoreException
     *
     */
    public Object[] read(Class objClass) throws PersistentStoreException {
        Object[] objs = null;
        for (int i=0; ((persistentStores != null) && (i<persistentStores.length)); ++i) {
            if ((objs = persistentStores[i].read(objClass)) != null) {
                return objs;
            }
        }

        return null;
    }

    /**
     * This method performs the following configuration taks:
     * <ul>
     *  <li>for each persistent store in <i>persistentStores</i></li>
     *  <ul>
     *    <li>configure the persistent store instance</li>
     *  </ul>
     * </ul>
     *
     * NOTE: this method is different from the init method by design choice. It
     * is more generic and allow to add additional properties that a particular
     * datastore may require. For example a JDO-based persistent store may need
     * more configuration properties than just the jndi name of the datasource.
     *
     * @throws ConfigPersistentStoreException if one of the persistent store
     *         instances could not be configured
     *
     */
    public void configure(final Map config)
    throws ConfigPersistentStoreException {
        if (persistentStores == null) {
            return;
        }

        for (int i=0; i<persistentStores.length; ++i) {
            try {
                persistentStores[i].configure(config);
            } catch (ConfigPersistentStoreException e) {
                throw new ConfigPersistentStoreException(
                    "Error configuring item " + i + " of the PersistentStore (" +
                    e.getMessage() + ")", e);
            }
        }
    }

    /**
     * Initialization of the PersistentStoreManager.
     * Th initialization is done as follows:
     * <ul>
     *  <li>for each persistent store in <i>stores</i></li>
     *  <ul>
     *    <li>create the persistent store instance</li>
     *  </ul>
     *  <li>call <i>configure(...)</i></li>
     * </ul>
     *
     * @throws BeanInitializationException in case of inittialization errors
     */
    public void init()
    throws BeanInitializationException{
        //
        // Instantiates the persistent stores
        //
        if ((stores == null) || (stores.length == 0)) {
            return;
        }

        persistentStores = new PersistentStore[stores.length];


        //
        // Creates the managed persistent stores
        //
        Configuration config = Configuration.getConfiguration();

        try {
            for (int i=0; ((stores != null) && (i<stores.length)); ++i) {
                persistentStores[i] =
                    (PersistentStore)config.getBeanInstanceByName(stores[i], true);
            }
        } catch (Exception e) {
            throw new BeanInitializationException(e.getMessage(), e.getCause());
        }


        try {
            //
            // Prepares the configuration map for the persistent stores
            //
            HashMap props = new HashMap(3);
            props.put(CONFIG_JNDI_DATA_SOURCE_NAME, jndiDataSourceName);
            props.put(CONFIG_USERNAME, username);
            props.put(CONFIG_PASSWORD, password);

            configure(props);

        } catch (ConfigPersistentStoreException e) {
            throw new BeanInitializationException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Adds the given <CODE>PersistenStore</CODE> to the array of the available
     * <CODE>PersistenStores</CODE>
     * @param persistentStore the <CODE>PersistenStore</CODE> to add
     */
    public void addPersistentStore(PersistentStore persistentStore) {
        //
        // We create an ArrayList around the list returned by Arrays.asList
        // because the list instance returned by that method doesn't support
        // a lot of optional methods defined in the List interface
        //
        List persistentStoresList = null;
        if (persistentStores != null) {
            persistentStoresList = new ArrayList(Arrays.asList(persistentStores));
        } else {
            persistentStoresList = new ArrayList();
        }

        persistentStoresList.add(persistentStore);

        persistentStores =
            (PersistentStore[])persistentStoresList.toArray(new PersistentStore[0]);

    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(getClass().getName()).append(" - {");
        sb.append("stores: ");
        for (int i=0; ((persistentStores != null) && (i<persistentStores.length)); ++i) {
            if (i>0) {
                sb.append(',');
            }
            sb.append(persistentStores[i]);
        }

        return sb.toString();
    }

    public boolean delete(Object o) throws PersistentStoreException
    {
        for (int i=0; ((persistentStores != null) && (i<persistentStores.length)); ++i) {
            if (persistentStores[i].delete(o)) {
                return true;
            }
        }

        return false;
    }

    public Object[] read(Object o, Clause clause) throws PersistentStoreException
    {
        Object[] objs = null;
        for (int i=0; ((persistentStores != null) && (i<persistentStores.length)); ++i) {
            if ((objs = persistentStores[i].read(o, clause)) != null) {
                return objs;
            }
        }

        return null;
    }

    public int count(Object o, Clause clause) throws PersistentStoreException
    {
        int count = -1;
        for (int i=0; ((persistentStores != null) && (i<persistentStores.length)); ++i) {
            if ((count = persistentStores[i].count(o, clause)) != -1) {
                return count;
            }
        }
        return -1;
    }

}
