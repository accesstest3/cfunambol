/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.framework.tools.id;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

/**
 * Provides a factory of DBIDGenerators
 *
 * @version $Id: DBIDGeneratorFactory.java,v 1.3 2007-11-28 11:16:10 nichele Exp $
 */
public class DBIDGeneratorFactory {

    // ------------------------------------------------------------ Private Data

    private static Map<String, DBIDGenerator> instancesCache = new HashMap();


    /**
     * Returns a new instance of DBIDGenerator searching for already created instance
     * in the instancesCache. If no instance is found, a new one is created using
     * the given datasouce. Using this factory, a class that needs a DBIDGenerator,
     * can use a not-static instance because anyhow, calling
     * DBIDGeneratorFactory.getDBIDGenerator with the same nameSpace, always the same
     * instance is returned.
     * <p>
     * <b>NOTE: if an instance of a DBIDGenerator with the same nameSpace has been
     * already created, this is returned without using the given DataSource. So,
     * the use of the given datasource is not guaranteed.
     * This is accettable in a lot of cases, because usually a server application uses
     * just one datasource (obtained via jndi) or at least different datasources
     * that use the same database. <br/>As alternative, create yourself a <code>
     * com.funambol.framework.tools.id.DBIDGenerator</code> with your datasource.</b>
     * @param name the name of the needed space
     * @param dataSource the datasource to use creating the DBIDGenerators
     * @return DBIDGenerator the DBIDGenerator with the given name space
     */
    public static synchronized DBIDGenerator getDBIDGenerator(String     nameSpace,
                                                              DataSource dataSource) {

        DBIDGenerator instance = instancesCache.get(nameSpace);
        if (instance == null) {
            instance = new DBIDGenerator(dataSource, nameSpace);
            instancesCache.put(nameSpace, instance);
        }
        return instance;
    }
}
