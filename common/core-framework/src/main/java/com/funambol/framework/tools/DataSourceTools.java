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
package com.funambol.framework.tools;

import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This class provides implementation for the datasource name lookup
 *
 * NOTE: the methods lookupDataSource() are not
 *       synchronized by design. We are introducing this class to improve
 *       performance and synchronized methods on frequently used classes
 *       reduce concurrency significantly. From a functionality point of view,
 *       a race condition won't be very negative: just after startup some lookups
 *       could be performed uselessly
 *
 * @version $Id: DataSourceTools.java,v 1.3 2008-05-14 12:33:55 nichele Exp $
 */
public class DataSourceTools {

    // ------------------------------------------------------------ Private data

    /** See class description about not synchronized access */
    private static Map<String, DataSource> cache = new HashMap<String, DataSource>();


    // ---------------------------------------------------------- Public methods

    /**
     * Performs datasource lookup using the given dataSourceName and context.
     * This class is usefull because given a datasource name like 'java:jdbc/fnblds' it
     * tries with:
     * <ui>
     * <li>jdbc/fnblds</li>
     * <li>java:jdbc/fnblds</li>
     * <li>java:comp/env/jdbc/fnblds</li>
     * </ui>
     *
     * The algorithm used is: given the datasource name, checks if it starts with
     * 'java:comp/env/' or with 'java:'. If this happen, the "base" datasource name is
     * obtained removing 'java:comp/env/' or 'java:'.
     * Using the "base_datasource", tries to lookup:
     * <ui>
     * <li>base_datasource</li>
     * <li>java:base_datasource</li>
     * <li>java:comp/env/base_datasource</li>
     * </ui>
     *
     * @param dataSourceName datasource name that has to be looked up
     * @param context        initial context used for lookup process
     * @return the datasource with the given name
     * @throws javax.naming.NamingException if an error occurs looking up the resource
     */
    public static DataSource lookupDataSource(String dataSourceName,
                                              InitialContext context)
    throws NamingException {

        if (dataSourceName == null) {
            throw new IllegalArgumentException("dataSourceName must be not null");
        }

        DataSource dataSource = cache.get(dataSourceName);

        if ( dataSource != null) {
            return dataSource;
        }
        String originalDataSourceName = dataSourceName;
        
        //
        // We first try with the name as it is; if we get a NameNotFound
        // exception than we try prepending 'java:comp/env/'
        //
        if (dataSourceName.startsWith("java:comp/env/")) {
            dataSourceName = (dataSourceName.length() > 15)
                    ? dataSourceName.substring(15)
                    : dataSourceName
                    ;
        }
        if (dataSourceName.startsWith("java:")) {
            dataSourceName = (dataSourceName.length() > 6)
                    ? dataSourceName.substring(6)
                    : dataSourceName
                    ;
        }


        try {
            dataSource = (DataSource) context.lookup(dataSourceName);
        } catch (NamingException e) {
            try {
                dataSource = (DataSource) context.lookup("java:" +
                        dataSourceName);
            } catch (NamingException nNE) {
                try {
                    dataSource = (DataSource) context.lookup("java:comp/env/" +
                        dataSourceName);
                } catch (NamingException nnNE) {
                    //
                    // We catch this exception in order to throw an exception with
                    // the original datasource. Without that, the error message was
                    // always "Name java:comp is not bound in this Context"
                    //
                    throw new NameNotFoundException("Name " + dataSourceName + " is not bound in this Context");
                }
            }
        }

        cache.put(originalDataSourceName, dataSource);
        return dataSource;
    }

    /**
     * Performs datasource lookup using the given dataSourceName creating an
     * InitialContext. 
     * <br>
     * See lookupDataSource(String dataSourceName, InitialContext context)
     * for more details
     *
     * @param dataSourceName datasource name that has to be looked up
     * @return the datasource with the given name
     * @throws javax.naming.NamingException if an error occurs looking up the resource
     */
    public static DataSource lookupDataSource(String dataSourceName)
    throws NamingException {
        InitialContext initialContext = new InitialContext();
        return lookupDataSource(dataSourceName, initialContext);
    }
    

    /**
     * Usefull method for troubleshooting
     * @throws java.lang.Exception
     */
    public static void printContext(String contextName) throws Exception {
        System.out.println("---------- Listing '" + contextName + "' ------------");
        InitialContext initialContext = new InitialContext();

        NamingEnumeration<NameClassPair> nameEnum = initialContext.list(contextName);
        if (nameEnum != null) {
            while (nameEnum.hasMore()) {
                NameClassPair name = nameEnum.next();
                String nameInSpace = name.getName();
                String className = name.getClassName();
                System.out.println("NameInSpace: " + nameInSpace + ", class: " + className);
            }
        }
        System.out.println("--------------------------------------------");
    }
}
