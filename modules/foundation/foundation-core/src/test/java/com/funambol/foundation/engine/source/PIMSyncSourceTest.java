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

package com.funambol.foundation.engine.source;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.tools.beans.BeanInitializationException;

/**
 * PIMSyncSource's test cases
 * @version $Id: PIMSyncSourceTest.java,v 1.1.1.1 2008-03-20 21:39:12 stefano_fornari Exp $
 */
public class PIMSyncSourceTest extends TestCase {

    public PIMSyncSourceTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    public void testGetSourceQueryParametersWithNullValue() throws Throwable {

        String SourceQuery = null;
        PIMSyncSource instance = getPIMSyncSourceInstance();

        Map parameters = (Map) PrivateAccessor.invoke(
            instance,
            "getSourceQueryParameters",
            new Class[] {String.class},
            new Object[] {SourceQuery}
        );

        assertEquals(parameters, new HashMap());
    }

    public void testGetSourceQueryParametersWithOneValue() throws Throwable {

        String SourceQuery = "photo=true";
        PIMSyncSource instance = getPIMSyncSourceInstance();

        Map parameters = (Map) PrivateAccessor.invoke(
            instance,
            "getSourceQueryParameters",
            new Class[] {String.class},
            new Object[] {SourceQuery}
        );

        Map expectedMap = new HashMap();
        expectedMap.put("photo", "true");

        assertEquals(parameters, expectedMap);
    }

    public void testGetSourceQueryParametersWithTwoValues() throws Throwable {

        String SourceQuery = "photo=true&end=ok";
        PIMSyncSource instance = getPIMSyncSourceInstance();

        Map parameters = (Map) PrivateAccessor.invoke(
            instance,
            "getSourceQueryParameters",
            new Class[] {String.class},
            new Object[] {SourceQuery}
        );

        Map expectedMap = new HashMap();
        expectedMap.put("photo", "true");
        expectedMap.put("end", "ok");

        assertEquals(parameters, expectedMap);
    }

    public void testGetSourceQueryParametersWithEmptyValue() throws Throwable {

        String SourceQuery = "";
        PIMSyncSource instance = getPIMSyncSourceInstance();

        Map parameters = (Map) PrivateAccessor.invoke(
            instance,
            "getSourceQueryParameters",
            new Class[] {String.class},
            new Object[] {SourceQuery}
        );

        Map expectedMap = new HashMap();

        assertEquals(parameters, expectedMap);
    }

    public void testGetSourceQueryParametersWithWrongStartingValue() throws Throwable {

        String SourceQuery = "&photo=true";
        PIMSyncSource instance = getPIMSyncSourceInstance();

        Map parameters = (Map) PrivateAccessor.invoke(
            instance,
            "getSourceQueryParameters",
            new Class[] {String.class},
            new Object[] {SourceQuery}
        );

        Map expectedMap = new HashMap();
        expectedMap.put("photo", "true");
        assertEquals(parameters, expectedMap);
    }

    public void testGetSourceQueryParametersWithWrongEndingValue() throws Throwable {

        String SourceQuery = "&photo=true&";
        PIMSyncSource instance = getPIMSyncSourceInstance();

        Map parameters = (Map) PrivateAccessor.invoke(
            instance,
            "getSourceQueryParameters",
            new Class[] {String.class},
            new Object[] {SourceQuery}
        );

        Map expectedMap = new HashMap();
        expectedMap.put("photo", "true");
        assertEquals(parameters, expectedMap);
    }

    public void testGetSourceQueryParametersWithEmptyParameter() throws Throwable {

        String SourceQuery = "&photo=true&empty=";
        PIMSyncSource instance = getPIMSyncSourceInstance();

        Map parameters = (Map) PrivateAccessor.invoke(
            instance,
            "getSourceQueryParameters",
            new Class[] {String.class},
            new Object[] {SourceQuery}
        );

        Map expectedMap = new HashMap();
        expectedMap.put("photo", "true");
        expectedMap.put("empty", null);
        assertEquals(parameters, expectedMap);
    }

    public void testGetSourceQueryParametersMisc() throws Throwable {

        String SourceQuery = "&photo=true&empty=&test=&end=o%2Bk&=";
        PIMSyncSource instance = getPIMSyncSourceInstance();

        Map parameters = (Map) PrivateAccessor.invoke(
            instance,
            "getSourceQueryParameters",
            new Class[] {String.class},
            new Object[] {SourceQuery}
        );

        Map expectedMap = new HashMap();
        expectedMap.put("photo", "true");
        expectedMap.put("empty", null);
        expectedMap.put("test", null);
        expectedMap.put("end", "o+k");
        assertEquals(parameters, expectedMap);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // --------------------------------------------------------- Private methods

    private PIMSyncSource getPIMSyncSourceInstance() {
        PIMSyncSource instance = new PIMSyncSource() {

            @Override
            public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public SyncItemKey[] getDeletedSyncItemKeys(Timestamp sinceTs, Timestamp untilTs) throws SyncSourceException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public SyncItemKey[] getNewSyncItemKeys(Timestamp sinceTs, Timestamp untilTs) throws SyncSourceException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public SyncItem getSyncItemFromId(SyncItemKey syncItemKey) throws SyncSourceException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void removeSyncItem(SyncItemKey syncItemKey, Timestamp time, boolean softDelete) throws SyncSourceException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public SyncItem updateSyncItem(SyncItem syncInstance) throws SyncSourceException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public SyncItem addSyncItem(SyncItem syncInstance) throws SyncSourceException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem) throws SyncSourceException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp sinceTs, Timestamp untilTs) throws SyncSourceException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean mergeSyncItems(SyncItemKey syncItemKey, SyncItem syncItem) throws SyncSourceException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void init() throws BeanInitializationException {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        return instance;
    }

}
