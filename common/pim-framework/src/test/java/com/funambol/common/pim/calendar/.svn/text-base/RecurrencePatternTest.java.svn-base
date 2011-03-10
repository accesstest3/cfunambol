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

package com.funambol.common.pim.calendar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import junit.framework.TestCase;

/**
 * This class tests some methods of class RecurrencePattern.
 * 
 * @version $Id: RecurrencePatternTest.java,v 1.2 2008-06-24 12:14:29 mauro Exp $
 */
public class RecurrencePatternTest extends TestCase {
    
    public RecurrencePatternTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getExceptions method, of class RecurrencePattern.
     * This test simulates a typical way of setting the exception list into a
     * RecurrencePattern object. If the behaviour of RecurrencePattern is
     * changed in a way that requires the removal of this test, backward
     * compatibility with prior connectors will be broken. 
     * Hower, the best way of setting the exception list is via the 
     * setExceptions methods.
     */
    public void testGetExceptions() {
        
        RecurrencePattern rp = null;
        try {
             rp = RecurrencePattern.getYearlyRecurrencePattern(1               , 
                                                              (short)1         , 
                                                              (short)1         , 
                                                              "20070101T120000");
        } catch (RecurrencePatternException e) {
            fail("Instantiation of RecurrencePattern failed");
        }
        List<ExceptionToRecurrenceRule> list = rp.getExceptions(); // NB!
        try {
            // Add single positive exception
            list.add(new ExceptionToRecurrenceRule(true, "20080201T120000"));

            // Add duplicate negative exception
            list.add(new ExceptionToRecurrenceRule(false, "20090101T120000"));
            list.add(new ExceptionToRecurrenceRule(false, "20090101T120000"));

            // Add duplicate positive exception
            list.add(new ExceptionToRecurrenceRule(true, "20100201T120000"));
            list.add(new ExceptionToRecurrenceRule(true, "20100201T120000"));

            // Add single negative exception
            list.add(new ExceptionToRecurrenceRule(false, "20110101T120000"));
            
        } catch (ParseException e) {
            fail("Parse error while adding exceptions to the list");
        }
        
        // NB: No call to setExceptions method.
        
        list = rp.getExceptions();
        
        assertEquals(4, list.size());
        assertEquals("20080201T120000", list.get(0).getDate());
        assertTrue(list.get(0).isAddition());
        assertEquals("20090101T120000", list.get(1).getDate());
        assertFalse(list.get(1).isAddition());
        assertEquals("20100201T120000", list.get(2).getDate());
        assertTrue(list.get(2).isAddition());        
        assertEquals("20110101T120000", list.get(3).getDate());
        assertFalse(list.get(3).isAddition());
    }

    /**
     * Test of setExceptions(List) method, of class RecurrencePattern.
     */
    public void testSetExceptions_List() {
        
        RecurrencePattern rp = null;
        try {
             rp = RecurrencePattern.getYearlyRecurrencePattern(1               , 
                                                              (short)1         , 
                                                              (short)1         , 
                                                              "20070101T120000");
        } catch (RecurrencePatternException e) {
            fail("Instantiation of RecurrencePattern failed");
        }
        List<ExceptionToRecurrenceRule> list = 
                new ArrayList<ExceptionToRecurrenceRule>();
        try {
            // Add single positive exception
            list.add(new ExceptionToRecurrenceRule(true, "20080201T120000"));

            // Add duplicate negative exception
            list.add(new ExceptionToRecurrenceRule(false, "20090101T120000"));
            list.add(new ExceptionToRecurrenceRule(false, "20090101T120000"));

            // Add duplicate positive exception
            list.add(new ExceptionToRecurrenceRule(true, "20100201T120000"));
            list.add(new ExceptionToRecurrenceRule(true, "20100201T120000"));

            // Add single negative exception
            list.add(new ExceptionToRecurrenceRule(false, "20110101T120000"));
            
        } catch (ParseException e) {
            fail("Parse error while adding exceptions to the list");
        }
        
        rp.setExceptions(list);
        
        list = rp.getExceptions();
        
        assertEquals(4, list.size());
        assertEquals("20080201T120000", list.get(0).getDate());
        assertTrue(list.get(0).isAddition());
        assertEquals("20090101T120000", list.get(1).getDate());
        assertFalse(list.get(1).isAddition());
        assertEquals("20100201T120000", list.get(2).getDate());
        assertTrue(list.get(2).isAddition());        
        assertEquals("20110101T120000", list.get(3).getDate());
        assertFalse(list.get(3).isAddition());
    }

    /**
     * Test of setExceptions(SortedSet) method, of class RecurrencePattern.
     */
    public void testSetExceptions_SortedSet() {
        
        RecurrencePattern rp = null;
        try {
             rp = RecurrencePattern.getYearlyRecurrencePattern(1               , 
                                                              (short)1         , 
                                                              (short)1         , 
                                                              "20070101T120000");
        } catch (RecurrencePatternException e) {
            fail("Instantiation of RecurrencePattern failed");
        }
        SortedSet<ExceptionToRecurrenceRule> set = 
                new TreeSet<ExceptionToRecurrenceRule>();
        try {
            // Add single positive exception
            set.add(new ExceptionToRecurrenceRule(true, "20080201T120000"));

            // Add duplicate negative exception
            set.add(new ExceptionToRecurrenceRule(false, "20090101T120000"));
            set.add(new ExceptionToRecurrenceRule(false, "20090101T120000"));

            // Add duplicate positive exception
            set.add(new ExceptionToRecurrenceRule(true, "20100201T120000"));
            set.add(new ExceptionToRecurrenceRule(true, "20100201T120000"));

            // Add single negative exception
            set.add(new ExceptionToRecurrenceRule(false, "20110101T120000"));
            
        } catch (ParseException e) {
            fail("Parse error while adding exceptions to the list");
        }
        
        rp.setExceptions(set);
        
        List<ExceptionToRecurrenceRule> list = rp.getExceptions();
        
        assertEquals(4, list.size());
        assertEquals("20080201T120000", list.get(0).getDate());
        assertTrue(list.get(0).isAddition());
        assertEquals("20090101T120000", list.get(1).getDate());
        assertFalse(list.get(1).isAddition());
        assertEquals("20100201T120000", list.get(2).getDate());
        assertTrue(list.get(2).isAddition());        
        assertEquals("20110101T120000", list.get(3).getDate());
        assertFalse(list.get(3).isAddition());
    }


}
