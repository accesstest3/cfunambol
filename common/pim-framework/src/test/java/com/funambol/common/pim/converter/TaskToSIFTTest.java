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

package com.funambol.common.pim.converter;

import java.text.MessageFormat;

import junit.framework.TestCase;

import com.funambol.common.pim.calendar.Task;
import com.funambol.common.pim.common.Property;
import com.funambol.framework.tools.IOTools;

/**
 * Test cases for TaskToSIFT class.
 * 
 * @version $Id: TaskToSIFTTest.java 35029 2010-07-12 14:21:06Z filmac $
 */
public class TaskToSIFTTest extends TestCase {

    public final static String EXPECTED_SIF_T_STATUS_SUPPORT =
        "src/test/resources/data/com/funambol/common/pim/converter/TaskToSIFTTest/sif-t-testing-status-support.xml";
    public final static String EXPECTED_SIF_T_STATUS_SUPPORT_COMPLETED =
        "src/test/resources/data/com/funambol/common/pim/converter/TaskToSIFTTest/sif-t-testing-status-support-completed.xml";


    TaskToSIFT converter = new TaskToSIFT(null, null);

    public TaskToSIFTTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases
    public void testConvert() throws Exception {
        String expectedValuePattern =
            IOTools.readFileString(EXPECTED_SIF_T_STATUS_SUPPORT);

        Task task = new Task();
        task.getSummary().setPropertyValue("Test the task twin search");
        task.getDtStart().setPropertyValue("20080623T000000");
        task.getDueDate().setPropertyValue("20080627T000000");
        task.getStatus().setPropertyValue("0");

        String expected = MessageFormat.format(expectedValuePattern, new Object[]{"0"});
        String result = converter.convert(task);
        assertEquals(expected.replaceAll("\\r", ""),result.replaceAll("\\r", ""));

        task.getStatus().setPropertyValue("1");
        result = converter.convert(task);
        assertEquals(expected.replaceAll("\\r", ""),result.replaceAll("\\r", ""));
       
        task.getStatus().setPropertyValue("2");
        result = converter.convert(task);
        assertEquals(expected.replaceAll("\\r", ""),result.replaceAll("\\r", ""));

        expected = MessageFormat.format(expectedValuePattern, new Object[]{"1"});
        task.getStatus().setPropertyValue("3");
        result = converter.convert(task);
        assertEquals(expected.replaceAll("\\r", ""),result.replaceAll("\\r", ""));

        task.getStatus().setPropertyValue("4");
        result = converter.convert(task);
        assertEquals(expected.replaceAll("\\r", ""),result.replaceAll("\\r", ""));

        expected = IOTools.readFileString(EXPECTED_SIF_T_STATUS_SUPPORT_COMPLETED);
        task.getStatus().setPropertyValue("5");
        result = converter.convert(task);
        assertEquals(expected.replaceAll("\\r", ""),result.replaceAll("\\r", ""));



        expected = MessageFormat.format(expectedValuePattern, new Object[]{"3"});
        // changed in order to avoid normalization
        task.getStatus().setPropertyValue("6");
        task.getComplete().setPropertyValue(null);
        task.getPercentComplete().setPropertyValue(null);
        result = converter.convert(task);
        assertEquals(expected.replaceAll("\\r", ""),result.replaceAll("\\r", ""));

        task.getStatus().setPropertyValue("7");
        result = converter.convert(task);
        assertEquals(expected.replaceAll("\\r", ""),result.replaceAll("\\r", ""));

        expected = MessageFormat.format(expectedValuePattern, new Object[]{"4"});
        task.getStatus().setPropertyValue("8");
        result = converter.convert(task);
        assertEquals(expected.replaceAll("\\r", ""),result.replaceAll("\\r", ""));

    }

}
