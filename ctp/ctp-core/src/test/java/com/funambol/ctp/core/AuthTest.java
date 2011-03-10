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
 * @version $Id: AuthTest.java,v 1.2 2007-11-28 11:26:15 nichele Exp $
 */
public class AuthTest extends TestCase {

    public AuthTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }


    /**
     * Test of deepequals method, of class com.funambol.ctp.core.Auth.
     */
    public void testDeepequals() {

        Auth auth1 = new Auth();
        auth1.setCred("credential");
        auth1.setDevid("deviceid");
        auth1.setFrom("from");
        auth1.setUsername("username");

        assertFalse(auth1.deepequals(null));

        Auth auth2 = new Auth();
        assertFalse(auth1.deepequals(auth2));
        auth2.setCred("credential");
        assertFalse(auth1.deepequals(auth2));
        auth2.setDevid("deviceid");
        assertFalse(auth1.deepequals(auth2));
        auth2.setFrom("from");
        assertFalse(auth1.deepequals(auth2));
        auth2.setUsername("username");
        assertTrue(auth1.deepequals(auth2));

        Bye bye = new Bye();
        assertFalse(auth1.deepequals(bye));

        Ready ready = new Ready();
        assertFalse(auth1.deepequals(ready));
    }
}
