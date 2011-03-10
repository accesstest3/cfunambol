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

package com.funambol.ctp.core;

import junit.framework.*;

/**
 *
 * @version $Id: MessageTest.java,v 1.2 2007-11-28 11:26:15 nichele Exp $
 */
public class MessageTest extends TestCase {

    public MessageTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }


    /**
     * Test of deepequals method, of class com.funambol.ctp.core.Message.
     */
    public void testDeepequals() {

        Message instance1 = new Message();
        instance1.setProtocolVersion("1.0");
        Auth auth1 = new Auth();
        auth1.setCred("credential");
        auth1.setDevid("deviceid");
        auth1.setFrom("from");
        auth1.setUsername("username");
        instance1.setCommand(auth1);

        assertFalse(instance1.deepequals(null));

        Message instance2 = new Message();
        assertFalse(instance1.deepequals(instance2));
        instance2.setProtocolVersion("1.0");
        assertFalse(instance1.deepequals(instance2));
        Auth auth2 = new Auth();
        instance2.setCommand(auth2);
        assertFalse(instance1.deepequals(instance2));
        auth2.setCred("credential");
        assertFalse(instance1.deepequals(instance2));
        auth2.setDevid("deviceid");
        assertFalse(instance1.deepequals(instance2));
        auth2.setFrom("from");
        assertFalse(instance1.deepequals(instance2));
        auth2.setUsername("username");
        assertTrue(instance1.deepequals(instance2));
    }
}
