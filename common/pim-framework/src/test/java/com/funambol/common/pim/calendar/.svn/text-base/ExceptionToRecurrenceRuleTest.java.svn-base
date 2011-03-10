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

package com.funambol.common.pim.calendar;

import java.text.ParseException;
import junit.framework.*;

/**
 *
 * @version $Id: ExceptionToRecurrenceRuleTest.java,v 1.2 2007-11-28 11:14:31 nichele Exp $
 */
public class ExceptionToRecurrenceRuleTest extends TestCase {
    
    public ExceptionToRecurrenceRuleTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

   public void testGetDate() throws Exception {
       
       /* Initialization */
       ExceptionToRecurrenceRule alpha = 
               new ExceptionToRecurrenceRule(true,  "20070929T120000");
       ExceptionToRecurrenceRule beta = 
               new ExceptionToRecurrenceRule(false, "20070929T120000Z");
       ExceptionToRecurrenceRule gamma = 
               new ExceptionToRecurrenceRule(true,  "20070929");
       ExceptionToRecurrenceRule delta = 
               new ExceptionToRecurrenceRule(false, "2007-09-29");
       ExceptionToRecurrenceRule epsilon = 
               new ExceptionToRecurrenceRule(true,  "2007-09-29T12:00:00");
       boolean parseExceptionRaised = false;
       try {
           ExceptionToRecurrenceRule zeta = 
                   new ExceptionToRecurrenceRule(true, "Invalid value");
       } catch (ParseException e) {
           parseExceptionRaised = true;
       }
       
       /* Test assertions */
       assertEquals(true,  alpha.isAddition());
       assertEquals(false, beta.isAddition());
       assertEquals(true,  gamma.isAddition());
       assertEquals(false, delta.isAddition());
       assertEquals(true,  epsilon.isAddition());
       assertEquals("20070929T120000",  alpha.getDate());
       assertEquals("20070929T120000Z", beta.getDate());
       assertEquals("2007-09-29",       gamma.getDate());
       assertEquals("2007-09-29",       delta.getDate());
       assertEquals("20070929T120000",  epsilon.getDate());
       assertTrue(parseExceptionRaised);
   }
    
}
