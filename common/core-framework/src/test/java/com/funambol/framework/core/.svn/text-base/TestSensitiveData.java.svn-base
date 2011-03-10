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
package com.funambol.framework.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;

import junit.framework.*;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

import com.funambol.framework.tools.IOTools;

/**
 *
 * @version $Id: TestSensitiveData.java,v 1.2 2008-02-25 12:58:12 luigiafassina Exp $
 */
public class TestSensitiveData extends TestCase {

    // ------------------------------------------------------------ Constructors
    public TestSensitiveData(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Tests the marshall process using the bindingHiddenData.xml and then the
     * binding.xml in order to verify if the sensitive data are hidden in the
     * first case and are shown in the second case.
     */
    public void testMarshalHiddenData() throws Exception {
        File f  = new File(".", "src/test/data/msg1.xml");
        File sd = new File(".", "src/test/data/msg1WithSensitiveData.xml");
        File hd = new File(".", "src/test/data/msg1WithoutSensitiveData.xml");

        SyncML msg = unmarshal(f);
        String syncml   = marshalHiddenData(msg);
        String expected = IOTools.readFileString(hd);

        expected = expected.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        syncml   = syncml.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");

        assertEquals("The expected result and the marshalled message (with hidden sensitive data) do not match", expected, syncml);

        syncml   = marshalShowData(msg);
        expected = IOTools.readFileString(sd);

        expected = expected.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        syncml   = syncml.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");

        assertEquals("The expected result and the marshalled message (with shown sensitive data) do not match", expected, syncml);
    }

    // --------------------------------------------------------- Private methods
    /**
     * Converts the SyncML object into String message using the
     * bindingHiddenData.xml in order to hide the sensitive data contained in
     * the credential and in the data item of an update and an add commands.
     *
     * @param msg the SyncML message
     * @return the string message
     */
    private String marshalHiddenData(SyncML msg) throws Exception {
        IMarshallingContext c =
            BindingDirectory.getFactory("bindingHiddenData", SyncML.class)
                            .createMarshallingContext();
        c.setIndent(0);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        c.setOutput(new OutputStreamWriter(baos));

        c.marshalDocument(msg);

        return baos.toString();
    }

    /**
     * Converts the SyncML object into String message using the binding.xml in
     * order to show the sensitive data contained in the credential and in the
     * data item of an update and an add commands.
     *
     * @param msg the SyncML message
     * @return the string message
     */
    private String marshalShowData(SyncML msg) throws Exception {
        IMarshallingContext c =
            BindingDirectory.getFactory("binding", SyncML.class)
                            .createMarshallingContext();
        c.setIndent(0);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        c.setOutput(new OutputStreamWriter(baos));

        c.marshalDocument(msg);

        return baos.toString();
    }

    /**
     * Converts the input file's content in SyncML object using binding.xml.
     *
     * @param f the file
     * @return the SyncML object
     */
    private SyncML unmarshal(File f) throws Exception {
        FileReader r = new FileReader(f);

        IUnmarshallingContext c =
            BindingDirectory.getFactory("binding", SyncML.class)
                            .createUnmarshallingContext();

        return (SyncML)c.unmarshalDocument(r);
    }
    
    // ------------------------------------------------------- Protected methods
    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
}
