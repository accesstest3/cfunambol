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

package com.funambol.common.sms.core;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

/**
 * BinarySMSMessage test cases
 * @version $Id: BinarySMSMessageTest.java,v 1.1 2008-07-03 10:09:48 nichele Exp $
 */
public class BinarySMSMessageTest extends TestCase {
    
    public BinarySMSMessageTest(String testName) {
        super(testName);
    }            

    // -------------------------------------------------------------- Test cases
    /**
     * Test of constructors
     */
    public void testConstructors() {
        byte[] wdp = new byte[] {0x01, 0x02};
        byte[] wsp = new byte[] {0x03, 0x04};
        byte[] content = new byte[] {0x05, 0x06, 0x07};
        BinarySMSMessage message = new BinarySMSMessage(wdp, wsp, content);
        
        ArrayAssert.assertEquals("Wrong wdp", wdp, message.getWdp());
        ArrayAssert.assertEquals("Wrong wsp", wsp, message.getWsp());
        ArrayAssert.assertEquals("Wrong content", content, message.getContent());

        message = new BinarySMSMessage(null, wsp, content);
        
        assertNull("Wrong wdp", message.getWdp());
        ArrayAssert.assertEquals("Wrong wsp", wsp, message.getWsp());
        ArrayAssert.assertEquals("Wrong content", content, message.getContent());        

        message = new BinarySMSMessage(wdp, null, content);
        
        ArrayAssert.assertEquals("Wrong wdp", wdp, message.getWdp());
        assertNull("Wrong wsp", message.getWsp());
        ArrayAssert.assertEquals("Wrong content", content, message.getContent());        
        
        message = new BinarySMSMessage(wdp, wsp, null);
        
        ArrayAssert.assertEquals("Wrong wdp", wdp, message.getWdp());
        ArrayAssert.assertEquals("Wrong wsp", wsp, message.getWsp());
        assertNull("Wrong content", message.getContent());       
    }

}

