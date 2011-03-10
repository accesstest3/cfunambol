/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.phonessupport.bsh;

import com.funambol.framework.core.*;
import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.SyncMLUtil;
import com.funambol.tools.test.BeanShellTestCase;
import java.io.File;
import java.util.ArrayList;

/**
 * RemoveEmailDatastoragesFromServerCapabilites
 *
 * Tests if the synclet correctly remove email datasources from server
 * capabilities.
 * 
 */
public class Nokia7610out extends BeanShellTestCase {

    // ------------------------------------------------------------ Private data

    private static final String bshFileName =
            "./src/main/config/com/funambol/server/engine/pipeline/phones-support/bsh/Nokia7610out.bsh";
    private static final String XML_BASE_PATH =
            "./src/test/resources/data/com/funambol/phonessupport/bsh/Nokia7610out";

    // ---------------------------------------------------------- Public methods
    public Nokia7610out(String testName) throws Exception {
        super(testName);
        setBshFileName(bshFileName);
    }

    // -------------------------------------------------------------- Test cases

    public void testRemoveEmailDatastores() throws  Throwable
    {
        final String filePath = XML_BASE_PATH + "/servercapabilities.xml";

        //unmarshall the syncml message into a SyncML object
        SyncML syncml = null;
        try {
            File f = new File(".", filePath);
            String xml = IOTools.readFileString(filePath);
            syncml = SyncMLUtil.fromXML(xml);
        } catch(Exception e) {
            fail("Error unmarshalling " + filePath + ":\n " + e);
        }

        //checks if the source is correct
        assertTrue("Source message doesn't contain email datastores", checkIfMessageContainsEmailDatastores(syncml));

        //remove the email datastore
        String method = "removeEmailDataStoresFromResultsItem";
        execWithoutReturn(method, syncml);

        //checks if the removal was done
        assertFalse("Source message contains email datastores", checkIfMessageContainsEmailDatastores(syncml));
    }


    // --------------------------------------------------------- Private methods

    /**
     * Checks if the SyncML message contains at least one email datastorage
     *
     * @param syncml
     * @return
     */
    private boolean  checkIfMessageContainsEmailDatastores(SyncML syncml)
    {
        if (null == syncml) return false;

        boolean existsEmail = false;

        //checks if the syncml object has the email syncsource
        ArrayList commands = syncml.getSyncBody().getCommands();
        for (Object commandO : commands) {
            if (commandO instanceof Results || commandO instanceof Put) {
                ItemizedCommand command = (ItemizedCommand) commandO;
                for (Object itemO : command.getItems()) {
                    DevInfItem devinfitem = (DevInfItem) itemO;
                    for (Object datastoreO : devinfitem.getDevInfData().getDevInf().getDataStores()) {
                        DataStore datastore = (DataStore) datastoreO;
                        //checks txPref object
                        CTInfo txPref = datastore.getTxPref();
                        if (null != txPref && null != txPref.getCTType()) {
                            if (txPref.getCTType().contains("vnd.omads-email")) {
                                existsEmail = true;
                            }
                        }
                        CTInfo rxPref = datastore.getRxPref();
                        if (null != rxPref && null != rxPref.getCTType()) {
                            if (rxPref.getCTType().contains("vnd.omads-email")) {
                                existsEmail = true;
                            }
                        }
                    }
                }
            }
        }

        return existsEmail;
    }

}
