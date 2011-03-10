/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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

package com.funambol.foundation.items.model;

import java.io.File;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import com.funambol.common.media.file.FileDataObject;

/**
 * Test cases for FileDataObjectWrapper class.
 *
 * @version $Id: FileDataObjectWrapperTest.java 36000 2010-09-21 15:20:52Z luigiafassina $
 */
public class FileDataObjectWrapperTest extends TestCase {

    // ------------------------------------------------------------ Constructors
    public FileDataObjectWrapperTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    public void testIsComplete() throws NoSuchFieldException {
        // Testing incomplete fdow, it doesn't contain a file body
        FileDataObjectWrapper fdow = createIncompleteFDOW();

        boolean result = fdow.isComplete();
        assertFalse("Expected incomplete item",result);

        // Testing a complete fdow, it
        fdow.getFileDataObject().setBodyFile(new File("."));
        result = fdow.isComplete();
        assertTrue("Expected complete item",result);
    }

    public void testGetFileDataObject() throws NoSuchFieldException {
        FileDataObjectWrapper fdow = createIncompleteFDOW();

        PrivateAccessor.setField(fdow,"fileDataObject", null);

        try {
            fdow.getFileDataObject();
            fail("IllegalStateException expected");
        } catch(IllegalStateException e) {
            
        }
    }

    //---------------------------------------------------------- Private methods
    private FileDataObjectWrapper createIncompleteFDOW() {
        FileDataObject fdo = new FileDataObject();
        return new FileDataObjectWrapper("id", "userid",fdo);
    }

}
