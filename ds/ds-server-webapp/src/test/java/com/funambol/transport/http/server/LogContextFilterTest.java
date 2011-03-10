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
package com.funambol.transport.http.server;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 * LogContextFilter test cases
 * @version $Id: LogContextFilterTest.java 33086 2009-12-12 17:54:23Z nichele $
 */
public class LogContextFilterTest extends TestCase {
    
    public LogContextFilterTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases


    /**
     * Test of getNewThreadName method, of class LogContextFilter.
     */
    public void testGetNewThreadName() throws Throwable {
        String threadName = null;
        String deviceId = null;
        String username = null;
        String newThreadName = null;

        threadName = null;
        username = null;
        deviceId = null;
        newThreadName = (String) PrivateAccessor.invoke(LogContextFilter.class,
                "getNewThreadName",
                new Class[]{String.class, String.class, String.class},
                new Object[]{threadName, deviceId, username});
        assertNull(newThreadName);

        threadName = "TP-Processor128";
        username = null;
        deviceId = null;
        newThreadName = (String) PrivateAccessor.invoke(LogContextFilter.class,
                "getNewThreadName",
                new Class[]{String.class, String.class, String.class},
                new Object[]{threadName, deviceId, username});
        assertEquals("TP-Processor128 [] []", newThreadName);


        threadName = "TP-Processor150";
        username = "mark";
        deviceId = null;
        newThreadName = (String) PrivateAccessor.invoke(LogContextFilter.class,
                "getNewThreadName",
                new Class[]{String.class, String.class, String.class},
                new Object[]{threadName, deviceId, username});
        assertEquals("TP-Processor150 [] [mark]", newThreadName);

        threadName = "TP-Processor150";
        username = "smith";
        deviceId = "dev-id-smith";
        newThreadName = (String) PrivateAccessor.invoke(LogContextFilter.class,
                "getNewThreadName",
                new Class[]{String.class, String.class, String.class},
                new Object[]{threadName, deviceId, username});
        assertEquals("TP-Processor150 [dev-id-smith] [smith]", newThreadName);

        threadName = null;
        username = "john";
        deviceId = "devId";
        newThreadName = (String) PrivateAccessor.invoke(LogContextFilter.class,
                "getNewThreadName",
                new Class[]{String.class, String.class, String.class},
                new Object[]{threadName, deviceId, username});
        assertNull(newThreadName);

        threadName = "TP-Processor250";
        username = "";
        deviceId = "dev-id-1";
        newThreadName = (String) PrivateAccessor.invoke(LogContextFilter.class,
                "getNewThreadName",
                new Class[]{String.class, String.class, String.class},
                new Object[]{threadName, deviceId, username});
        assertEquals("TP-Processor250 [dev-id-1] []", newThreadName);

    }


}
