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

package com.funambol.ctp.server.session;

import junit.framework.*;

/**
 *
 */
public class StateAdapterTest extends TestCase {

    public StateAdapterTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }


    /**
     * Test of deepequals method, of class com.funambol.ctp.server.session.StateAdapter.
     */
    public void testDeepequals() {

        assertTrue( new StateConnected().deepequals(new StateConnected()) );
        assertFalse( new StateConnected().deepequals(new StateAuthenticated()) );
        assertFalse( new StateConnected().deepequals(new StateReady()) );
        assertFalse( new StateConnected().deepequals(new StateLeaving()) );
        assertFalse( new StateConnected().deepequals(new StateDiconnected()) );

        assertFalse( new StateAuthenticated().deepequals(new StateConnected()) );
        assertTrue( new StateAuthenticated().deepequals(new StateAuthenticated()) );
        assertFalse( new StateAuthenticated().deepequals(new StateReady()) );
        assertFalse( new StateAuthenticated().deepequals(new StateLeaving()) );
        assertFalse( new StateAuthenticated().deepequals(new StateDiconnected()) );

        assertFalse( new StateReady().deepequals(new StateConnected()) );
        assertFalse( new StateReady().deepequals(new StateAuthenticated()) );
        assertTrue( new StateReady().deepequals(new StateReady()) );
        assertFalse( new StateReady().deepequals(new StateLeaving()) );
        assertFalse( new StateReady().deepequals(new StateDiconnected()) );

        assertFalse( new StateLeaving().deepequals(new StateConnected()) );
        assertFalse( new StateLeaving().deepequals(new StateAuthenticated()) );
        assertFalse( new StateLeaving().deepequals(new StateReady()) );
        assertTrue( new StateLeaving().deepequals(new StateLeaving()) );
        assertFalse( new StateLeaving().deepequals(new StateDiconnected()) );

        assertFalse( new StateDiconnected().deepequals(new StateConnected()) );
        assertFalse( new StateDiconnected().deepequals(new StateAuthenticated()) );
        assertFalse( new StateDiconnected().deepequals(new StateReady()) );
        assertFalse( new StateDiconnected().deepequals(new StateLeaving()) );
        assertTrue( new StateDiconnected().deepequals(new StateDiconnected()) );
    }
}
