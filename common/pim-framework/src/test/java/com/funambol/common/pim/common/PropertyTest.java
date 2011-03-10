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

package com.funambol.common.pim.common;

import junit.framework.*;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @version $Id: PropertyTest.java,v 1.2 2007-11-28 11:14:31 nichele Exp $
 */
public class PropertyTest extends TestCase {
    
    public PropertyTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    //--------------------------------------------------------------- Test cases
    
    public void testGetPropertyValueAsString_1() {
        
        /* Initialization */
        final Integer TWO = new Integer(2);
        Property noSpaces =          new Property("abc");
        Property headingSpaces =     new Property("   abc");
        Property trailingSpaces =    new Property("abc   ");
        Property spacesOnBothSides = new Property("   abc   ");
        Property headingSpace =      new Property(" abc");
        Property trailingSpace =     new Property("abc ");
        Property spacesEverywhere =  new Property(" a b c ");
        Property noValue =           new Property();
        Property nullValue =         new Property();
        nullValue.setPropertyValue(null);
        Property nonString =         new Property();
        nonString.setPropertyValue(TWO);

        /* Test assertions */
        assertEquals("abc",    noSpaces         .getPropertyValueAsString());
        assertEquals("abc",    noSpaces         .getPropertyValue());
        assertEquals("   abc", headingSpaces    .getPropertyValueAsString());
        assertEquals("   abc", headingSpaces    .getPropertyValue());
        assertEquals("abc",    trailingSpaces   .getPropertyValueAsString());
        assertEquals("abc",    trailingSpaces   .getPropertyValue());
        assertEquals("   abc", spacesOnBothSides.getPropertyValueAsString());
        assertEquals("   abc", spacesOnBothSides.getPropertyValue());
        assertEquals(" abc",   headingSpace     .getPropertyValueAsString());
        assertEquals(" abc",   headingSpace     .getPropertyValue());       
        assertEquals("abc",    trailingSpace    .getPropertyValueAsString());
        assertEquals("abc",    trailingSpace    .getPropertyValue());       
        assertEquals(" a b c", spacesEverywhere .getPropertyValueAsString());
        assertEquals(" a b c", spacesEverywhere .getPropertyValue());
        assertEquals(null,     noValue          .getPropertyValueAsString());
        assertEquals(null,     noValue          .getPropertyValue());
        assertEquals(null,     nullValue        .getPropertyValueAsString());
        assertEquals(null,     nullValue        .getPropertyValue());
        assertEquals("2",      nonString        .getPropertyValueAsString());
        assertEquals(TWO,      nonString        .getPropertyValue());
        
    }
}
