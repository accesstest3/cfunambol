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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.*;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IUnmarshallingContext;

import com.funambol.framework.tools.XMLSizeCalculator;

/**
 * Test cases for XMLSizeCalculator class.
 *
 * @version $Id$
 */
public class TestMsgSizeCalculator extends TestCase {

    // ------------------------------------------------------------ Constructors
    public TestMsgSizeCalculator(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Tests the size of an unmarshalled SyncML object is the expected size
     */
    public void testSize() throws Exception {
        File f[] = new File[] {
            new File(".", "src/test/data/syncml1.1.xml"),
            new File(".", "src/test/data/syncml1.2.xml")
        };

        SyncML msg = null;
        for(int i=0; i<f.length; ++i) {
            FileReader r = new FileReader(f[i]);

            IUnmarshallingContext c =
                BindingDirectory.getFactory("binding", SyncML.class)
                                .createUnmarshallingContext();

            msg = (SyncML)c.unmarshalDocument(r);

            XMLSizeCalculator calculator = new XMLSizeCalculator();

            long size = calculator.getSize(msg);
            long expected = getExpectedSize(f[i]);

            Assert.assertEquals(expected, size);
        }

    }

    // --------------------------------------------------------- Private methods

    /**
     * The expected size is in the first line of the file in a comment starting
     * with:
     *
     * <!-- size:xxxx
     *
     * where xxxx is the expected size
     *
     * @param f the file
     *
     * @return the expected size as specified in the file
     *
     * @throws IOException in case of problems reading the given file
     */
    private long getExpectedSize(File f) throws IOException {
        BufferedReader r = new BufferedReader(new FileReader(f));

        String sizeLine = r.readLine();

        r.close();

        if (!sizeLine.startsWith("<!-- size:")) {
            throw new IOException("Size line not found!");
        }

        sizeLine = sizeLine.substring(10).trim();

        int i = 0;
        while (Character.isDigit(sizeLine.charAt(i++))) {}

        return Long.parseLong(sizeLine.substring(0, i-1));
    }

}
