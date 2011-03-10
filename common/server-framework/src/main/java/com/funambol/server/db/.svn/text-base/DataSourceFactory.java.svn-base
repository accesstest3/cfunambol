/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.server.db;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import javax.sql.DataSource;

/**
 * It is a factory used in the tomcat context.xml file as factory for the datasources.
 * <br/>
 * Using this class, any datasource is configured reading an xml serialization of
 * a <code>DataSourceConfiguration</code> class.
 * @version $Id: DataSourceFactory.java,v 1.3 2008-06-14 09:35:36 nichele Exp $
 */
public class DataSourceFactory implements ObjectFactory {

    // --------------------------------------------------------------- Constants

    /** Default datasource factory class to use */
    public static final String DEFAULT_DATASOURCE_FACTORY =
        "org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory";

    private static final String DATASOURCE_CLASS_NAME  = "javax.sql.DataSource";

    private static final String PROP_FACTORY_CLASS_NAME  = "wrappedFactory";

    // -------------------------------------------------------------- Properties

    /** The wrapped datasource factory */
    private ObjectFactory wrappedFactory = null;


    // ------------------------------------------------------------- Constructor
    public DataSourceFactory() {

    }

    // ---------------------------------------------------------- Public methods


    public Object getObjectInstance(Object obj,
                                    Name name,
                                    Context nameCtx,
                                    Hashtable<?, ?> environment) throws Exception {

        if ((obj == null) || !(obj instanceof Reference)) {
            return null;
        }
        //
        // Just javax.sql.DataSource is handled
        //
        Reference ref = (Reference) obj;
        if (!(DATASOURCE_CLASS_NAME.equals(ref.getClassName()))) {
            NamingException ex = new NamingException
                ("Unable to handle '" + ref.getClassName() + "'");
            ex.printStackTrace();
            throw ex;
        }
        RefAddr wrappedFactoryRefAddr = ref.get(PROP_FACTORY_CLASS_NAME);
        String wrappedClass = null;
        if (wrappedFactoryRefAddr != null) {
            wrappedClass = wrappedFactoryRefAddr.getContent().toString();
        }

        if (wrappedClass == null || "".equals(wrappedClass)) {
            wrappedClass = DEFAULT_DATASOURCE_FACTORY;
        }

        ClassLoader tcl =
            Thread.currentThread().getContextClassLoader();

        Class factoryClass = null;
        if (tcl != null) {
            try {
                factoryClass = tcl.loadClass(wrappedClass);
            } catch(ClassNotFoundException e) {
                NamingException ex = new NamingException
                    ("Could not load resource factory class");
                ex.initCause(e);
                ex.printStackTrace();
                throw ex;
            }
        } else {
            try {
                factoryClass = Class.forName(wrappedClass);
            } catch(ClassNotFoundException e) {
                NamingException ex = new NamingException
                    ("Could not load resource factory class");
                ex.initCause(e);
                ex.printStackTrace();
                throw ex;
            }
        }
        if (factoryClass != null) {
            try {
                wrappedFactory = (ObjectFactory) factoryClass.newInstance();
            } catch (Throwable t) {
                if (t instanceof NamingException)
                    throw (NamingException) t;
                NamingException ex = new NamingException
                    ("Could not create resource factory instance");
                ex.initCause(t);
                ex.printStackTrace();
                throw ex;
            }
        }

        //
        // The name is just the last part of the jndiName since the firt parts are
        // subcontext. Declaring jdbc/fnblds the name is fnblds, declaring jdbc/fnbl/core
        // the name is core.
        // This is an accettable limitation.
        // Of course, we can not have jdbc/fnbl/core and jdbc/core since
        // both datasource read jdbc/core.xml file.
        //
        DataSourceConfiguration dataSourceConfiguration =
            DataSourceConfigurationHelper.getJDBCDataSourceConfiguration(name.toString());

        if (dataSourceConfiguration != null) {

            Properties properties = dataSourceConfiguration.getProperties();
            if (properties != null) {
                Map refAddrs = getRefAddrsMap(ref);

                Enumeration enumProp = properties.propertyNames();
                while (enumProp.hasMoreElements()) {

                    String propName = (String)enumProp.nextElement();
                    String propValue = properties.getProperty(propName);

                    RefAddr ra = new StringRefAddr(propName, propValue);

                    //
                    // If there is a refAddr with the same name, it will be
                    // overwritten
                    //
                    refAddrs.put(propName, ra);
                }
                //
                // In refAddrs we have all the configuration properties (the ones
                // from the dataSourceConfiguration and the ones coming from the 'ref'.
                // Now we cleat the 'ref' in order to re-set the new properties set.
                // The same thing is done with the dataSourceConfiguration so that
                // both sets contain the same properties
                //
                ref.clear();
                dataSourceConfiguration.clear();
                Collection<RefAddr> refAddrsCollection = refAddrs.values();
                for (RefAddr refAddr :refAddrsCollection ) {
                    ref.add(refAddr);
                    dataSourceConfiguration.setProperty(refAddr.getType(), refAddr.getContent().toString());
                }
            }
        }

        if (wrappedFactory != null) {

            if (dataSourceConfiguration instanceof RoutingDataSourceConfiguration) {
                //
                // A RoutingDataSource must be created.
                //
                RoutingDataSource rds = new RoutingDataSource((RoutingDataSourceConfiguration)dataSourceConfiguration);
                //
                // note that we can not call the RoutingDataSource.init and
                // RoutingDataSource.configure since they could use the jdbc/fnblcore that maybe
                // is not configured yet. See RoutingDataSource.getRoutedConnection. Those
                // methods are called there.
                //
                return rds;
            } else {
                return (DataSource) wrappedFactory.getObjectInstance(ref, name, nameCtx, environment);
            }

        } else {

            throw new NamingException("Cannot create resource instance");
        }

    }

    // --------------------------------------------------------- Private methods

    /**
     * Returns a map with all the RefAddr of the given reference
     * @param ref the reference
     * @return a map with all the RefAddr of the given reference
     */
    private Map getRefAddrsMap(Reference ref) {
        Map refAddrsMap = new HashMap();

        Enumeration<RefAddr> refAddrs = ref.getAll();
        while (refAddrs.hasMoreElements()) {
            RefAddr refAddr = refAddrs.nextElement();
            String type = refAddr.getType();
            refAddrsMap.put(type, refAddr);
        }
        return refAddrsMap;
    }
}
