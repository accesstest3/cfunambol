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

package com.funambol.server.db;

import junit.framework.TestCase;

/**
 * Partition's testcases
 * @version $Id: PartitionTest.java,v 1.2 2008-05-15 05:19:25 nichele Exp $
 */
public class PartitionTest extends TestCase {

    public PartitionTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of constructor with null name
     */
    public void testConstructorWithNullName() {

        boolean created = true;

        Partition instance = null;
        try {
            instance = new Partition(null);
        } catch (IllegalArgumentException e) {
            created = false;
        }
        assertFalse(created);
    }

    /**
     * Test of hashCode method, of class Partition.
     */
    public void testHashCodeAndEquals() {
        Partition instance1 = new Partition("test");
        Partition instance2 = new Partition("test");
        assertEquals(instance1.hashCode(), instance2.hashCode());

        assertTrue(instance1.equals(instance2));
        assertTrue(instance2.equals(instance2));

        String anotherObject = "test";
        assertFalse(instance1.equals(anotherObject));

    }

    /**
     * Test of getName method, of class Partition.
     */
    public void testGetName() {
        Partition instance = new Partition("test-name-1", true);
        assertEquals("test-name-1", instance.getName());
        
        instance = new Partition("test-name-2");
        assertEquals("test-name-2", instance.getName());
    }

    /**
     * Test of isActive method, of class Partition.
     */
    public void testIsActive() {
        Partition instance = new Partition("test-name-1", true);
        assertTrue(instance.isActive());
        
        instance = new Partition("test-name-2", false);
        assertFalse(instance.isActive());
        
        instance = new Partition("test-name-3");
        assertTrue(instance.isActive());
        
    }

}
