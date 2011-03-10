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

package com.funambol.server.tools;

import javax.naming.NamingException;

import javax.sql.DataSource;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.protocol.IdGenerator;
import com.funambol.framework.tools.DataSourceTools;

import com.funambol.server.tools.id.DBIDGenerator;
import com.funambol.server.tools.id.DBIDGeneratorException;
import com.funambol.server.tools.id.DBIDGeneratorFactory;



/**
 * This class works as a simple id generator that read his value from the datastore
 *
 * @deprecated This generator is replaced, starting from v6, by
 *             <code>com.funambol.server.tools.id.DBIDGenerator</code>
 *
 * @version $Id: IdSpaceGenerator.java,v 1.1.1.1 2008-02-21 23:36:02 stefano_fornari Exp $
 */
public class IdSpaceGenerator
implements IdGenerator, java.io.Serializable {

    // ------------------------------------------------------------ Private data
    private FunambolLogger log =
        FunambolLoggerFactory.getLogger("funambol.server.tools.id");

    private String        idSpace       = null;
    private String        lastValue     = null;
    private DBIDGenerator dbIDGenerator = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of IdSpaceGenerator
     *
     * @param idSpace the id space
     */
    public IdSpaceGenerator(String idSpace) {
        this.idSpace = idSpace;
        DataSource dataSource;
        try {
            dataSource = DataSourceTools.lookupDataSource("jdbc/fnblds");
        } catch (NamingException ex) {
            log.error("Error looking up the datasource jdbc/fnblds", ex);
            throw new IllegalStateException("Error looking up the datasource jdbc/fnblds", ex);

        }

        dbIDGenerator = DBIDGeneratorFactory.getDBIDGenerator(idSpace, dataSource);
    }


    /**
     * Reset the generator to 0.
     */
    public void reset() {
    }

    /**
     * Returns the next value of the counter
     *
     * @return the next generated value
     */
    public synchronized String next() {
        long value;
        try {
            value = dbIDGenerator.next();
        } catch (DBIDGeneratorException ex) {
            log.error("Error reading the counter: " + idSpace, ex);
            throw new IllegalStateException("Error reading the counter: "
                                            + idSpace, ex);
        }
        lastValue = String.valueOf(value);
        return lastValue;
    }

    /**
     * Returns the last generated id (which is the current id).
     *
     * @return the last generated id
     */
    public synchronized String current() {
        return lastValue;
    }
}
