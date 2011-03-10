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
package com.funambol.framework.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;

import junit.framework.*;

import org.jibx.runtime.*;

import com.funambol.framework.tools.IOTools;
import java.util.ArrayList;
import java.util.List;

/**
 * Test cases to test DevInf.
 *
 * @version $Id$
 */
public class TestDevInf extends TestCase {

    // ------------------------------------------------------------ Constructors
    public TestDevInf(String name) {
        super(name);
    }

    // -------------------------------------------------------------- Test cases
    public void testUnmarshalDevInfV1() {
        File f = new File(".", "src/test/data/syncml1.1.xml");

        try {
            unmarshal(f);
        } catch(Exception e) {
            fail("Error unmarshalling " + f.getAbsolutePath() + ": " + e);
        }
    }

    public void testMarshalDevInfV1() throws Exception {
        File f = new File(".", "src/test/data/syncml1.1.xml");

        SyncML msg = unmarshal(f);

        IMarshallingContext c =
            BindingDirectory.getFactory("binding", SyncML.class)
                            .createMarshallingContext();
        c.setIndent(0);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        c.setOutput(new OutputStreamWriter(baos));

        c.marshalDocument(msg);

        //
        // Let's compare the source with the marshalled document,
        // removing all newlines and spaces. Note that we have to discard the
        // first line of the expected results, since it is the comment containin
        // the size of the message.
        //
        String expected = skipFirstLine(IOTools.readFileString(f));
        String syncml   = baos.toString();

        expected = expected.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        syncml   = syncml.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");

        assertEquals("The expected result and the marshalled message do not match", expected, syncml);
    }

    public void testUnmarshalDevInfV2() {
        File f = new File(".", "src/test/data/syncml1.2.xml");

        try {
            unmarshal(f);
        } catch(Exception e) {
            fail("Error unmarshalling " + f.getAbsolutePath() + ": " + e);
        }
    }

    public void testUnmarshalDevTwoDataStores() {
        File f = new File(".", "src/test/data/twods.xml");

        try {
            unmarshal(f);
        } catch(Exception e) {
            fail("Error unmarshalling " + f.getAbsolutePath() + ": " + e);
        }
    }

    public void testUnmarshalWrongNokiaDevInf12() {
        File f = new File(".", "src/test/data/nokia-wrong-syncml1.2.xml");

        try {
            unmarshal(f);
        } catch(Exception e) {
            fail("Error unmarshalling " + f.getAbsolutePath() + ": " + e);
        }
    }

    public void testUnmarshalWrongSE_w910i() {
        File f = new File(".", "src/test/data/sonyericsson-W910i.xml");

        try {
            unmarshal(f);
        } catch(Exception e) {
            fail("Error unmarshalling " + f.getAbsolutePath() + ": " + e);
        }
    }

	public void testIsSupportLargeObjs_SetTrue() {
		DevInf devInf = new DevInf();

		devInf.setSupportLargeObjs(true);
		assertTrue(devInf.isSupportLargeObjs());
	}
    
	public void testIsSupportLargeObjs_SetFalse() {
		DevInf devInf = new DevInf();

		devInf.setSupportLargeObjs(false);
		assertFalse(devInf.isSupportLargeObjs());
	}

    /**
     * Test of getDataStoreNames method, of class Capabilities.
     * @throws Throwable
     */
    public void testContainsDataStore() throws Throwable {

        DevInf devInf =
                createDevInf(new String[] {"CONTACTS", "CALENDAR"});

        assertTrue(devInf.containsDataStore("CONTACTS"));
        assertTrue(devInf.containsDataStore("CALENDAR"));
        assertFalse(devInf.containsDataStore("EVENT"));
    }

    public void testContainsDataStore_CaseSensitive() throws Throwable {

        DevInf devInf =
                createDevInf(new String[] {"Contacts", "CALENDAR"});

        assertTrue(devInf.containsDataStore("CALENDAR"));
        assertFalse(devInf.containsDataStore("Calendar"));
        assertFalse(devInf.containsDataStore("calendar"));
        assertTrue(devInf.containsDataStore("Contacts"));
        assertFalse(devInf.containsDataStore("contacts"));
        assertFalse(devInf.containsDataStore("CONTACTS"));
    }

    // --------------------------------------------------------- Private methods
    private SyncML unmarshal(File f) throws Exception {
        FileReader r = new FileReader(f);

        IUnmarshallingContext c =
            BindingDirectory.getFactory("binding", SyncML.class)
                            .createUnmarshallingContext();

        return (SyncML)c.unmarshalDocument(r);
    }

    private String skipFirstLine(String text) {
        int i = text.indexOf('\n');

        if (i < 0) {
            return text;
        }

        return text.substring(i+1);
    }

    /**
     * Create a simple DevInf containing an array of simple DataStores
     * @param dataStoreNames
     * @return a DevInf
     */
    private DevInf createDevInf(String[] dataStoreNames) {
        DevInf devInf = new DevInf();

        List<DataStore> datastores = new ArrayList();
        for (String dataStoreName : dataStoreNames) {
            DataStore dataStore = new DataStore();
            dataStore.setSourceRef(new SourceRef(dataStoreName));

            datastores.add(dataStore);
        }
        devInf.setDataStores(datastores.toArray(new DataStore[datastores.size()]));
        return devInf;
    }

    public static void main(String[] args) {
        TestDevInf t = new TestDevInf(TestDevInf.class.getName());

        try {
            t.testUnmarshalDevInfV1();
            t.testUnmarshalDevInfV2();

            t.testMarshalDevInfV1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
